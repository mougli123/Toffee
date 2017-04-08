package com.iodynelabs.toffee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Activity showing all the channels the user has saved for the current server.
 * The channels displayed in this screen do NOT reflect the total channels on the server.
 * Doing so would likely cause the list to be overwhelmingly large on popular servers.
 */
public class ChannelListActivity extends AppCompatActivity {
    /**
     * Static variable used to store the server t prevent the app from crashing when it is closed and then opened on this screen.
     */
    public static String SERVER_ID = "server";
    /**
     * ListView that displays the channels.
     */
    ListView listView;
    /**
     * Current context of the application. Used for starting the conversation activity.
     */
    Context context;
    /**
     * Adapter that controls how the channels are displayed in the ListView.
     */
    ChannelAdapter mAdapter;
    /**
     * List of all the channels the user has saved for the server.
     */
    private List<Channel> channels;
    /**
     * Current server being accessed.
     */
    private Server server;

    /**
     * Called when the activity is first called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        if (savedInstanceState == null && getIntent().hasExtra(SERVER_ID))
            server = getIntent().getParcelableExtra(SERVER_ID);
        else if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SERVER_ID))
                server = savedInstanceState.getParcelable(SERVER_ID);
        }
        channels = server.getChannelList();

        setTitle(server.getServerName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChannel();
            }
        });

        context = this;

        listView = (ListView) findViewById(R.id.channel_list);
        mAdapter = new ChannelAdapter(this, channels);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationFragment.SERVER_ID, server);
                intent.putExtra(ConversationFragment.CHANNEL_ID, i);

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeChannel(i);
                return true;
            }
        });
    }

    /**
     * Updates the ListView adapter to reflect any changes.
     */
    private void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Remove a channel from the server at the specified index after asking for confirmation.
     */
    private void removeChannel(final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Channel?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        channels.remove(index);

                        server.setChannels(channels);
                        FileIO.updateServer(server);
                        updateAdapter();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create().show();
    }

    /**
     * Display a dialog box with a text box for creating a channel with the typed name and adding it to the server.
     */
    private void addChannel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Add Channel");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.add_channel_dialog, null))
                // Add action buttons
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);


        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText input = (EditText) alertDialog.findViewById(R.id.name_entry);

                        if (!input.getText().toString().equals("")) {
                            input.getBackground().clearColorFilter();

                            server.addChannel(input.getText().toString());
                            channels = server.getChannelList();

                            FileIO.updateServer(server);
                            updateAdapter();
                            alertDialog.dismiss();
                        } else
                            input.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    }
                });
            }
        });

        alertDialog.show();
    }

    /**
     * Save the current status of the activity. Used to prevent crashing if the user closes and reopens the app.
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(SERVER_ID, server);
    }

    /**
     * Custom adapter for the Channel ListView.
     */
    public class ChannelAdapter extends ArrayAdapter<Channel> {
        ChannelAdapter(Context context, List<Channel> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            // Get the data item for this position
            Channel channel = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.channel_list_content, parent, false);
            }

            // Lookup view for data population
            TextView channelName = (TextView) view.findViewById(R.id.channel_content);

            // Populate the data into the template view using the data object
            assert channel != null;
            channelName.setText(channel.getChannelName());

            // Return the completed view to render on screen
            return view;
        }
    }
}

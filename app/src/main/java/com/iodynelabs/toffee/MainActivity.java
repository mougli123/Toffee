package com.iodynelabs.toffee;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import io.paperdb.Paper;

/**
 * Starting point of the application and server list
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Tag used for debug messages.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Controls the server list layout.
     */
    private RecyclerView.Adapter mAdapter;
    /**
     * Text that prompts the user to add servers if none are added.
     */
    private TextView noneAdded;

    /**
     * The list of servers.
     */
    private List<Server> mData;

    /**
     * Called when the program starts for the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.server_list_title));

        // Initialize file IO
        Paper.init(this);

        noneAdded = (TextView) findViewById(R.id.none_added);

        mData = FileIO.readServers();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.server_recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new ServerListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        updateAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_server_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServer();
            }
        });

        //createTestingData();

    }

    /**
     * Add a server to the lsit of servers and update the UI accordingly.
     */
    private void addServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Add Server");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.add_server_dialog, null))
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
                        EditText ipField = (EditText) alertDialog.findViewById(R.id.ip_input);
                        EditText nickField = (EditText) alertDialog.findViewById(R.id.nick_input);
                        if (!ipField.getText().toString().equals("")) {
                            ipField.getBackground().clearColorFilter();
                            if (!nickField.getText().toString().equals("")) {
                                nickField.getBackground().clearColorFilter();
                                mData.add(new Server(ipField.getText().toString(), nickField.getText().toString()));
                                FileIO.storeServers(mData);
                                updateAdapter();
                                alertDialog.dismiss();
                            } else
                                nickField.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                        } else
                            ipField.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    }
                });
            }
        });

        alertDialog.show();
    }

    /**
     * Update the internal list of servers to reflect any changes.
     */
    public void updateFiles() {
        mData.clear();
        List<Server> data = FileIO.readServers();
        for (Server s : data)
            mData.add(s);
        updateAdapter();
    }

    /**
     * Update the list of servers to reflect any changes.
     */
    private void updateAdapter() {
        mAdapter.notifyDataSetChanged();
        if (mData.size() == 0)
            noneAdded.setVisibility(View.VISIBLE);
        else
            noneAdded.setVisibility(View.GONE);
    }

    /**
     * Unused in final operation, but would be used for creating testing data to test how the layout looks.
     */
    private void createTestingData(){
        Server te = new Server("192.168.21.115", "Sylver");
        //Server te = new Server("irc.caffie.net", "Sylver");
        //te.addChannel("smwc");
        te.addChannel("woot");

        mData.add(te);
        for (int i = 0; i < 10; i++){
            Server t = new Server("Server " + i, "Sylver" + i);
            for (int j = 0; j <= ((int)(Math.random()*10))+1; j++){
                t.addChannel("Channel" + j);
            }
            t.setStatus((int)(Math.random()*2) == 1);
            mData.add(t);
        }
        updateAdapter();
    }

    /**
     * Delete a server at the specified position.
     */
    private void removeServer(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Server?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mData.remove(position);
                        updateAdapter();

                        FileIO.storeServers(mData);
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
     * Called when screen becomes visible again. Used for updating the list of channels after they've been modified in the channels screen.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        updateFiles();
        Log.w(LOG_TAG, "onRestart");
    }

    /**
     * Adapter for managing the look of the server list.
     */
    private class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {
        private List<Server> mDataset;

        ServerListAdapter(List<Server> data){
            mDataset = data;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ServerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.server_list_card, parent, false);
            // set the view's size, margins, paddings and layout parameters

            return new ViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.serverName.setText(mDataset.get(position).getServerName());
            //holder.channels.setText(""+mDataset.get(position).getChannelCount());
            holder.nickname.setText(mDataset.get(position).getNickname());

            holder.server = mDataset.get(position);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChannelListActivity.class);

                    // Add current server to next page so it can display all the channels
                    intent.putExtra(ChannelListActivity.SERVER_ID, holder.server);

                    // Start the activity
                    startActivity(intent);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeServer(position);
                    return false;
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView serverName;
            TextView nickname;

            Server server;
            View mView;

            ViewHolder(View view) {
                super(view);
                mView = view;
                serverName = (TextView) view.findViewById(R.id.server_name);
                nickname = (TextView) view.findViewById(R.id.list_nick);
            }
        }
    }
}

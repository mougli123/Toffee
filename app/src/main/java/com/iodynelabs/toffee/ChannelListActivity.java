package com.iodynelabs.toffee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Channels. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChannelDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChannelListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static String SERVER_ID = "channel";
    private List<Channel> channels;
    private Server server;

    ListView listView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        if (savedInstanceState == null && getIntent().hasExtra(SERVER_ID))
            server = getIntent().getParcelableExtra(SERVER_ID);

        channels = server.getChannelList();

        setTitle(server.getServerName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        context = this;
        //View recyclerView = findViewById(R.id.channel_list);
        //assert recyclerView != null;
        //setupRecyclerView((RecyclerView) recyclerView);

        listView = (ListView) findViewById(R.id.channel_list);
        listView.setAdapter(new ChannelAdapter(this, channels));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ChannelDetailActivity.class);
                intent.putExtra(ConversationFragment.SERVER_ID, server);
                intent.putExtra(ConversationFragment.CHANNEL_ID, i);

                startActivity(intent);
            }
        });
    }


    public class ChannelAdapter extends ArrayAdapter<Channel> {

        private final List<Channel> mValues;

        public ChannelAdapter(Context context, List<Channel> items) {
            super(context, 0, items);
            mValues = items;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // Get the data item for this position
            Channel channel = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.channel_list_content, parent, false);
            }

            // Lookup view for data population
            TextView channelName = (TextView) view.findViewById(R.id.channel_content);

            // Populate the data into the template view using the data object
            channelName.setText(channel.getChannelName());

            // Return the completed view to render on screen
            return view;
        }
    }
}

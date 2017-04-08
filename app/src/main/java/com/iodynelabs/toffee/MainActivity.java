package com.iodynelabs.toffee;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noneAdded;

    private List<Server> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.server_list_title));

        // Initialize file IO
        Paper.init(this);

        noneAdded = (TextView) findViewById(R.id.none_added);

        mData = new ArrayList<>();
        if (mData.size() == 0) noneAdded.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.server_recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ServerListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_server_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        createTestingData();

    }

    private void createTestingData(){
        Server te = new Server("192.168.21.115", "Sylver");
        te.addChannel("woot");
        Log.w(LOG_TAG, "Channels: " + te.getChannelCount());
        mData.add(te);
        for (int i = 0; i < 10; i++){
            Server t = new Server("Server " + i, "Sylver" + i);
            for (int j = 0; j <= ((int)(Math.random()*10))+1; j++){
                t.addChannel("Channel" + j);
            }
            t.setStatus((int)(Math.random()*2) == 1);
            mData.add(t);
        }
        noneAdded.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    private class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

        private List<Server> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView serverName;
            TextView channels;
            TextView nickname;
            ImageView serverStatus;

            Server server;
            View mView;

            ViewHolder(View view) {
                super(view);
                mView = view;
                serverName = (TextView) view.findViewById(R.id.server_name);
                nickname = (TextView) view.findViewById(R.id.list_nick);
            }
        }

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
        public void onBindViewHolder(final ViewHolder holder, int position) {
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
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}

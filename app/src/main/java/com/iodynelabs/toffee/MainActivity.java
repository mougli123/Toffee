package com.iodynelabs.toffee;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noneAdded;

    private List<Server> mData;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.server_list_title));

        noneAdded = (TextView) findViewById(R.id.none_added);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });

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

        createTestingData();
    }

    private void createTestingData(){
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
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!isRefreshing()) {
                    setRefreshing(true);
                }

                // Start our refresh background task
                initiateRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns whether the {@link android.support.v4.widget.SwipeRefreshLayout} is currently
     * refreshing or not.
     */
    private boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    /**
     * Set whether the {@link android.support.v4.widget.SwipeRefreshLayout} should be displaying
     * that it is refreshing or not.
     */
    private void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    private void onRefreshComplete(List<Server> result) {
        Log.i(LOG_TAG, "onRefreshComplete");

        mData = result;
        mAdapter.notifyDataSetChanged();

        // Stop the refreshing indicator
        setRefreshing(false);
    }

    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");

        /**
         * Execute the background task, which uses {@link android.os.AsyncTask} to load the data.
         */
        new CheckServerStatus().execute();
    }

    // TODO: Implement server status checks
    private class CheckServerStatus extends AsyncTask<Void, Void, List<Server>>{

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected List<Server> doInBackground(Void... voids) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<Server> temp = mData;
            for (int i = 0; i < temp.size(); i++){
                temp.get(i).setStatus(!temp.get(i).getStatus());
            }
            return temp;
        }

        @Override
        protected void onPostExecute(List<Server> result) {
            super.onPostExecute(result);

            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result);
        }
    }
}

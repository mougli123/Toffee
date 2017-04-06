package com.iodynelabs.toffee;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 4/5/2017.
 */

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

    private List<Server> mDataset;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView serverName;
        TextView channels;
        TextView nickname;
        ImageView serverStatus;
        ViewHolder(View view) {
            super(view);
            serverName = (TextView) view.findViewById(R.id.server_name);
            channels = (TextView) view.findViewById(R.id.list_channels);
            nickname = (TextView) view.findViewById(R.id.list_nick);
            serverStatus = (ImageView) view.findViewById(R.id.server_status);
        }
    }

    public ServerListAdapter(List<Server> data){
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.serverName.setText(mDataset.get(position).getServerName());
        holder.channels.setText(""+mDataset.get(position).getChannelCount());
        holder.nickname.setText(mDataset.get(position).getNickname());
        holder.serverStatus.setImageResource(mDataset.get(position).getStatus() ? R.drawable.ic_check_circle_24dp : R.drawable.ic_error_24dp);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

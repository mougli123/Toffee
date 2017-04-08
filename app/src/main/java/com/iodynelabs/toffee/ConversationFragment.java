package com.iodynelabs.toffee;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The actual content of the Conversation Screen.
 */
public class ConversationFragment extends Fragment implements ConversationHandler.ConversationCallback{
    /**
     * Static variable containing a tag for use in persisting data between activity and fragment.
     */
    public static final String SERVER_ID = "server_id";
    /**
     * Static variable containing a tag for use in persisting data between activity and fragment.
     */
    public static final String CHANNEL_ID = "channel_id";
    /**
     * Static variable used for debug purposes.
     */
    private static final String LOG_TAG = ConversationFragment.class.getSimpleName();
    /**
     * Chat history.
     */
    List<Message> messages;
    /**
     * Server currently in.
     */
    Server currentServer;
    /**
     * Channel index user is currently in.
     */
    int currentChannel;
    /**
     * Class where all the networking work gets done since Android does not allow networking on the UI thread.
     */
    ConversationHandler handler;
    /**
     * Thread where all the networking is done on.
     */
    Thread handlerThread;
    /**
     * Send button.
     */
    FloatingActionButton fab;
    /**
     * Message text box.
     */
    EditText editor;
    /**
     * Chat history controller.
     */
    private RecyclerView messageLog;
    /**
     * Controls how the chat history is displayed.
     */
    private RecyclerView.Adapter mAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() {
    }

    /**
     * Called when the fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getActivity().getIntent().hasExtra(SERVER_ID)) {
            currentServer = getActivity().getIntent().getParcelableExtra(SERVER_ID);
            if (getActivity().getIntent().hasExtra(CHANNEL_ID))
                currentChannel = getActivity().getIntent().getIntExtra(CHANNEL_ID, 0);
            else
                currentChannel = 0;
        }

        ((ConversationActivity) getActivity()).setChild(this);
    }

    /**
     * Called when the fragment is made visible.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conversation_fragment, container, false);

        messages = new ArrayList<>(); // Can assume is empty since chat history resets every time

        //createDummyMessages();

        messageLog = (RecyclerView) rootView.findViewById(R.id.chat_log);
        mAdapter = new ConversationAdapter(messages, currentServer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        messageLog.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        messageLog.setLayoutManager(mLayoutManager);

        messageLog.setAdapter(mAdapter);
        messageLog.scrollToPosition(messages.size()-1);

        handler = new ConversationHandler(currentServer, currentChannel, this);
        handlerThread = new Thread(handler);
        connect();

        fab = (FloatingActionButton) rootView.findViewById(R.id.sendText);
        editor = (EditText) rootView.findViewById(R.id.editor_text);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message(currentServer.getNickname(),
                        editor.getText().toString(),
                        DateFormat.getDateTimeInstance().format(new Date()));
                sendMessage(msg);
                editor.setText("");
            }
        });

        return rootView;
    }

    /**
     * Send a message to the current channel.
     */
    void sendMessage(Message msg){
        Log.w(LOG_TAG, "Preparing to send: " + msg.getMessage());
        if (msg.getMessage().equals("")) return;
        handler.sendMessage(msg);
        messages.add(msg);
        updateAdapter();
        messageLog.scrollToPosition(messages.size() - 1);
    }

    /**
     * Update the chat history.
     */
    void updateAdapter(){
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Create fake debug messages to pupulate the list to test the UI.
     */
    void createDummyMessages(){
        Log.w(LOG_TAG, "Present");
        for (int i = 0; i < 30; i++){
            String ms = "";
            for (int j = 0; j < (int)(Math.random()*100)+1; j++)
                ms += "Blah ";
            Message blah = new Message("Dude", ms, DateFormat.getDateTimeInstance().format(new Date()));
            messages.add(blah);
        }
    }

    /**
     * Update the chat history to reflect any new messages.
     */
    public void receivedMessage(Message message){
        messages.add(message);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAdapter();
                messageLog.scrollToPosition(messages.size()-1);
            }
        });

    }

    /**
     * Show a message in a Toast dialog (the text that appears briefly onscreen).
     */
    public void toastMessage(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Disconnect from the channel.
     */
    public void disconnect(){
        Log.w(LOG_TAG, "Ordering stop");
        handler.stop();
    }

    /**
     * Connect to the channel.
     */
    public void connect() {
        Log.w(LOG_TAG, "Ordering connect");

        handlerThread.start();
    }

    /**
     * Callback from the network thread to notify a message has been received so it can be added to the history appropriately.
     *
     * @param msg
     */
    @Override
    public void messageReceived(Message msg) {
        receivedMessage(msg);
    }

    /**
     * Class for the adapter that controls how the chat history is displayed.
     */
    private class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {

        Server server;
        private List<Message> mDataset;

        ConversationAdapter(List<Message> data, Server srv){
            mDataset = data;
            server = srv;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ConversationAdapter.ConversationHolder onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
            // create a new view
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.message_tile, parent, false);
            // set the view's size, margins, paddings and layout parameters

            return new ConversationAdapter.ConversationHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ConversationAdapter.ConversationHolder holder, int position) {
            holder.sender.setText(mDataset.get(position).getSender());
            holder.message.setText(mDataset.get(position).getMessage());
            holder.time.setText(mDataset.get(position).getTime());

            if (mDataset.get(position).getSender().equals(server.getNickname()))
                holder.sender.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        class ConversationHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView sender;
            TextView message;
            TextView time;

            Message msg;
            View mView;

            ConversationHolder(View view) {
                super(view);
                mView = view;
                sender = (TextView) view.findViewById(R.id.sender_text);
                message = (TextView) view.findViewById(R.id.message_content);
                time = (TextView) view.findViewById(R.id.time_text);
            }
        }
    }
}

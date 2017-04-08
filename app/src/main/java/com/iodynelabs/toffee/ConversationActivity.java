package com.iodynelabs.toffee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity for the conversation screen. The actual contents are in {@see ConversationFragment}.
 */
public class ConversationActivity extends AppCompatActivity {

    /**
     * Reference to the actual conversation fragment. Used mostly for handling connecting and disconnecting when the view is created or destroyed.
     */
    ConversationFragment child;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }

    /**
     * Set the fragment that has the conversation. Set by the fragment itself.
     */
    public void setChild(ConversationFragment c) {
        child = c;
    }

    /**
     * Disconnect from the current channel as soon as the user leaves the conversation screen.
     */
    @Override
    protected void onPause() {
        super.onPause();

        child.disconnect();
    }

    /**
     * Reconnect to the current channel if the app was closed while in a channel.
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        child.connect();
    }
}

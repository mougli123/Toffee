package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/7/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.output.OutputIRC;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Background thread to handle all network communications since Android does not allow networking o nthe main thread
 */
public class ConversationHandler implements Runnable {

    interface ConversationCallback{
        void messageReceived(Message msg);
        void toastMessage(String msg);
    }

    private Server currentServer;
    private int currentChannel;
    private ConversationCallback callback;
    private Message messageToSend;
    private PircBotX bot;
    private Configuration.Builder templateConfig;

    private boolean isStopped = false;

    private static final String LOG_TAG = ConversationHandler.class.getSimpleName();

    public ConversationHandler(Server srv, int chnl, ConversationCallback cb){
        currentServer = srv;
        currentChannel = chnl;
        callback = cb;
        messageToSend = null;

        templateConfig = new Configuration.Builder()
                .setName(currentServer.getNickname())
                .addAutoJoinChannel(currentServer.getChannelList().get(currentChannel).getChannelName())
                .addListener(new MessageListener())
                .setAutoNickChange(true)
                .setLogin("Tofee");
    }

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        bot = new PircBotX(templateConfig.buildForServer(currentServer.getServerName()));

        try {
            bot.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final Message msg){
        Log.w(LOG_TAG, "Received order to send message: " + msg.getMessage());
        new Thread(new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().message(currentServer.getChannel(currentChannel).channelName, msg.getMessage());
            }
        }).start();

    }

    /**
     * This should run until the user disconnects from the channel
     * Messages will only update while user is inside the channel
     */
    /*@Override
    protected Boolean doInBackground(Void... voids) {



        return false;
    }*/

    public void stop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().quitServer();
                bot.stopBotReconnect();
            }
        }).start();


    }
    private class MessageListener extends ListenerAdapter{
        @Override
        public void onGenericMessage(GenericMessageEvent event) {
            // Parse message received into Message object
            Message msg = new Message(event.getUser().getNick(),
                    event.getMessage(),
                    DateFormat.getDateTimeInstance().format(new Date(event.getTimestamp())));

            Log.i(LOG_TAG, "Received Message: " + msg.getMessage());
            // Notify main thread a message was received so it writes it on screen
            callback.messageReceived(msg);
        }

        @Override
        public void onConnect(ConnectEvent event) throws Exception {
            super.onConnect(event);
            Log.i(LOG_TAG, "Connected");
            callback.toastMessage("Connected to channel: " + currentServer.getChannel(currentChannel).getChannelName());
        }
    }
}

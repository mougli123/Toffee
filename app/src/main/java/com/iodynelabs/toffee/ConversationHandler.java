package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/7/2017.
 */

import android.util.Log;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Background thread to handle all network communications since Android does not allow networking on the main thread.
 */
class ConversationHandler implements Runnable {

    /**
     * Tag used for debug purposes
     */
    private static final String LOG_TAG = ConversationHandler.class.getSimpleName();
    /**
     * Server currently connected to.
     */
    private Server currentServer;

    /**
     * Index of the channel currently connected to.
     */
    private int currentChannel;

    /**
     * Reference to the UI thread.
     */
    private ConversationCallback callback;

    /**
     * Bot that handles all the communication and networking.
     */
    private PircBotX bot;

    /**
     * Settings for the bot.
     */
    private Configuration.Builder templateConfig;

    /**
     * Constructor for the handler.
     *
     * @param srv  Server to connect to.
     * @param chnl Index of channel to connect to.
     * @param cb   Reference to the callback object.
     */
    ConversationHandler(Server srv, int chnl, ConversationCallback cb) {
        currentServer = srv;
        currentChannel = chnl;
        callback = cb;

        templateConfig = new Configuration.Builder()
                .setName(currentServer.getNickname())
                .addAutoJoinChannel(currentServer.getChannelList().get(currentChannel).getChannelName())
                .addListener(new MessageListener())
                .setAutoNickChange(true)
                .setLogin("Tofee");
    }

    /**
     * Run when thread is started. Used for starting the bot.
     */
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

    /**
     * Called when the bot is supposed to send a message to the server
     */
    void sendMessage(final Message msg){
        Log.w(LOG_TAG, "Received order to send message: " + msg.getMessage());
        new Thread(new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().message(currentServer.getChannel(currentChannel).channelName, msg.getMessage());
            }
        }).start();

    }

    /**
     * Stop the bot and disconnect it.
     */
    void stop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bot.isConnected()) {
                    bot.sendIRC().quitServer();
                    bot.stopBotReconnect();
                }
            }
        }).start();


    }

    /**
     * Callback interface for notifying the UI thread of any messages.
     */
    interface ConversationCallback {
        void messageReceived(Message msg);

        void toastMessage(String msg);
    }

    /**
     * Listeners used by the bot.
     */
    private class MessageListener extends ListenerAdapter {

        /**
         * Called when the bot receives a message
         */
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

        /**
         * Called when the bot successfully connects to the server. Shows the user that the connection was successful
         */
        @Override
        public void onConnect(ConnectEvent event) throws Exception {
            super.onConnect(event);
            Log.i(LOG_TAG, "Connected");
            callback.toastMessage("Connected to channel: " + currentServer.getChannel(currentChannel).getChannelName());
        }
    }
}

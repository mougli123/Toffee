package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/7/2017.
 */

/**
 * Data class for storing a message.
 */
public class Message {
    /**
     * Who sent the message.
     */
    private final String sender;

    /**
     * The content of the message.
     */
    private final String message;

    /**
     * Timestamp of the message.
     */
    private final String time;

    /**
     * Constructor for a message.
     *
     * @param s Sender.
     * @param m Message.
     * @param t Time.
     */
    public Message(String s, String m, String t){
        sender = s;
        message = m;
        time = t;
    }

    /**
     * @return Message contents.
     */
    String getMessage() {
        return message;
    }

    /**
     * @return Sender.
     */
    String getSender() {
        return sender;
    }

    /**
     * @return Timestamp.
     */
    String getTime() {
        return time;
    }
}

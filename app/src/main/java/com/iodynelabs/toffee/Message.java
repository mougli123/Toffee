package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/7/2017.
 */

public class Message {
    private String sender;
    private String message;
    private String time;

    public Message(String s, String m, String t){
        sender = s;
        message = m;
        time = t;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }
}

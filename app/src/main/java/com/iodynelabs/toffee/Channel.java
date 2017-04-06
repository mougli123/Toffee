package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/6/2017.
 */

/**
 * Container class to house channel information
 */
public class Channel {
    Server parent;
    String channelName;

    public Channel(Server parent, String name){
        this.parent = parent;
        channelName = name;
    }

    public Server getParent(){
        return parent;
    }

    public String getChannelName(){
        return channelName;
    }
}

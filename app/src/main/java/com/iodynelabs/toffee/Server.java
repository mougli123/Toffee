package com.iodynelabs.toffee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 4/5/2017.
 */

/**
 * Container class to house server information such as name and channels
 */
public class Server {

    private String serverName;
    private List<Channel> channels;
    private boolean status;
    private String nickname;

    /**
     * Create new server with the specified name
     * @param name Name of the server, will be used for connecting
     */
    public Server(String name, String nick){
        channels = new ArrayList<>();
        serverName = name;
        nickname = nick;
    }

    /**
     * Set nickname after creating
     * @param nick Nickname to set to
     */
    public void setNickname(String nick){
        nickname = nick;
    }

    /**
     *
     * @return Current nickname for server
     */
    public String getNickname(){
        return nickname;
    }

    /**
     * Set server name after creating
     * @param name New name to change to
     */
    public void setServerName(String name){
        serverName = name;
    }

    /**
     *
     * @return Name of the server
     */
    public String getServerName(){
        return serverName;
    }

    /**
     * Add a channel to the current server
     * @param name The name of the channel.
     *             If it doesn't have '#' as the first character, it will be inserted and the name will be converted to lowercase for consistency
     */
    public void addChannel(String name){
        if (!name.substring(0,1).equals("#"))
            name = "#" + name;
        channels.add(new Channel(this, name.toLowerCase()));
    }

    /**
     *
     * @return List of channels in server
     */
    public List<Channel> getChannelList(){
        return channels;
    }

    /**
     *
     * @return Number of channels the server has
     */
    public int getChannelCount(){
        return channels.size();
    }

    /**
     * Set current server status: true=live, false=error
     * @param status Status to set to
     */
    public void setStatus(boolean status){
        this.status = status;
    }

    /**
     *
     * @return Server status: true=live, false=error
     */
    public boolean getStatus(){
        return status;
    }
}

package com.iodynelabs.toffee;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 4/5/2017.
 */

/**
 * Container class to house server information such as name and channels
 */
public class Server implements Parcelable{

    public static final Creator<Server> CREATOR = new Creator<Server>() {
        @Override
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        @Override
        public Server[] newArray(int size) {
            return new Server[size];
        }
    };
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

    protected Server(Parcel in) {
        channels = new ArrayList<>();
        in.readTypedList(channels, Channel.CREATOR);
        serverName = in.readString();
        status = in.readByte() != 0;
        nickname = in.readString();
    }

    /**
     * @return Current nickname for server
     */
    public String getNickname() {
        return nickname;
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
     * @return Name of the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Set server name after creating
     * @param name New name to change to
     */
    public void setServerName(String name){
        serverName = name;
    }

    /**
     * Add a channel to the current server
     * @param name The name of the channel.
     *             If it doesn't have '#' as the first character, it will be inserted and the name will be converted to lowercase for consistency
     */
    public void addChannel(String name){
        if (!name.substring(0,1).equals("#"))
            name = "#" + name;
        channels.add(new Channel(name.toLowerCase()));
    }

    /**
     * Update the list of channels all at once
     */
    public void setChannels(List<Channel> channelList) {
        this.channels = channelList;
    }

    /**
     *
     * @return List of channels in server
     */
    public List<Channel> getChannelList(){
        return channels;
    }

    /**
     * Get channel at the specified index
     */
    public Channel getChannel(int index){
        return channels.get(index);
    }
    /**
     *
     * @return Number of channels the server has
     */
    public int getChannelCount(){
        return channels.size();
    }

    /**
     *
     * @return Server status: true=live, false=error
     */
    public boolean getStatus(){
        return status;
    }

    /**
     * Set current server status: true=live, false=error
     *
     * @param status Status to set to
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeTypedList(channels);
        parcel.writeString(serverName);
        parcel.writeByte((byte) (status ? 1 : 0));
        parcel.writeString(nickname);
    }
}

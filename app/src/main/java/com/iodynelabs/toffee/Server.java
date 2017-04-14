package com.iodynelabs.toffee;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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
    private String nickname;

    /**
     * Create new server with the specified name
     * @param name Name of the server, will be used for connecting
     */
    Server(String name, String nick) {
        channels = new ArrayList<>();
        serverName = name;
        nickname = nick;
    }

    Server(Parcel in) {
        channels = new ArrayList<>();
        in.readTypedList(channels, Channel.CREATOR);
        serverName = in.readString();
        nickname = in.readString();
    }

    /**
     * @return Current nickname for server
     */
    String getNickname() {
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
    String getServerName() {
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
    void addChannel(String name) {
        if (!name.substring(0,1).equals("#"))
            name = "#" + name;
        channels.add(new Channel(name.toLowerCase()));
    }

    /**
     * Update the list of channels all at once
     */
    void setChannels(List<Channel> channelList) {
        this.channels = channelList;
    }

    /**
     *
     * @return List of channels in server
     */
    List<Channel> getChannelList() {
        return channels;
    }

    /**
     * Get channel at the specified index
     */
    Channel getChannel(int index) {
        return channels.get(index);
    }
    /**
     *
     * @return Number of channels the server has
     */
    public int getChannelCount(){
        return channels.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeTypedList(channels);
        parcel.writeString(serverName);
        parcel.writeString(nickname);
    }
}

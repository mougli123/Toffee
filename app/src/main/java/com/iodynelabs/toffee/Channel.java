package com.iodynelabs.toffee;

/**
 * Created by Alejandro on 4/6/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Container class to house channel information
 */
public class Channel implements Parcelable{
    String channelName;

    public Channel(Server parent, String name){
        channelName = name;
    }

    protected Channel(Parcel in) {
        channelName = in.readString();
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };


    public String getChannelName(){
        return channelName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(channelName);
    }
}

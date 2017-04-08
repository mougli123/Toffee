package com.iodynelabs.toffee;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Container class to house channel information.
 */
class Channel implements Parcelable {
    /**
     * Also used for saving and loading instances of the Channel object.
     */
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
    /**
     * Name of the channel.
     */
    String channelName;

    /**
     * Initialize a Channel object with the given name.
     */
    Channel(String name) {
        channelName = name;
    }

    /**
     * Initialize a Channel object from a Parcelable object, used when saving and loading objects.
     */
    private Channel(Parcel in) {
        channelName = in.readString();
    }

    /**
     * @return Name of the channel.
     */
    String getChannelName() {
        return channelName;
    }

    /**
     * Used for saving and loading parcelable instances of Channel.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Save the object to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(channelName);
    }
}

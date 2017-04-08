package com.iodynelabs.toffee;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by Alejandro on 4/7/2017.
 */

public class FileIO {
    public static String SERVER_ID = "servers";
    public static String BOOK_ID = "book";

    /**
     * @return List of all the added servers stored on the device
     */
    public static List<Server> readServers(){
        return Paper.book(BOOK_ID).read(SERVER_ID, new ArrayList<Server>());
    }

    /**
     * Updates the data for a specified server and stores it
     * @param srv
     */
    public static void updateServer(Server srv){
        List<Server> servers = readServers();
        for (int i = 0; i < servers.size(); i++){
            if (servers.get(i).getServerName().equals(srv.getServerName())){
                servers.set(i, srv);
                break;
            }
        }

        Paper.book(BOOK_ID).write(SERVER_ID, servers);
    }

    /**
     * Store a list of servers in file storage
     */
    public static void storeServers(List<Server> servers){
        Paper.book(BOOK_ID).write(SERVER_ID, servers);
    }

}

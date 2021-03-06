package com.iodynelabs.toffee;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Class for handling any File access operations
 */
class FileIO {
    /**
     * Tag used for accessing the server list
     */
    private static String SERVER_ID = "servers";

    /**
     * Tag used for accessing book
     */
    private static String BOOK_ID = "book";

    /**
     * @return List of all the added servers stored on the device
     */
    static List<Server> readServers() {
        return Paper.book(BOOK_ID).read(SERVER_ID, new ArrayList<Server>());
    }

    /**
     * Updates the data for a specified server and stores it
     */
    static void updateServer(Server srv) {
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
    static void storeServers(List<Server> servers) {
        Paper.book(BOOK_ID).write(SERVER_ID, servers);
    }

}

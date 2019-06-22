package com.example.dtanp.masoi.singleton;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import java.net.URISyntaxException;

public class SocketSingleton {
    private static Socket instance;
    private static String HOST = "http://192.168.1.6:3000" ;

    public static Socket getInstance() {
        if (instance==null){
            try {
                instance = IO.socket(HOST);
                instance.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}

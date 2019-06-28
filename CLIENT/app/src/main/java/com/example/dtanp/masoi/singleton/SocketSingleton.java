package com.example.dtanp.masoi.singleton;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import java.net.URISyntaxException;

public class SocketSingleton {
    private static Socket instance;
    public static String HOST="192.168.1.8"; ;

    public static Socket getInstance() {
        if (instance==null){
            try {
                instance = IO.socket("http://"+HOST.trim()+":3000");
                instance.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}

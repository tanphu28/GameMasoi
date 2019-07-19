package com.example.dtanp.masoi.singleton;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import java.net.URISyntaxException;

public class SocketSingleton {
    private static Socket instance;
    public static String HOST ;

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

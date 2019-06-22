package com.example.dtanp.masoi.presenter;

import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.socketio.client.Socket;

public class HomePresenter {
    private Socket socket;

    public HomePresenter() {
        socket = SocketSingleton.getInstance();
    }
}

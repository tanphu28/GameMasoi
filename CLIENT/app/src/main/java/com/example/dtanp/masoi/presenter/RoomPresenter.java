package com.example.dtanp.masoi.presenter;

import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.socketio.client.Socket;

public class RoomPresenter {
    private Socket socket;

    public RoomPresenter() {
        socket = SocketSingleton.getInstance();
    }
}

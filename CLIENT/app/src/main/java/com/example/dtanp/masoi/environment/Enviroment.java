package com.example.dtanp.masoi.environment;

import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserFriends;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

public class Enviroment {
    public  static User user;
    public static User userHost;
    public static Phong phong;
    public  static Socket socket;
    public static Gson gson;
    public  static  final int TOTAL_PEOPLE = 7;
    public  static UserFriends userFriends;
    public  static  int METHOD_LOGIN;
}

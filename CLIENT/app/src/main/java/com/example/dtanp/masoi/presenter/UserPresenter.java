package com.example.dtanp.masoi.presenter;

import android.app.Activity;

import com.example.dtanp.masoi.appinterface.UserView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;


public class UserPresenter {
    private Socket socket;
    private UserView userView;
    private Activity context;
    public UserPresenter(UserView userView, Activity context) {
        socket = SocketSingleton.getInstance();
        this.userView = userView;
        this.context = context;
    }

    public  void  emitFeedBack(String email, String message){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email",email);
        jsonObject.addProperty("message",message);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("feedback",json);
    }

    public  void emitUpdateUserInfo(User user){
        String json = Enviroment.gson.toJson(user);
        Enviroment.socket.emit("updateuserinfo",json);
    }

}

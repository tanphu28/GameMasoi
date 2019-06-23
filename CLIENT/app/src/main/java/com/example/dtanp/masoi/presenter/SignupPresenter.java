package com.example.dtanp.masoi.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.example.dtanp.masoi.SignupActivity;
import com.example.dtanp.masoi.appinterface.SignupView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class SignupPresenter {
    private Socket socket;
    private SignupView signupView;
    private Activity context;
    public SignupPresenter(SignupView signupView, Activity context) {
        socket = SocketSingleton.getInstance();
        this.signupView = signupView;
        this.context = context;
    }

    public  void  emitCheckUser(String st){
        this.socket.emit("CheckUser",st);
    }

    public  void  emitRegistNickname(String userId, String name){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("name", name);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("Registnickname",json);
    }

    public  void listenCheckUser(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if((boolean)args[0]==true)
                        {
                           signupView.checkUser(true);
                        }
                        else {
                            signupView.checkUser(false);
                        }
                    }
                });
            }
        };
        this.socket.on("CheckUser",listener);
    }

    public void  listenRegistnickname(){
        Emitter.Listener listenerLogin = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Enviroment.user = Enviroment.gson.fromJson(jsonObject.toString(), User.class);
                        signupView.loginSuccess();
                    }
                });
            }
        };

        this.socket.on("Registnickname",listenerLogin);
    }

    public void  listenRegister(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if((boolean)args[0]==true)
                        {
                            signupView.register(true);
                        }
                        else
                        {
                            signupView.register(false);
                        }
                    }
                });
            }
        };

       this.socket.on("register_user",listener);
    }

    public void emitRegister(UserStore userStore){
        String json =  Enviroment.gson.toJson(userStore);
        this.socket.emit("register_user",json);
    }

}

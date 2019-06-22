package com.example.dtanp.masoi.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.dtanp.masoi.MainActivity;
import com.example.dtanp.masoi.appinterface.LoginView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.example.dtanp.masoi.utils.MD5Util;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class LoginPresenter {
    private Socket socket;
    private LoginView loginView;
    private Activity context;
    public LoginPresenter(LoginView loginView, Activity context) {
        socket = SocketSingleton.getInstance();
        this.loginView = loginView;
        this.context = context;
    }

    public  void listenRegister(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if((boolean)args[0]==true)
                {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginView.showDialogRegister();
                        }
                    });

                }
            }
        };

        socket.on("register_user",listener);
    }
     public  void listenLogin(){
         Emitter.Listener listener =  new Emitter.Listener() {
             @Override
             public void call(final Object... args) {
                 JSONObject jsonObject = (JSONObject) args[0];
                 Enviroment.user = Enviroment.gson.fromJson(jsonObject.toString(), User.class);
                 loginView.loginSuccess();
             }
         };
         socket.on("LonginSuccess",listener);
     }

     public  void emitCheckUser(String st){
        socket.emit("CheckUser",st);
     }

     public  void  listenCheckUser(){
         Emitter.Listener listener2 = new Emitter.Listener() {
             @Override
             public void call(final Object... args) {
                 context.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         if((boolean)args[0]==true)
                         {
                             loginView.checkUser(true);
                         }
                         else {
                             loginView.checkUser(false);
                         }
                     }
                 });

             }
         };
         socket.on("CheckUser",listener2);
     }

     public void registNicknameLoginFb(String us, String name){
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("userId", us);
         jsonObject.addProperty("name", name);
         String json = Enviroment.gson.toJson(jsonObject);
         socket.emit("RegistnicknameLoginFb",json);
     }

     public void listenRegistNickname(){
         Emitter.Listener listenerLogin = new Emitter.Listener() {
             @Override
             public void call(final Object... args) {
                 JSONObject jsonObject = (JSONObject) args[0];
                 Enviroment.user = Enviroment.gson.fromJson(jsonObject.toString(),User.class);
                 loginView.registNicknameSuccess();
             }
         };

         socket.on("Registnickname",listenerLogin);
     }

     public void emitLoginFB(String userId, String name){
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", userId);
         jsonObject.addProperty("name", name);
         String json = Enviroment.gson.toJson(jsonObject);
         socket.emit("LoginFB",json);
     }

     public void login(String username, String pass){
         Enviroment.METHOD_LOGIN = 1;
         pass = MD5Util.getMD5(pass);
         UserStore userStore = new UserStore(username,pass);
         String json = Enviroment.gson.toJson(userStore);
         socket.emit("login",json);
     }

     public void listenVersionName(final String version){
         Emitter.Listener listener = new Emitter.Listener() {
             @Override
             public void call(final Object... args) {
                 String ver = (String) args[0];
                 if(!version.equals(ver)){
                     context.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             loginView.showDialogUpdate();
                         }
                     });

                 }

             }
         };
         socket.on("CheckVersionName",listener);
     }

     public void listenUserLogin(){
         Emitter.Listener emitterUserLogin=new Emitter.Listener(){

             @Override
             public void call(final Object... args) {
                 context.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         if(args[0]==null)
                         {
                             loginView.userLoginSuccess(false);
                         }
                         else
                         {
                             JSONObject jsonObject = (JSONObject) args[0];
                             Enviroment.user = Enviroment.gson.fromJson(jsonObject.toString(), User.class);
                             loginView.userLoginSuccess(true);
                         }
                     }
                 });

             }
         };
         socket.on("userlogin",emitterUserLogin);
     }

     public void emitCheckVersionName(){
        socket.emit("CheckVersionName",1);
     }

}

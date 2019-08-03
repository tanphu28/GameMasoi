package com.example.dtanp.masoi.presenter;

import android.app.Activity;

import com.example.dtanp.masoi.appinterface.LoginView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserStore;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.example.dtanp.masoi.utils.CommonFunction;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
         pass = CommonFunction.getMD5(pass);
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
    public void emitFinishGame(List<NhanVat> list, int win){
        String json = Enviroment.gson.toJson(list);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("list",json);
        jsonObject.addProperty("win",win);
        this.socket.emit("finishgame",jsonObject.toString());
    }

    public void emitFogetPass(String userId, int method){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId",userId);
        jsonObject.addProperty("method",method);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("fogot",json);
    }

    public void listenFogetPass(){
        this.socket.on("fogot", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            int code  = jsonObject.getInt("code");
                            String userId = jsonObject.getString("userId");
                            loginView.updateFogotPass(code,userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void emitChangePass(String userId, String passNew, String otp){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId",userId);
        jsonObject.addProperty("pass",CommonFunction.getMD5(passNew));
        jsonObject.addProperty("otp",otp);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("changepass", json);
    }

    public void listenChangePass(){
        this.socket.on("changepass", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag  = (boolean) args[0];
                        loginView.updateChangePass(flag);
                    }
                });
            }
        });
    }

    public void listenErrorLogin(){
        this.socket.on("loidangnhap", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginView.updateErrorLogin();
                    }
                });
            }
        });
    }

    public void removeListener(){
        this.socket.off("register_user");
        this.socket.off("LonginSuccess");
        this.socket.off("CheckUser");
        this.socket.off("Registnickname");
        this.socket.off("CheckVersionName");
        this.socket.off("userlogin");
        this.socket.off("fogot");
        this.socket.off("changepass");
        this.socket.off("loidangnhap");
    }

    public void emitCrash(String track, String  message){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("track",track);
        jsonObject.addProperty("message",message);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("crash",json);
    }


}

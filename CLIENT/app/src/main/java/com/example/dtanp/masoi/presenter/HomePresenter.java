package com.example.dtanp.masoi.presenter;

import android.app.Activity;

import com.example.dtanp.masoi.HomeActivity;
import com.example.dtanp.masoi.appinterface.HomeView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserFriends;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomePresenter {
    private Socket socket;
    private HomeView homeView;
    private Activity context;

    public HomePresenter(HomeView homeView, Activity context) {
        socket = SocketSingleton.getInstance();
        this.homeView = homeView;
        this.context = context;
    }

    public void emitGetAllUserFreinds(){
        this.socket.emit("alluserfriend");
    }

    public void listenGetAllUserFreinds(){
        final ArrayList<UserFriends> list = new ArrayList<>();
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObject = (JSONArray) args[0];
                        System.out.println(jsonObject.toString());
                        for (int i =0;i<jsonObject.length();i++)
                        {
                            try {

                                JSONObject jsonObject1 = jsonObject.getJSONObject(i);
                                System.out.println(jsonObject1.toString());
                                UserFriends user = Enviroment.gson.fromJson(jsonObject1.toString(),UserFriends.class);
                                if(Enviroment.user.getUserId().equals(user.getUserId1())){
                                    list.add(user);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        homeView.updateListUserFreinds(list);
                    }

                });
            }
        };
        this.socket.on("alluserfriend",listener);
    }

    public void emitChat(Chat chat){
        String json = Enviroment.gson.toJson(chat);
        this.socket.emit("ChatAll", json);
    }

    public void listenAllChat(){
        Emitter.Listener listenerChatMes = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(json);
                            Chat chat = Enviroment.gson.fromJson(jsonObject.toString(), Chat.class);
                            if (!chat.getMesage().equals(" ")) {
                                homeView.addChatMessage(chat);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };
        this.socket.on("ChatAll", listenerChatMes);
    }


}

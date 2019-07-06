package com.example.dtanp.masoi.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.example.dtanp.masoi.ChooseRoomActivity;
import com.example.dtanp.masoi.appinterface.ChooseRoomView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ChooseRoomPresenter {
    private Socket socket;
    private ChooseRoomView chooseRoomView;
    private Activity context;

    public ChooseRoomPresenter(ChooseRoomView chooseRoomView, Activity context) {
        socket = SocketSingleton.getInstance();
        this.chooseRoomView = chooseRoomView;
        this.context = context;
    }

    public void listenAllRoom() {
        final ArrayList<Phong> list = new ArrayList<>();
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObject = (JSONArray) args[0];
                        System.out.println(jsonObject.toString());
                        list.clear();
                        for (int i = 0; i < jsonObject.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonObject.getJSONObject(i);
                                System.out.println(jsonObject1.toString());
                                Phong phong = Enviroment.gson.fromJson(jsonObject1.toString(), Phong.class);
                                list.add(phong);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        chooseRoomView.updateListView(list);

                    }
                });
            }
        };
        this.socket.on("allroom", listener);
    }

    public void emitAllRoom() {
        this.socket.emit("allroom");
    }

    public void listenJoinRoom() {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = (JSONObject) args[0];
                        boolean flag = false;
                        try {
                            flag = json.getBoolean("flag");
                            if (flag == true) {
                                chooseRoomView.checkRoomFullPeople(true,null);
                            } else {
                                Phong phong = Enviroment.gson.fromJson(json.getString("room"), Phong.class);
                                chooseRoomView.checkRoomFullPeople(false, phong);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        this.socket.on("FullPeople", listener);
    }

    public void listenNewRoom() {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Phong phong = Enviroment.gson.fromJson(jsonObject.toString(), Phong.class);
                        chooseRoomView.addNewRoom(phong);
                    }
                });
            }
        };
        this.socket.on("newroom", listener);
    }

    public void emitCreateRoom(Phong phong) {
        String jsonroom = Enviroment.gson.toJson(phong);
        this.socket.emit("createroom", jsonroom);
    }

    public void emitJoinRoom(User user){
        String jsonuser = Enviroment.gson.toJson(user);
        this.socket.emit("joinroom", jsonuser);
    }

    public void listenRemoveRoom(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chooseRoomView.removeRoom((String) args[0]);
                    }
                });
            }
        };
        this.socket.on("DeleteRoom",listener);
    }
}

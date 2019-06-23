package com.example.dtanp.masoi.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.dtanp.masoi.ChooseRoomActivity;
import com.example.dtanp.masoi.HostActivity;
import com.example.dtanp.masoi.R;
import com.example.dtanp.masoi.appinterface.RoomView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserRoom;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RoomPresenter {
    private Socket socket;
    private RoomView roomView;
    private Activity context;

    public RoomPresenter(RoomView roomView, Activity context) {
        this.socket = SocketSingleton.getInstance();
        this.context = context;
        this.roomView = roomView;
    }

    public void listenChat(){
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
                                roomView.updateChatMessage(chat);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        };
        this.socket.on("Chat", listenerChatMes);
    }

    public void listenUserExit(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        roomView.updateUserExit(id);
                    }
                });
            }
        };
        this.socket.on("userexit", listener);
    }

    public void listenUserReady(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
               context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int t = (int) args[0];
                        roomView.updateUserReady(t);
                    }
                });
            }
        };
        this.socket.on("ready", listener);
    }

    public void emitOk(boolean flag){
        this.socket.emit("OK", flag);
    }

    public void emitBangBoPhieu(int number){
        this.socket.emit("BangBoPhieu", number);
    }

    public void emitUserHostExit(String userId){
        this.socket.emit("userhostexit", Enviroment.user.getUserId());
    }

    public void emitKickUser(String userId){
        this.socket.emit("kickuser", userId);
    }

    public void emitChatMessage(Chat chat){
        String json = Enviroment.gson.toJson(chat);
        this.socket.emit("Chat", json);
    }

    public void listenNewUserJoinRoom(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        User user = Enviroment.gson.fromJson(jsonObject.toString(), User.class);
                        roomView.updateNewUserJoinRoom(user);
                    }
                });
            }
        };
        this.socket.on("newuser", listener);
    }

    public void emitUserBiBoPhieuTat(String id){
        this.socket.emit("UserBoPhieuTat", id);
    }

    public void listenTime(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomView.updateTime((String) args[0]);
                    }
                });
            }
        };
        this.socket.on("time",listener);
    }

    public void listenUserDie(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        roomView.updateUserDie(id);
                    }
                });
            }
        };
        this.socket.on("UserDie", listener);
    }

    public void emitSendListNhanVat(List<NhanVat> listNhanVat){
        String jsonlist = Enviroment.gson.toJson(listNhanVat);
        this.socket.emit("SendListNhanVat", jsonlist);
        this.socket.emit("ListNhanVat", jsonlist);
    }

    public void emitLuot(int luot){
        this.socket.emit("Luot", luot);
    }

    public void listenGetNhanVat(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    NhanVat nv = Enviroment.gson.fromJson(jsonObject.toString(), NhanVat.class);
                                    if (nv.getId().toString().trim().equals(Enviroment.user.getUserId().trim())) {
                                        roomView.updateNhanVat(nv);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        this.socket.on("ListNhanVat", listener);
    }

    public void emitLuotDB(int luot){
        this.socket.emit("Luot", luot);
    }

    public void listenLuotDB(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int l = (int) args[0];
                        roomView.updateLuotDB(l);
                    }
                });
            }
        };
        this.socket.on("Luot", listener);
    }

    public void emitIDBiBoPhieu(String userId){
        this.socket.emit("IDBiBoPhieu", userId);
    }

    public void emitUserBoPhieu(String userId){
        this.socket.emit("UserBoPhieu", userId);
    }

    public void emitUserDie(String userId){
        this.socket.emit("UserDie", userId);
    }

    public void listenKetQuaBoPhieu(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int t = (int) args[0];
                        roomView.updateKetQuaBoPhieu(t);
                    }
                });
            }
        };
        this.socket.on("BangBoPhieu", listener);
    }

    public void emitNhanvatSang(int number){
        this.socket.emit("NhanVatsang", number);
    }

    public  void emitNhanvatTat(int number){
        this.socket.emit("NhanVatTat", number);
    }

    public void emitAllChat(boolean flag){
        this.socket.emit("AllChat", flag);
    }

    public void emitAllManHinhChon(boolean flag){
        this.socket.emit("AllManHinhChon", flag);
    }

    public void listenSuKien(){
        Emitter.Listener listenerBaoVe = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               roomView.updateIdBaoVeChon((String) args[0]);
            }
        };
        this.socket.on("4", listenerBaoVe);
        //Phu thuy
        Emitter.Listener listenerTienTri = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                roomView.updateIdTientriChon((String) args[0]);
            }
        };
        this.socket.on("6", listenerTienTri);

        //Tho San

        Emitter.Listener listenerThoSan = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               roomView.updateIdThoSanChon((String) args[0]);
            }
        };
        this.socket.on("3", listenerThoSan);

        //Ma Soi
        Emitter.Listener listenerMaSoi = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                roomView.updateIdMaSoiChon((String) args[0]);
            }
        };
        this.socket.on("1", listenerMaSoi);
    }

    public void listenUserIdBiGiet(){
        Emitter.Listener listenerBangIdChon = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                roomView.updateIdBiGiet((String) args[0]);
            }
        };
        this.socket.on("BangIdChon", listenerBangIdChon);
    }

    public void listenLuot(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomView.updateNhanVatSang((int) args[0]);
                    }
                });

            }
        };
        this.socket.on("NhanVatsang", listener);
        Emitter.Listener listenerTat = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomView.updateNhanVatTat((int) args[0]);
                    }
                });

            }
        };
        this.socket.on("NhanVatTat", listenerTat);
    }

    public void listenAllChat(){
        Emitter.Listener listenerChat = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        roomView.updateAllChat(flag);
                    }
                });

            }
        };
        this.socket.on("AllChat", listenerChat);
    }

    public void emitChonUser(String st, int manv, String idchon){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("manv", manv + "");
        jsonObject.addProperty("idchon", idchon+ "");
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit(st, json);
    }

    public void listenAllManHinh(){
        Emitter.Listener listenerAll = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        roomView.updateAllManhinh(flag);
                    }
                });

            }
        };
        this.socket.on("AllManHinhChon", listenerAll);
    }

    public void listenBangIDChon(){
        Emitter.Listener listenerIdChon = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String st = (String) args[0];
                        String name = "";
                        roomView.updateBangIdChon(st);
                    }
                });

            }
        };
        this.socket.on("IDBiBoPhieu", listenerIdChon);
    }

    public void listenOK(){
        Emitter.Listener listenerOK = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];

                    }
                });

            }
        };
        this.socket.on("OK", listenerOK);
    }

    public void emitReady(int number){
        this.socket.emit("ready", number);
    }

}

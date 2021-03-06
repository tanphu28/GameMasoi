package com.example.dtanp.masoi.presenter;

import android.app.Activity;
import com.example.dtanp.masoi.appinterface.RoomView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
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
        JsonObject jsonObject =new JsonObject();
        jsonObject.addProperty("id",number);
        jsonObject.addProperty("name",Enviroment.user.getName());
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("BangBoPhieu", json);
    }

    public void emitUserHostExit(String userId){
        this.socket.emit("userhostexit", userId);
    }

    public void emitUserExit(String userId){
        this.socket.emit("userexit", userId);
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

    public void emitTime(int time){
        this.socket.emit("time",time+"");
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
                            ArrayList<NhanVat> list = new ArrayList<>();
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    NhanVat nv = Enviroment.gson.fromJson(jsonObject.toString(), NhanVat.class);
                                    list.add(nv);
                                    if (nv.getId().toString().trim().equals(Enviroment.user.getUserId().trim())) {
                                        roomView.updateNhanVat(nv);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            roomView.updateListNhanVat(list);
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
    public void emitNhanVatChucNangDie(int number){
        this.socket.emit("NhanVatChucNangDie",number);
    }

    public void listenNhanVatChucNangDie(){
        this.socket.on("NhanVatChucNangDie", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomView.updateNhanVatChucNangDie((int) args[0]);
                    }
                });
            }
        });
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

    public void emitChonUser(String st, int manv, String idchon, String name, String nameChoose){
        JsonObject jsonObject = new JsonObject();
//        try {
//            jsonObject.put("manv",manv + "");
//            jsonObject.put("idchon",idchon + "");
//            jsonObject.put("name",name + "");
//            jsonObject.put("namechoose",nameChoose + "");

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        jsonObject.addProperty("manv", manv + "");
        jsonObject.addProperty("idchon", idchon+ "");
        jsonObject.addProperty("name",name);
        jsonObject.addProperty("namechoose",nameChoose);
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
                        roomView.updateOK(flag);
                    }
                });

            }
        };
        this.socket.on("OK", listenerOK);
    }

    public void emitReady(int number){
        this.socket.emit("ready", number);
    }

    public void listenKickUser() {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        if (Enviroment.user.getUserId().trim().equals(id)) {
                            socket.emit("userexit", Enviroment.user.getUserId() + "");
                            Enviroment.phong.getUsers().clear();
                            Enviroment.phong = null;
                            Enviroment.user.setId_room("");
                            roomView.updateLeaveRoom();
                        }
                    }
                });
            }
        };
        this.socket.on("leaveroom", listener);
    }

    public void listenUserUpHost(){
        Emitter.Listener listener= new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        if (Enviroment.user.getUserId().equals(id)){
                            roomView.updateHost();
                        }
                    }
                });
            }
        };
        this.socket.on("useruphost",listener);
    }

    public void emitFinishGame(List<NhanVat> list, int win, int gold){
        String json = Enviroment.gson.toJson(list);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gold",gold);
        jsonObject.addProperty("list",json);
        jsonObject.addProperty("win",win);
        this.socket.emit("finishgame",jsonObject.toString());
    }

    public void emitFinishToClient(int win){
        this.socket.emit("win",win);
    }

    public void listenFinish(){
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int win = (int) args[0];
                        roomView.updateFinish(win);
                    }
                });
            }
        };
        this.socket.on("win",listener);
    }

    public void emitXuLyCuoiNgay(){
        this.socket.emit("cuoingay");
    }

    public void listenXuLyCuoiNgay(){
        this.socket.on("cuoingay", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        System.out.println(jsonObject.toString());
                        try {
                            String idBoPhieu = jsonObject.getString("idBOPHIEU");
                            String idUserDie = jsonObject.getString("idUserDie");
                            String idBaoVeChon = jsonObject.getString("idBaoVeChon");
                            String idThoSanChon = jsonObject.getString("idThoSanChon");
                            List<String> listAll = new ArrayList<>();
                            List<Integer> listKQBP = new ArrayList<>();
                            List<String> listMaSoiChon = new ArrayList<>();

                            JSONArray jsonArray = jsonObject.getJSONArray("arrAll");
                            JSONArray jsonArray2 = jsonObject.getJSONArray("arrKetQuaBoPhieu");
                            JSONArray jsonArray3 = jsonObject.getJSONArray("arrMaSoiChon");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                listAll.add(jsonArray.getString(i));
                            }
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                listKQBP.add(jsonArray2.getInt(i));
                            }
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                listMaSoiChon.add(jsonArray3.getString(i));
                            }
                            roomView.updateCuoiNgay(idBaoVeChon,idThoSanChon,listMaSoiChon,listKQBP,idBoPhieu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void emitResetNgayMoi(){
        this.socket.emit("resetngaymoi");
    }

    public void emitListDanLangChon(){
        this.socket.emit("listdanlangchon");
    }

    public void listenListDanLangChon(){
        this.socket.on("listdanlangchon", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> listAll = new ArrayList<>();
                        JSONArray jsonArray = (JSONArray) args[0];
                        for (int i=0; i<jsonArray.length();i++){
                            try {
                                listAll.add(jsonArray.getString(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        roomView.updateListDanLangChon(listAll);
                    }
                });
            }
        });
    }

    public void emitSync(boolean flagChat, boolean flagXuLi,int manv ){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("flagchat",flagChat);
        jsonObject.addProperty("flagxuli",flagXuLi);
        jsonObject.addProperty("manv",manv);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("sync",json);
    }

    public void listenSync(){
        this.socket.on("sync", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
             context.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     JSONObject jsonObject = null;
                     try {
                         jsonObject = new JSONObject((String) args[0]);
                         boolean flagChat = jsonObject.getBoolean("flagchat");
                         boolean flagXuLi = jsonObject.getBoolean("flagxuli");
                         int manv = jsonObject.getInt("manv");
                         roomView.updateSync(flagChat,flagXuLi,manv);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                 }
             });
            }
        });
    }

    public void emitUpdateHost(){
        this.socket.emit("updatehost");
    }

    public void listenListAllChon(){
        socket.on("listallchon", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            String name = jsonObject.getString("name");
                            String nameChoose = jsonObject.getString("namechoose");
                            roomView.updateListAllChon(name,nameChoose);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    public void listenDisconect(){
        this.socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomView.updateDisconnect();
                    }
                });
            }
        });
    }
    public void removeListen(){
        this.socket.off("disconnect");
        this.socket.off("Chat");
        this.socket.off("userexit");
        this.socket.off("ready");
        this.socket.off("newuser");
        this.socket.off("time");
        this.socket.off("UserDie");
        this.socket.off("ListNhanVat");
        this.socket.off("Luot");
        this.socket.off("BangBoPhieu");
        this.socket.off("NhanVatChucNangDie");
        this.socket.off("4");
        this.socket.off("6");
        this.socket.off("3");
        this.socket.off("1");
        this.socket.off("BangIdChon");
        this.socket.off("NhanVatsang");
        this.socket.off("NhanVatTat");
        this.socket.off("AllChat");
        this.socket.off("AllManHinhChon");
        this.socket.off("IDBiBoPhieu");
        this.socket.off("OK");
        this.socket.off("leaveroom");
        this.socket.off("useruphost");
        this.socket.off("win");
        this.socket.off("cuoingay");
        this.socket.off("listdanlangchon");
        this.socket.off("sync");
        this.socket.off("listallchon");
        this.socket.off("ListBangBoPhieu");
        this.socket.off("listuserdie");
        this.socket.off("listuserexit");
        this.socket.off("synclistnhanvat");
        this.socket.off("syncforuser");
    }

    public void listenListBangBoPhieu(){
        this.socket.on("ListBangBoPhieu", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            String name = jsonObject.getString("name");
                            int number = jsonObject.getInt("id");
                            String bp = "";
                            if(number==1){
                                bp = "Giet";
                            }else {
                                bp = "Khong Giet";
                            }
                            roomView.updateListBoPhieu(name,bp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void emitListUserDie(List<String> list){
        String json = Enviroment.gson.toJson(list);
        this.socket.emit("listuserdie",json);
    }

    public void listenListUserDie(){
        this.socket.on("listuserdie", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> list = new ArrayList<>();
                       JSONArray jsonArray = (JSONArray) args[0];
                       for (int i=0; i<jsonArray.length();i++){
                           try {
                               list.add(jsonArray.getString(i));
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                        roomView.updateListUserDie(list);
                    }
                });
            }
        });
    }

    public void removeListenMember(){
        this.socket.off("listuserdie");
        this.socket.off("sync");
        this.socket.off("leaveroom");
        this.socket.off("useruphost");
    }

    public void listenSyncListNhanVat(){
        this.socket.on("synclistnhanvat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> listDie = new ArrayList<>();
                        List<NhanVat> listNhanVat = new ArrayList<>();
                        JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            JSONArray jsonArrayDie = new JSONArray(jsonObject.getString("listdie"));
                            JSONArray jsonArrayNhanVat = new JSONArray(jsonObject.getString("listnhanvat"));
                            for (int i=0; i<jsonArrayDie.length();i++){
                                listDie.add(jsonArrayDie.getString(i));
                            }
                            System.out.println("toi 1");
                            for (int i=0; i<jsonArrayNhanVat.length();i++){
                                JSONObject jsonObject1 = jsonArrayNhanVat.getJSONObject(i);
                                NhanVat nhanVat = Enviroment.gson.fromJson(jsonObject1.toString(),NhanVat.class);
                                listNhanVat.add(nhanVat);
                            }
                            System.out.println("toi 2");
                            roomView.updateListReset(listDie,listNhanVat);
                            System.out.println("Ok  synclistnhanvat");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
    public void emitSyncListNhanVat(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid",Enviroment.user.getUserId());
        jsonObject.addProperty("phong",Enviroment.phong.get_id());
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("synclistnhanvat",json);
    }

    public void lisetSyncForUser(){
        this.socket.on("syncforuser", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        try {
                            String userId  = jsonObject.getString("userId");
                            String id  = jsonObject.getString("id");
                            roomView.updateSyncForUser(userId,id);
                            System.out.println("syncforuser ok");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    public void emitSyncForUser(List<String> listDie,List<NhanVat> list,String userId){
        String jsonldie = Enviroment.gson.toJson(listDie);
        String jsonnhanvat = Enviroment.gson.toJson(list);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid",userId);
        jsonObject.addProperty("listdie",jsonldie);
        jsonObject.addProperty("listnhanvat",jsonnhanvat);
        String json = Enviroment.gson.toJson(jsonObject);
        this.socket.emit("syncforuser",json);
    }

    public void emitListenRoom(){
        this.socket.emit("listenroom",Enviroment.phong.get_id());
        System.out.println("room ne" + Enviroment.phong.get_id());
    }

    public void emitListUserExit(List<String> list){
        String json  = Enviroment.gson.toJson(list);
        this.socket.emit("listuserexit",json);
    }

    public void listenListUserExit(){
            this.socket.on("listuserexit", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> list = new ArrayList<>();

                        try {
                            JSONArray jsonArray = new JSONArray((String) args[0]);
                            for (int i=0; i<jsonArray.length(); i++){
                                list.add(jsonArray.getString(i));
                            }
                            roomView.updateListUserExit(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    public void emitListUserInGame(List<User> list){
        String json = Enviroment.gson.toJson(list);
        this.socket.emit("updateuseringame",json);
    }




}

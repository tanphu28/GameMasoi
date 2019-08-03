package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConnectAtivity extends Activity {

    private   EditText edtConnect;
    private Button btnConnect,btnConnectServer;
    boolean flag= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ativity);
        edtConnect = findViewById(R.id.edtConnect);
        btnConnect = findViewById(R.id.btnConnect);
        btnConnectServer=findViewById(R.id.btnConnectServer);
//        final Socket socket = SocketSingleton.getInstance();
//        socket.on("room", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                System.out.println(args[0]);
////                JSONObject jsonObject = (JSONObject) args[0];
////                System.out.println(jsonObject.toString());
////                try {
////                    String idbophieu = jsonObject.getString("idBOPHIEU");
////                    String idUserDie= jsonObject.getString("idUserDie");
////                    String idBaoVeChon= jsonObject.getString("idBaoVeChon");
////                    String idThoSanChon= jsonObject.getString("idThoSanChon");
////                    List<String> listAll = new ArrayList<>();
////                    List<Integer> listKQBP = new ArrayList<>();
////                    List<String> listMaSoiChon = new ArrayList<>();
////
////                    JSONArray jsonArray = jsonObject.getJSONArray("arrAll");
////                    JSONArray jsonArray2 = jsonObject.getJSONArray("arrKetQuaBoPhieu");
////                    JSONArray jsonArray3 = jsonObject.getJSONArray("arrMaSoiChon");
////
////                    for (int i=0;i<jsonArray.length();i++){
////                        listAll.add(jsonArray.getString(i));
////                    }
////                    for (int i=0;i<jsonArray2.length();i++){
////                        listKQBP.add(jsonArray2.getInt(i));
////                    }
////                    for (int i=0;i<jsonArray3.length();i++){
////                        listMaSoiChon.add(jsonArray3.getString(i));
////                    }
////
////                    System.out.println(listAll.size());
////                    System.out.println(listKQBP.size());
////                    System.out.println(listMaSoiChon.size());
////
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//            }
//        });
//        socket.emit("room");
////        socket.on("sync", new Emitter.Listener() {
////            @Override
////            public void call(final Object... args) {
////                ConnectAtivity.this.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        JSONObject jsonObject = null;
////                        try {
////                            jsonObject = new JSONObject((String) args[0]);
////                            boolean flagChat = jsonObject.getBoolean("flagchat");
////                            boolean flagXuLi = jsonObject.getBoolean("flagxuli");
////                            int manv = jsonObject.getInt("manv");
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////
////                    }
////                });
////            }
////        });
////        JsonObject jsonObject = new JsonObject();
////        jsonObject.addProperty("flagchat",true);
////        jsonObject.addProperty("flagxuli",false);
////        jsonObject.addProperty("manv",2);
////        Enviroment.gson = new Gson();
////        String json = Enviroment.gson.toJson(jsonObject);
////        socket.emit("sync",json);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketSingleton.HOST ="http://" +  edtConnect.getText().toString().trim() + ":3000";
                SharedPreferences sharedPreferences = getSharedPreferences("host", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("url",SocketSingleton.HOST);
                editor.commit();
                Intent intent = new Intent(ConnectAtivity.this,MainActivity.class);
                startActivity(intent);
                finish();
//                if (flag==true){
//                    socket.emit("rs");
//                    flag=false;
//                }
//                else {
//                    socket.emit("room");
//                    flag=true;
//                }
            }
        });

        btnConnectServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketSingleton.HOST ="https://app-gamemasoi.herokuapp.com/";
                Intent intent = new Intent(ConnectAtivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}

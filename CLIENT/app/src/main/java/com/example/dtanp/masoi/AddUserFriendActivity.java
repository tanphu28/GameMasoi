package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.adapter.CustomListUser;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.User;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddUserFriendActivity extends Activity {

    private RecyclerView recyclerView;
    CustomListUser mRcvAdapter, mRcvAdapterSearch;
    List<User> list,filterdList;
    // private ArrayList<User> filterdList;
    EditText edtsearch ,edtUser;
    Emitter.Listener eListenerAllUser;
    int textlength=0;
    ImageButton btnback;

    List<Chat> listChat;
    ListView listViewChat1;
    Button btnGoToChat;
    CustomAdapterChat adapterChat;
    TextView edt ;
    Button btn;
    RelativeLayout relativeLayoutChat;
    ImageButton imgChat,imgCancleChat;
    boolean flagChat = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_friend);
        recyclerView=findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        edtsearch = findViewById(R.id.search1);
        btnback = findViewById(R.id.btnBack1);
        list=new ArrayList<>();
        filterdList=new ArrayList<>();
        LangNgheAllChat();
        mRcvAdapter = new CustomListUser(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(mRcvAdapter);
        mRcvAdapter.notifyDataSetChanged();

        edt = findViewById(R.id.edtChat1);
        btn = findViewById(R.id.btnSend1);
        listViewChat1 = findViewById(R.id.listChat1);
        listViewChat1.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listChat = new ArrayList<>();
        adapterChat = new CustomAdapterChat(AddUserFriendActivity.this, R.layout.custom_chat, listChat);
        listViewChat1.setAdapter(adapterChat);

        relativeLayoutChat =findViewById(R.id.groupChat);
        imgChat = findViewById(R.id.imgChat);
        imgCancleChat = findViewById(R.id.imgCancleChat);

        adapterChat.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewChat1.setSelection(adapterChat.getCount() - 1);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ok ");
                Chat chat = new Chat();

                if (edt.getText().toString() != "") {
                    chat.setUsername(StaticUser.user.getName());
                    chat.setMesage(edt.getText().toString());
                    sendUser(chat);
                    edt.setText("");
                    System.out.println("aaaa");

                }
            }
        });
        imgCancleChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChat = false;
                relativeLayoutChat.setVisibility(View.INVISIBLE);
            }
        });

        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagChat==false)
                {
                    relativeLayoutChat.setVisibility(View.VISIBLE);
                    flagChat = true;
                }
                else
                {
                    relativeLayoutChat.setVisibility(View.INVISIBLE);
                    flagChat = false;
                }
            }
        });
        imgCancleChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagChat = false;
                relativeLayoutChat.setVisibility(View.INVISIBLE);
            }
        });

        StaticUser.socket.emit("alluser");
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startmhhome();
            }
        });
        eListenerAllUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                AddUserFriendActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonObject = (JSONArray) args[0];
                        System.out.println(jsonObject.toString());
                        for (int i =0;i<jsonObject.length();i++)
                        {
                            try {
                                JSONObject jsonObject1 = jsonObject.getJSONObject(i);
                                System.out.println(jsonObject1.toString());
                                User user = StaticUser.gson.fromJson(jsonObject1.toString(),User.class);
                                list.add(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        mRcvAdapter.notifyDataSetChanged();

                    }

                });
            }
        };
        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(AddUserFriendActivity.this,"long click",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        StaticUser.socket.on("alluser",eListenerAllUser);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                if(s.length()>0){


                    textlength = edtsearch.getText().length();
                    for(int i=0;i< list.size();i++){
                        //   final String text =list.get(i).getName().toString();
                        if(textlength<=list.get(i).getName().toLowerCase().length()){
                            if(list.get(i).getName().toLowerCase().trim().contains(edtsearch.getText().toString().toLowerCase().trim())){
                                filterdList.add(list.get(i));

                            }
                        }


                    }
                    mRcvAdapter=new CustomListUser(filterdList);
                    recyclerView.setAdapter(mRcvAdapter);
                    mRcvAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }
    public void sendUser(Chat chat){
        String json = StaticUser.gson.toJson(chat);
        StaticUser.socket.emit("ChatUser", json);
    }
    public void LangNgheAllChat()
    {
        Emitter.Listener listenerChatMes = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                AddUserFriendActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(json);
                            Chat chat = StaticUser.gson.fromJson(jsonObject.toString(), Chat.class);

                            if (!chat.getMesage().equals(" ")) {
                                listChat.add(chat);
                                adapterChat.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("ChatUser", listenerChatMes);
    }

    public void startmhhome()
    {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    public   void filter(String text){
        ArrayList<User> filterName=new ArrayList<>();
        for(User s: list){
            if(s.toString().toLowerCase().contains(text.toLowerCase())){
                filterName.add(s);
            }
        }


    }

}

package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.adapter.CustomListUserFriends;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.UserFriends;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class HomeActivity extends Activity {

    private static final boolean AUTO_HIDE = true;


    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    List<Chat> listChat;
    ListView listViewChat;
    CustomAdapterChat adapterChat;
    EditText edtChat;
    Button btnSend;
    RelativeLayout relativeLayoutChat;
    ImageButton imgChat,imgCancleChat;
    boolean flagChat = false;
    TextView txtUser;
    LoginButton loginButton;
    ImageButton btnUserinfo;

    private RecyclerView recyclerView;
    CustomListUserFriends mRcvAdapter;
    List<UserFriends> list;

    EditText edtsearch;
    ImageButton imgsearch;
    Emitter.Listener eListenerAllUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        AddEvents();
        AddConTrols();
        LangNgheAllChat();
        txtUser.setText(Enviroment.user.getName());

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void AddEvents() {
        findViewById(R.id.btnchonban).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startmhroom();
                //finish();
            }
        });
        listViewChat = findViewById(R.id.listChat);
        listViewChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listChat = new ArrayList<>();
        adapterChat = new CustomAdapterChat(this, R.layout.custom_chat, listChat);
        listViewChat.setAdapter(adapterChat);
        adapterChat.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewChat.setSelection(adapterChat.getCount() - 1);
            }
        });
        btnSend = findViewById(R.id.btnSend);
        edtChat = findViewById(R.id.edtChat);
        relativeLayoutChat =findViewById(R.id.groupChat);
        imgChat = findViewById(R.id.imgChat);
        imgCancleChat = findViewById(R.id.imgCancleChat);
        txtUser = findViewById(R.id.txtuser);
        loginButton = findViewById(R.id.login_button);
        btnUserinfo = findViewById(R.id.btnUserinfo);
        //user friend
        recyclerView=findViewById(R.id.recycler_view);
        edtsearch = findViewById(R.id.edtsearch);
        imgsearch = findViewById(R.id.imgsearch);
        list=new ArrayList<>();
        mRcvAdapter = new CustomListUserFriends(list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(mRcvAdapter);

        Enviroment.socket.emit("alluserfriend");

        eListenerAllUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                HomeActivity.this.runOnUiThread(new Runnable() {
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
                        mRcvAdapter.notifyDataSetChanged();

                    }

                });
            }
        };
        Enviroment.socket.on("alluserfriend",eListenerAllUser);
    }

    private void AddConTrols() {

        imgsearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startmhuserfr();

            }
        });

        btnUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,UserActivity.class);
                startActivity(intent);
            }
        });
        edtChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chat = new Chat();
                if (edtChat.getText().toString() != "") {
                    chat.setUsername(Enviroment.user.getName());
                    chat.setMesage(edtChat.getText().toString());
                    send(chat);
                    edtChat.setText("");
                }
                hide();
                System.out.println("aaaa");
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

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                s=s.toString().toLowerCase();
                final List<UserFriends> filterdList =new ArrayList<>();
                for(int i=0;i< list.size();i++){

                    final String text =list.get(i).toString();
                    if(text.contains(s)){
                        filterdList.add(list.get(i));
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRcvAdapter=new CustomListUserFriends(filterdList);
                recyclerView.setAdapter(mRcvAdapter);
                mRcvAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    public   void filter(String text){
        List<UserFriends> filterName=new ArrayList<>();
        for(UserFriends s: list){
            if(s.getUserId1().contains(text.toLowerCase())){
                filterName.add(s);
            }
        }
        this.list=filterName;
    }

    public void send(Chat chat) {
        String json = Enviroment.gson.toJson(chat);
        Enviroment.socket.emit("ChatAll", json);

    }

    public void LangNgheAllChat()
    {
        Emitter.Listener listenerChatMes = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                HomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(json);
                            Chat chat = Enviroment.gson.fromJson(jsonObject.toString(), Chat.class);
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
        Enviroment.socket.on("ChatAll", listenerChatMes);
    }

    public void startmhroom()
    {
        Intent intent = new Intent(this,ChooseRoomActivity.class);
        startActivity(intent);
        finish();
    }

    public void startmhuserfr()
    {
        Intent intent = new Intent(this,AddUserFriendActivity.class);
        startActivity(intent);
        // finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}

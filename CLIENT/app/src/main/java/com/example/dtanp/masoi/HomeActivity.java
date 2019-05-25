package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
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
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Chat;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        AddEvents();
        AddConTrols();
        LangNgheAllChat();
        txtUser.setText(StaticUser.user.getName());

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
    }

    private void AddConTrols() {

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
                    chat.setUsername(StaticUser.user.getName());
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
    }

    public void send(Chat chat) {
        String json = StaticUser.gson.toJson(chat);
        StaticUser.socket.emit("ChatAll", json);

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
        StaticUser.socket.on("ChatAll", listenerChatMes);
    }

    public void startmhroom()
    {
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
        finish();
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

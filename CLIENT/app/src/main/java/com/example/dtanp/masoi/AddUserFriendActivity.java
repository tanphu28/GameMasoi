package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.adapter.CustomListUser;
import com.example.dtanp.masoi.appinterface.UserFriendView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.presenter.UserFriendPresenter;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddUserFriendActivity extends Activity  implements UserFriendView {
    private static final boolean AUTO_HIDE = true;


    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

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
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private RecyclerView recyclerView;
    CustomListUser mRcvAdapter;
    List<User> list,filterdList;
    EditText edtsearch;
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
    private UserFriendPresenter userFriendPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_friend);
        mContentView = findViewById(R.id.fullscreen_content);
        userFriendPresenter = new UserFriendPresenter(AddUserFriendActivity.this,AddUserFriendActivity.this);
        userFriendPresenter.listenAllChat();
        userFriendPresenter.listenGetAllUser();
        userFriendPresenter.emitGetAllUser();
        recyclerView=findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        edtsearch = findViewById(R.id.search1);
        btnback = findViewById(R.id.btnBack1);
        list=new ArrayList<>();
        filterdList=new ArrayList<>();

        mRcvAdapter = new CustomListUser(list,AddUserFriendActivity.this);
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
        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ok ");
                Chat chat = new Chat();
                if (edt.getText().toString() != "") {
                    chat.setUsername(Enviroment.user.getName());
                    chat.setMesage(edt.getText().toString());
                    userFriendPresenter.emitChatUserFriend(chat);
                    edt.setText("");
                    System.out.println("aaaa");
                }
                hide();
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
                                User user = Enviroment.gson.fromJson(jsonObject1.toString(),User.class);
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

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    filterdList.clear();
                    textlength = edtsearch.getText().length();
                    for(int i=0;i< list.size();i++){
                            if(textlength<=list.get(i).getName().toLowerCase().length()){
                            if(list.get(i).getName().toLowerCase().trim().contains(edtsearch.getText().toString().toLowerCase().trim())){
                                filterdList.add(list.get(i));
                            }
                        }
                    }
                    mRcvAdapter=new CustomListUser(filterdList,AddUserFriendActivity.this);
                    recyclerView.setAdapter(mRcvAdapter);
                    mRcvAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
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
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

    @Override
    public void updateListUser(ArrayList<User> list) {
        for (User us : list){
            this.list.add(us);
        }
        mRcvAdapter.notifyDataSetChanged();
    }

    @Override
    public void addChatMessage(Chat chat) {
        listChat.add(chat);
        adapterChat.notifyDataSetChanged();
    }
}

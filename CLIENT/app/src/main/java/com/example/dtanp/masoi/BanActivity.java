package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.UserRoom;
import com.example.dtanp.masoi.model.User;
import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BanActivity extends Activity {

    ListView listChat;
    CustomAdapterChat adapterChat;
    Button btnSS, btnSend, btnGiet, btnKhongGiet;
    ImageView imgNhanVat, imgTreoCo;
    LinearLayout linearLayoutListUser, linearLayoutTreoCo, linearLayoutChat;
    List<UserRoom> userRoomList, userRoomListSong, userRoomListDanThuong;
    List<Chat> list;
    FirebaseDatabase database;
    EditText edtChat;
    DatabaseReference reference;
    TextView user1, user2, user3, user4, user5, user6, txtTenUser, txtSoPhong, txtTenPhong, txtThoiGian, txtTreoCo, txtluot;
    NhanVat nhanvat;
    private ImageButton btnUser1, btnUser2, btnUser3, btnUser4, btnUser5, btnUser6, btnBack;
    int dem;
    Timer timer;
    Handler handler;
    HashMap<String, String> hashMap;
    List<NhanVat> listnhanvat;
    List<User> listUser;
    boolean die = false, nap = false, ready = false, flagStart = false;
    AlertDialog dialog;
    String iDBiBoPhieu;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        AnhXa();
        AddConTrols();
        capnhatlistChat();
        //capnhatlistUser();
        laylistUser();
        LangNgheUserExit();
        LangNgheKickUser();

        //LangNgheAllManHinh();
        LangNgheOK();
        getNhanVat();
        getListNhanVat();
        NapLangNghe();
        LangNgheTime();
        LangNgheUpUserHost();
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

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

    private void LangNgheKickUser() {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        if (StaticUser.user.getId().trim().equals(id)) {
                            StaticUser.socket.emit("userexit", StaticUser.user.getId() + "");
                            StaticUser.phong.getUsers().clear();
                            StaticUser.phong = null;
                            StaticUser.user.setId_room("");
                            Intent intent = new Intent(BanActivity.this, RoomActivity.class);
                            startActivity(intent);
                            //finish();
                            finishAffinity();
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("leaveroom", listener);
    }


    private void LangNgheUserExit() {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        if (!id.trim().equals(StaticUser.user.getId().toString().trim())) {
                            for (User us : StaticUser.phong.getUsers()) {

                                if (us.getId().trim().equals(id)) {

                                    RemoveUserList(us);
                                    RemoveUser(us);
                                    StaticUser.phong.getUsers().remove(us);
                                    break;
                                }


                            }
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("userexit", listener);
    }

    public void RemoveUserList(User user) {
        for (User us : listUser) {
            if (us.getId().toString().trim().equals(us.getId())) {
                listUser.remove(us);
                break;
            }
        }
    }


    public void addDialoguser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_user, null);
        builder.setView(view);
        TextView txtuser = view.findViewById(R.id.txtuser);
        TextView txtid = view.findViewById(R.id.txtid);
        TextView txtlevel = view.findViewById(R.id.txtlevel);
        TextView txtwin = view.findViewById(R.id.txtwwin);
        TextView txtloss = view.findViewById(R.id.txtloss);

        txtid.setText(user.getId());
        txtuser.setText(user.getName());

        Button btnkick = view.findViewById(R.id.btnkick);
        btnkick.setVisibility(View.INVISIBLE);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thoát");
        builder.setMessage("Bạn muốn thoát ra khỏi phòng !");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //reference.child("Room").child(StaticUser.userHost.getId()).child("listUser").child(StaticUser.user.getId()).removeValue();
                StaticUser.socket.emit("userexit", StaticUser.user.getId());
                StaticUser.phong.getUsers().clear();
                StaticUser.phong = null;
                StaticUser.user.setId_room("");
                Intent intent = new Intent(BanActivity.this, RoomActivity.class);
                startActivity(intent);
                //finish();
                finishAffinity();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builder.create();

    }

    public  void LangNgheUpUserHost()
    {
        Emitter.Listener listener =new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(BanActivity.this,HostActivity.class);
                        startActivity(intent);
                        //finish();
                        finishAffinity();
                    }
                });
            }
        };
        StaticUser.socket.on("useruphost",listener);

    }
    public void AddConTrols() {

        edtChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();
            }
        });
        btnGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reference.child("Room").child(StaticUser.userHost.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(1);
                StaticUser.socket.emit("BangBoPhieu", 1);
                btnGiet.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnKhongGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reference.child("Room").child(StaticUser.userHost.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(2);
                StaticUser.socket.emit("BangBoPhieu", 2);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ready == false) {
                    StaticUser.socket.emit("ready", 1);
                    ready = true;
                    btnSS.setText("BỎ SẲN SÀNG");
                } else {
                    StaticUser.socket.emit("ready", 0);
                    ready = false;
                    btnSS.setText("SẲN SÀNG");
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat chat = new Chat();
                if (edtChat.getText().toString() != "") {
                    chat.setUsername(StaticUser.user.getName());
                    chat.setMesage(edtChat.getText().toString());
                    send(chat);
                    edtChat.setText("");
                }
                hide();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public  void  LangNgheTime()
    {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtThoiGian.setText((Integer) args[0]+"");
                    }
                });
            }
        };
        StaticUser.socket.on("time",listener);
    }
    public void LangNgheOK() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("OK").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean flag = dataSnapshot.getValue(Boolean.class);
//                if (flag==false && flagStart==true)
//                {
//                    OntouchUser(userRoomList);
//                    linearLayoutChat.setVisibility(View.INVISIBLE);
//                    imgNhanVat.setVisibility(View.INVISIBLE);
//                    txtThoiGian.setVisibility(View.INVISIBLE);
//                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
//                    linearLayoutListUser.setVisibility(View.VISIBLE);
//                    die=false;
//                    userRoomListSong.clear();
//                    userRoomListDanThuong.clear();
//                    listnhanvat.clear();
//                    list.clear();
//                    ResetAnhUser();
//                }
//                else if (flag==true)
//                {
//                    OffTouchUser(userRoomList);
//                    getListSong();
//                    getNhanVat();
//                    getListNhanVat();
//                    NapLangNghe();
//                    flagStart=true;
//
//                }
//                System.out.println("Lang Nghe OK");
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listenerOK = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        if (flag == false && flagStart == true) {
                            OntouchUser(userRoomList);
                            linearLayoutChat.setVisibility(View.INVISIBLE);
                            imgNhanVat.setVisibility(View.INVISIBLE);
                            txtThoiGian.setVisibility(View.INVISIBLE);
                            linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                            linearLayoutListUser.setVisibility(View.VISIBLE);
                            die = false;
                            userRoomListSong.clear();
                            userRoomListDanThuong.clear();
                            listnhanvat.clear();
                            list.clear();
                            ResetAnhUser();
                        } else if (flag == true) {
                            OffTouchUser(userRoomList);
                            getListSong();
                            flagStart = true;
                            btnSS.setVisibility(View.INVISIBLE);

                        }
                    }
                });

            }
        };
        StaticUser.socket.on("OK", listenerOK);
    }

    public void ResetAnhUser() {
        for (UserRoom text : userRoomList) {
            text.getUser().setImageResource(R.drawable.image_user);
        }
    }

    public void getListSong() {
        for (UserRoom text : userRoomList) {
            userRoomListSong.add(text);
        }
    }

    public void NapLangNghe() {
        if (nap == false) {
            LangNgheAllManHinh();
            LangNgheChat();
            setThoiGian();
            LangNgheLuotDB();
            LangNgheIdChon();
            LangNgheDie();
            LangNgheLuot();
            nap = true;
        }
    }

    public void LamMoUser() {
        for (UserRoom userRoom : userRoomList) {
            userRoom.getUser().setAlpha(0.4f);
            userRoom.getUser().setEnabled(false);
        }
    }

    public void RemoveUser(User user) {
        for (UserRoom text : userRoomList) {
            if (text.getUseradd() != null) {
                if (text.getUseradd().getId().toString().trim().equals(user.getId().toString())) {
                    text.getTxtuser().setText("");
                    text.setFlag(false);
                    text.setUseradd(null);
                    text.getUser().setEnabled(false);
                    text.getUser().setAlpha(0.4f);
                    break;
                }
            }

        }
        hashMap.remove(user.getName().toString());
    }

    public void LangNgheChat() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("AllChat").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean flag = dataSnapshot.getValue(Boolean.class);
//                if (flag == true) {
//                    linearLayoutChat.setVisibility(View.VISIBLE);
//                    listChat.setVisibility(View.VISIBLE);
//                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
//                    txtThoiGian.setVisibility(View.VISIBLE);
//                    DemGiay(30);
//                } else {
//                    linearLayoutChat.setVisibility(View.INVISIBLE);
//                    txtThoiGian.setVisibility(View.INVISIBLE);
//                    listChat.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listenerChat = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        if (flag == true) {
                            linearLayoutChat.setVisibility(View.VISIBLE);
                            listChat.setVisibility(View.VISIBLE);
                            findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                            txtThoiGian.setVisibility(View.VISIBLE);
                            //DemGiay(30);
                        } else {
                            linearLayoutChat.setVisibility(View.INVISIBLE);
                            txtThoiGian.setVisibility(View.INVISIBLE);
                            listChat.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("AllChat", listenerChat);
    }

    public void DemGiay(int giay) {
        dem = giay;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dem--;
                handler.sendEmptyMessage(0);

            }
        }, 0, 1000);


    }

    public void setThoiGian() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                txtThoiGian.setText(dem + "");
                if (dem < 1) {
                    //handlerMaSoi.sendEmptyMessage(0);
                    timer.cancel();
                    //manv=8;
                }
            }
        };
    }

    public void setImageNhanVat(int ma) {
        switch (ma) {
            case 1:
                imgNhanVat.setImageResource(R.drawable.imgmasoi);
                break;
            case 2:
                imgNhanVat.setImageResource(R.drawable.imgdanlang);
                break;
            case 3:
                imgNhanVat.setImageResource(R.drawable.imgthosan);
                break;
            case 4:
                imgNhanVat.setImageResource(R.drawable.imgbaove);
                break;
            case 5:
                imgNhanVat.setImageResource(R.drawable.imggialang);
                break;
            case 6:
                imgNhanVat.setImageResource(R.drawable.imgtientri);
                break;
            default:
                break;
        }
        imgNhanVat.setVisibility(View.VISIBLE);
    }

    public void LangNgheLuot() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("listUserSang").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean flag = dataSnapshot.getValue(Boolean.class);
//                if (flag == true) {
//                    txtThoiGian.setVisibility(View.VISIBLE);
//                    DemGiay(30);
//                    AddClickUser("BangChonChucNang");
//                    if (nhanvat.getManv() == 1) {
//                        OntouchUser(userRoomListDanThuong);
//                        System.out.println("so nguoi" + userRoomListDanThuong.size());
//                    } else {
//                        OntouchUser(userRoomListSong);
//                    }
//                } else {
//                    if (nhanvat.getManv() == 1) {
//                        OffTouchUser(userRoomListDanThuong);
//                        txtThoiGian.setVisibility(View.INVISIBLE);
//                    } else {
//                        txtThoiGian.setVisibility(View.INVISIBLE);
//                        OffTouchUser(userRoomListSong);
//                    }
//                }
//                System.out.println("Lang Nghe Luot");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nhanvat.getManv() == (int) args[0]) {
                            txtThoiGian.setVisibility(View.VISIBLE);
                            //DemGiay(30);
                            AddClickUser("BangChonChucNang");
                            if (nhanvat.getManv() == 1) {
                                OntouchUser(userRoomListDanThuong);
                                //System.out.println("NhanVat ne");
                            } else {
                                OntouchUser(userRoomListSong);
                            }
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("NhanVatsang", listener);
        Emitter.Listener listenerTat = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nhanvat.getManv() == (int) args[0]) {
                            if (nhanvat.getManv() == 1) {
                                OffTouchUser(userRoomListDanThuong);
                                txtThoiGian.setVisibility(View.INVISIBLE);
                            } else {
                                OffTouchUser(userRoomListSong);
                                txtThoiGian.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("NhanVatTat", listenerTat);
    }

    public void setDieUser(UserRoom text) {

        text.getUser().setEnabled(false);
        text.getUser().setImageResource(R.drawable.die);
        text.getUser().setAlpha(0.3f);
        userRoomList.remove(text);
    }


    public void LangNgheDie() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("BangDie").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String id = dataSnapshot.getValue(String.class);
//                if (!id.equals("A")) {
//                    if (id.equals(StaticUser.user.getId())) {
//                        die = true;
//                    } else {
//                        XoaNhanVat(id);
//                        for (UserRoom text : userRoomList) {
//                            if (text.getUseradd().getId().toString().equals(id)) {
//                                setDieUser(text);
//                                break;
//                            }
//                        }
//                    }
//                }
//                System.out.println("Lang Nghe Die");
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String id = dataSnapshot.getValue(String.class);
//                if (!id.equals("A")) {
//                    if (id.equals(StaticUser.user.getId())) {
//                        die = true;
//                    } else {
//                        XoaNhanVat(id);
//                        for (UserRoom text : userRoomList) {
//                            if (text.getUseradd().getId().toString().equals(id)) {
//                                setDieUser(text);
//                                break;
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String id = (String) args[0];
                        if (!id.equals("A")) {
                            if (id.equals(StaticUser.user.getId())) {
                                die = true;
                            } else {
                                for (UserRoom text : userRoomList) {
                                    if (text.getUseradd() != null) {
                                        if (text.getUseradd().getId().toString().equals(id)) {
                                            setDieUser(text);
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("UserDie", listener);
    }

    public void OffTouchUser(List<UserRoom> list) {
        for (UserRoom text : list) {
            text.getUser().setEnabled(false);
            text.getUser().setAlpha(0.8f);
        }
    }

    public void OntouchUser(List<UserRoom> list) {
        for (UserRoom text : list) {
            text.getUser().setEnabled(true);
            text.getUser().setAlpha(1f);
        }
    }

    public void getNhanVat() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("BangNhanVat").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                nhanvat = dataSnapshot.getValue(NhanVat.class);
//                System.out.println(nhanvat.getId());
//
//                setImageNhanVat(nhanvat.getManv());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    NhanVat nv = StaticUser.gson.fromJson(jsonObject.toString(), NhanVat.class);
                                    if (nv.getId().toString().trim().equals(StaticUser.user.getId().trim())) {
                                        nhanvat = nv;
                                        System.out.println(nhanvat.getId());
                                        setImageNhanVat(nhanvat.getManv());
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
        StaticUser.socket.on("ListNhanVat", listener);

    }


    public void capnhatlistUser() {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.userHost.getId()).child("listUser").child(StaticUser.user.getId()).setValue(StaticUser.user);
    }

    public void capnhatlistChat() {
//        reference = database.getReference();
//        reference.child("Chat").child(StaticUser.userHost.getId()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                if (!chat.getMesage().equals(" ")) {
//                    list.add(chat);
//                    adapterChat.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listenerChatMes = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = (String) args[0];
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(json);
                            Chat chat = StaticUser.gson.fromJson(jsonObject.toString(), Chat.class);
                            if (!chat.getMesage().equals(" ")) {
                                list.add(chat);
                                adapterChat.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("Chat", listenerChatMes);
    }

    public void send(Chat chat) {
//        reference = database.getReference();
//        reference.child("Chat").child(StaticUser.userHost.getId()).push().setValue(chat);
        String json = StaticUser.gson.toJson(chat);
        StaticUser.socket.emit("Chat", json);

    }

    public void AnhXa() {

        database = StaticFirebase.database;
        reference = database.getReference();
        userRoomList = new ArrayList<>();

        btnSS = findViewById(R.id.btnSS);
        //btnSS.setVisibility(View.INVISIBLE);
        imgNhanVat = findViewById(R.id.imgNhanVat);
        // imgNhanVat.setImageAlpha(1);
        txtThoiGian = findViewById(R.id.txtThoiGian);
        txtThoiGian.setVisibility(View.VISIBLE);
        txtSoPhong = findViewById(R.id.txtSoPhong);
        txtTenPhong = findViewById(R.id.txtTenPhong);
        txtTenPhong.setText(StaticUser.phong.getName());
        txtSoPhong.setText(StaticUser.phong.getRoomnumber() + "");
        listChat = findViewById(R.id.listChat);
        listChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list = new ArrayList<>();
        adapterChat = new CustomAdapterChat(this, R.layout.custom_chat, list);
        listChat.setAdapter(adapterChat);
        adapterChat.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listChat.setSelection(adapterChat.getCount() - 1);
            }
        });
        btnSend = findViewById(R.id.btnSend);
        edtChat = findViewById(R.id.edtChat);
        linearLayoutChat = findViewById(R.id.lnrchat);
        linearLayoutChat.setVisibility(View.INVISIBLE);
        hashMap = new HashMap<>();
        listnhanvat = new ArrayList<>();
        linearLayoutListUser = findViewById(R.id.lnrlistUser);
        linearLayoutTreoCo = findViewById(R.id.lnrtreoco);
        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
        linearLayoutListUser.setVisibility(View.VISIBLE);
        imgNhanVat.setVisibility(View.INVISIBLE);
        btnGiet = findViewById(R.id.btngiet);
        btnKhongGiet = findViewById(R.id.btnkhonggiet);
        txtTreoCo = findViewById(R.id.txttreoco);
        imgTreoCo = findViewById(R.id.imgtreoco);
        listUser = new ArrayList<>();
        txtluot = findViewById(R.id.txtLuot);
        addDialog();
        btnBack = findViewById(R.id.btnBack);

        user1 = findViewById(R.id.txtuser1);
        user2 = findViewById(R.id.txtuser2);
        user3 = findViewById(R.id.txtuser3);
        user4 = findViewById(R.id.txtuser4);
        user5 = findViewById(R.id.txtuser5);
        user6 = findViewById(R.id.txtuser6);


        btnUser1 = findViewById(R.id.btnUser1);
        btnUser2 = findViewById(R.id.btnUser2);
        btnUser3 = findViewById(R.id.btnUser3);
        btnUser4 = findViewById(R.id.btnUser4);
        btnUser5 = findViewById(R.id.btnUser5);
        btnUser6 = findViewById(R.id.btnUser6);


        userRoomList.add(new UserRoom(btnUser1, user1, false));
        userRoomList.add(new UserRoom(btnUser2, user2, false));
        userRoomList.add(new UserRoom(btnUser3, user3, false));
        userRoomList.add(new UserRoom(btnUser4, user4, false));
        userRoomList.add(new UserRoom(btnUser5, user5, false));
        userRoomList.add(new UserRoom(btnUser6, user6, false));


        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(StaticUser.user.getName());

        userRoomListDanThuong = new ArrayList<>();
        userRoomListSong = new ArrayList<>();
        LamMoUser();


    }

    public void ghitextview(int id, User user) {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.userHost.getId()).child("TextView").child(user.getId()).setValue(id);
    }

    public void adduser(final User user) {
        //int id = 1;
        for (UserRoom text : userRoomList) {
            if (text.isFlag() == false) {
                text.setFlag(true);
                text.getTxtuser().setText(user.getName().toString());
                text.setUseradd(user);
                text.getUser().setAlpha(1f);
                text.getUser().setEnabled(true);
                text.getUser().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialoguser(user);
                    }
                });
                break;

            }
        }


        hashMap.put(user.getName().toString(), user.getId().toString());
    }

    public void laylistUser() {
        for (User us : StaticUser.phong.getUsers()) {
            if (us.getId() != StaticUser.user.getId()) {
                System.out.println(us.getName());
                adduser(us);
                listUser.add(us);

            }
        }
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        User user = StaticUser.gson.fromJson(jsonObject.toString(), User.class);
                        System.out.println(user.getName());
                        if(user.getId().equals(StaticUser.user.getId())==false)
                        {
                            adduser(user);
                            listUser.add(user);
                            StaticUser.phong.getUsers().add(user);
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("newuser", listener);
//        reference = database.getReference();
//        reference.child("Room").child(StaticUser.userHost.getId()).child("listUser").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                User us = dataSnapshot.getValue(User.class);
//                if (us.getId() != StaticUser.user.getId()) {
//                    System.out.println(us.getName());
//                    adduser(us);
//                    listUser.add(us);
//
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                User us = dataSnapshot.getValue(User.class);
//                if(!us.getId().toString().trim().equals(StaticUser.user.getId().toString().trim()))
//                {
//                    RemoveUser(us);
//                }
//                else
//                {
//                    Intent intent = new Intent(BanActivity.this,RoomActivity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    public void HienThiLuot(int luot) {
        String to = "";
        switch (luot) {
            case 1:
                to = "Ma Soi Dang Chon";
                break;
            case 2:
                to = "Bao Ve Dang chon";
                break;
            case 3:
                to = "Tho San Dang Chon";
                break;
            case 4:
                to = "Tien Tri Dang Chon";
                break;
            case 5:
                to = "Dan Lang Bieu Quyet";
                break;
            case 6:
                to = "Nguoi Treo co giai trinh";
                break;
            case 7:
                to = "Bo Phieu Giet";
                break;
            default:
                break;
        }
        txtluot.setText(to.toString().trim());
    }

    public void LangNgheLuotDB() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("Luot").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int l = dataSnapshot.getValue(Integer.class);
////                if (l != 0) {
////                    if (l == 1) {
////                        linearLayoutListUser.setVisibility(View.VISIBLE);
////                        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
////                        linearLayoutChat.setVisibility(View.INVISIBLE);
////                    }
////                    if (l == 7) {
////                        if (die == false) {
////                            if (StaticUser.user.getId().toString().trim().equals(iDBiBoPhieu) == false) {
////                                btnKhongGiet.setVisibility(View.VISIBLE);
////                                btnGiet.setVisibility(View.VISIBLE);
////                            }
////                        }
////                    }
////                    HienThiLuot(l);
////                } else {
////                    txtluot.setText("");
////                }
//                System.out.println("Lang Nghe Luot DB");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int l = (int) args[0];
                        if (l != 0) {
                            if (l == 1) {
                                linearLayoutListUser.setVisibility(View.VISIBLE);
                                linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                                linearLayoutChat.setVisibility(View.INVISIBLE);
                            }
                            if (l == 7) {
                                if (die == false) {
                                    if (StaticUser.user.getId().toString().trim().equals(iDBiBoPhieu) == false) {
                                        btnKhongGiet.setVisibility(View.VISIBLE);
                                        btnGiet.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            HienThiLuot(l);
                        } else {
                            txtluot.setText("");
                        }
                    }
                });
            }
        };
        StaticUser.socket.on("Luot", listener);
    }

    public void AddClickUser(final String st) {
        for (final UserRoom text : userRoomList) {
            text.getUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //timer.cancel();
                    if (nhanvat.getManv() == 6 && st.equals("BangChonChucNang") == true) {
                        for (NhanVat nv : listnhanvat) {
                            if (text.getUseradd() != null) {
                                if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                    if (nv.getManv() == 1) {
                                        Toast.makeText(BanActivity.this, "day la ma soi", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(BanActivity.this, "day khong phai la soi la ma soi", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }

                        }
                    }
                    //reference.child("Room").child(StaticUser.userHost.getId()).child(st).child(StaticUser.user.getId()).setValue(hashMap.get(text.getTxtuser().getText().toString()));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("manv", nhanvat.getManv() + "");
                    jsonObject.addProperty("idchon", hashMap.get(text.getTxtuser().getText().toString()) + "");
                    String json = StaticUser.gson.toJson(jsonObject);
                    StaticUser.socket.emit(st + "", json + "");
                    OffTouchUser(userRoomListSong);

                }
            });
        }
    }

    public void getListNhanVat() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("BangNhanVat").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                NhanVat nv = dataSnapshot.getValue(NhanVat.class);
//                if (!nv.getId().toString().equals(nhanvat.getId().toString())) {
//                    listnhanvat.add(nv);
//                    if (nv.getManv() != 1) {
//                        for (UserRoom text : userRoomList) {
//                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
//                                userRoomListDanThuong.add(text);
//                                System.out.println(nv);
//                                System.out.println(text.getUseradd().getName());
//                                System.out.println(text.getTxtuser().getText().toString());
//                                break;
//                            }
//
//                        }
//                    } else {
//                        System.out.println("NhanVat la soi " + nv);
//                    }
//
//                } else {
//                    System.out.println("La Toi " + nv);
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
////                NhanVat nv = dataSnapshot.getValue(NhanVat.class);
////                if (!nv.getId().toString().equals(nhanvat.getId().toString())) {
////                    listnhanvat.add(nv);
////                    if (nv.getManv() != 1) {
////                        for (UserRoom text : userRoomList) {
////                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
////                                userRoomListDanThuong.add(text);
////                                break;
////                            }
////
////                        }
////                    }
////
////                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String st = (String) args[0];
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(st);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    NhanVat nv = (NhanVat) StaticUser.gson.fromJson(jsonObject.toString(), NhanVat.class);
                                    if (!nv.getId().toString().equals(nhanvat.getId().toString())) {
                                        listnhanvat.add(nv);
                                        if (nv.getManv() != 1) {
                                            for (UserRoom text : userRoomList) {
                                                if (text.getUseradd() != null) {
                                                    if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                                        userRoomListDanThuong.add(text);
                                                        System.out.println(userRoomListDanThuong.size()+"size list dan thuong ban");
                                                        break;
                                                    }
                                                }


                                            }
                                        } else {
                                            System.out.println("NhanVat la soi " + nv);
                                        }

                                    } else {
                                        System.out.println("La Toi " + nv);
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
        StaticUser.socket.on("ListNhanVat", listener);

    }

    public void LangNgheAllManHinh() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("AllManHinhChon").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean flag = dataSnapshot.getValue(Boolean.class);
//                if (flag == true) {
//                    if (die == false) {
//                        OntouchUser(userRoomListSong);
//                        AddClickUser("BangIdChon");
//                    }
//                } else {
//                    OffTouchUser(userRoomListSong);
//                }
//                System.out.println("Lang Nghe ALL Man Hinh");
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listenerAll = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        if (flag == true) {
                            if (die == false) {
                                OntouchUser(userRoomListSong);
                                AddClickUser("BangIdChon");
                            }

                        } else {
                            OffTouchUser(userRoomListSong);
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("AllManHinhChon", listenerAll);
    }

    public void LangNgheIdChon() {
//        reference.child("Room").child(StaticUser.userHost.getId()).child("IDBiBoPhieu").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String st = dataSnapshot.getValue(String.class);
//                iDBiBoPhieu = st;
//                if (!st.equals("A")) {
//                    if (st.equals(StaticUser.user.getId().toString())) {
//                        linearLayoutChat.setVisibility(View.VISIBLE);
//                        listChat.setVisibility(View.VISIBLE);
//                        findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
//                        linearLayoutListUser.setVisibility(View.INVISIBLE);
//                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
//                        txtTreoCo.setText(StaticUser.user.getName());
//                    } else {
//                        btnGiet.setVisibility(View.INVISIBLE);
//                        btnKhongGiet.setVisibility(View.INVISIBLE);
//                        findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
//                        linearLayoutListUser.setVisibility(View.INVISIBLE);
//                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
//                        linearLayoutChat.setVisibility(View.VISIBLE);
//                        listChat.setVisibility(View.VISIBLE);
//                        System.out.println("Toi IDBIBOPHIEU");
//
//
//                        for (User user : listUser) {
//                            if (user.getId().equals(st)) {
//                                txtTreoCo.setText(user.getName());
//                                break;
//                            }
//                        }
//                    }
//                }
//                System.out.println("Lang Nghe ID Bo Phieu");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        Emitter.Listener listenerIdChon = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                BanActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String st = (String) args[0];
                        iDBiBoPhieu = st;
                        if (!st.equals("A")) {
                            if (st.equals(StaticUser.user.getId().toString())) {
                                linearLayoutChat.setVisibility(View.VISIBLE);
                                listChat.setVisibility(View.VISIBLE);
                                findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                                linearLayoutListUser.setVisibility(View.INVISIBLE);
                                btnGiet.setVisibility(View.INVISIBLE);
                                btnKhongGiet.setVisibility(View.INVISIBLE);
                                linearLayoutTreoCo.setVisibility(View.VISIBLE);
                                txtTreoCo.setText(StaticUser.user.getName());
                            } else {
                                btnGiet.setVisibility(View.INVISIBLE);
                                btnKhongGiet.setVisibility(View.INVISIBLE);
                                findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                                linearLayoutListUser.setVisibility(View.INVISIBLE);
                                linearLayoutTreoCo.setVisibility(View.VISIBLE);
                                linearLayoutChat.setVisibility(View.VISIBLE);
                                listChat.setVisibility(View.VISIBLE);
                                System.out.println("Toi IDBIBOPHIEU");


                                for (User user : listUser) {
                                    if (user.getId().equals(st)) {
                                        txtTreoCo.setText(user.getName());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });

            }
        };
        StaticUser.socket.on("IDBiBoPhieu", listenerIdChon);
    }

    public void XoaNhanVat(String id) {
        UserRoom userRoom = new UserRoom();
        for (UserRoom text : userRoomListSong) {
            if (text.getUseradd() != null) {
                if (text.getUseradd().getId().toString().equals(id)) {
                    userRoom = text;
                    userRoomListSong.remove(text);
                    break;
                }
            }

        }
        for (UserRoom text : userRoomListDanThuong) {
            if (text.getUseradd() != null) {
                if (text.getUseradd().getId().toString().equals(id)) {
                    userRoom = text;
                    userRoomListDanThuong.remove(text);
                    break;
                }

            }

        }
        if (!id.equals(StaticUser.user.getId())) {
            setDieUser(userRoom);
        } else {
            die = true;
        }

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

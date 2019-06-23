package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.adapter.CustomAdapterChat;

import com.example.dtanp.masoi.appinterface.RoomView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserRoom;
import com.example.dtanp.masoi.presenter.RoomPresenter;
import com.google.gson.JsonArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.fabric.sdk.android.Fabric;

public class HostActivity extends Activity implements RoomView {
    ListView listChat;
    CustomAdapterChat adapterChat;
    Button btnBatDau, btnSend, btnGiet, btnKhongGiet;
    ImageView imgNhanVat, imgTreoCo;
    List<UserRoom> userRoomList, userRoomListSong, userRoomListDanThuong;
    List<Chat> list;
    EditText edtChat;
    public List<User> listUser, listUserInGame;
    TextView user1, user2, user3, user4, user5, user6, txtTenUser, txtSoPhong, txtTenPhong, txtThoiGian, txtLuot, txtTreoCo;
    LinearLayout linearLayoutChat, linearLayoutListUser, linearLayoutTreoCo, linearLayoutKhungChat;
    private Timer timer;
    private Handler handler, handlerMaSoi;
    private ImageButton btnUser1, btnUser2, btnUser3, btnUser4, btnUser5, btnUser6,btnback;
    List<User> listUserMaSoi, listUserDanLang;
    User userThoSan, userBaoVe, userTienTri;
    List<NhanVat> listNhanVat;
    NhanVat nhanvat;
    int manv, countUserReady = 0;
    List<String> listIdMaSoichon, listAllChon;
    String idThoSanChon, idTienTriChon = "", idBaoVeChon, IDBoPhieu;
    HashMap<String, String> hashMap;
    boolean die = false;
    private boolean flagThoSan = false, flagTienTri = false, flagBaoVe = false,flagBiBoPhieu=false;
    AlertDialog dialog;
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
    private RoomPresenter roomPresenter;
    private boolean host, ready = false;
    private Button btnSS;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_host);
        host = getIntent().getBooleanExtra("host",false);
        roomPresenter = new RoomPresenter(HostActivity.this,HostActivity.this);
        AnhXa();
        taophong();
        ConTrols();
        LamMoUser();
        roomPresenter.listenChat();
        laylistUser();
        setThoiGian();
        roomPresenter.listenUserExit();
        roomPresenter.listenLuotDB();
        roomPresenter.listenGetNhanVat();
        roomPresenter.listenAllManHinh();
        roomPresenter.listenLuot();
        roomPresenter.listenAllChat();
        roomPresenter.listenUserDie();
        roomPresenter.listenOK();
        roomPresenter.listenTime();
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        roomPresenter.listenBangIDChon();
        if(host==true){
            getHost();
            btnBatDau.setVisibility(View.VISIBLE);
        }
        else
        {
            btnSS.setVisibility(View.VISIBLE);
        }

    }

    public void getHost(){
        XuLyChon();
        roomPresenter.listenUserReady();
        roomPresenter.listenSuKien();
        roomPresenter.listenKetQuaBoPhieu();
        roomPresenter.listenUserIdBiGiet();
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

    public void ConTrols() {
        edtChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hide();;
            }
        });
        btnSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ready == false) {
                    roomPresenter.emitReady(1);
                    ready = true;
                    btnSS.setText("BỎ SẲN SÀNG");
                } else {
                    roomPresenter.emitReady(0);
                    ready = false;
                    btnSS.setText("SẲN SÀNG");
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (die == false) {
                    Chat chat = new Chat();
                    if (edtChat.getText().toString() != "") {
                        chat.setUsername(Enviroment.user.getName());
                        chat.setMesage(edtChat.getText().toString());
                        send(chat);
                        edtChat.setText("");
                    }
                }
                hide();
            }
        });
        findViewById(R.id.btnBatDau).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OffTouchUser(userRoomList);
                RanDom();
                PushNhanVat();
                getListXuLy();
                getTextViewAddList();
                btnBatDau.setVisibility(View.INVISIBLE);
                roomPresenter.emitOk(true);

            }
        });
        btnGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomPresenter.emitBangBoPhieu(1);
                btnGiet.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnKhongGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomPresenter.emitBangBoPhieu(2);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
            }
        });
    }

    public void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thoát");
        builder.setMessage("Bạn muốn thoát ra khỏi phòng !");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                roomPresenter.emitUserHostExit(Enviroment.user.getUserId());
                Enviroment.phong.getUsers().clear();
                Enviroment.phong = null;
                Enviroment.user.setId_room("");
                Intent intent = new Intent(HostActivity.this, ChooseRoomActivity.class);
                startActivity(intent);
                finish();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void AnhXa() {
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
        userRoomList = new ArrayList<>();


        userRoomList.add(new UserRoom(btnUser1, user1, false));
        userRoomList.add(new UserRoom(btnUser2, user2, false));
        userRoomList.add(new UserRoom(btnUser3, user3, false));
        userRoomList.add(new UserRoom(btnUser4, user4, false));
        userRoomList.add(new UserRoom(btnUser5, user5, false));
        userRoomList.add(new UserRoom(btnUser6, user6, false));


        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(Enviroment.user.getName());

        txtSoPhong = findViewById(R.id.txtSoPhong);
        txtTenPhong = findViewById(R.id.txtTenPhong);

        btnBatDau = findViewById(R.id.btnBatDau);
        btnBatDau.setAlpha(0.6f);
        btnBatDau.setEnabled(false);
        btnSS = findViewById(R.id.btnSS);
        btnback = findViewById(R.id.btnBack);

        imgNhanVat = findViewById(R.id.imgNhanVat);
        imgNhanVat.setVisibility(View.INVISIBLE);
        txtThoiGian = findViewById(R.id.txtThoiGian);

        listUser = new ArrayList<>();

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

        linearLayoutKhungChat = findViewById(R.id.lnrkhungchat);

        listUserMaSoi = new ArrayList<User>();
        listUserDanLang = new ArrayList<>();
        listNhanVat = new ArrayList<>();
        txtLuot = findViewById(R.id.txtLuot);
        listIdMaSoichon = new ArrayList<>();
        listAllChon = new ArrayList<>();
        hashMap = new HashMap<>();

        userRoomListDanThuong = new ArrayList<>();
        userRoomListSong = new ArrayList<>();

        txtThoiGian.setVisibility(View.INVISIBLE);

        linearLayoutListUser = findViewById(R.id.lnrlistUser);
        linearLayoutTreoCo = findViewById(R.id.lnrtreoco);
        btnGiet = findViewById(R.id.btngiet);
        btnKhongGiet = findViewById(R.id.btnkhonggiet);
        txtTreoCo = findViewById(R.id.txttreoco);
        imgTreoCo = findViewById(R.id.imgtreoco);
        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
        listUserInGame = new ArrayList<>();
    }

    public void LamMoUser() {
        for (UserRoom userRoom : userRoomList) {
            userRoom.getUser().setAlpha(0.4f);
            userRoom.getUser().setEnabled(false);
        }
    }

    public void getlistUser(List<User> list) {
        for (User us : listUser) {
            list.add(us);
        }
    }


    public void addDialoguser(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_user, null);
        builder.setView(view);
        TextView txtuser = view.findViewById(R.id.txtuser);
        TextView txtid = view.findViewById(R.id.txtid);
        TextView txtlevel = view.findViewById(R.id.txtlevel);
        TextView txtwin = view.findViewById(R.id.txtwwin);
        TextView txtloss = view.findViewById(R.id.txtloss);

        txtid.setText(user.getUserId());
        txtuser.setText(user.getName());

        Button btnkick = view.findViewById(R.id.btnkick);
        if(host==false){
            btnkick.setVisibility(View.GONE);
        }
        btnkick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomPresenter.emitKickUser(user.getUserId().toString());
                dialog.cancel();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }


    public void AddUser(final User user) {
        for (UserRoom text : userRoomList) {
            if (text.isFlag() == false) {
                text.getTxtuser().setText(user.getName());
                text.setFlag(true);
                text.setUseradd(user);
                text.getUser().setEnabled(true);
                text.getUser().setAlpha(1f);
                text.getUser().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialoguser(user);
                    }
                });
                break;
            }
        }
        hashMap.put(user.getName().toString(), user.getUserId().toString());
    }

    public void RemoveUser(User user) {
        for (UserRoom text : userRoomList) {
            if (text.getUseradd() != null) {
                if (text.getUseradd().getUserId().toString().trim().equals(user.getUserId().toString())) {
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

    public void setDieUser(UserRoom text) {
        text.getUser().setEnabled(false);
        text.getUser().setImageResource(R.drawable.die);
        text.getUser().setAlpha(0.3f);
    }


    public void send(Chat chat) {
        roomPresenter.emitChatMessage(chat);

    }

    public void laylistUser() {
        for (User us : Enviroment.phong.getUsers()) {
            if (us.getUserId() != Enviroment.user.getUserId()) {
                System.out.println(us.getName());
                AddUser(us);
            }
            listUser.add(us);
        }

        roomPresenter.listenNewUserJoinRoom();
    }

    public void RemoveUserList(User user) {
        for (User us : listUser) {
            if (us.getUserId().toString().trim().equals(us.getUserId())) {
                listUser.remove(us);
                break;
            }
        }
    }

    public void taophong() {
        txtTenPhong.setText(Enviroment.phong.getName());
        txtSoPhong.setText(Enviroment.phong.getRoomnumber() + "");
    }

    int dem;
    boolean flagchat = false;

    public void DemGiay(int giay) {
        dem = giay;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dem--;
                handler.sendEmptyMessage(0);

            }
        }, 0, 1500);

    }

    public void setThoiGian() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Enviroment.socket.emit("time",dem);
                if (dem < 0) {
                    if (flagchat == true) {
                        manv = 7;
                        handlerMaSoi.sendEmptyMessage(0);
                        flagchat = false;
                    }
                    if (flagxuli == true) {
                        roomPresenter.emitUserBiBoPhieuTat(IDBoPhieu);
                        XuLyLuot(7, false);
                        if (die == false) {
                            if (Enviroment.user.getUserId().toString().trim().equals(IDBoPhieu) == false) {
                                btnKhongGiet.setVisibility(View.VISIBLE);
                                btnGiet.setVisibility(View.VISIBLE);
                            }
                        }
                        setLuotDB(7);
                        manv = 9;
                        flagxuli = false;
                    }
                    timer.cancel();
                    txtThoiGian.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    public void RanDom() {
        List<User> listUserRandom = new ArrayList<>();
        getlistUser(listUserRandom);
        getlistUser(listUserInGame);
        for (int i = 0; i < Enviroment.TOTAL_PEOPLE; i++) {
            NhanVat nv = new NhanVat();
            int k;
            if (listUserRandom.size() > 1) {
                k = (int) (Math.random() * listUserRandom.size());

            } else
                k = 0;
            if (i < 2) {
                listUserMaSoi.add(listUserRandom.get(k));
                nv.setManv(1);
                nv.setResource(R.drawable.imgmasoi);
            } else if (i < 4) {
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(2);
                nv.setResource(R.drawable.imgdanlang);
            } else if (i == 4) {
                userThoSan = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(3);
                nv.setResource(R.drawable.imgthosan);
            } else if (i == 5) {
                userBaoVe = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(4);
                nv.setResource(R.drawable.imgbaove);
            } else if (i == 6) {
                userTienTri = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(6);
                nv.setResource(R.drawable.imgtientri);


            }
            nv.setId(listUserRandom.get(k).getUserId());
            listUserRandom.remove(k);
            listNhanVat.add(nv);

        }
    }

    public void getListXuLy() {
        for (NhanVat nv : listNhanVat) {
            if (nv.getManv() != 1) {
                for (UserRoom text : userRoomList) {
                    if (text.getUseradd() != null) {
                        if (text.getUseradd().getUserId().toString().equals(nv.getId().toString())) {
                            userRoomListDanThuong.add(text);
                            break;
                        }
                    }

                }
            }

        }
    }

    public void getTextViewAddList() {
        for (UserRoom text : userRoomList) {
            userRoomListSong.add(text);
        }
    }

    public void PushNhanVat() {
        JsonArray jsonArray = new JsonArray();
        for (NhanVat nv : listNhanVat) {
          JSONObject jsonObject = new JSONObject();

        }
        roomPresenter.emitSendListNhanVat(listNhanVat);


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

            case 6:
                imgNhanVat.setImageResource(R.drawable.imgtientri);
                break;
            default:
                break;
        }
        imgNhanVat.setVisibility(View.VISIBLE);
    }

    public void pushLuot(int t) {
        roomPresenter.emitLuot(t);
    }


    public void removelistUserInGameID(String id) {
        for (User us : listUserInGame) {
            if (us.getUserId().toString().equals(id)) {
                listUserInGame.remove(us);
                break;
            }
        }
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
        txtLuot.setText(to.toString().trim());
    }

    public void setLuotDB(int luot) {
        roomPresenter.emitLuotDB(luot);
    }


    public void XuLyChon() {
        handlerMaSoi = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (manv == 1) {
                    if (listUserMaSoi.size() == listIdMaSoichon.size()) {
                        setLuotDB(2);
                        XuLyLuot(1, false);
                        XuLyLuot(4, true);
                    }
                } else if (manv == 4) {
                    setLuotDB(3);
                    XuLyLuot(4, false);
                    XuLyLuot(3, true);
                } else if (manv == 3) {
                    setLuotDB(4);
                    XuLyLuot(3, false);
                    XuLyLuot(6, true);
                } else if (manv == 6) {

                    XuLyLuot(6, false);
                    XuLyLuot(7, true);
                } else if (manv == 7) {
                    XuLyLuot(7, false);
                    XuLyLuot(8, true);
                    setLuotDB(5);
                } else if (manv == 8) {
                    XuLyLuot(8, false);
                    IDBoPhieu = getIDBOPhieu();
                    roomPresenter.emitIDBiBoPhieu(IDBoPhieu);
                    roomPresenter.emitUserBoPhieu(IDBoPhieu);
                    XuLiGiaiTrinh();
                } else if (manv == 9) {
                    if (giet == true) {
                        giet = false;
                        XoaNhanVat(IDBoPhieu);
                        XoaNhanVatChucNang(IDBoPhieu);
                        removelistUserInGameID(IDBoPhieu);
                        roomPresenter.emitUserDie(IDBoPhieu);
                    }

                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    XuLiCuoiNgay();

                }

            }
        };

    }

    public void XoaNhanVat(String id) {
        UserRoom userRoom = new UserRoom();
        for (UserRoom text : userRoomListSong) {
            if(text.getUseradd()!=null)
            {
                if (text.getUseradd().getUserId().toString().equals(id)) {
                    userRoom = text;
                    userRoomListSong.remove(text);
                    break;
                }
            }

        }
        for (UserRoom text : userRoomListDanThuong) {
            if(text.getUseradd()!=null)
            {
                if (text.getUseradd().getUserId().toString().equals(id)) {
                    userRoom = text;
                    userRoomListDanThuong.remove(text);
                    break;
                }
            }

        }
        for (User user : listUserMaSoi) {
            if (user.getUserId().toString().equals(id)) {
                listUserMaSoi.remove(user);
                break;
            }
        }
        for (User user : listUserDanLang) {
            if (user.getUserId().toString().equals(id)) {
                listUserDanLang.remove(user);
                break;
            }
        }
        if (!id.equals(Enviroment.user.getUserId())) {
            setDieUser(userRoom);
        } else {
            die = true;
        }

    }

    boolean flagxuli = false;

    public void XuLiGiaiTrinh() {

        //XuLyLuot(7,true);
        setLuotDB(6);
        flagchat = false;
        flagxuli = true;
        txtThoiGian.setVisibility(View.VISIBLE);
        DemGiay(30);

    }

    private int countYes = 0, countNo = 0;
    boolean giet = false;


    public void XuLyLuot(int luot, boolean flag) {


        if (luot == 4) {
            if (flagBaoVe == true) {
                luot = 6;
            }
        }
        if (luot == 3) {
            if (flagThoSan == true) {
                luot = 4;
            }
        }
        if (luot == 6) {
            if (flagTienTri == true) {
                luot = 7;
            }
        }

        if (luot == 1) {
            pushLuot(1);
            if (listUserMaSoi.size() > 0) {
                if (flag == true) {
                    roomPresenter.emitNhanvatSang(1);
                } else {
                    roomPresenter.emitNhanvatTat(1);
                }

            }

        } else if (luot == 3) {
            pushLuot(3);
            if (flag == true) {
                roomPresenter.emitNhanvatSang(3);
            } else {
                roomPresenter.emitNhanvatTat(3);
            }

        } else if (luot == 4) {
            pushLuot(2);
            if (flag == true) {
                roomPresenter.emitNhanvatSang(4);
            } else {
                roomPresenter.emitNhanvatTat(4);
            }

        } else if (luot == 6) {
            pushLuot(4);
            if (flag == true) {
                roomPresenter.emitNhanvatSang(6);
            } else {
                roomPresenter.emitNhanvatTat(6);
            }

        } else if (luot == 7) {
            roomPresenter.emitAllChat(flag);
        } else if (luot == 8) {
            roomPresenter.emitAllManHinhChon(flag);
        }
    }

    public void AddClickUser(final String st) {
        for (final UserRoom text : userRoomList) {
            text.getUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timer.cancel();
                    if (nhanvat.getManv() == 6) {
                        for (NhanVat nv : listNhanVat) {
                            if (text.getUseradd().getUserId().toString().equals(nv.getId().toString())) {
                                if (nv.getManv() == 1) {
                                    Toast.makeText(HostActivity.this, "day la ma soi", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(HostActivity.this, "day khong phai la soi la ma soi", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                    }
                    roomPresenter.emitChonUser(st,nhanvat.getManv(),hashMap.get(text.getTxtuser().getText().toString()) + "");
                    OffTouchUser(userRoomListSong);
                }
            });
        }
    }


    public String getIdSoiChon() {
        String st = "";
        if (listIdMaSoichon.size() == 3) {
            if (listIdMaSoichon.get(0).toString().equals(listIdMaSoichon.get(1).toString())) {
                st = listIdMaSoichon.get(0);
            } else if (listIdMaSoichon.get(1).toString().equals(listIdMaSoichon.get(2).toString())) {
                st = listIdMaSoichon.get(1);
            } else {
                st = listIdMaSoichon.get(0);
            }
        }
        st = listIdMaSoichon.get(0);
        return st;
    }

    public String getIDBOPhieu() {
        String id = "";
        int max = 1, count = 0;
        {
            for (int i = 0; i < listAllChon.size(); i++) {
                count = 0;
                for (int j = i + 1; j < listAllChon.size(); j++) {
                    if (listAllChon.get(i).toString().equals(listAllChon.get(j).toString())) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                    id = listAllChon.get(i);
                }
            }
        }
        return id;

    }


    public void XuLiCuoiNgay() {
        String idMaSoiChon = getIdSoiChon();
        if (idMaSoiChon.equals(idBaoVeChon)) {
            return;
        } else if (idMaSoiChon.equals(IDBoPhieu))
            return;
        else if (idMaSoiChon.equals(userThoSan.getUserId().toString())) {
            XoaNhanVat(idMaSoiChon);
            XoaNhanVat(idThoSanChon);
            XoaNhanVatChucNang(idMaSoiChon);
            XoaNhanVatChucNang(idThoSanChon);
            removelistUserInGameID(idMaSoiChon);
            removelistUserInGameID(idThoSanChon);
            if (!idMaSoiChon.equals(IDBoPhieu)) {
                roomPresenter.emitUserDie(idMaSoiChon);
                roomPresenter.emitUserDie(idThoSanChon);
            }
        } else {

            XoaNhanVat(idMaSoiChon);
            XoaNhanVatChucNang(idMaSoiChon);
            removelistUserInGameID(idMaSoiChon);
            roomPresenter.emitUserDie(idMaSoiChon);
        }
        if (listUserMaSoi.size() < 1) {
            resetLaiGameMoi();
        } else if (listUserMaSoi.size() >= listUserDanLang.size()) {
            resetLaiGameMoi();
        }
        ResetLaiNgayMoi();
    }

    public void resetLaiGameMoi() {
        roomPresenter.emitOk(false);
        listUserMaSoi.clear();
        listUserDanLang.clear();
        flagTienTri = false;
        flagBaoVe = false;
        flagThoSan = false;

        listIdMaSoichon.clear();
        listAllChon.clear();
        listNhanVat.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
        userRoomListDanThuong.clear();
        userRoomListSong.clear();
        list.clear();
        listUserInGame.clear();
        die = false;
        ResetAnhUser();

    }


    public void XoaNhanVatChucNang(String id) {


        if (flagTienTri == false && userTienTri.getUserId().equals(id)) {
            flagTienTri = true;
        }
        if (flagThoSan == false && userThoSan.getUserId().equals(id)) {
            flagThoSan = true;
        }
        if (flagBaoVe == false && userBaoVe.getUserId().equals(id)) {
            flagBaoVe = true;
        }
    }

    public void ResetLaiNgayMoi() {
        XuLyLuot(1, true);
        listIdMaSoichon.clear();
        listAllChon.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
    }

    public void ResetAnhUser() {
        for (UserRoom text : userRoomList) {
            text.getUser().setImageResource(R.drawable.image_user);
        }
    }
    //Chưa reset lai game moi

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

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void updateChatMessage(Chat chat) {
        list.add(chat);
        adapterChat.notifyDataSetChanged();
    }

    @Override
    public void updateUserExit(String userId) {
        if (!userId.trim().equals(Enviroment.user.getUserId().toString().trim())) {
            for (User us : Enviroment.phong.getUsers()) {
                if (us.getUserId().trim().equals(userId)) {
                    System.out.println(us.getUserId() + "id ne");
                    RemoveUserList(us);
                    RemoveUser(us);
                    break;
                }


            }
        } else {
            Intent intent = new Intent(HostActivity.this, ChooseRoomActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void updateUserReady(int number) {
        if (number  == 1) {
            //countUserReady=0;
            btnBatDau.setAlpha(1);
            btnBatDau.setEnabled(true);
        } else {
            //countUserReady=0;
            btnBatDau.setAlpha(0.3f);
            btnBatDau.setEnabled(false);
        }
    }

    @Override
    public void updateOK(boolean flag) {
        if (flag == false) {
            OntouchUser(userRoomList);
            linearLayoutChat.setVisibility(View.INVISIBLE);
            imgNhanVat.setVisibility(View.INVISIBLE);
            txtThoiGian.setVisibility(View.INVISIBLE);
            linearLayoutTreoCo.setVisibility(View.INVISIBLE);
            linearLayoutListUser.setVisibility(View.VISIBLE);
            resetLaiGameMoi();
            if(host==true){
                btnBatDau.setVisibility(View.VISIBLE);
            }
            else {
                btnSS.setVisibility(View.VISIBLE);
            }
        } else {
            XuLyLuot(1, true);
            DemGiay(20);
        }
    }

    @Override
    public void updateNewUserJoinRoom(User user) {
        if (user.getUserId().equals(Enviroment.user.getUserId()) == false) {
            AddUser(user);
            listUser.add(user);
            Enviroment.phong.getUsers().add(user);
            Enviroment.phong.setPeople(Enviroment.phong.getPeople() + 1);
        }
    }

    @Override
    public void updateTime(String time) {
        txtThoiGian.setText(time);
    }

    @Override
    public void updateUserDie(String userId) {
            if (userId.equals(Enviroment.user.getUserId())) {
                die = true;
            } else {
                for (UserRoom text : userRoomList) {
                    if(text.getUseradd()!=null)
                    {
                        if (text.getUseradd().getUserId().toString().equals(userId)) {
                            setDieUser(text);
                            break;
                        }
                    }

                }
            }
    }

    @Override
    public void updateNhanVat(NhanVat nhanVat) {
        nhanvat = nhanVat;
        System.out.println(nhanvat.getId());
        setImageNhanVat(nhanvat.getManv());
    }

    @Override
    public void updateLuotDB(int luot) {
        if (luot != 0) {
            if (luot == 1) {
                linearLayoutChat.setVisibility(View.INVISIBLE);
                listChat.setVisibility(View.INVISIBLE);
                linearLayoutKhungChat.setVisibility(View.INVISIBLE);

            }
            if (luot == 7) {
                if (die == false) {
                    if (Enviroment.user.getUserId().toString().trim().equals(IDBoPhieu) == false) {
                        System.out.println("toi luot 7");
                        btnGiet.setVisibility(View.VISIBLE);
                        btnKhongGiet.setVisibility(View.VISIBLE);
                    }
                }
            }
            HienThiLuot(luot);
        } else {
            txtLuot.setText("");
        }
    }

    @Override
    public void updateKetQuaBoPhieu(int kq) {
        if (kq != 0) {
            if (kq == 1) {
                countYes++;
            } else {
                countNo++;
            }
        }
        if (countNo + countYes == (listUserInGame.size() - 1)) {
            if (countYes > countNo) {
                giet = true;
                countNo = 0;
                countYes = 0;
            }
            manv = 9;
            handlerMaSoi.sendEmptyMessage(0);
        }

    }

    @Override
    public void updateIdTientriChon(String id) {
        manv = 6;
        idTienTriChon = id;
        handlerMaSoi.sendEmptyMessage(0);
    }

    @Override
    public void updateIdMaSoiChon(String id) {
        manv = 1;
        listIdMaSoichon.add(id);
        handlerMaSoi.sendEmptyMessage(0);
    }

    @Override
    public void updateIdThoSanChon(String id) {
        manv = 3;
        idThoSanChon = id;
        handlerMaSoi.sendEmptyMessage(0);
    }

    @Override
    public void updateIdBaoVeChon(String id) {
        manv = 4;
        idBaoVeChon = id;
        handlerMaSoi.sendEmptyMessage(0);
    }

    @Override
    public void updateIdBiGiet(String id) {
        listAllChon.add(id);
        if (listAllChon.size() == listUserInGame.size()) {
            manv = 8;
            handlerMaSoi.sendEmptyMessage(0);
        }
    }

    @Override
    public void updateNhanVatSang(int nv) {
        if (nhanvat.getManv() == nv) {
            txtThoiGian.setVisibility(View.VISIBLE);
            DemGiay(30);
            AddClickUser("BangChonChucNang");
            if (nhanvat.getManv() == 1) {
                OntouchUser(userRoomListDanThuong);
            } else {
                OntouchUser(userRoomListSong);
            }
        }
    }

    @Override
    public void updateNhanVatTat(int nv) {
        if (nhanvat.getManv() == nv) {
            if (nhanvat.getManv() == 1) {
                OffTouchUser(userRoomListDanThuong);
                txtThoiGian.setVisibility(View.INVISIBLE);
            } else {
                OffTouchUser(userRoomListSong);
                txtThoiGian.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void updateAllChat(boolean flag) {
        if (flag == true && die == false) {
            linearLayoutChat.setVisibility(View.VISIBLE);
            findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
            listChat.setVisibility(View.VISIBLE);
            txtThoiGian.setVisibility(View.VISIBLE);
            flagchat = true;
            DemGiay(30);
        } else {
            linearLayoutChat.setVisibility(View.INVISIBLE);
            findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
            listChat.setVisibility(View.INVISIBLE);
            txtThoiGian.setVisibility(View.INVISIBLE);
            flagchat = false;
        }
    }

    @Override
    public void updateAllManhinh(boolean flag) {
        if (flag == true) {
            if (die == false) {
                OntouchUser(userRoomListSong);
                AddClickUser("BangIdChon");
            }

        } else {
            OffTouchUser(userRoomListSong);
        }
    }

    @Override
    public void updateBangIdChon(String id) {
        if (id.equals(Enviroment.user.getUserId())) {
            linearLayoutListUser.setVisibility(View.INVISIBLE);
            linearLayoutChat.setVisibility(View.VISIBLE);
            findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
            linearLayoutTreoCo.setVisibility(View.VISIBLE);
            btnGiet.setVisibility(View.INVISIBLE);
            btnKhongGiet.setVisibility(View.INVISIBLE);
            listChat.setVisibility(View.VISIBLE);
            txtTreoCo.setText(Enviroment.user.getName());
            flagBiBoPhieu=true;
        } else {
            if (!id.equals("A")) {
                findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                linearLayoutListUser.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
                linearLayoutTreoCo.setVisibility(View.VISIBLE);
                linearLayoutChat.setVisibility(View.VISIBLE);
                listChat.setVisibility(View.VISIBLE);

                for (User user : listUser) {
                    if (user.getUserId().equals(id)) {
                        txtTreoCo.setText(user.getName());
                        break;
                    }
                }
            }
        }
    }
}

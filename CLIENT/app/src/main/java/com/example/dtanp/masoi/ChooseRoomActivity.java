package com.example.dtanp.masoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.dtanp.masoi.adapter.CustomAdapter;
import com.example.dtanp.masoi.appinterface.ChooseRoomView;
import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.presenter.ChooseRoomPresenter;
import com.example.dtanp.masoi.utils.CommonFunction;
import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static android.widget.Toast.LENGTH_SHORT;

public class ChooseRoomActivity extends Activity implements ChooseRoomView {

    private FirebaseDatabase database;
    ListView listroom;
    ArrayList<Phong> list, listSearch;
    Button btnnew, btnChoiNgay;
    ImageView imgback;
    DatabaseReference reference;
    CustomAdapter adapter, adapterSearch;
    EditText edtsearch;
    ImageButton imgsearch;
    TextView txtTenUser,txtGold;
    Emitter.Listener eListenerAllRoom;
    List<String> listString;
    private ChooseRoomPresenter chooseRoomPresenter;
    private boolean flagChoiNgay = false;
//    private static final boolean AUTO_HIDE = true;
//
//
//    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
//
//
//    private static final int UI_ANIMATION_DELAY = 300;
//    private final Handler mHideHandler = new Handler();
//    private View mContentView;
//    private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    };
//    private View mControlsView;
//    private final Runnable mShowPart2Runnable = new Runnable() {
//        @Override
//        public void run() {
//            ActionBar actionBar = getActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }
//        }
//    };
//    private boolean mVisible;
//    private final Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            hide();
//        }
//    };
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };

    private ArrayList<String> listCuoc;
    private AlertDialog dialogCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_room);
        chooseRoomPresenter = new ChooseRoomPresenter(ChooseRoomActivity.this,ChooseRoomActivity.this);
        //mVisible = true;
        // mContentView = findViewById(R.id.fullscreen_content);
        listSearch = new ArrayList<>();
        adapterSearch = new CustomAdapter(this, R.layout.custom_adapter, listSearch);
        AddConTrols();
        AddEvents();
        list = new ArrayList<>();
        adapter = new CustomAdapter(this, R.layout.custom_adapter, list);
        listString = new ArrayList<>();
        listroom.setAdapter(adapter);

        //laylistroom();
//        eListenerAllRoom = new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                ChooseRoomActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONArray jsonObject = (JSONArray) args[0];
//                        System.out.println(jsonObject.toString());
//                        for (int i = 0; i < jsonObject.length(); i++) {
//                            try {
//                                JSONObject jsonObject1 = jsonObject.getJSONObject(i);
//                                System.out.println(jsonObject1.toString());
//                                Phong phong = Enviroment.gson.fromJson(jsonObject1.toString(), Phong.class);
//                                list.add(phong);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//
//                    }
//                });
//            }
//        };
//        Enviroment.socket.on("allroom", eListenerAllRoom);
//
//        Enviroment.socket.emit("allroom");
        chooseRoomPresenter.listenAllRoom();
        chooseRoomPresenter.emitAllRoom();
        chooseRoomPresenter.listenJoinRoom();
        chooseRoomPresenter.listenNewRoom();
        //AddNewRoom();
        //LangNgheJoinRoom();
        //LangNgheXoaPhong();
        chooseRoomPresenter.listenRemoveRoom();
        addDialogCreateRoom();
    }

    public void addDialogCreateRoom(){
        listCuoc = new ArrayList<>();
        listCuoc.add("1000");
        listCuoc.add("2000");
        listCuoc.add("3000");
        listCuoc.add("5000");
        listCuoc.add("10000");
        listCuoc.add("20000");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,listCuoc);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_createroom,null);
        builder.setView(view);
        final Spinner spinner = view.findViewById(R.id.sltCuoc);
        spinner.setAdapter(arrayAdapter);
        Button btnCreate = view.findViewById(R.id.btnCreate);
        final EditText edtRoomName = view.findViewById(R.id.edtRoomName);
        final TextView txtErr = view.findViewById(R.id.txtErr);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Enviroment.user.getMoney()<Integer.parseInt(listCuoc.get((int) spinner.getSelectedItemId()))){
                    txtErr.setText("Bạn Không Đủ Tiền!");
                }
                else
                {
                    Phong phong = new Phong();
                    phong.setId(Enviroment.user.getUserId());
                    phong.setRoomnumber(list.size() + 1);
                    if(edtRoomName.getText().toString().length()>0){
                        phong.setName(edtRoomName.getText().toString());
                    }else{
                        phong.setName(Enviroment.user.getName());
                    }
                    phong.setPeople(1);
                    phong.getUsers().add(Enviroment.user);
                    phong.setHost(1);
                    phong.setMoney(Integer.parseInt(listCuoc.get((int) spinner.getSelectedItemId())));
                    Enviroment.phong = phong;
                    chooseRoomPresenter.emitCreateRoom(phong);
                    startmhhost(true);
                    finish();
                    dialogCreate.cancel();
                }

            }
        });

        dialogCreate = builder.create();
    }

//    public void LangNgheXoaPhong(){
//        Emitter.Listener listener = new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                ChooseRoomActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String id = (String) args[0];
//                        for (Phong p : list){
//                            if (p.get_id().equals(id)){
//                                list.remove(p);
//                                adapter.notifyDataSetChanged();
//                                adapter.notifyDataSetInvalidated();
//                                break;
//                            }
//                        }
//                    }
//                });
//            }
//        };
//
//    }

//    public  void  LangNgheJoinRoom(){
//        Emitter.Listener listener = new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                ChooseRoomActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean flag  = (boolean) args[0];
//                        if (flag==true){
//                            Toast.makeText(ChooseRoomActivity.this, "Phong Day!", LENGTH_SHORT).show();
//                        }else{
//                            startmhban();
//                            finish();
//                        }
//                    }
//                });
//            }
//        };
//        Enviroment.socket.on("FullPeople",listener);
//    }

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

//    private void AddNewRoom() {
//        Emitter.Listener listener = new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                ChooseRoomActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject jsonObject = (JSONObject) args[0];
//                        Phong phong = Enviroment.gson.fromJson(jsonObject.toString(), Phong.class);
//                        list.add(phong);
//                        adapter.notifyDataSetChanged();
//
//                    }
//                });
//            }
//        };
//        Enviroment.socket.on("newroom", listener);
//    }

    private void AddEvents() {
        edtsearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //hide();
            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtsearch.getText().toString().isEmpty()==false)
                {
                    boolean flag= false;
                    int sophong = Integer.parseInt(edtsearch.getText().toString());
                    for (Phong p : list)
                    {
                        if (p.getRoomnumber()==sophong)
                        {
                            listSearch.clear();
                            listSearch.add(p);
                            listroom.setAdapter(adapterSearch);
                            adapterSearch.notifyDataSetChanged();
                            flag=true;
                            break;
                        }
                    }
                    if (flag==false)
                    {
                        Toast.makeText(ChooseRoomActivity.this,"Không Tìm Thấy Phong Số " + sophong +" !",LENGTH_SHORT).show();
                    }
                }else{
                    listSearch.clear();
                    adapterSearch.notifyDataSetChanged();
                    listroom.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        btnChoiNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagChoiNgay = true;
                chooseRoomPresenter.emitAllRoom();
            }
        });
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCreate.show();
            }
        });

        listroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (list.get(i).getPeople() >= 7) {
                    Toast.makeText(ChooseRoomActivity.this, "Phòng Đầy!", LENGTH_SHORT).show();
                }else if (list.get(i).getMoney()>Enviroment.user.getMoney()){
                    Toast.makeText(ChooseRoomActivity.this, "Bạn Không Đủ Tiền!", LENGTH_SHORT).show();
                }else {

                   Enviroment.phong = (Phong) listroom.getAdapter().getItem(i);
                    Enviroment.user.setId_room(Enviroment.phong.get_id());
                    //
                    chooseRoomPresenter.emitJoinRoom(Enviroment.user);
//                    String jsonuser = Enviroment.gson.toJson(Enviroment.user);
//                    Enviroment.socket.emit("joinroom", jsonuser);
                }


            }
        });


        imgback.setClickable(true);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseRoomActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void AddConTrols() {
        txtGold = findViewById(R.id.txtGold);
        txtGold.setText(CommonFunction.formatGold(Enviroment.user.getMoney()));
        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(Enviroment.user.getName());
        listroom = findViewById(R.id.listroom);
        btnnew = findViewById(R.id.btnnew);
        imgback = findViewById(R.id.imgback);
        btnChoiNgay = findViewById(R.id.btnchoingay);
        edtsearch = findViewById(R.id.edtsearch);
        imgsearch = findViewById(R.id.imgsearch);
    }

    public Phong getPhongChoiNgay() {
        for (Phong phong : list) {
            if (phong.getPeople() < 10) {
                return phong;
            }
        }
        return null;
    }

    public void startmhhost(boolean flag) {
        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra("host",flag);
        startActivity(intent);
        finish();
        chooseRoomPresenter.removeListener();
    }

    @Override
    public void updateListView(ArrayList<Phong> list) {
        this.list.clear();
        System.out.println(this.list.size());
        System.out.println(list.size());
        for (Phong p : list)
        {
            this.list.add(p);
        }
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        if(flagChoiNgay==true){
            for (Phong p : this.list){
                if(p.getUsers().size()<7 && Enviroment.user.getMoney()>p.getMoney()){

                    Enviroment.phong = p;
                    System.out.println(Enviroment.phong.get_id() + "id");
                    Enviroment.user.setId_room(Enviroment.phong.get_id());
                    chooseRoomPresenter.emitJoinRoom(Enviroment.user);
                    flagChoiNgay=false;
                    break;
                }
            }
            if(flagChoiNgay==true){
                flagChoiNgay=false;
                Toast.makeText(ChooseRoomActivity.this, "Không tồn tại phòng phù hợp!",LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void checkRoomFullPeople(boolean flag, Phong phong) {
        if (flag==true){
            Toast.makeText(ChooseRoomActivity.this, "Phong Day!", LENGTH_SHORT).show();
        }else{
            startmhhost(false);
            Enviroment.phong = phong;
            finish();
        }
    }

    public void addDialogNoInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.conectServer);
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void addNewRoom(Phong phong) {
        this.list.add(phong);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void removeRoom(String roomId) {
        for (Phong p : this.list){
            if (p.get_id().equals(roomId)){
                this.list.remove(p);
                this.adapter.notifyDataSetChanged();
                this.adapter.notifyDataSetInvalidated();
                break;
            }
        }
    }

    // @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        delayedHide(100);
//    }
//
//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }
//
//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mVisible = false;
//
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    /**
//     * Schedules a call to hide() in delay milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }

}

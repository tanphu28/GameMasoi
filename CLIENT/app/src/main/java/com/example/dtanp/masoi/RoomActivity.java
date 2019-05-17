package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapter;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.User;
import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class RoomActivity extends Activity {

    private FirebaseDatabase database;
    ListView listroom;
    List<Phong> list,listSearch;
    Button btnnew,btnChoiNgay;
    ImageView imgback;
    DatabaseReference reference;
    CustomAdapter adapter,adapterSearch;
    EditText edtsearch;
    ImageButton imgsearch;
    TextView txtTenUser;
    Emitter.Listener eListenerAllRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        database = StaticFirebase.database;
        reference =database.getReference();
        listSearch = new ArrayList<>();
        adapterSearch = new CustomAdapter(this,R.layout.custom_adapter,listSearch);
        AddConTrols();
        AddEvents();
        list=new ArrayList<>();
        adapter  = new CustomAdapter(this,R.layout.custom_adapter,list);
        listroom.setAdapter(adapter);

        //laylistroom();
        StaticUser.socket.emit("allroom");
        eListenerAllRoom = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                RoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                public void run() {
                        JSONArray jsonObject = (JSONArray) args[0];
                        System.out.println(jsonObject.toString());
                        for (int i =0;i<jsonObject.length();i++)
                        {
                            try {
                                JSONObject jsonObject1 = jsonObject.getJSONObject(i);
                                System.out.println(jsonObject1.toString());
                                Phong phong = StaticUser.gson.fromJson(jsonObject1.toString(),Phong.class);
                                list.add(phong);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        adapter.notifyDataSetChanged();

                }
            });



            }
        };
        StaticUser.socket.on("allroom",eListenerAllRoom);
        AddNewRoom();




    }

    private void AddNewRoom()
    {
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                RoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = (JSONObject) args[0];
                        Phong phong = StaticUser.gson.fromJson(jsonObject.toString(),Phong.class);
                        list.add(phong);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        };
        StaticUser.socket.on("newroom",listener);
    }

    private void AddEvents() {
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
                        Toast.makeText(RoomActivity.this,"Khong Tìm Thấy Phong Số " + sophong +" !",LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnChoiNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phong phong = getPhongChoiNgay();
                if(phong!=null)
                {
                    User us = new User();
                    String s = phong.getId();
                    us.setId(s);
                    StaticUser.userHost = us;
                    startmhban();
                    finish();
                }
                else
                {
                    startmhhost();
                    finish();
                }
            }
        });
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phong phong = new Phong();
                // System.out.println(StaticUser.UserActivity.getId());
                //int soPhong = getIntent().getIntExtra("sophong",0);
                phong.setId(StaticUser.user.getId());
                phong.setRoomnumber(list.size()+1);
                phong.setName(StaticUser.user.getName());
                phong.setPeople(1);
                phong.getUsers().add(StaticUser.user);
                phong.setHost(1);
                StaticUser.phong=phong;
                String jsonroom = StaticUser.gson.toJson(phong);
                StaticUser.socket.emit("createroom",jsonroom);
                startmhhost();
                finish();
            }
        });

        listroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(list.get(i).getPeople()>=10)
                {
                    Toast.makeText(RoomActivity.this,"Phong Day!",LENGTH_SHORT).show();
                }
                else
                {
                    //User us = new User();
                    //Phong  s = (Phong) listroom.getAdapter().getItem(i);
                   // us.setId(s.getId());
                   // StaticUser.userHost = us;
                    StaticUser.phong= (Phong) listroom.getAdapter().getItem(i);
                    StaticUser.user.setId_room(StaticUser.phong.getId());
                    String jsonuser = StaticUser.gson.toJson(StaticUser.user);
                    StaticUser.socket.emit("joinroom",jsonuser);
                    startmhban();
                    finish();


//                    if(StaticUser.phong.getHost()==1) {
//
//                    startmhban();
//                    finish();
//                    }
//                    else
//                    {
//                        startmhhost();
//                        finish();
//                    }
                }


            }
        });


        imgback.setClickable(true);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("back");
            }
        });
    }

    private void AddConTrols() {
        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(StaticUser.user.getName());
        listroom=findViewById(R.id.listroom);
        btnnew =findViewById(R.id.btnnew);
        imgback =  findViewById(R.id.imgback);
        btnChoiNgay = findViewById(R.id.btnchoingay);
        edtsearch = findViewById(R.id.edtsearch);
        imgsearch = findViewById(R.id.imgsearch);
    }

    public Phong getPhongChoiNgay()
    {
        for(Phong phong : list)
        {
            if (phong.getPeople()<10)
            {
                return phong;
            }
        }
        return null;
    }

    public void startmhhost()
    {
        Intent intent = new Intent(this,HostActivity.class);
        //intent.putExtra("sophong",list.size()+1);
        startActivity(intent);
        finish();
    }

    public void startmhban()
    {
        Intent intent = new Intent(this,BanActivity.class);
        startActivity(intent);
        finish();
    }

    public void listroom()
    {

    }
    public void TaoChat()
    {

    }

    public void laylistroom()
    {
      reference.child("Room").addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              Phong phong = new Phong();
              phong.setId(dataSnapshot.child("id").getValue(String.class));
              phong.setName(dataSnapshot.child("tenphong").getValue(String.class));
              phong.setPeople(dataSnapshot.child("songuoi").getValue(Integer.class));
              phong.setRoomnumber(dataSnapshot.child("sophong").getValue(Integer.class));
              list.add(phong);
              adapter.notifyDataSetChanged();
          }

          @Override
          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              Phong phong = new Phong();
              phong.setId(dataSnapshot.child("id").getValue(String.class));
              phong.setPeople(dataSnapshot.child("songuoi").getValue(Integer.class));
              for (Phong p : list)
              {
                  if(p.getId().toString().equals(phong.getId().toString()))
                  {
                      p.setPeople(phong.getPeople());
                      break;
                  }
              }
              adapter.notifyDataSetChanged();

          }

          @Override
          public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      })   ;
    }
}

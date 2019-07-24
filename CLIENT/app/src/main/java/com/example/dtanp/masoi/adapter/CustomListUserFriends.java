package com.example.dtanp.masoi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dtanp.masoi.AddUserFriendActivity;
import com.example.dtanp.masoi.R;

import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.UserFriends;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomListUserFriends extends  RecyclerView.Adapter<CustomListUserFriends.RecyclerViewHolder> implements Filterable  {
    private List<UserFriends> data;
    private  List<UserFriends> filter;
    private Activity context;
    private Socket socket;

    public CustomListUserFriends(List<UserFriends> data,Activity context) {
        this.data = data;
        this.filter=data;
        this.context=context;
        this.socket = SocketSingleton.getInstance();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_recycler, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (Enviroment.user.getUserId().equals(data.get(position).getUserId1())){
            holder.txtUserName.setText(data.get(position).getUserId2()+"");
            //holder.txtNumber.setText(data.get(position).+"");
        }
        else {
            holder.txtUserName.setText(data.get(position).getUserId1()+"");
        }


    }


    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        else
            return data.size();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString= constraint.toString();
                if(charString.isEmpty()){
                    filter=data;
                }else{
                    List<UserFriends> filterList=new ArrayList<>();
                    for(UserFriends userDT :data){
                        if(userDT.getUserId1().toLowerCase().contains(charString)||userDT.getUserId2().contains(charString))
                        {
                            filterList.add(userDT);
                        }
                    }
                    filter=filterList;
                }
                FilterResults filterResults =new FilterResults();
                filterResults.values=filter;
                return  filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filter=(ArrayList<UserFriends>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    public class sss extends Activity{
        List<Chat> listChat;
        ListView listViewChat;
        CustomAdapterChat adapterChat;

        public void LangNgheAllChat()
        {
            listChat = new ArrayList<>();
            CustomAdapterChat adapterChat;
            adapterChat = new CustomAdapterChat(context, R.layout.custom_chat, listChat);
            listViewChat.setAdapter(adapterChat);
            adapterChat.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    //listViewChat.setSelection(adapterChat.getCount() - 1);
                }
            });
            Emitter.Listener listenerChatMes = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    sss.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String json = (String) args[0];
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(json);
                                Chat chat = Enviroment.gson.fromJson(jsonObject.toString(), Chat.class);
                                if (!chat.getMesage().equals(" ")) {
                                    listChat.add(chat);
                                    //adapterChat.notifyDataSetChanged();
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
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUserName ,txtNumber;
        List<Chat> listChat;
        ListView listViewChat;
        CustomAdapterChat adapterChat;
        RelativeLayout relativeLayoutChat;
        private boolean mVisible;
        private View mContentView;
        private static final int UI_ANIMATION_DELAY = 300;
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
        private final Handler mHideHandler = new Handler();
        AlertDialog dialog;
        public RecyclerViewHolder(View itemView ) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txtFriends);
            txtNumber=(TextView) itemView.findViewById(R.id.txtNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatDialog(v.getContext());
                }
            });
            System.out.println("click");

        }
        public  void InitDialog(final Context context){
            int position =getLayoutPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("user info");
            alertDialog.setMessage(data.get(position).getUserId2());
            // final String s=data.get(position).getUserId();
            alertDialog.setPositiveButton("Unfriend", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // addChatDialog(context);
                    Intent intent = new Intent(context, AddUserFriendActivity.class);
                    //intent.putExtra("id", item.getSnippet());
                    context.startActivity(intent);

                }
            });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }


        public void sendUser(Chat chat){
            String json = Enviroment.gson.toJson(chat);
            Enviroment.socket.emit("ChatUser", json);
        }


        public  void addChatDialog(final Context context){
            final int position =getLayoutPosition();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            View view = inflater.inflate(R.layout.dialog_userfriend, null);
            alertDialog.setView(view);
            final TextView edt = view.findViewById(R.id.edtChat);
            TextView btn = view.findViewById(R.id.btnSend);
            listViewChat = view.findViewById(R.id.listChat);
            listViewChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            listChat = new ArrayList<>();
            adapterChat = new CustomAdapterChat(context, R.layout.custom_chat, listChat);
            listViewChat.setAdapter(adapterChat);
            adapterChat.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    listViewChat.setSelection(adapterChat.getCount() - 1);
                }
            });

              btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ok ");
                    Chat chat = new Chat();

                    if (!edt.getText().toString().equals("")) {
                        Chat chat1 =new Chat();
                        chat1.setUsername(Enviroment.user.getName());
                        chat1.setMesage(edt.getText().toString());
                        if (Enviroment.user.getUserId().equals(data.get(position).getUserId1())){
                            //System.out.println(data.get(position).getUserId2());
                            //chat.setUsername(data.get(position).getUserId2());
                            sendChat(data.get(position).getUserId2(),chat1);
                        }
                        else {
                            //System.out.println(data.get(position).getUserId1());
                            //chat.setUsername(data.get(position).getUserId1());
                            sendChat(data.get(position).getUserId1(),chat1);
                        }

                    }

                }
            });
            alertDialog.setMessage(data.get(position).getUserId2());

            alertDialog.setPositiveButton("Thoat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            alertDialog.create().show();

        }

        public void sendChat(String userId, Chat chat){
            JsonObject jsonObject =new JsonObject();
            jsonObject.addProperty("userId",userId);
            String jsonChat = Enviroment.gson.toJson(chat);
            jsonObject.addProperty("message",jsonChat);
            String json = Enviroment.gson.toJson(jsonObject);
            socket.emit("chatuserfreind",json);

        }

        public void listenChat(){

        }

    }


}

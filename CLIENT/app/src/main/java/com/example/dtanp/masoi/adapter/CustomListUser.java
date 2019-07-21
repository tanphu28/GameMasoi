package com.example.dtanp.masoi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.R;

import com.example.dtanp.masoi.environment.Enviroment;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserFriends;
import com.example.dtanp.masoi.singleton.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CustomListUser extends RecyclerView.Adapter<CustomListUser.RecyclerViewHolder> implements Filterable {

    private List<User> data;
    private List<User> filter;
    private Activity context;


    public CustomListUser(List<User> datas,Activity context) {
        this.data = datas;
        this.filter = datas;
        this.context = context;

    }




    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_recycler, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.txtUserName.setText(data.get(position).getName() + "");
        holder.txtNumber.setText(data.get(position).getFullname() + "");
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public int getCount() {
        return filter.size();
    }

    public User getItem(int position ){
        return filter.get(position);
    }
    public long getItemId(int position){
        return  position;
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
                    ArrayList<User> filterList=new ArrayList<>();
                    for(User userDT :data){
                        if(userDT.getName().toLowerCase().contains(charString)||userDT.getFullname().contains(charString))
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
                filter=(ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUserName, txtNumber;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txtFriends);
            txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            InitDialog(v.getContext());
        }

        public void InitDialog(Context context) {
            int position = getLayoutPosition();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(" user info");
            alertDialog.setMessage(data.get(position).getName());
            final String userIdN = data.get(position).getUserId();
            String userID2=data.get(position).getUserId();
            final User u=new User(userID2,userID2,userID2);
            alertDialog.setPositiveButton("Add friend", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    UserFriends userFriends = new UserFriends();
                    userFriends.setFriend_no(Enviroment.user.getUserId());
                    userFriends.setUserId1(Enviroment.user.getUserId());
                    userFriends.setUserId2(userIdN);

                    userFriends.getUsers().add(Enviroment.user);
                    userFriends.getUsers().add(u);

                    Enviroment.userFriends = userFriends;
                    String jsonFriends = Enviroment.gson.toJson(userFriends);
                    Socket socket  = SocketSingleton.getInstance();
                    socket.on("ketquakb", onNewMessage);
                    socket.emit("createUserFriend", jsonFriends);

                }
            });

            alertDialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
        private Emitter.Listener onNewMessage=new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = (boolean) args[0];
                        if (flag==true){
                            Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,"Fail",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }
}










































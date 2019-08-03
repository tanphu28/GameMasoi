package com.example.dtanp.masoi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dtanp.masoi.R;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.UserFriends;

import java.util.ArrayList;

public class CustomListUserFriend extends ArrayAdapter<UserFriends> {
    Context context;
    int resource;
    ArrayList<UserFriends> list;
    public CustomListUserFriend(Context context, int resource, ArrayList<UserFriends> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;
    }

    public static class ViewHolder
    {
        TextView txtname;
        TextView txttenban;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        CustomAdapter.ViewHolder holder;
        if(view==null)
        {
            view=inflater.inflate(resource,parent,false);
            holder = new CustomAdapter.ViewHolder();
            holder.txtban = view.findViewById(R.id.txtban);
            holder.txttenban = view.findViewById(R.id.txttenban);
            holder.txtcuoc= view.findViewById(R.id.txtcuoc);
            holder.txtsonguoi=view.findViewById(R.id.txtsonguoi);
            view.setTag(holder);
        }
        //holder = (CustomListUserFriend.ViewHolder) view.getTag();
       // holder.txtban.setText(list.get(position).getRoomnumber()+"");
       // holder.txttenban.setText(list.get(position).getName()+"");

        return view;
    }

    public void  RemoveAdapter(){
        list.clear();
        CustomListUserFriend.super.notifyDataSetChanged();
    }

}

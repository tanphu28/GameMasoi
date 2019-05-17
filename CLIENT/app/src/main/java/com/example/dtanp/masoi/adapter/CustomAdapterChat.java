package com.example.dtanp.masoi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dtanp.masoi.R;
import com.example.dtanp.masoi.model.Chat;

import java.util.List;

public class CustomAdapterChat extends ArrayAdapter<Chat> {
    Context context;
    int resource;
    List<Chat> list;

    public CustomAdapterChat(@NonNull Context context, int resource, @NonNull List<Chat> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource =resource;
        this.list=objects;
    }

    public static class ViewHolder{
        TextView txtuser;
        TextView txtchat;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if(view==null)
        {
            ViewHolder viewHolder = new ViewHolder();
            view = inflater.inflate(resource,parent,false);
            viewHolder.txtuser = view.findViewById(R.id.user);
            viewHolder.txtchat = view.findViewById(R.id.message);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtuser.setText(list.get(position).getUsername().toString());
        holder.txtchat.setText(list.get(position).getMesage().toString());
        return view;

    }
}

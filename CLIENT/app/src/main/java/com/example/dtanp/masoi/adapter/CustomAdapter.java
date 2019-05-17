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
import com.example.dtanp.masoi.model.Phong;

import java.util.List;

public class CustomAdapter extends ArrayAdapter {
    Context context;
    int resource;
    List<Phong> list;
    public CustomAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        list=objects;
    }

    public static class ViewHolder
    {
        TextView txtban;
        TextView txttenban;
        TextView txtcuoc;
        TextView txtsonguoi;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if(view==null)
        {
            view=inflater.inflate(resource,parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtban = view.findViewById(R.id.txtban);
            viewHolder.txttenban = view.findViewById(R.id.txttenban);
            viewHolder.txtcuoc= view.findViewById(R.id.txtcuoc);
            viewHolder.txtsonguoi=view.findViewById(R.id.txtsonguoi);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtban.setText(list.get(position).getRoomnumber()+"");
        holder.txttenban.setText(list.get(position).getName()+"");
        holder.txtcuoc.setText(3000+"");
        holder.txtsonguoi.setText(list.get(position).getPeople()+"");
        return view;
    }
}

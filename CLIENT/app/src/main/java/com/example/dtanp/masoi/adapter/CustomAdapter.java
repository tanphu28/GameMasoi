package com.example.dtanp.masoi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dtanp.masoi.R;
import com.example.dtanp.masoi.model.Phong;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Phong> {
    Context context;
    int resource;
    ArrayList<Phong> list;
    public CustomAdapter(Context context, int resource, ArrayList<Phong> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;
    }

    public static class ViewHolder
    {
        TextView txtban;
        TextView txttenban;
        TextView txtcuoc;
        TextView txtsonguoi;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        ViewHolder holder;
        if(view==null)
        {
            view=inflater.inflate(resource,parent,false);
             holder = new ViewHolder();
            holder.txtban = view.findViewById(R.id.txtban);
            holder.txttenban = view.findViewById(R.id.txttenban);
            holder.txtcuoc= view.findViewById(R.id.txtcuoc);
            holder.txtsonguoi=view.findViewById(R.id.txtsonguoi);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        holder.txtban.setText(list.get(position).getRoomnumber()+"");
        holder.txttenban.setText(list.get(position).getName()+"");
        holder.txtcuoc.setText(3000+"");
        holder.txtsonguoi.setText(list.get(position).getPeople()+"");
        return view;
    }

    public void  RemoveAdapter(){
        list.clear();
        CustomAdapter.super.notifyDataSetChanged();
    }
}

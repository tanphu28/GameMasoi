package com.example.dtanp.masoi.appinterface;

import com.example.dtanp.masoi.model.Phong;

import java.util.ArrayList;

public interface ChooseRoomView {
    public void updateListView(ArrayList<Phong> list);
    public void checkRoomFullPeople(boolean flag, Phong phong);
    public void  addNewRoom(Phong phong);
    public void removeRoom(String roomId);
}

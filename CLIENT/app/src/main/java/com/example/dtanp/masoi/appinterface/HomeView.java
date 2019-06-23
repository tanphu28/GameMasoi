package com.example.dtanp.masoi.appinterface;

import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.UserFriends;

import java.util.ArrayList;

public interface HomeView {
    public void updateListUserFreinds(ArrayList<UserFriends> list);
    public void addChatMessage(Chat chat);
}

package com.example.dtanp.masoi.appinterface;

import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.User;

import java.util.ArrayList;

public interface UserFriendView {
    public void updateListUser(ArrayList<User> list);
    public void addChatMessage(Chat chat);
}

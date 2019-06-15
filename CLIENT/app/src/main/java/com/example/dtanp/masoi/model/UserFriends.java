package com.example.dtanp.masoi.model;

import java.util.ArrayList;
import java.util.Date;

public class UserFriends {
    private  String friend_no;
    private String userId1;
    private  String userId2;
    private Date regist_dt;
    private ArrayList<User> users;


    public UserFriends(String friend_no,String userId1, String userId2,Date regist_dt, ArrayList<User> users) {
        this.friend_no=friend_no;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.regist_dt=regist_dt;
        this.users = users;
    }


    public UserFriends() {
        this.users = new ArrayList<>();
    }
    public String getFriend_no(){
        return  friend_no;
    }
    public  void setFriend_no(String friend_no){this.friend_no=friend_no;}
    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }


    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public  void setDate(Date regist_dt){this.regist_dt=regist_dt;}

    public  Date getDate(){return regist_dt;}

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}

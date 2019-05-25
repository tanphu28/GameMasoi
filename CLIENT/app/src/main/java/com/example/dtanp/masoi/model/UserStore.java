package com.example.dtanp.masoi.model;

public class UserStore {
    private String userId;
    private String passWord;

    public UserStore(String userId, String passWord) {
        this.userId = userId;
        this.passWord = passWord;
    }

    public UserStore() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}

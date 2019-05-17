package com.example.dtanp.masoi.model;

public class Chat {
    String mesage;
    String username;

    public Chat() {
    }

    public Chat(String mesage, String username) {
        this.mesage = mesage;
        this.username = username;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

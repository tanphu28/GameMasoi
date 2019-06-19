package com.example.dtanp.masoi.model;

public class User {
    private String userId;
    private String fullname;
    private String name;
    private String phone_number;
    private String birthday;
    private String address;
    private String email;
    private int level;
    private int win;
    private int lose;
    private int cancle;
    private Float monney;
    private String id_room;

    public User(String userId, String fullname, String name, String phone_number, String birthday, String address, String email, int level, int win, int lose, int cancle, Float monney, String id_room) {
        this.userId = userId;
        this.fullname = fullname;
        this.name = name;
        this.phone_number = phone_number;
        this.birthday = birthday;
        this.address = address;
        this.email = email;
        this.level = level;
        this.win = win;
        this.lose = lose;
        this.cancle = cancle;
        this.monney = monney;
        this.id_room = id_room;
    }

    public  User(String userId, String fullname, String name){
        this.userId = userId;
        this.fullname = fullname;
        this.name = name;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getCancle() {
        return cancle;
    }

    public void setCancle(int cancle) {
        this.cancle = cancle;
    }

    public Float getMonney() {
        return monney;
    }

    public void setMonney(Float monney) {
        this.monney = monney;
    }

    public String getId_room() {
        return id_room;
    }

    public void setId_room(String id_room) {
        this.id_room = id_room;
    }
}

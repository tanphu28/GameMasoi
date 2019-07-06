package com.example.dtanp.masoi.model;

import java.util.ArrayList;
import java.util.List;

public class Phong {
    private  String _id;
    private String id;
    private String name;
    private int people;
    private int totalpeople;
    private int roomnumber;
    private int host;
    private int money;
    private ArrayList<User> users;


    public Phong() {
        this.users = new ArrayList<>();
    }

    public Phong(String _id, String id, String name, int people, int totalpeople, int roomnumber, int host, int money, ArrayList<User> users) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.people = people;
        this.totalpeople = totalpeople;
        this.roomnumber = roomnumber;
        this.host = host;
        this.money = money;
        this.users = users;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getTotalpeople() {
        return totalpeople;
    }

    public void setTotalpeople(int totalpeople) {
        this.totalpeople = totalpeople;
    }

    public int getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(int roomnumber) {
        this.roomnumber = roomnumber;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}

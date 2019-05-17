package com.example.dtanp.masoi.model;

public class User {
    private String id;
    private String  name;
    private String username;
    private String id_room;

    public User(String id, String name, String username, String id_room) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.id_room = id_room;
    }

    public User() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_room() {
        return id_room;
    }

    public void setId_room(String id_room) {
        this.id_room = id_room;
    }
}

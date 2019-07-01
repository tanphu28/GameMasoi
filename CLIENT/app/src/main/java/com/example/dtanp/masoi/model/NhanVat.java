package com.example.dtanp.masoi.model;

public class NhanVat {
    private String id;
    private int manv;
    private int resource;
    private String name;

    public NhanVat() {
    }

    public NhanVat(String id, int manv, int resource, String name) {
        this.id = id;
        this.manv = manv;
        this.resource = resource;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getManv() {
        return manv;
    }

    public void setManv(int manv) {
        this.manv = manv;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
       return manv + "-" + id;
    }
}

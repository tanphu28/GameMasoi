package com.example.dtanp.masoi.model;

public class NhanVat {
    private String id;
    private int manv;
    private int resource;

    public NhanVat(String id, int manv, int resource) {
        this.id = id;
        this.manv = manv;
        this.resource = resource;
    }

    public NhanVat() {
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

    @Override
    public String toString() {
       return manv + "-" + id;
    }
}

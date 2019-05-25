package com.example.dtanp.masoi.model;

import java.util.Date;

public class UserHistory {
    private String userId;
    private Date login_dt;

    public UserHistory(String userId, Date login_dt) {
        this.userId = userId;
        this.login_dt = login_dt;
    }

    public UserHistory() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLogin_dt() {
        return login_dt;
    }

    public void setLogin_dt(Date login_dt) {
        this.login_dt = login_dt;
    }
}


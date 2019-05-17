package com.example.dtanp.masoi.model;

import android.widget.ImageButton;
import android.widget.TextView;

public class UserRoom {

    private ImageButton user;
    private TextView txtuser;
    private boolean flag;
    private NhanVat nhanVat;

    public NhanVat getNhanVat() {
        return nhanVat;
    }

    public void setNhanVat(NhanVat nhanVat) {
        this.nhanVat = nhanVat;
    }

    public User getUseradd() {
        return useradd;
    }

    public void setUseradd(User useradd) {
        this.useradd = useradd;
    }

    private User useradd;

    public TextView getTxtuser() {
        return txtuser;
    }

    public void setTxtuser(TextView txtuser) {
        this.txtuser = txtuser;
    }

    public UserRoom(ImageButton user, TextView txtuser, boolean flag) {

        this.user = user;
        this.txtuser = txtuser;
        this.flag = flag;
    }




    public UserRoom() {
    }

    public ImageButton getUser() {
        return user;
    }

    public void setUser(ImageButton user) {
        this.user = user;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

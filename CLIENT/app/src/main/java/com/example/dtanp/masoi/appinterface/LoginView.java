package com.example.dtanp.masoi.appinterface;

public interface LoginView {
    public void showDialogRegister();
    public void loginSuccess();
    public void checkUser(boolean flag);
    public void registNicknameSuccess();
    public void showDialogUpdate();
    public void userLoginSuccess(boolean flag);
    public void updateFogotPass(int code, String userId);
    public void updateChangePass(boolean flag);
    public void updateErrorLogin();
}

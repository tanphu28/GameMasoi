package com.example.dtanp.masoi.appinterface;

import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.User;

import java.util.ArrayList;
import java.util.List;

public interface RoomView {
    public void updateChatMessage(Chat chat);
    public void updateUserExit(String userId);
    public void updateUserReady(int number);
    public void updateOK(boolean flag);
    public void  updateNewUserJoinRoom(User user);
    public void updateTime(String time);
    public void updateUserDie(String userId);
    public void updateNhanVat(NhanVat nhanVat);
    public void updateLuotDB(int luot);
    public void updateKetQuaBoPhieu(int kq);
    public void updateIdTientriChon(String id);
    public void updateIdMaSoiChon(String id);
    public void updateIdThoSanChon(String id);
    public void updateIdBaoVeChon(String id);
    public void updateIdBiGiet(String id);
    public void updateNhanVatSang(int nv);
    public void updateNhanVatTat(int nv);
    public void updateAllChat(boolean flag);
    public void updateAllManhinh(boolean flag);
    public void updateBangIdChon(String id);
    public void updateLeaveRoom();
    public void updateHost();
    public  void updateListNhanVat(ArrayList<NhanVat> list);
    public void updateFinish(int win);
    public void updateNhanVatChucNangDie(int number);
    public void updateCuoiNgay(String idBV, String idTS, List<String> listMaSoiChon, List<Integer> kqBP,String idBoPhieu);
    public void updateListDanLangChon(List<String> list);
    public void updateSync(boolean flagChat, boolean flagXuLi,int manv);
    public void updateListAllChon(String name , String nameChoose);
    public void updateDisconnect();
    public void updateListBoPhieu(String name, String bp);
    public void updateListUserDie(List<String> list);
    public void updateSyncForUser(String userId,String id);
    public void updateListReset(List<String> listDie, List<NhanVat> listNhanVatUpdate);
    public void updateListUserExit(List<String> list);
}

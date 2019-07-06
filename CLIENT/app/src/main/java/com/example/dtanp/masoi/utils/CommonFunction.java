package com.example.dtanp.masoi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonFunction {
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertByteToHex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String formatGold(int gold){
        if(gold>=10000){
            return gold/1000 + "K";
        }else if (gold>=1000000){
            return gold/1000000 + "M";
        }
        else if (gold>=1000000000){
            return gold/1000000 + "B";
        }else {
            return gold+"";
        }
    }
}

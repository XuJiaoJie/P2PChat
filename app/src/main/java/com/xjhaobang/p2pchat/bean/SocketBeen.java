package com.xjhaobang.p2pchat.bean;

import java.io.Serializable;

/**
 * Created by PC on 2017/6/3.
 */

public class SocketBeen implements Serializable{
    private String userName;
    private String userIP;
    private String msg;
    private String time;
    private String otherIP;

    public String getOtherIP() {
        return otherIP;
    }

    public void setOtherIP(String otherIP) {
        this.otherIP = otherIP;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

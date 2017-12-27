package cn.com.tianyudg.socketiodemo.bean;

import java.io.Serializable;

/**
 * Author : WaterFlower.
 * Created on 2017/12/27.
 * Desc :
 */

public class LoginInfoBean implements Serializable {


    public String account;
    public String psw;

    public LoginInfoBean(String account, String psw) {
        this.account = account;
        this.psw = psw;
    }
}

package cn.com.tianyudg.socketiodemo.socket_io;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.com.tianyudg.socketiodemo.config.SocketIoConfig;
import cn.com.tianyudg.socketiodemo.util.encrypt.AESEncrypt;

/**
 * @author JeremyHwc;
 * @date 2017/11/23/023 14:09;
 * @email jeremy_hwc@163.com ;
 * @desc
 */

public class SocketUtil {

    public static final String TAG = SocketIoConfig.SOCKET_LOG;
    private static final String appName = SocketIoConfig.appName;

    public static String login(String userId, String userName, String password) {
        JSONObject dataObject = new JSONObject();
        String data = aes128(userName, password);
        try {
            dataObject.put("uid", userId);
            dataObject.put("app", appName);
            dataObject.put("data", data);
            //            Logger.t(TAG).e(dataObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataObject.toString();
    }

    private static String aes128(String userName, String password) {
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("username", userName);
            dataObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AESEncrypt aesEncrypt = new AESEncrypt();
        byte[] bytes = new byte[0];
        try {
            bytes = dataObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encrypt = aesEncrypt.encrypt(bytes);
        //        Logger.t(TAG).e("aes128: "+encrypt);
        return encrypt;
    }


    public static String reconnect(String userId) {
        JSONObject dataObject = new JSONObject();
        String data = aes128(userId);
        try {
            dataObject.put("app", appName);
            dataObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //        Logger.t(TAG).e(dataObject.toString());
        return dataObject.toString();
    }

    private static String aes128(String uid) {
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AESEncrypt aesEncrypt = new AESEncrypt();
        byte[] bytes = new byte[0];
        try {
            bytes = dataObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encrypt = aesEncrypt.encrypt(bytes);
        //        Logger.t(TAG).e("aes128: "+encrypt);
        return encrypt;
    }


}

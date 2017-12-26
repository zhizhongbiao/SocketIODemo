package cn.com.tianyudg.socketiodemo.socket_io;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import cn.com.tianyudg.socketiodemo.config.SocketIoConfig;
import cn.com.tianyudg.socketiodemo.util.LogUtils;
import cn.com.tianyudg.socketiodemo.util.MathUtils;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by LYX on 2017/12/13.
 *
 * @author LYX
 * @data 2017/12/13
 * @description {Socket服务,用于接受新消息}
 */

public class SocketService extends Service {

    private static final String TAG = "SocketService";
    Socket mSocket;
    Handler handler;
    private String socketRandomCode;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        initSocket();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }


    private void initSocket() {

        if (mSocket == null) {
            try {
                mSocket = IO.socket(SocketIoConfig.CHAT_SERVER_URL);
            } catch (URISyntaxException e) {
                throw new RuntimeException("socket地址有问题" + e.toString());
            }
        }

        mSocket.off();
        mSocket.on(Socket.EVENT_CONNECT, onConnectListener);//打开连接监听
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnectListener);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorListener);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorListener);

        mSocket.on(SocketIoConfig.EVENT.LOGIN_SUCCEED, loginSucceedListener);//打开登录连接成功监听
        mSocket.on(SocketIoConfig.EVENT.MSG_FROM_SERVER, onNewMessageListener);
        mSocket.on(SocketIoConfig.EVENT.NEW_MSG_FROM_CLIENT, onNewMessageListener);
        mSocket.connect();
    }


    private void disConnect() {

        if (mSocket == null) {
            LogUtils.e("-------- disConnect()    mSocket == null ------------");
            return;
        }
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnectListener);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnectListener);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectErrorListener);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorListener);

        mSocket.off(SocketIoConfig.EVENT.LOGIN_SUCCEED, loginSucceedListener);
        mSocket.off(SocketIoConfig.EVENT.MSG_FROM_SERVER, onNewMessageListener);
        mSocket.off(SocketIoConfig.EVENT.NEW_MSG_FROM_CLIENT, onNewMessageListener);
    }

    private Emitter.Listener onConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
                socketLogin();
        }
    };



    /*
     * socket登录
     */
    private void socketLogin() {

        socketRandomCode = MathUtils.generateRandomStr(5);

//        String userId = LoginUtils.getUserMsg().get(2) + "_" + socketRandomCode;
//        String userName = LoginUtils.getUserMsg().get(0);//Account
//        String userPassword = LoginUtils.getUserMsg().get(1);

//        具体的登录方法
//        mSocket.emit(SocketIoConfig.EVENT.LOGIN, SocketUtil.login(userId, userName, userPassword));

    }


    /**
     * 发送重新连接
     */
//    private void socketReconnect() {
//        String socketRandom = SocketUtils.getSocketRandomNUm();
        //具体的重连方法
//        mSocket.emit(SocketIoConfig.EVENT.RECONNECT, SocketUtil.reconnect(companyId));
//    }



    /**
     * Socket登录成功的服务的监听
     */
    private Emitter.Listener loginSucceedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String msg = (String) args[0];

        }
    };


    /**
     * 断开Socket服务的监听
     */
    private Emitter.Listener onDisconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };


    /**
     * 连接异常
     */
    private Emitter.Listener onConnectErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };


    /**
     * 新的消息
     * 子线程
     */
    private Emitter.Listener onNewMessageListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final String newMsg = (String) args[0];


            handler.post(new Runnable() {
                @Override
                public void run() {
                    //解析发送JSon数据
                    parseJsonData(newMsg);
                }
            });
        }
    };


    private void parseJsonData(String socketJosonMsg) {

        //        {
        //            "event": "connect_callback",
        //                "data": {
        //            "login_status": 1
        //        },
        //            "push_time": 1513773897
        //        }


        //            {
        //                "event": "pending_push",
        //                    "data": {
        //                        "pending_new_count": 0,
        //                        "pending_es_check_count": 0,
        //                        "pending_es_sign_count": 0,
        //                        "pending_rent_check_count": 0,
        //                        "pending_rent_sign_count": 0
        //            },
        //                "push_time": 1513323461
        //            }


        //        {
        //            "event": "project_push",
        //             "data": {
        //                    "project_id": "4901",
        //                    "bb_count": 6,
        //                    "visit_count": 0,
        //                    "xd_count": 0,
        //                    "sign_count": 0
        //                      },
        //            "push_time": 1513573287
        //        }

        try {
            JSONObject object = new JSONObject(socketJosonMsg);
            String eventName = object.getString("event");
            JSONObject data = object.getJSONObject("data");
            switch (eventName) {
                case "connect_callback"://Socket登陆成功
                    int login_status = data.getInt("login_status");
                    if (login_status == 1) {

                        Log.e(TAG, "parseJsonData: 登陆成功");

                    }
                    break;
                case "pending_push"://待办信息
                    break;
                case "project_push"://项目信息
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        disConnect();
    }


}

package cn.com.tianyudg.socketiodemo;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.com.tianyudg.socketiodemo.bean.LoginBean;
import cn.com.tianyudg.socketiodemo.bean.LoginInfoBean;
import cn.com.tianyudg.socketiodemo.config.ApiConfig;
import cn.com.tianyudg.socketiodemo.config.SocketIoConfig;
import cn.com.tianyudg.socketiodemo.socket_io.SocketService;
import cn.com.tianyudg.socketiodemo.util.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Callback, ServiceConnection {

    private static final String TAG = "MainActivity";
    public static final String KEY_LOGIN_BEAN = "keyLoginBean";
    public static final String KEY_LOGIN_INFO_BEAN = "keyLoginInfoBean";
    private static final int MSG_LOGIN_SUCCESS = 1232;
    private static final int MSG_LOGIN_ERROR = 1112;
    private TextView tvMsg;
    private EditText etAccount;
    private EditText etPsw;
    private MyHanlder myHanlder;

    private static ProgressDialog progressDialog;
    private SocketService socketService;
    private LoginInfoBean loginInfoBean;
    private EditText etSocketServerIp;
    private EditText etHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        etAccount = (EditText) findViewById(R.id.etAccount);
        etPsw = (EditText) findViewById(R.id.etPsw);
        etHost = (EditText) findViewById(R.id.etHost);
        etSocketServerIp = (EditText) findViewById(R.id.etSocketServerIp);
        etAccount.setText("13826914162");
        etPsw.setText("888888");
        myHanlder = new MyHanlder(this);
        progressDialog = new ProgressDialog(this);

    }

    public void login(View view) {
        String account = etAccount.getText().toString().trim();
        String psw = etPsw.getText().toString().trim();
        String host = etHost.getText().toString().trim();
        String ip = etSocketServerIp.getText().toString().trim();

        if (!TextUtils.isEmpty(ip))
        {
            SocketIoConfig.SERVER_URL="http://"+ip;
        }

        if (TextUtils.isEmpty(host))
        {
           host=ApiConfig.HOST;
        }

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psw)) {
            Toast.makeText(this, "密码或账号错误", Toast.LENGTH_SHORT);
            return;
        }
        progressDialog.show();
        login(account, psw,host);
        loginInfoBean = new LoginInfoBean(account, psw);
    }

    private void login(String account, String psw,String host) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        FormBody body = new FormBody.Builder()
                .add("username", account)
                .add("password", psw)
                .build();

        Request request = new Request.Builder()
                .url(host + ApiConfig.API_LOGIN)
                .post(body)
                .build();

        Call newCall = okHttpClient.newCall(request);
        newCall.enqueue(this);
    }


    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: e= ------ " + e.getMessage());
        Toast.makeText(this, "登录出错", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        String json = response.body().string().trim();
        Log.e(TAG, "登录成功: json= ----- " + json);

        Message msg = Message.obtain();
        if (response.isSuccessful()) {
            msg.what = MSG_LOGIN_SUCCESS;
            msg.obj = json;
        } else {
            msg.what = MSG_LOGIN_ERROR;
        }
        myHanlder.sendMessage(msg);
    }


    private static class MyHanlder extends Handler {

        WeakReference<MainActivity> reference;

        public MyHanlder(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = reference.get();
            if (activity == null) return;
            handlerResult(msg, activity);
        }
    }

    private static void handlerResult(Message msg, MainActivity activity) {
        switch (msg.what) {
            case MSG_LOGIN_SUCCESS:
                String json = (String) msg.obj;
                progressDialog.dismiss();
                Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                LoginBean loginBean = gson.fromJson(json, LoginBean.class);
                //启动推送服务
                activity.startSocketService(loginBean);
                break;
            case MSG_LOGIN_ERROR:
                Toast.makeText(activity, "登录出错", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void startSocketService(LoginBean loginBean) {
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra(KEY_LOGIN_BEAN, loginBean);
        intent.putExtra(KEY_LOGIN_INFO_BEAN, loginInfoBean);
        bindService(intent, this, BIND_AUTO_CREATE);
    }


    public void onSocketMsgArrive(String msg) {
        if (TextUtils.isEmpty(msg)) msg = "消息为空";
        tvMsg.setText(msg);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        socketService = ((SocketService.MyBinder) service).getSocketService();
        socketService.setCurrentActivity(this);
        LogUtils.e(" onServiceConnected   ComponentName= " + name.toString());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtils.e(" onServiceDisconnected   ComponentName= " + name.toString());
    }


    @Override
    protected void onDestroy() {
        myHanlder.removeCallbacksAndMessages(null);
        unbindService(this);
        super.onDestroy();
    }
}

package cn.com.tianyudg.socketiodemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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
import cn.com.tianyudg.socketiodemo.config.ApiConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "MainActivity";
    private static final int MSG_LOGIN_SUCCESS = 1232;
    private static final int MSG_LOGIN_ERROR = 1112;
    private TextView tvMsg;
    private EditText etAccount;
    private EditText etPsw;
    private MyHanlder myHanlder;

    private static ProgressDialog progressDialog;
    public static LoginBean loginBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        etAccount = (EditText) findViewById(R.id.etAccount);
        etPsw = (EditText) findViewById(R.id.etPsw);
        etAccount.setText("13826914162");
        etPsw.setText("888888");
        myHanlder = new MyHanlder(this);
        progressDialog = new ProgressDialog(this);

    }

    public void login(View view) {
        String account = etAccount.getText().toString().trim();
        String psw = etPsw.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psw)) {
            Toast.makeText(this, "密码或账号错误", Toast.LENGTH_SHORT);
            return;
        }
        progressDialog.show();
        login(account, psw);
    }

    private void login(String account, String psw) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        FormBody body = new FormBody.Builder()
                .add("username", account)
                .add("password", psw)
                .build();

        Request request = new Request.Builder()
                .url(ApiConfig.HOST + ApiConfig.API_LOGIN)
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

        WeakReference<Activity> reference;

        public MyHanlder(AppCompatActivity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = reference.get();
            if (activity == null) return;
            handlerResult(msg, activity);
        }
    }

    private static void handlerResult(Message msg, Activity activity) {
        switch (msg.what) {
            case MSG_LOGIN_SUCCESS:
                String json = (String) msg.obj;
                progressDialog.dismiss();
                Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                loginBean = gson.fromJson(json, LoginBean.class);

                //启动推送服务
                break;
            case MSG_LOGIN_ERROR:
                Toast.makeText(activity, "登录出错", Toast.LENGTH_SHORT).show();
                break;
        }


    }


    @Override
    protected void onDestroy() {
        myHanlder.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

package com.mao.remind_test2.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.mao.remind_test2.Login.LoginActivity;
import com.mao.remind_test2.Main.MainActivity;
import com.mao.remind_test2.R;


public class WelcomeActivity extends AppCompatActivity {
    private static final int TIME = 1000;
    private static final int GO_MAIN = 1;
    private static final int GO_LOGIN = 2;


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN:
                    goMain();
                    break;
                case GO_LOGIN:
                    goLogin();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.welcome_layout);


        SharedPreferences sf = getSharedPreferences("loginsuccess", MODE_PRIVATE);
        String account = sf.getString("nameurl", "");
        if (account == "") {
            SharedPreferences spf1 = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = spf1.edit();
            editor1.putBoolean("isFirstIn", true);
            editor1.commit();
        }
        init();
    }

    private void init() {
        SharedPreferences spf = getSharedPreferences("data", MODE_PRIVATE);//判断是否登陆
        boolean isFirstIn = spf.getBoolean("isFirstIn", true);
        SharedPreferences.Editor editor = spf.edit();

        if (isFirstIn) {     //若为true，则未登陆
            editor.putBoolean("isFirstIn", false);
            mhandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
        } else {
            mhandler.sendEmptyMessageDelayed(GO_MAIN, TIME);
        }
        editor.commit();

    }

    private void goMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
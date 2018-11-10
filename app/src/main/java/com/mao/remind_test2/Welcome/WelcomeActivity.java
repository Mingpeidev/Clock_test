package com.mao.remind_test2.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.mao.remind_test2.Clock.Clockinfo;
import com.mao.remind_test2.Login.LoginActivity;
import com.mao.remind_test2.Main.MainActivity;
import com.mao.remind_test2.R;
import com.mao.remind_test2.Util.ClockHelper;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {
    private static final int TIME = 1000;
    private static final int GO_MAIN = 1;
    private static final int GO_LOGIN = 2;

    private ClockHelper clockHelp;


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
        initClock();
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

    public void initClock() {//启动app时重设闹钟，防止因为app被杀闹钟失效
        clockHelp = new ClockHelper(this);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        List<Clockinfo> clockinfos = DataSupport.findAll(Clockinfo.class);
        for (Clockinfo clockinfo : clockinfos) {
            int sign = clockinfo.getSign();
            String repead = clockinfo.getRepead();
            String on_off = clockinfo.getOn_off();
            String text = clockinfo.getText();
            String ringUrl = clockinfo.getRing();

            mCalendar.set(Calendar.HOUR_OF_DAY, clockinfo.getHour());
            mCalendar.set(Calendar.MINUTE, clockinfo.getMinute());
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);

            if (ringUrl.equals("布谷鸟")) {
                ringUrl = "buguniao";
            } else if (ringUrl.equals("滴滴")) {
                ringUrl = "didi";
            } else if (ringUrl.equals("嘟嘟")) {
                ringUrl = "dudu";
            } else if (ringUrl.equals("闹铃")) {
                ringUrl = "naozhong";
            }
            if (on_off.equals("on")) {
                clockHelp.openClock(sign, repead, text, ringUrl, mCalendar.getTimeInMillis());
            }
        }
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
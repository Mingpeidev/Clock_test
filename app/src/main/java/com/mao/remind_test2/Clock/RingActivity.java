package com.mao.remind_test2.Clock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mao.remind_test2.R;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class RingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private String ringUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String message = this.getIntent().getStringExtra("msg");//接受备注信息

        ringUrl = this.getIntent().getStringExtra("ringUrl");
        ApplicationInfo applicationInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(ringUrl, "raw", applicationInfo.packageName);

        mediaPlayer = MediaPlayer.create(RingActivity.this, resID);
        mediaPlayer.setLooping(true);//循环播放
        mediaPlayer.start();

        if (message.equals("时间到")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("停止计时器？")
                    .setIcon(R.drawable.welcome)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    RingActivity.this.finish();
                                }
                            }).create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("停止闹钟？")
                    .setIcon(R.drawable.welcome)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    RingActivity.this.finish();
                                }
                            }).create().show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}

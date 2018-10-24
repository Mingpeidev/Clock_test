package com.mao.remind_test2.Clock;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mao.remind_test2.Util.ClockHelper;
import com.mao.remind_test2.R;

import java.util.Calendar;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class AddclockActivity extends AppCompatActivity {

    private ClockHelper clockHelp;

    private Button sure = null;
    private Button cancal = null;
    private Button repeat = null;
    private TextView repeat_text = null;
    private Button ring = null;
    private TextView ring_text = null;
    private EditText text = null;

    private TimePicker timePicker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.addclock_layout);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//关闭通知栏
        notificationManager.cancel(1);//取消通知栏

        sure = (Button) findViewById(R.id.sure_btn);
        cancal = (Button) findViewById(R.id.cancel_btn);
        repeat = (Button) findViewById(R.id.repeat_btn);
        ring = (Button) findViewById(R.id.ring_btn);
        repeat_text = (TextView) findViewById(R.id.repeat_text);
        ring_text = (TextView) findViewById(R.id.ring_text);
        text = (EditText) findViewById(R.id.text);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        clockHelp = new ClockHelper(this);


        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                int mHour = timePicker.getCurrentHour();
                int mMinute = timePicker.getCurrentMinute();
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                int sign = 60 * mHour + mMinute;
                String ringUrl = "";

                while (repeat_text.getText().equals("")) {
                    repeat_text.setText("只响一次");
                }

                if (ring_text.getText().equals("") || ring_text.getText().equals("布谷鸟")) {
                    ring_text.setText("布谷鸟");
                    ringUrl = "buguniao";
                } else if (ring_text.getText().equals("滴滴")) {
                    ringUrl = "didi";
                } else if (ring_text.getText().equals("嘟嘟")) {
                    ringUrl = "dudu";
                } else if (ring_text.getText().equals("闹铃")) {
                    ringUrl = "naozhong";
                }

                //设置闹钟
                clockHelp.openClock(sign, repeat_text.getText().toString(), text.getText().toString(), ringUrl, mCalendar.getTimeInMillis());

                //保存闹钟到sqlite
                Clockinfo clockinfo = new Clockinfo();
                clockinfo.setSign(sign);
                clockinfo.setHour(mHour);
                clockinfo.setMinute(mMinute);
                clockinfo.setOn_off("on");
                clockinfo.setRepead(repeat_text.getText().toString());
                clockinfo.setRing(ring_text.getText().toString());
                clockinfo.setText(text.getText().toString());

                clockinfo.save();

                finish();

            }
        });

        cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String items[] = {"只响一次", "每天"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddclockActivity.this);
                builder.setTitle("重复选择");
                builder.setIcon(R.drawable.welcome);
                // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]) {
                            case "只响一次":
                                repeat_text.setText("只响一次");
                                break;
                            case "每天":
                                repeat_text.setText("每天");
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
        ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String items[] = {"布谷鸟", "滴滴", "嘟嘟", "闹铃"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddclockActivity.this);
                builder.setTitle("铃声选择");
                builder.setIcon(R.drawable.welcome);
                // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]) {
                            case "布谷鸟":
                                ring_text.setText("布谷鸟");
                                break;
                            case "滴滴":
                                ring_text.setText("滴滴");
                                break;
                            case "嘟嘟":
                                ring_text.setText("嘟嘟");
                                break;
                            case "闹铃":
                                ring_text.setText("闹铃");
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }
}

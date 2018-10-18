package com.mao.remind_test2.Clock;

import android.app.AlarmManager;
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

import com.mao.remind_test2.R;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/7/1.
 */

public class ModifyclockActivity extends AppCompatActivity {
    private AlarmManager alarmManager;
    private PendingIntent pi;

    private Button sure = null;
    private Button cancal = null;
    private Button repeat = null;
    private TextView repeat_text = null;
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

        sure = (Button) findViewById(R.id.sure_btn);
        cancal = (Button) findViewById(R.id.cancel_btn);
        repeat = (Button) findViewById(R.id.repeat_btn);
        repeat_text = (TextView) findViewById(R.id.repeat_text);
        text = (EditText) findViewById(R.id.text);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //显示此闹钟数据
        Intent intent1 = getIntent();
        final String id = intent1.getStringExtra("clockidput");
        List<Clockinfo> clockinfos = DataSupport.where("id =?", id).find(Clockinfo.class);
        for (Clockinfo clockinfo : clockinfos) {
            repeat_text.setText(clockinfo.getRepead());
            text.setText(clockinfo.getText());
            timePicker.setCurrentHour(clockinfo.getHour());
            timePicker.setCurrentMinute(clockinfo.getMinute());
        }

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Clockinfo> clockinfos1 = DataSupport.where("id =?", id).find(Clockinfo.class);
                for (Clockinfo clockinfo : clockinfos1) {//取消选中行闹钟
                    Intent intent = new Intent(ModifyclockActivity.this, ClockReceiver.class);
                    int sign = clockinfo.getSign();
                    pi = PendingIntent.getBroadcast(ModifyclockActivity.this, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pi);
                }

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                int mHour = timePicker.getCurrentHour();
                int mMinute = timePicker.getCurrentMinute();
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                int sign = 60 * mHour + mMinute;

                while (repeat_text.getText().equals("")) {
                    repeat_text.setText("只响一次");
                }

                //设置闹钟
                if (repeat_text.getText().equals("只响一次")) {

                    Intent intent = new Intent(ModifyclockActivity.this, ClockReceiver.class);
                    intent.putExtra("msg", text.getText().toString());
                    pi = PendingIntent.getBroadcast(ModifyclockActivity.this, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (mCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);
                    }
                }

                if (repeat_text.getText().equals("每天")) {

                    Intent intent = new Intent(ModifyclockActivity.this, ClockReceiver.class);
                    intent.putExtra("msg", text.getText().toString());
                    intent.putExtra("interval", 1000 * 24 * 60 * 60);
                    pi = PendingIntent.getBroadcast(ModifyclockActivity.this, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    intent.putExtra("sign", sign);
                    if (mCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000, 1000 * 24 * 60 * 60, pi);
                    } else {
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), 1000 * 24 * 60 * 60, pi);
                    }
                }

                //更新闹钟到sqlite
                Clockinfo clockinfo = new Clockinfo();
                clockinfo.setSign(sign);
                clockinfo.setHour(mHour);
                clockinfo.setMinute(mMinute);
                clockinfo.setRepead(repeat_text.getText().toString());
                clockinfo.setText(text.getText().toString());
                clockinfo.setOn_off("on");
                clockinfo.updateAll("id=?", id);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyclockActivity.this);
                builder.setTitle("重复选择");
                builder.setIcon(R.mipmap.ic_launcher);
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
    }
}

package com.mao.remind_test2.Today;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.mao.remind_test2.Clock.ClockReceiver;
import com.mao.remind_test2.R;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class ModifytodayActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pi;
    private PendingIntent pi1;

    private int sign;
    private long time;

    private EditText subject = null;
    private EditText body = null;
    private EditText date = null;
    private Button chooseDate = null;
    private Button sure = null;
    private Button cacel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.addtoday_layout);

        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        date = (EditText) findViewById(R.id.date);
        chooseDate = (Button) findViewById(R.id.chooseDate);
        sure = (Button) findViewById(R.id.sure);
        cacel = (Button) findViewById(R.id.cacel);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //显示已有数据
        Intent intent1 = getIntent();
        String id = intent1.getStringExtra("idput");
        List<Todayinfo> todayinfos = DataSupport.where("id =?", id).find(Todayinfo.class);
        for (Todayinfo todayinfo : todayinfos) {
            subject.setText(todayinfo.getSubject());
            body.setText(todayinfo.getBody());
            sign = todayinfo.getSign();
            date.setText(todayinfo.getDate());
        }

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                new DatePickerDialog(ModifytodayActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayofmonth);
                                calendar.set(Calendar.HOUR_OF_DAY, 8);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                time = calendar.getTimeInMillis();
                                sign = year + month + dayofmonth;
                                date.setText(year + "-" + month + "-" + dayofmonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifytodayActivity.this, ClockReceiver.class);
                pi1 = PendingIntent.getBroadcast(ModifytodayActivity.this, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pi1);


                String subStr = subject.getText().toString();
                String bodyStr = body.getText().toString();
                String dateStr = date.getText().toString();

                Intent intent1 = getIntent();
                String id = intent1.getStringExtra("idput");

                Intent intent3 = new Intent(ModifytodayActivity.this, ClockReceiver.class);
                pi = PendingIntent.getBroadcast(ModifytodayActivity.this, sign, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
                if (time < System.currentTimeMillis()) {
                    Toast.makeText(ModifytodayActivity.this, "已记录。但时间已过，不提醒", Toast.LENGTH_SHORT).show();
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
                }

                Todayinfo todayinfo = new Todayinfo();
                todayinfo.setSubject(subStr);
                todayinfo.setBody(bodyStr);
                todayinfo.setDate(dateStr);
                todayinfo.setSign(sign);
                todayinfo.updateAll("id =? ", id);

                finish();
            }
        });

        cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

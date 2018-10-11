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
import java.util.Calendar;

/**
 * Created by Mingpeidev on 2018/6/26.
 */

public class AddtodayActivity extends AppCompatActivity{

    private AlarmManager alarmManager;
    private PendingIntent pi;
    private int sign;
    private long time;

    private EditText subject = null;
    private EditText body = null;
    private EditText date = null;
    private Button chooseDate = null;
    private Button sure=null;
    private Button cacel=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        setContentView(R.layout.addtoday_layout);

        subject=(EditText)findViewById(R.id.subject);
        body=(EditText)findViewById(R.id.body);
        date=(EditText)findViewById(R.id.date);
        chooseDate=(Button) findViewById(R.id.chooseDate);
        sure=(Button)findViewById(R.id.sure);
        cacel=(Button)findViewById(R.id.cacel);

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                new DatePickerDialog(AddtodayActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {

                                calendar.set(Calendar.YEAR,year);
                                calendar.set(Calendar.MONTH,month);
                                calendar.set(Calendar.DAY_OF_MONTH,dayofmonth);
                                calendar.set(Calendar.HOUR_OF_DAY,8);
                                calendar.set(Calendar.MINUTE,0);
                                calendar.set(Calendar.SECOND,0);
                                calendar.set(Calendar.MILLISECOND,0);
                                time=calendar.getTimeInMillis();
                                date.setText(year + "-" + (month+1) + "-" + dayofmonth);
                                sign=year+month+dayofmonth;
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subStr = subject.getText().toString();
                String bodyStr = body.getText().toString();
                String dateStr = date.getText().toString();

                Todayinfo todayinfo=new Todayinfo();
                todayinfo.setSubject(subStr);
                todayinfo.setBody(bodyStr);
                todayinfo.setDate(dateStr);
                todayinfo.setSign(sign);
                todayinfo.save();

                Intent intent = new Intent(AddtodayActivity.this,ClockReceiver.class);
                pi = PendingIntent.getBroadcast(AddtodayActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                if (time<System.currentTimeMillis()){
                    Toast.makeText(AddtodayActivity.this,"已记录。但时间已过，不提醒",Toast.LENGTH_SHORT).show();
                }else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,time,pi);
                }

                Intent intent1=new Intent(AddtodayActivity.this,TodayActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddtodayActivity.this,TodayActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

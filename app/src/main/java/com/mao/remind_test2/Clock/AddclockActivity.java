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
import java.util.Calendar;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class AddclockActivity extends AppCompatActivity{

    private AlarmManager alarmManager;
    private PendingIntent pi;

    private Button sure=null;
    private Button cancal=null;
    private Button repeat=null;
    private TextView repeat_text=null;
    private EditText text=null;

    private TimePicker timePicker=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        setContentView(R.layout.addclock_layout);

        sure=(Button)findViewById(R.id.sure_btn);
        cancal=(Button)findViewById(R.id.cancel_btn);
        repeat=(Button)findViewById(R.id.repeat_btn);
        repeat_text=(TextView)findViewById(R.id.repeat_text);
        text=(EditText)findViewById(R.id.text);

        timePicker=(TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);


        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mCalendar=Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                int mHour=timePicker.getCurrentHour();
                int mMinute=timePicker.getCurrentMinute();
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                int sign=60*mHour+mMinute;

                while (repeat_text.getText().equals("")){
                    repeat_text.setText("只响一次");
                }

                //设置闹钟
                if(repeat_text.getText().equals("只响一次")){

                    Intent intent = new Intent(AddclockActivity.this,ClockReceiver.class);
                    intent.putExtra("msg",text.getText().toString());
                    pi = PendingIntent.getBroadcast(AddclockActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    if(mCalendar.getTimeInMillis()<System.currentTimeMillis()){
                        alarmManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis()+24*60*60*1000,pi);
                    }else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),pi);
                    }
                }

                if(repeat_text.getText().equals("每天")){

                    Intent intent = new Intent(AddclockActivity.this,ClockReceiver.class);
                    intent.putExtra("interval",1000*24*60*60);
                    intent.putExtra("msg",text.getText().toString());
                    pi = PendingIntent.getBroadcast(AddclockActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    intent.putExtra("sign",sign);
                    if(mCalendar.getTimeInMillis()<System.currentTimeMillis()){
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis()+24*60*60*1000,1000*24*60*60,pi);
                    }else {
                        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),1000*24*60*60,pi);
                    }
                }

                //保存闹钟到sqlite
                Clockinfo clockinfo=new Clockinfo();
                clockinfo.setSign(sign);
                clockinfo.setHour(mHour);
                clockinfo.setMinute(mMinute);
                clockinfo.setOn_off("on");
                clockinfo.setRepead(repeat_text.getText().toString());
                clockinfo.setText(text.getText().toString());

                clockinfo.save();

                Intent intent=new Intent(AddclockActivity.this,ClockActivity.class);
                startActivity(intent);
                finish();

            }
        });

        cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddclockActivity.this,ClockActivity.class);
                startActivity(intent);
                finish();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String items[] = {"只响一次", "每天"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddclockActivity.this);
                builder.setTitle("重复选择");
                builder.setIcon(R.mipmap.ic_launcher);
                // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]){
                            case "只响一次":
                                repeat_text.setText("只响一次");
                                break;
                            case "每天":
                                repeat_text.setText("每天");
                                break;
                                default:break;
                        }
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }
}

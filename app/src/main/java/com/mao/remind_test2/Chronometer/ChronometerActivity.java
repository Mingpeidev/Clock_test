package com.mao.remind_test2.Chronometer;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mao.remind_test2.Clock.RingActivity;
import com.mao.remind_test2.R;


/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class ChronometerActivity extends AppCompatActivity {
    private TextView timevw=null;
    private Button settime=null;
    private Button start=null;
    private Button stop=null;
    private TextView returntime=null;

    private long timer_unit = 1000;
    private long total;
    private long timer_couting;

    private ChronometerService chronometerService;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    timevw.setText(formateTimer(chronometerService.getCountingTime()));
                    if (chronometerService.getCountingTime()==total&&timevw.getText().equals(formateTimer(total))&&start.getText().toString().equals("暂停"))
                    {
                        returntime.setText("时间到");
                        start.setText("开始");
                        Intent intent=new Intent(ChronometerActivity.this, RingActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    private class MyCountDownLisener implements ChronometerListener {

        @Override
        public void onChange() {
            mHandler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        setContentView(R.layout.chronometer_layout);

        timevw=(TextView)findViewById(R.id.tv_time);
        settime=(Button)findViewById(R.id.btn_settime);
        start=(Button)findViewById(R.id.btn_start);
        stop=(Button)findViewById(R.id.btn_stop);
        returntime=(TextView)findViewById(R.id.returntime);


        timevw.setText(formateTimer(timer_couting));

        chronometerService = ChronometerService.getInstance(new MyCountDownLisener(),total);
        initServiceCountDownTimerStatus();


        settime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(ChronometerActivity.this,android.R.style.Theme_Holo_Light_Panel, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofday, int minute) {
                     total=(long) (hourofday*60+minute)*60000;
                     chronometerService = ChronometerService.getInstance(new MyCountDownLisener(),total);
                     initServiceCountDownTimerStatus();
                     start.setText("开始");
                     if (total>0){
                         returntime.setText("时间设置成功");
                     }else {
                         returntime.setText("");
                     }
                     chronometerService.stopSet();//设置时停止上一个计时
                    }
                },0,0,true).show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (chronometerService.getTimerStatus()){
                    case ChronometerUtil.PREPARE:
                        if (chronometerService.getCountingTime()==0){
                            Toast.makeText(ChronometerActivity.this,"未设置时间！",Toast.LENGTH_SHORT).show();
                        }else {
                            chronometerService.startCountDown();
                            start.setText("暂停");
                            returntime.setText("正在计时。。。");
                        }
                        break;
                    case ChronometerUtil.START:
                        chronometerService.pauseCountDown();
                        start.setText("继续");
                        returntime.setText("暂停计时");
                        break;
                    case ChronometerUtil.PASUSE:
                        chronometerService.startCountDown();
                        start.setText("暂停");
                        returntime.setText("正在计时。。。");
                        break;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setText("开始");
                chronometerService.stopCountDown();//停止计时并归零
                returntime.setText("");
                Toast.makeText(ChronometerActivity.this,"停止计时",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String formateTimer(long time){
        String str = "00:00:00";
        int hour = 0;
        if(time>=1000*3600){
            hour = (int)(time/(1000*3600));
            time -= hour*1000*3600;
        }
        int minute = 0;
        if(time>=1000*60){
            minute = (int)(time/(1000*60));
            time -= minute*1000*60;
        }
        int sec = (int)(time/1000);
        str = formateNumber(hour)+":"+formateNumber(minute)+":"+formateNumber(sec);
        return str;
    }

    private String formateNumber(int time){
        return String.format("%02d", time);
    }

    private void initServiceCountDownTimerStatus(){
        switch (chronometerService.getTimerStatus()) {
            case ChronometerUtil.PREPARE:
                start.setText("开始");
                break;
            case ChronometerUtil.START:
                start.setText("暂停");
                break;
            case ChronometerUtil.PASUSE:
                start.setText("继续");
                break;
        }
        timevw.setText(formateTimer(chronometerService.getCountingTime()));
    }
}
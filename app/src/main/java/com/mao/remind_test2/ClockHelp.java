package com.mao.remind_test2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mingpeidev on 2018/8/6.
 */

public class ClockHelp extends AppCompatActivity{
    private AlarmManager alarmManager;
    private PendingIntent pi;


    public void Setclock(){
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
    }

}

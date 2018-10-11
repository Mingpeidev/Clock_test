package com.mao.remind_test2.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class ClockReceiver extends BroadcastReceiver {
    private PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        int inteval=intent.getIntExtra("interval",0);
        int sign=intent.getIntExtra("sign",0);
        if(inteval!=0){
            pi = PendingIntent.getBroadcast(context, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+inteval,inteval,pi);
        }
        intent.setClass(context, RingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

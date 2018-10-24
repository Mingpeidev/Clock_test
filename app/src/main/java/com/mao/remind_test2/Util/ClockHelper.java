package com.mao.remind_test2.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mao.remind_test2.Clock.ClockReceiver;

/**
 * Created by Mingpeidev on 2018/8/6.
 */

public class ClockHelper {

    private Context context;
    private AlarmManager alarmManager;

    public ClockHelper(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void openClock(int sign, String repeat_text, String text, String ringUrl, long time) {
        //设置闹钟
        if (repeat_text.equals("只响一次")) {

            Intent intent = new Intent(context, ClockReceiver.class);
            intent.putExtra("msg", text);
            intent.putExtra("ringUrl", ringUrl);
            PendingIntent pi = PendingIntent.getBroadcast(context, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (time < System.currentTimeMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time + 24 * 60 * 60 * 1000, pi);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
            }
        }

        if (repeat_text.equals("每天")) {

            Intent intent = new Intent(context, ClockReceiver.class);
            intent.putExtra("interval", 1000 * 24 * 60 * 60);
            intent.putExtra("msg", text);
            intent.putExtra("ringUrl", ringUrl);
            PendingIntent pi = PendingIntent.getBroadcast(context, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            intent.putExtra("sign", sign);
            if (time < System.currentTimeMillis()) {
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time + 24 * 60 * 60 * 1000, 1000 * 24 * 60 * 60, pi);
            } else {
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time, 1000 * 24 * 60 * 60, pi);
            }
        }
    }

    public void closeClock(int sign) {
        Intent intent = new Intent(context, ClockReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, sign, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

}

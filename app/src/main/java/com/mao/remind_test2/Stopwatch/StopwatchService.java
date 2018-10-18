package com.mao.remind_test2.Stopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mingpeidev on 2018/10/18.
 */

public class StopwatchService extends Service {

    private static final long timer_unit = 100;
    private Timer timer;
    private MyTimerTask timerTask;

    private static long timer_couting = 0;

    private int timerStatus = StopwatchUtil.PREPARE;

    public static StopwatchService stopwatchService;

    private static StopwachListener mstopwachListener;

    public static StopwatchService getInstance(StopwachListener stopwachListener) {
        if (stopwatchService == null) {
            stopwatchService = new StopwatchService();
        }
        setCountDownTimerListener(stopwachListener);
        return stopwatchService;
    }

    public static void setCountDownTimerListener(StopwachListener stopwachListener) {
        mstopwachListener = stopwachListener;
    }

    public void startCountDown() {
        startTimer();
        timerStatus = StopwatchUtil.START;
    }

    public void pauseCountDown() {
        timer.cancel();
        timerStatus = StopwatchUtil.PASUSE;
    }

    public void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer_couting = 0;
            timerStatus = StopwatchUtil.PREPARE;
            mstopwachListener.onChange();
        }
    }

    public long getCountingTime() {
        return timer_couting;
    }

    public int getTimerStatus() {
        return timerStatus;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timer_couting += timer_unit;
            mstopwachListener.onChange();
        }
    }
}

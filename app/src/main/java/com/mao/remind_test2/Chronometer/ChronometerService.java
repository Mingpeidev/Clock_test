package com.mao.remind_test2.Chronometer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mingpeidev on 2018/7/2.
 */

public class ChronometerService extends Service{
    private static final long timer_unit =1000;
    private static long mDistination_total;
    private Timer timer;
    private MyTimerTask timerTask;

    private static long timer_couting = 0;
    private long time_return;

    private int timerStatus = ChronometerUtil.PREPARE;

    public static ChronometerService chronometerService;

    private static ChronometerListener mChronometerListener;

    public static ChronometerService getInstance(ChronometerListener chronometerListener, long distination_total){
        if(chronometerService ==null){
            chronometerService = new ChronometerService();
        }
        setCountDownTimerListener(chronometerListener);
        mDistination_total = distination_total;
        if(timer_couting==0) {
            timer_couting = mDistination_total;
        }
        return chronometerService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public long getCountingTime(){
        return timer_couting;
    }

    public int getTimerStatus(){
        return  timerStatus;
    }


    public void startCountDown(){
        startTimer();
        timerStatus = ChronometerUtil.START;
    }

    public void pauseCountDown(){
        timer.cancel();
        timerStatus = ChronometerUtil.PASUSE;
    }

    public void stopSet(){
        if(timer!=null){
            timer.cancel();
            initTimerStatus();
            mChronometerListener.onChange();
        }
    }
    public void stopCountDown(){
        if(timer!=null){
            timer.cancel();
            timer_couting = 0;
            timerStatus = ChronometerUtil.PREPARE;
            mChronometerListener.onChange();
        }
    }

    public static void  setCountDownTimerListener(ChronometerListener chronometerListener){
        mChronometerListener = chronometerListener;
    }


    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timer_couting -=timer_unit;
            Log.d("timmer", timer_couting + "");
            mChronometerListener.onChange();
            if(timer_couting==0){
                cancel();
                initTimerStatus();
            }
        }
    }

    private void initTimerStatus(){
        timer_couting = mDistination_total;
        timerStatus = ChronometerUtil.PREPARE;
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }

}

package com.mao.remind_test2.Chronometer;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mao.remind_test2.Clock.RingActivity;
import com.mao.remind_test2.R;

/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class ChronometerFragment extends Fragment {
    private TextView timevw = null;
    private Button settime = null;
    private Button start = null;
    private Button stop = null;
    private TextView returntime = null;

    private long total;

    private ChronometerService chronometerService;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    timevw.setText(formateTimer(chronometerService.getCountingTime()));
                    if (chronometerService.getCountingTime() == total && timevw.getText().equals(formateTimer(total)) && start.getText().toString().equals("暂停")) {
                        returntime.setText("时间到");
                        start.setText("开始");
                        Intent intent = new Intent(getActivity(), RingActivity.class);
                        intent.putExtra("msg", "时间到");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chronometer_layout, null);

        timevw = view.findViewById(R.id.tv_time);
        settime = view.findViewById(R.id.btn_settime);
        start = view.findViewById(R.id.btn_start);
        stop = view.findViewById(R.id.btn_stop);
        returntime = view.findViewById(R.id.returntime);

        chronometerService = ChronometerService.getInstance(new MyCountDownLisener(), total);
        initServiceCountDownTimerStatus();

        settime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Panel, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofday, int minute) {
                        total = (long) (hourofday * 60 + minute) * 60000;
                        chronometerService = ChronometerService.getInstance(new MyCountDownLisener(), total);
                        initServiceCountDownTimerStatus();
                        start.setText("开始");
                        if (total > 0) {
                            returntime.setText("时间设置成功");
                        } else {
                            returntime.setText("");
                        }
                        chronometerService.stopSet();//设置时停止上一个计时
                    }
                }, 0, 0, true).show();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (chronometerService.getTimerStatus()) {
                    case ChronometerUtil.PREPARE:
                        if (chronometerService.getCountingTime() == 0) {
                            Toast.makeText(getActivity(), "未设置时间！", Toast.LENGTH_SHORT).show();
                        } else {
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
                Toast.makeText(getActivity(), "停止计时", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String formateTimer(long time) {
        String str = "00:00:00";
        int hour = 0;
        if (time >= 1000 * 3600) {
            hour = (int) (time / (1000 * 3600));
            time -= hour * 1000 * 3600;
        }
        int minute = 0;
        if (time >= 1000 * 60) {
            minute = (int) (time / (1000 * 60));
            time -= minute * 1000 * 60;
        }
        int sec = (int) (time / 1000);
        str = formateNumber(hour) + ":" + formateNumber(minute) + ":" + formateNumber(sec);
        return str;
    }

    private String formateNumber(int time) {
        return String.format("%02d", time);
    }

    private void initServiceCountDownTimerStatus() {
        switch (chronometerService.getTimerStatus()) {
            case ChronometerUtil.PREPARE:
                start.setText("开始");
                break;
            case ChronometerUtil.START:
                start.setText("暂停");
                returntime.setText("正在计时。。。");
                break;
            case ChronometerUtil.PASUSE:
                start.setText("继续");
                returntime.setText("暂停计时");
                break;
        }
        timevw.setText(formateTimer(chronometerService.getCountingTime()));
    }
}
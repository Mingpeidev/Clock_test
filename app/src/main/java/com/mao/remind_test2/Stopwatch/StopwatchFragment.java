package com.mao.remind_test2.Stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mao.remind_test2.R;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class StopwatchFragment extends Fragment {

    private List<Stopwatchinfo> stopwatchinfoList = new ArrayList<>();

    private TextView tvtime = null;
    private Button start = null;
    private Button stop = null;
    private RecyclerView stopwatch_recycle = null;

    private long total = 0;

    public StopwatchService stopwatchService;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (stopwatchService.getCountingTime() == total) {
                        start.setText("开始");
                    }
                    tvtime.setText(formateTimer(stopwatchService.getCountingTime()));
                    break;
            }
        }
    };

    private class MyCountDownLisener implements StopwachListener {

        @Override
        public void onChange() {
            mHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stopwatch_layout, null);
        tvtime = view.findViewById(R.id.tv_time1);
        start = view.findViewById(R.id.btn_start1);
        stop = view.findViewById(R.id.btn_stop1);

        stopwatch_recycle = view.findViewById(R.id.stopwatch_result);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        stopwatch_recycle.setLayoutManager(linearLayoutManager);
        final StopwatchAdapter stopwatchAdapter = new StopwatchAdapter(stopwatchinfoList);
        stopwatch_recycle.setAdapter(stopwatchAdapter);
        stopwatch_recycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        LitePal.getDatabase();

        stopwatchinfoList.clear();
        List<Stopwatchinfo> stopwatchinfos = DataSupport.order("id desc").find(Stopwatchinfo.class);
        for (Stopwatchinfo stopwatchinfo1 : stopwatchinfos) {
            String time = stopwatchinfo1.getTime();
            Stopwatchinfo stopwatchinfo2 = new Stopwatchinfo(time);
            stopwatchinfoList.add(stopwatchinfo2);
        }
        stopwatchAdapter.refreshData(stopwatchinfoList);

        stopwatchService = StopwatchService.getInstance(new MyCountDownLisener());
        initServiceCountDownTimerStatus();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (stopwatchService.getTimerStatus()) {
                    case StopwatchUtil.PREPARE:
                        stopwatchService.startCountDown();
                        start.setText("暂停");
                        stop.setText("计次");
                        break;
                    case StopwatchUtil.START:
                        stopwatchService.pauseCountDown();
                        start.setText("继续");
                        stop.setText("重置");
                        break;
                    case StopwatchUtil.PASUSE:
                        stopwatchService.startCountDown();
                        start.setText("暂停");
                        stop.setText("计次");
                        break;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stop.getText().equals("重置")) {
                    stopwatchService.stopCountDown();

                    stopwatchinfoList.clear();
                    DataSupport.deleteAll(Stopwatchinfo.class);
                    stopwatchAdapter.refreshData(stopwatchinfoList);
                } else {
                    Stopwatchinfo stopwatchinfo = new Stopwatchinfo();
                    stopwatchinfo.setTime(tvtime.getText().toString());
                    stopwatchinfo.save();

                    stopwatchinfoList.clear();
                    List<Stopwatchinfo> stopwatchinfos = DataSupport.order("id desc").find(Stopwatchinfo.class);
                    for (Stopwatchinfo stopwatchinfo1 : stopwatchinfos) {
                        String time = stopwatchinfo1.getTime();
                        Stopwatchinfo stopwatchinfo2 = new Stopwatchinfo(time);
                        stopwatchinfoList.add(stopwatchinfo2);
                    }
                    stopwatchAdapter.refreshData(stopwatchinfoList);
                }
            }
        });

    }

    private void initServiceCountDownTimerStatus() {
        switch (stopwatchService.getTimerStatus()) {
            case StopwatchUtil.PREPARE:
                start.setText("开始");
                break;
            case StopwatchUtil.START:
                start.setText("暂停");
                stop.setText("计次");
                break;
            case StopwatchUtil.PASUSE:
                start.setText("继续");
                stop.setText("重置");
                break;
        }
        tvtime.setText(formateTimer(stopwatchService.getCountingTime()));
    }

    private String formateTimer(long time) {
        String str = "00:00:00:00";
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

        int msec = (int) (time % 1000 / 100);
        str = formateNumber2(hour) + ":" + formateNumber2(minute) + ":" + formateNumber2(sec) + ":" + formateNumber1(msec);
        return str;
    }

    private String formateNumber2(int time) {
        return String.format("%02d", time);
    }

    private String formateNumber1(int time) {
        return String.format("%1d", time);
    }
}

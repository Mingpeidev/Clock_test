package com.mao.remind_test2.Clock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mao.remind_test2.R;
import com.mao.remind_test2.Util.ClockHelper;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class ClockFragment extends Fragment {

    private ClockHelper clockHelper;

    private List<Clockinfo> clockinfoList = new ArrayList<>();

    private ImageButton addBtn = null;
    private RecyclerView clock_recycle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clock_layout, null);
        addBtn = view.findViewById(R.id.addclock);
        clock_recycle = view.findViewById(R.id.clock_recycleview);

        clockHelper = new ClockHelper(getActivity());

        return view;
    }

    @Override
    public void onResume() {//在此生命周期刷新界面
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());//瀑布流
        clock_recycle.setLayoutManager(linearLayoutManager);

        final ClockAdapter clockAdapter = new ClockAdapter(clockinfoList);//绑定适配器
        clock_recycle.setAdapter(clockAdapter);

        clock_recycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//添加分割线

        LitePal.getDatabase();

        //显示所有闹钟
        clockinfoList.clear();
        final List<Clockinfo> clockinfos = DataSupport.order("hour asc,minute asc").find(Clockinfo.class);
        for (Clockinfo clockinfo : clockinfos) {
            int id = clockinfo.getId();
            int hour = clockinfo.getHour();
            int minute = clockinfo.getMinute();
            String repead = clockinfo.getRepead();
            String info = clockinfo.getText();
            String on_off = clockinfo.getOn_off();
            String time = hour + ":" + minute;
            Clockinfo clockinfo1 = new Clockinfo(id, time, repead, info, on_off);
            clockinfoList.add(clockinfo1);
        }

        clockAdapter.setOnCheckedChangeListener(new ClockAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b, int position) {
                if (!compoundButton.isPressed()) {
                    return;
                }//非点击开关不使用此监听
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());

                int firstVisibleItems;//第一个可见item
                firstVisibleItems = ((LinearLayoutManager) clock_recycle.getLayoutManager()).findFirstVisibleItemPosition();

                View view1 = clock_recycle.getChildAt(position - firstVisibleItems);
                TextView id1 = view1.findViewById(R.id.clockid);
                String id2 = id1.getText().toString();
                String on_off = "";
                int sign;
                int mHour;
                int mMinute;
                String mText;
                String ringUrl;
                List<Clockinfo> clockinfos1 = DataSupport.where("id =?", id2).find(Clockinfo.class);
                for (Clockinfo clockinfo : clockinfos1) {
                    sign = clockinfo.getSign();
                    mHour = clockinfo.getHour();
                    mMinute = clockinfo.getMinute();
                    String mRepead = clockinfo.getRepead();
                    ringUrl = clockinfo.getRing();
                    mText = clockinfo.getText();

                    if (ringUrl.equals("布谷鸟")) {
                        ringUrl = "buguniao";
                    } else if (ringUrl.equals("滴滴")) {
                        ringUrl = "didi";
                    } else if (ringUrl.equals("嘟嘟")) {
                        ringUrl = "dudu";
                    } else if (ringUrl.equals("闹铃")) {
                        ringUrl = "naozhong";
                    }

                    mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                    mCalendar.set(Calendar.MINUTE, mMinute);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MILLISECOND, 0);

                    clockinfoList.clear();

                    if (b) {
                        //打开闹钟
                        clockHelper.openClock(sign, mRepead, mText, ringUrl, mCalendar.getTimeInMillis());
                        on_off = "on";
                    } else {
                        clockHelper.closeClock(sign);
                        on_off = "off";
                    }
                }
                Clockinfo clockinfo = new Clockinfo();
                clockinfo.setOn_off(on_off);
                clockinfo.updateAll("id=?", id2);
                Log.d("haha", "onCheckedChanged: " + on_off + position);

                List<Clockinfo> clockinfos = DataSupport.order("hour asc,minute asc").find(Clockinfo.class);
                for (Clockinfo clockinfo1 : clockinfos) {
                    int id = clockinfo1.getId();
                    int hour = clockinfo1.getHour();
                    int minute = clockinfo1.getMinute();
                    String repead = clockinfo1.getRepead();
                    String info = clockinfo1.getText();
                    String of = clockinfo1.getOn_off();
                    String time = hour + ":" + minute;
                    Clockinfo clockinfo2 = new Clockinfo(id, time, repead, info, of);
                    clockinfoList.add(clockinfo2);
                }
                clockAdapter.refreshData(clockinfoList);
            }
        });


        clockAdapter.setOnItemClickListener(new ClockAdapter.OnItemClickListener() {//点击修改闹钟
            @Override
            public void onItemClick(View view, int position) {//点击修改

                int firstVisibleItems;//第一个可见item
                firstVisibleItems = ((LinearLayoutManager) clock_recycle.getLayoutManager()).findFirstVisibleItemPosition();

                View view1 = clock_recycle.getChildAt(position - firstVisibleItems);
                TextView id1 = view1.findViewById(R.id.clockid);
                String id2 = id1.getText().toString();

                Intent intent = new Intent(getActivity(), ModifyclockActivity.class);
                intent.putExtra("clockidput", id2);
                startActivity(intent);

            }
        });
        clockAdapter.setOnItemLongClickListener(new ClockAdapter.OnItemLongClickListener() {//删除
            @Override
            public void onItemLongClick(View view, final int position) {//长按删除

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示")
                        .setIcon(R.drawable.welcome)
                        .setMessage("是否删除选中闹钟?")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        int firstVisibleItems;//第一个可见item
                                        firstVisibleItems = ((LinearLayoutManager) clock_recycle.getLayoutManager()).findFirstVisibleItemPosition();

                                        View view1 = clock_recycle.getChildAt(position - firstVisibleItems);
                                        TextView id1 = view1.findViewById(R.id.clockid);
                                        String id2 = id1.getText().toString();
                                        List<Clockinfo> clockinfos1 = DataSupport.where("id =?", id2).find(Clockinfo.class);
                                        for (Clockinfo clockinfo : clockinfos1) {
                                            int sign = clockinfo.getSign();
                                            clockHelper.closeClock(sign);
                                        }

                                        DataSupport.deleteAll(Clockinfo.class, "id=?", id2);

                                        clockinfoList.remove(position);
                                        clockAdapter.notifyItemRemoved(position);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();

            }
        });
        clock_recycle.setAdapter(clockAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddclockActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.mao.remind_test2.Clock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mao.remind_test2.Main.MainActivity;
import com.mao.remind_test2.R;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class ClockActivity extends AppCompatActivity{

    private List<Clockinfo> clockinfoList=new ArrayList<>();
    private AlarmManager alarmManager;
    private PendingIntent pi;

    private Button addBtn=null;
    private RecyclerView clock_recycle;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        setContentView(R.layout.clock_layout);

        addBtn=(Button)findViewById(R.id.addclock);
        back=(ImageButton)findViewById(R.id.backall);
        clock_recycle=(RecyclerView)findViewById(R.id.clock_recycleview);

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);//瀑布流
        clock_recycle.setLayoutManager(linearLayoutManager);

        final ClockAdapter clockAdapter=new ClockAdapter(clockinfoList);//绑定适配器
        clock_recycle.setAdapter(clockAdapter);

        clock_recycle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));//添加分割线

        LitePal.getDatabase();

        //显示所有闹钟
        final List<Clockinfo> clockinfos= DataSupport.order("hour asc,minute asc").find(Clockinfo.class);
        for (Clockinfo clockinfo:clockinfos){
            int id=clockinfo.getId();
            int hour=clockinfo.getHour();
            int minute=clockinfo.getMinute();
            String repead=clockinfo.getRepead();
            String info=clockinfo.getText();
            String on_off=clockinfo.getOn_off();
            String time=hour+":"+minute;
            Clockinfo clockinfo1=new Clockinfo(id,time,repead,info,on_off);
            clockinfoList.add(clockinfo1);
        }

        clockAdapter.setOnButtonClickListener(new ClockAdapter.OnButtonClickListener() {
            @Override
            public void onclick(View view, int position) {

                Calendar mCalendar=Calendar.getInstance();
                mCalendar.setTimeInMillis(System.currentTimeMillis());

                View view1=clock_recycle.getChildAt(position);
                TextView id1=view1.findViewById(R.id.clockid);
                String id2=id1.getText().toString();
                String on_off="";
                int sign;
                int mHour;
                int mMinute;
                String mText;
                List<Clockinfo> clockinfos1= DataSupport.where("id =?",id2).find(Clockinfo.class);
                for (Clockinfo clockinfo:clockinfos1){
                    on_off=clockinfo.getOn_off();
                    sign=clockinfo.getSign();
                    mHour=clockinfo.getHour();
                    mMinute=clockinfo.getMinute();
                    String mRepead=clockinfo.getRepead();
                    mText=clockinfo.getText();

                    mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                    mCalendar.set(Calendar.MINUTE, mMinute);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MILLISECOND, 0);

                    clockinfoList.clear();

                    if (on_off.equals("on")){
                        Intent intent=new Intent(ClockActivity.this,ClockReceiver.class);
                        pi = PendingIntent.getBroadcast(ClockActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pi);
                        on_off="off";
                    }else {
                        if (mRepead.equals("只响一次")){
                            Intent intent=new Intent(ClockActivity.this,ClockReceiver.class);
                            intent.putExtra("msg",mText);
                            pi = PendingIntent.getBroadcast(ClockActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                            if(mCalendar.getTimeInMillis()<System.currentTimeMillis()){
                                alarmManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis()+24*60*60*1000,pi);
                            }else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),pi);
                            }
                        }else {
                            Intent intent1 = new Intent(ClockActivity.this,ClockReceiver.class);
                            intent1.putExtra("msg",mText);
                            intent1.putExtra("interval",1000*24*60*60);
                            pi = PendingIntent.getBroadcast(ClockActivity.this, sign,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                            intent1.putExtra("sign",sign);
                            if(mCalendar.getTimeInMillis()<System.currentTimeMillis()){
                                alarmManager.setWindow(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis()+24*60*60*1000,1000*24*60*60,pi);
                            }else {
                                alarmManager.setWindow(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),1000*24*60*60,pi);
                            }
                        }
                        on_off="on";
                    }}
                Clockinfo clockinfo=new Clockinfo();
                clockinfo.setOn_off(on_off);
                clockinfo.updateAll("id=?",id2);

                List<Clockinfo> clockinfos= DataSupport.order("hour asc,minute asc").find(Clockinfo.class);
                for (Clockinfo clockinfo1:clockinfos){
                    int id=clockinfo1.getId();
                    int hour=clockinfo1.getHour();
                    int minute=clockinfo1.getMinute();
                    String repead=clockinfo1.getRepead();
                    String info=clockinfo1.getText();
                    String of=clockinfo1.getOn_off();
                    String time=hour+":"+minute;
                    Clockinfo clockinfo2=new Clockinfo(id,time,repead,info,of);
                    clockinfoList.add(clockinfo2);
                }
                clockAdapter.refreshData(clockinfoList);
            }
        });


        clockAdapter.setOnItemClickListener(new ClockAdapter.OnItemClickListener() {//点击修改闹钟
            @Override
            public void onItemClick(View view, int position) {//点击修改

                View view1=clock_recycle.getChildAt(position);
                TextView id1=view1.findViewById(R.id.clockid);
                String id2=id1.getText().toString();

                Intent intent=new Intent(ClockActivity.this,ModifyclockActivity.class);
                intent.putExtra("clockidput",id2);
                startActivity(intent);
                finish();

            } });
        clockAdapter.setOnItemLongClickListener(new ClockAdapter.OnItemLongClickListener() {//删除
            @Override
            public void onItemLongClick(View view, final int position) {//长按删除

                AlertDialog.Builder builder = new AlertDialog.Builder(ClockActivity.this);
                builder.setTitle("提示")
                        .setIcon(R.drawable.welcome)
                        .setMessage("是否删除选中闹钟?")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        View view1=clock_recycle.getChildAt(position);
                                        TextView id1=view1.findViewById(R.id.clockid);
                                        String id2=id1.getText().toString();
                                        List<Clockinfo> clockinfos1= DataSupport.where("id =?",id2).find(Clockinfo.class);
                                        for (Clockinfo clockinfo:clockinfos1){
                                            Intent intent=new Intent(ClockActivity.this,ClockReceiver.class);
                                            int sign=clockinfo.getSign();
                                            pi = PendingIntent.getBroadcast(ClockActivity.this, sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                            alarmManager.cancel(pi);
                                        }

                                        DataSupport.deleteAll(Clockinfo.class,"id=?",id2);

                                        clockinfoList.remove(position);
                                        clockAdapter.notifyItemRemoved(position);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();

            } });
        clock_recycle.setAdapter(clockAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ClockActivity.this,AddclockActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}

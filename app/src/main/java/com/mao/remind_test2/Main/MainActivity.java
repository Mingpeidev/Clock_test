package com.mao.remind_test2.Main;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.mao.remind_test2.Chronometer.ChronometerActivity;
import com.mao.remind_test2.Clock.ClockActivity;
import com.mao.remind_test2.Login.LoginActivity;
import com.mao.remind_test2.R;
import com.mao.remind_test2.Stopwatch.StopwatchActivity;
import com.mao.remind_test2.Today.TodayActivity;
import com.mao.remind_test2.Util.HttpUtil;
import com.mao.remind_test2.Util.JsonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String REQUEST_URL = "http://192.168.43.138:8080/test_server/servlet/UserManager?";
    private String nameurl="";
    private int loginnumurl=0;
    private String signtimeurl="";
    private String signtimenow=null;
    private TextView name=null;//mainpopu内文本框
    private TextView number=null;

    private ImageButton setBtn=null;

    TextView title;
    private LinearLayout clock_layout;
    private LinearLayout stopwatch_layout;
    private LinearLayout chronometer_layout;
    private LinearLayout today_layout;
    ViewPager mViewPager;
    MyFragmentAdapter myFragmentAdapter;
    FragmentManager mfragmentManager;

    String[] titleName = new String[] {"闹钟","秒表","计时器","日程"};
    List<Fragment> mfragmentList=new ArrayList<Fragment>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        mfragmentManager=getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        initFragmetList();
        myFragmentAdapter=new MyFragmentAdapter(mfragmentManager,mfragmentList);
        initView();
        initViewpager();


        SharedPreferences spf= getSharedPreferences("loginsuccess",MODE_PRIVATE);//取出已登陆用户信息
        nameurl=spf.getString("nameurl","");

        Calendar calendar=Calendar.getInstance();//获取签到时系统时间
        calendar.setTimeInMillis(System.currentTimeMillis());
        signtimenow= calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clock_layout:
                mViewPager.setCurrentItem(0);
                updateBottomLinearLayoutSelect(true,false,false,false);
                break;
            case R.id.stopwatch_layout:
                mViewPager.setCurrentItem(1);
                updateBottomLinearLayoutSelect(false,true,false,false);
                break;
            case R.id.chronometer_layout:
                mViewPager.setCurrentItem(2);
                updateBottomLinearLayoutSelect(false,false,true,false);
                break;
            case R.id.today_layout:
                mViewPager.setCurrentItem(3);
                updateBottomLinearLayoutSelect(false,false,false,true);
                break;
            case R.id.set:
                showPopupMenu(setBtn);
                break;
                default:break;
        }

    }
    public void initFragmetList() {
        Fragment clock = new ClockActivity();
        Fragment stopwatch = new StopwatchActivity();
        Fragment chronometer = new ChronometerActivity();
        Fragment today = new TodayActivity();
        mfragmentList.add(clock);
        mfragmentList.add(stopwatch);
        mfragmentList.add(chronometer);
        mfragmentList.add(today);
    }
    public void initView(){
        title=(TextView)findViewById(R.id.title_text);
        setBtn=(ImageButton)findViewById(R.id.set);
        setBtn.setOnClickListener(this);
        clock_layout=(LinearLayout)findViewById(R.id.clock_layout);
        clock_layout.setOnClickListener(this);
        stopwatch_layout=(LinearLayout)findViewById(R.id.stopwatch_layout);
        stopwatch_layout.setOnClickListener(this);
        chronometer_layout=(LinearLayout)findViewById(R.id.chronometer_layout);
        chronometer_layout.setOnClickListener(this);
        today_layout=(LinearLayout)findViewById(R.id.today_layout);
        today_layout.setOnClickListener(this);
        mViewPager=(ViewPager)findViewById(R.id.pager);
    }

    public void initViewpager(){
        mViewPager.addOnPageChangeListener(new ViewPagetOnPagerChangedLisenter());
        mViewPager.setAdapter(myFragmentAdapter);
        mViewPager.setCurrentItem(0);
        title.setText(titleName[0]);
        updateBottomLinearLayoutSelect(true,false,false,false);

    }

    private void updateBottomLinearLayoutSelect(boolean f, boolean s, boolean t,boolean r) {
        clock_layout.setSelected(f);
        stopwatch_layout.setSelected(s);
        chronometer_layout.setSelected(t);
        today_layout.setSelected(r);
    }

    class ViewPagetOnPagerChangedLisenter implements ViewPager.OnPageChangeListener {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }
         @Override
         public void onPageSelected(int position) {
             boolean[] state=new boolean[titleName.length];
             state[position]=true;
             title.setText(titleName[position]);
             updateBottomLinearLayoutSelect(state[0],state[1],state[2],state[3]);
         }
         @Override
         public void onPageScrollStateChanged(int state) {

         }
    }

    private void showPopupMenu(View view) {

        final PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.setting, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.quit:
                        SharedPreferences spf= getSharedPreferences("loginsuccess",MODE_PRIVATE);//取出已登陆用户信息
                        SharedPreferences.Editor editor=spf.edit();
                        editor.putString("nameurl","");//清空登陆数据
                        editor.apply();

                        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.sign:
                        final String params="request_flag=sign&nameurl="+nameurl+"&loginnumurl="+loginnumurl+"&signtimeurl="+signtimeurl;

                        final View view1=View.inflate(MainActivity.this,R.layout.mainpopu,null);
                        final PopupWindow popupWindow=new PopupWindow(view1, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        popupWindow.showAtLocation(view1, Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);

                        name=view1.findViewById(R.id.name);
                        number=view1.findViewById(R.id.number);

                        if (nameurl!=""){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //得到json字符串
                                    String resultJson = HttpUtil.sendGet(REQUEST_URL,params);
                                    //得到返回值
                                    int res= JsonUtil.getResult("signnumber", resultJson);
                                    try {
                                        JSONObject jsonObject=new JSONObject(resultJson);
                                        String restime=jsonObject.getString("signtime");
                                        signtimeurl=restime.trim();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    loginnumurl=res;
                                    if (loginnumurl<0){
                                        handler.sendEmptyMessage(2);
                                    }else {
                                        if (signtimeurl.equals(signtimenow)){
                                            handler.sendEmptyMessage(0);
                                        }else {
                                            loginnumurl++;
                                            signtimeurl=signtimenow;
                                            handler.sendEmptyMessage(1);
                                        }
                                    }
                                }
                            }).start();
                        }else {
                            name.setText("未登录！不可签到！");
                        }

                        view1.findViewById(R.id.signsure).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow.dismiss();
                            }
                        });
                        break;
                    default:break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    @Override//按返回按钮退出确定
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setIcon(R.drawable.welcome)
                    .setMessage("是否退出?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            });
            builder.create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    name.setText(nameurl+"已签到"+loginnumurl+"天！");
                    break;
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String params="request_flag=sign&nameurl="+nameurl+"&loginnumurl="+loginnumurl+"&signtimeurl="+signtimeurl;
                            HttpUtil.sendGet(REQUEST_URL,params);
                        }
                    }).start();
                    name.setText("恭喜"+nameurl+"签到成功！");
                    number.setText("你已成功签到"+loginnumurl+"天");
                    break;
                case 2:
                    name.setText("签到失败!连接失败！");
                    break;
                default:break;
            }
        };
    };

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);//通知栏
        //获取通知栏
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentTitle("寸金")
                .setContentText("寸金寸光阴")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.welcome)
                .setOngoing(true)//无法滑动删除，常驻通知栏
                .setContentIntent(pendingIntent);//点击事件
        notificationManager.notify(1,builder.build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);//关闭通知栏
        notificationManager.cancel(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);//关闭通知栏
        notificationManager.cancel(1);
    }
}
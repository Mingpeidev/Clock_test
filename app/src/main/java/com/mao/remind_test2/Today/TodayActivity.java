package com.mao.remind_test2.Today;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mao.remind_test2.Clock.ClockReceiver;
import com.mao.remind_test2.R;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/22.
 */

public class TodayActivity extends Fragment {

    private AlarmManager alarmManager;
    private PendingIntent pi;
    private int sign;

    private List<Todayinfo> todayinfoList=new ArrayList<>();

    private ImageButton add=null;
    private ImageButton search=null;
    private EditText inputsubject=null;
    private Button inputdate;

    private RecyclerView result = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.today_layout, null);
        add=view.findViewById(R.id.addtoday);
        search=view.findViewById(R.id.searchtoday);
        inputsubject=view.findViewById(R.id.inputsubject);
        inputdate=view.findViewById(R.id.inputdate);

        result = view.findViewById(R.id.today_result);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        result.setLayoutManager(linearLayoutManager);
        final TodayAdapter todayAdapter=new TodayAdapter(todayinfoList);
        result.setAdapter(todayAdapter);
        result.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        LitePal.getDatabase();

        alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

        //显示所有日程
        final List<Todayinfo> todayinfos= DataSupport.findAll(Todayinfo.class);
        for (Todayinfo todayinfo:todayinfos){
            int id=todayinfo.getId();
            String subject=todayinfo.getSubject();
            String body=todayinfo.getBody();
            String date=todayinfo.getDate();
            Todayinfo todayinfo1=new Todayinfo(id,subject,body,date);
            todayinfoList.add(todayinfo1);
        }

        todayAdapter.setOnItemClickListener(new TodayAdapter.OnItemClickListener() {//修改
            @Override
            public void onItemClick(View view, int position) {

                View view1=result.getChildAt(position);
                TextView id1=view1.findViewById(R.id.num);
                String id2=id1.getText().toString();

                Intent intent=new Intent(getActivity(),ModifytodayActivity.class);
                intent.putExtra("idput",id2);
                startActivity(intent);

            } });
        todayAdapter.setOnItemLongClickListener(new TodayAdapter.OnItemLongClickListener() {//删除
            @Override
            public void onItemLongClick(View view,final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示")
                        .setIcon(R.drawable.welcome)
                        .setMessage("是否删除选中行?")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {

                                        View view1=result.getChildAt(position);
                                        TextView id1=view1.findViewById(R.id.num);
                                        String id2=id1.getText().toString();

                                        List<Todayinfo> todayinfos= DataSupport.where("id =?",id2).find(Todayinfo.class);
                                        for (Todayinfo todayinfo:todayinfos){
                                            sign=todayinfo.getSign();
                                            Intent intent=new Intent(getActivity(),ClockReceiver.class);
                                            pi = PendingIntent.getBroadcast(getActivity(), sign,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                            alarmManager.cancel(pi);
                                        }

                                        DataSupport.deleteAll(Todayinfo.class,"id=?",id2);

                                        todayinfoList.remove(position);
                                        todayAdapter.notifyItemRemoved(position);
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
        result.setAdapter(todayAdapter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AddtodayActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询
                String inputSubject = inputsubject.getText().toString();

                todayinfoList.clear();
                List<Todayinfo> todayinfos= DataSupport.where("subject like ?","%"+inputSubject+"%").find(Todayinfo.class);
                for (Todayinfo todayinfo:todayinfos){
                    int id=todayinfo.getId();
                    String subject=todayinfo.getSubject();
                    String body=todayinfo.getBody();
                    String date=todayinfo.getDate();
                    Todayinfo todayinfo1=new Todayinfo(id,subject,body,date);
                    todayinfoList.add(todayinfo1);
                }
                todayAdapter.refreshData(todayinfoList);
            }
        });
        inputdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
                                todayinfoList.clear();
                                String inputDate=year + "-" + (month+1) + "-" + dayofmonth;
                                List<Todayinfo> todayinfos= DataSupport.where("date like ?","%"+inputDate+"%").find(Todayinfo.class);
                                for (Todayinfo todayinfo:todayinfos){
                                    int id=todayinfo.getId();
                                    String subject=todayinfo.getSubject();
                                    String body=todayinfo.getBody();
                                    String date=todayinfo.getDate();
                                    Todayinfo todayinfo1=new Todayinfo(id,subject,body,date);
                                    todayinfoList.add(todayinfo1);
                                }
                                todayAdapter.refreshData(todayinfoList);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return view;
    }

}

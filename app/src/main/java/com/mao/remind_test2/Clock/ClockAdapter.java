package com.mao.remind_test2.Clock;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.mao.remind_test2.R;

import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/29.
 */

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder>{
    private List<Clockinfo> mClockinfo;

    public ClockAdapter(List<Clockinfo> clockinfoList) {
        mClockinfo = clockinfoList;
    }

    private ClockAdapter.OnItemClickListener mOnItemClickListener;
    private ClockAdapter.OnItemLongClickListener mOnItemLongClickListener;
    private ClockAdapter.OnCheckedChangeListener mOnCheckedChangeListener;

    public void setOnItemClickListener(ClockAdapter.OnItemClickListener mOnItemClickListener){//按钮点击方法
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public void setOnItemLongClickListener(ClockAdapter.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public void setOnCheckedChangeListener (ClockAdapter.OnCheckedChangeListener mOnCheckedChangeListener){
        this.mOnCheckedChangeListener=mOnCheckedChangeListener;
    }

    public interface OnItemClickListener{//接口
        void onItemClick(View view,int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    public interface OnCheckedChangeListener{
        void onCheckedChanged( CompoundButton compoundButton, boolean b,int position);
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView time;
        TextView repead;
        TextView info;
        Switch on_off;

       public ViewHolder(View itemView) {
            super(itemView);
            id=(TextView)itemView.findViewById(R.id.clockid);
            time = (TextView)itemView.findViewById(R.id.timeclock);
            repead = (TextView)itemView.findViewById(R.id.repead);
            info =(TextView)itemView.findViewById(R.id.info);
            on_off=(Switch) itemView.findViewById(R.id.on);
        }
    }

    @Override
    public ClockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_item, parent, false);
        ClockAdapter.ViewHolder viewHolder = new ClockAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ClockAdapter.ViewHolder holder, final int position) {

        Clockinfo clockinfo=mClockinfo.get(position);
        holder.id.setText(String.valueOf(clockinfo.getId()));
        holder.time.setText(clockinfo.getTime());
        holder.repead.setText(clockinfo.getRepead());
        holder.info.setText(clockinfo.getText());

        if (clockinfo.getOn_off().equals("off")){//初始化闹钟开关状态
            holder.on_off.setChecked(false);

        }else {
            holder.on_off.setChecked(true);
        }

        holder.on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mOnCheckedChangeListener!=null){
                    mOnCheckedChangeListener.onCheckedChanged(compoundButton,b,position);
                }
            }
        });


        if(mOnItemClickListener != null){ //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }
    }
    public void refreshData(List<Clockinfo> valueList) {
        this.mClockinfo = valueList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mClockinfo.size();
    }
}

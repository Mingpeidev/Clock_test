package com.mao.remind_test2.Today;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mao.remind_test2.R;

import java.util.List;

/**
 * Created by Mingpeidev on 2018/6/27.
 */

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.ViewHolder>{
    private List<Todayinfo> mTodayinfo;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView num;
        TextView messub;
        TextView mesbody;
        TextView mesdate;

        ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            messub =  itemView.findViewById(R.id.messub);
            mesbody = itemView.findViewById(R.id.mesbody);
            mesdate = itemView.findViewById(R.id.mesdate);
        }
    }

    public TodayAdapter(List<Todayinfo> todayinfoList) {
        mTodayinfo = todayinfoList;
    }


    @Override
    public TodayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Todayinfo todayinfo=mTodayinfo.get(position);
        holder.num.setText(String.valueOf(todayinfo.getId()));
        holder.messub.setText(todayinfo.getSubject());
        holder.mesbody.setText(todayinfo.getBody());
        holder.mesdate.setText(todayinfo.getDate());

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
            });}
    }
    public void refreshData(List<Todayinfo> valueList) {
        this.mTodayinfo = valueList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    @Override
    public int getItemCount() {
        return mTodayinfo.size();
    }
}

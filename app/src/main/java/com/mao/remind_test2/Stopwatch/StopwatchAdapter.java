package com.mao.remind_test2.Stopwatch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mao.remind_test2.R;

import java.util.List;

/**
 * Created by Mingpeidev on 2018/7/3.
 */

public class StopwatchAdapter extends RecyclerView.Adapter<StopwatchAdapter.ViewHolder> {

    private List<Stopwatchinfo> mStopwatchinfo;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stopwatchid;
        TextView stopwtchtime;

        ViewHolder(View itemView) {
            super(itemView);
            stopwatchid = itemView.findViewById(R.id.stopwatchid);
            stopwtchtime = itemView.findViewById(R.id.stopwatchtime);
        }
    }

    public StopwatchAdapter(List<Stopwatchinfo> stopwatchinfoList) {
        this.mStopwatchinfo = stopwatchinfoList;
    }


    @Override
    public StopwatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stopwatch_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Stopwatchinfo stopwatchinfo = mStopwatchinfo.get(position);

        holder.stopwatchid.setText(String.valueOf(getItemCount() - position));
        holder.stopwtchtime.setText(stopwatchinfo.getTime());

    }

    public void refreshData(List<Stopwatchinfo> valueList) {
        this.mStopwatchinfo = valueList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStopwatchinfo.size();
    }
}

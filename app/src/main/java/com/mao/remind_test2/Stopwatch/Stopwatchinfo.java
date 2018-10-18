package com.mao.remind_test2.Stopwatch;

import org.litepal.crud.DataSupport;

/**
 * Created by Mingpeidev on 2018/7/3.
 */

public class Stopwatchinfo extends DataSupport {
    private int id;
    private String time;

    public Stopwatchinfo() {
    }

    public Stopwatchinfo(String time) {
        this.time = time;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

}

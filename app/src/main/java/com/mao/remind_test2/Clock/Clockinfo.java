package com.mao.remind_test2.Clock;

import org.litepal.crud.DataSupport;

/**
 * Created by Mingpeidev on 2018/6/28.
 */

public class Clockinfo extends DataSupport {
    private int id;
    private int sign;
    private int hour;
    private int minute;
    private String time;
    private String repead;
    private String text;
    private String on_off;

    public Clockinfo() {
    }

    public Clockinfo(int id, String time, String repead, String text, String on_off) {
        this.id = id;
        this.time = time;
        this.repead = repead;
        this.text = text;
        this.on_off = on_off;
    }

    public Clockinfo(int hour, int minute, String repead, String text) {
        this.hour = hour;
        this.minute = minute;
        this.repead = repead;
        this.text = text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setRepead(String repead) {
        this.repead = repead;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public void setOn_off(String on_off) {
        this.on_off = on_off;
    }

    public int getId() {
        return id;
    }

    public String getRepead() {
        return repead;
    }

    public String getText() {
        return text;
    }

    public int getSign() {
        return sign;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getTime() {
        return time;
    }

    public String getOn_off() {
        return on_off;
    }
}

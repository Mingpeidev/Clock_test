package com.mao.remind_test2.Today;

import org.litepal.crud.DataSupport;

/**
 * Created by Mingpeidev on 2018/6/27.
 */

public class Todayinfo extends DataSupport{
    private int id;
    private String subject;
    private String body;
    private String date;
    private int sign;

    public Todayinfo(){}

    public Todayinfo(int id,String subject,String body,String date){
        this.id=id;
        this.subject=subject;
        this.body=body;
        this.date=date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public int getId() {
        return id;
    }

    public int getSign() {
        return sign;
    }
}

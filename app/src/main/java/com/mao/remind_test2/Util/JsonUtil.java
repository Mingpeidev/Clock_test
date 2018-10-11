package com.mao.remind_test2.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mingpeidev on 2018/6/25.
 */
//解析json数据
public class JsonUtil {
    public static int getResult(String key,String json){

        try {
            JSONObject jsonObject=new JSONObject(json);
            int result=jsonObject.getInt(key);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

package com.mao.remind_test2.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mingpeidev on 2018/6/25.
 */

public class HttpUtil {
    public HttpUtil(){

    }

    public static String sendGet(String request_url, String params){
        try {
            URL url = new URL(request_url + params);//转化类对象

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();//打开连接

            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            //设置连接
            connection.setDoInput(true);
            //200代表HTTP_OK
            if (connection.getResponseCode() == 200) {
                return inputStream2String(connection.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String inputStream2String(InputStream inputStream) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] bs = new byte[1024];
        //将服务端发过来的数据读取到bs中
        try {
            while ((len = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
            }
            return new String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

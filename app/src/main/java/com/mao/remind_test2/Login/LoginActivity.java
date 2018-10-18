package com.mao.remind_test2.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mao.remind_test2.Main.MainActivity;
import com.mao.remind_test2.R;
import com.mao.remind_test2.Util.HttpUtil;
import com.mao.remind_test2.Util.JsonUtil;


/**
 * Created by Mingpeidev on 2018/6/21.
 */

public class LoginActivity extends AppCompatActivity {
    public static final String REQUEST_URL = "http://192.168.43.138:8080/test_server/servlet/UserManager?";
    private String nameurl = "";
    private String passwordurl = "";

    private EditText username = null;
    private EditText psd = null;
    private Button loginBtn = null;
    private Button registerBtn = null;
    private CheckBox rememberpsd;
    private Button jump = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.login_layout);

        username = (EditText) findViewById(R.id.username);
        psd = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login);
        registerBtn = (Button) findViewById(R.id.register);
        rememberpsd = (CheckBox) findViewById(R.id.rememberpassword);
        jump = (Button) findViewById(R.id.jump);

        SharedPreferences spf = getSharedPreferences("loginremember", MODE_PRIVATE);//保存记住密码：用户名，密码，记住密码状态
        final SharedPreferences.Editor editor = spf.edit();
        boolean isRemember = spf.getBoolean("rememberpassword", false);
        if (isRemember) {
            //将账号和密码都设置到登陆界面文本中
            String account = spf.getString("username", "");
            String password = spf.getString("password", "");
            username.setText(account);
            psd.setText(password);
            rememberpsd.setChecked(true);
        }


        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameurl = username.getText().toString();
                passwordurl = psd.getText().toString();
                final String params = "request_flag=login&nameurl=" + nameurl + "&passwordurl=" + passwordurl;

                if (username.getText().toString().trim().equals("") || psd.getText().toString().trim().equals("") || psd.getText().length() < 6) {
                    Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //得到json字符串
                            String resultJson = HttpUtil.sendGet(REQUEST_URL, params);
                            //得到返回值
                            int res = JsonUtil.getResult("result", resultJson);
                            if (res == 1) {
                                handler.sendEmptyMessage(1);

                                SharedPreferences spf = getSharedPreferences("loginsuccess", MODE_PRIVATE);//保存用户名，签到用
                                SharedPreferences.Editor editor1 = spf.edit();
                                editor1.putString("nameurl", username.getText().toString().trim());
                                editor1.apply();

                                if (rememberpsd.isChecked()) {//记住密码，登陆用
                                    editor.putBoolean("rememberpassword", true);
                                    editor.putString("username", username.getText().toString().trim());
                                    editor.putString("password", psd.getText().toString().trim());
                                } else {
                                    editor.clear();
                                }
                                editor.apply();

                            } else {
                                handler.sendEmptyMessage(0);
                            }
                        }
                    }).start();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameurl = username.getText().toString();
                passwordurl = psd.getText().toString();
                final String params = "request_flag=register&nameurl=" + nameurl + "&passwordurl=" + passwordurl;

                if (username.getText().toString().trim().equals("") || psd.getText().toString().trim().equals("") || psd.getText().length() < 6) {
                    Toast.makeText(LoginActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //得到json字符串
                            String resultJson = HttpUtil.sendGet(REQUEST_URL, params);
                            //得到返回值
                            int res = JsonUtil.getResult("result", resultJson);
                            if (res == 1) {
                                handler1.sendEmptyMessage(1);
                            } else {
                                handler1.sendEmptyMessage(0);
                            }
                        }
                    }).start();
                }
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Intent intent = new Intent();
                    Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(LoginActivity.this, "注册失败,账号已存在！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        ;
    };
}

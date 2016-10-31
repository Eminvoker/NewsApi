package com.invok.newsapi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.invok.newsapi.framgent.NewsMainFramgentActivity;
import com.invok.newsapi.view.CircleImageView;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MainActivity extends AppCompatActivity {

    private Button loginBt,visitorBt;
    private TextView registTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //用Bmob把新闻存到云服务器
        Bmob.initialize(getApplicationContext(), "563a964bdf42a32f93cdd28755c0df8d");
        // 使用推送服务时的初始化操作
        //BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        //BmobPush.startWork(this);
    }

    private void initView() {
        loginBt = (Button) findViewById(R.id.login);
        visitorBt = (Button) findViewById(R.id.visitor);
        registTv = (TextView) findViewById(R.id.regist);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        visitorBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,ChannelListActivity.class));
                startActivity(new Intent(MainActivity.this,NewsMainFramgentActivity.class));
            }
        });

        registTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistActivity.class));
            }
        });

        initAppTitle();
    }

    public void initAppTitle(){
        CircleImageView topMe;
        ImageView topBack;
        topBack = (ImageView) findViewById(R.id.top_back);
        topMe = (CircleImageView) findViewById(R.id.top_me);
        topMe.setVisibility(View.INVISIBLE);
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("退出应用？？？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });
    }
}

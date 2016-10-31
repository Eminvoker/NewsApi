package com.invok.newsapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText idEt ,passwordEt;
    private Button loginBt;
    private CheckBox remember,autologin;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Bmob.initialize(LoginActivity.this, "563a964bdf42a32f93cdd28755c0df8d");
        initView();

        getSpContent();
    }

    private void getSpContent() {
        sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        boolean chooseRemember = sp.getBoolean("remember",false);
        if(chooseRemember){
            remember.setChecked(true);
            String userId = sp.getString("userId","");
            String password = sp.getString("password","");
            idEt.setText(userId);
            passwordEt.setText(password);
            boolean chooseAutoLogin = sp.getBoolean("autologin",false);
            if(chooseAutoLogin){
                autologin.setChecked(true);
                login();
            }
        }
    }

    private void setSpContent(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId",idEt.getText().toString());
        editor.putString("password",passwordEt.getText().toString());
        editor.putBoolean("remember",remember.isChecked());
        editor.putBoolean("autologin",autologin.isChecked());
        editor.commit();
    }

    private void initView() {
        idEt = (EditText) findViewById(R.id.userId_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        loginBt = (Button) findViewById(R.id.login_bt);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        remember = (CheckBox) findViewById(R.id.remember);
        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!remember.isChecked()){
                    autologin.setChecked(false);
                }
            }
        });
        autologin = (CheckBox) findViewById(R.id.autologin);
        autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!remember.isChecked()){
                    autologin.setChecked(false);
                    Toast.makeText(LoginActivity.this, "请先勾选“记住密码”", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initAppTitle();
    }

    private void login() {
        if(TextUtils.isEmpty(idEt.getText().toString())||TextUtils.isEmpty(passwordEt.getText().toString())){
            Toast.makeText(LoginActivity.this,"不能为空！",Toast.LENGTH_SHORT).show();
        }else {
            BmobQuery<User> bmobQuery = new BmobQuery<>("User");
            bmobQuery.addWhereEqualTo("userId",idEt.getText().toString());
            bmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e==null){
                        if (list.size() == 0){
                            Toast.makeText(LoginActivity.this,"账号不存在！",Toast.LENGTH_SHORT).show();
                        }else{
                            if(list.get(0).getPasswrod().equals(passwordEt.getText().toString())){
                                Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                setSpContent();
                                final User user = list.get(0);
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this,ChannelListActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("user",user);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                };
                                timer.schedule(task,0);//为了效果才延时
                            }else {
                                Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else {
                        Log.d(TAG, "done: "+e.getMessage());

                    }
                }
            });
        }
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
                finish();
            }
        });
    }
}

package com.invok.newsapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends AppCompatActivity {

    private EditText idEt ,passwordEt,password2Et,nameEt;
    private Button registBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        Bmob.initialize(RegistActivity.this, "563a964bdf42a32f93cdd28755c0df8d");
        initView();
    }

    private void initView() {
        idEt = (EditText) findViewById(R.id.userId_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        password2Et = (EditText) findViewById(R.id.password2_et);
        nameEt = (EditText) findViewById(R.id.userName_et);
        registBt = (Button) findViewById(R.id.regist_bt);

        registBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(idEt.getText().toString())||TextUtils.isEmpty(passwordEt.getText().toString())||
                        TextUtils.isEmpty(password2Et.getText().toString())||TextUtils.isEmpty(nameEt.getText().toString())){
                    Toast.makeText(RegistActivity.this,"不能为空！",Toast.LENGTH_SHORT).show();
                }
                else{
                    User user = new User();
                    user.setUserId(idEt.getText().toString());
                    user.setPasswrod(passwordEt.getText().toString());
                    user.setUserName(nameEt.getText().toString());
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                Toast.makeText(RegistActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(RegistActivity.this,LoginActivity.class));
                                    }
                                };
                                timer.schedule(task,1000);
                            }
                            else {
                                Toast.makeText(RegistActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
                finish();
            }
        });
    }
}

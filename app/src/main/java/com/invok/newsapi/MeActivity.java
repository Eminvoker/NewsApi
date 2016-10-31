package com.invok.newsapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

public class MeActivity extends AppCompatActivity {

    private TextView userNameTv ;

    private User user;

    private CircleImageView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();
        initData();
    }

    private void initView() {
        userNameTv = (TextView) findViewById(R.id.userName_tv);
        header = (CircleImageView) findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    Intent intent = new Intent();
                    intent.setClass(MeActivity.this,InfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
            }
        });

        initAppTitle();
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        if(user != null){
            userNameTv.setText(user.getUserName());
            if(user.getHeaderUrl()!=null){
                Glide.with(MeActivity.this).load(user.getHeaderUrl()).asBitmap().into(header);
            }
        }
    }

    public void toDynamic(View v){
        if(user != null){
            Intent intent = new Intent();
            intent.setClass(MeActivity.this,MyDynamicActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user",user);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }

    public void toCollection(View v){
        if(user != null){
            Intent intent = new Intent();
            intent.setClass(MeActivity.this,MyCollectionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user",user);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }

    public void toRecord(View v){
        if(user != null){
            Intent intent = new Intent();
            intent.setClass(MeActivity.this,MyRecordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user",user);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            user = (User) data.getSerializableExtra("user");
            Log.d("test", "onActivityResult: "+user.getHeaderUrl());
            if(user.getHeaderUrl()!=null){
                Glide.with(MeActivity.this).load(user.getHeaderUrl()).asBitmap().into(header);
            }
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

package com.invok.newsapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.invok.newsapi.adapter.NewsAdapter;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MyRecordActivity extends AppCompatActivity {

    private User user;
    private List<News> mData;

    private LinearLayout noContentLayout;
    private RecyclerView recordRv;
    private NewsAdapter newsAdapter;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);
        initView();
        initData();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private void initView() {
        noContentLayout = (LinearLayout) findViewById(R.id.noContentLayout);
        recordRv= (RecyclerView) findViewById(R.id.recordRv);
        initAppTitle();
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        mData = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(final User user, BmobException e) {
                if(e==null){
                    if(user.getRecord() == null){
                        noContentLayout.setVisibility(View.VISIBLE);
                    }else {
                        for (int i = 0; i < user.getRecord().size(); i++) {
                            mData.add(null);
                            BmobQuery<News> newsQuery = new BmobQuery<>();
                            final int finalI = i;
                            newsQuery.getObject(user.getRecord().get(i), new QueryListener<News>() {
                                @Override
                                public void done(News news, BmobException e) {
                                    if(e==null){
                                        mData.set(finalI,news);
                                        //当存入数据不能为null时，一定要先判断，这样保证最后一个数据
                                        // （异步线程不一定是user.getRecord().size()-1）已导入
                                       /* Log.d("invokerrecord", "布拉吉: 标识"+mData.size());
                                        for (int g= 0 ; g<mData.size();g++){
                                            if(mData.get(g) ==null) {
                                                Log.d("invokerrecord", "布拉吉: "+g);
                                            }
                                        }*/
                                        if(!mData.contains(null)) {
                                            if (newsAdapter == null) {
                                                newsAdapter = new NewsAdapter(MyRecordActivity.this, mData, user);
                                                recordRv.setAdapter(newsAdapter);
                                                linearLayoutManager = new LinearLayoutManager(MyRecordActivity.this);
                                                recordRv.setLayoutManager(linearLayoutManager);
                                            }
                                            newsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }else{
                }
            }
        });
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

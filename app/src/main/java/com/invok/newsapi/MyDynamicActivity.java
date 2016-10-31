package com.invok.newsapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.invok.newsapi.adapter.CommentAdapter;
import com.invok.newsapi.bean.Comment;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MyDynamicActivity extends AppCompatActivity {

    private final static int DynamicMethod = 1;

    private RecyclerView dynamicRv;
    private User user;
    private List<Comment> mData;
    private List<String> commentHeader;
    private CommentAdapter commentAdapter;
    private LinearLayoutManager mLayoutManager;

    private LinearLayout noContentLayout;

    private List<News> newsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamic);
        user = (User) getIntent().getSerializableExtra("user");
        noContentLayout = (LinearLayout) findViewById(R.id.noContentLayout);
        initView();
        initData();
    }

    private void initView() {
        dynamicRv = (RecyclerView) findViewById(R.id.dynamic_rv);
        initAppTitle();
    }

    private void initData() {
        commentHeader = new ArrayList<>();
        mData = new ArrayList<>();
        newsList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(MyDynamicActivity.this);
        dynamicRv.setLayoutManager(mLayoutManager);

        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("userObjId",user.getObjectId());
        query.order("-createdAt");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                mData = list;
                getNewsList();
            }
        });

    }

    private void getCommentHeader(){
        for (int i = 0; i <mData.size() ; i++) {
            BmobQuery<User> query = new BmobQuery();
            Log.d("mData", "ID: "+mData.get(i).getUserObjId());
            commentHeader.add(null);//先按照mData填满数据
            final int finalI = i;
            query.getObject(mData.get(i).getUserObjId(), new QueryListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if(e==null) {
                        //commentHeader.add(user.getHeaderUrl());
                        commentHeader.set(finalI,user.getHeaderUrl());//按照i值固定顺序插入头像，防止异步线程造成的顺序错乱
                        if(commentAdapter  == null){
                            commentAdapter = new CommentAdapter(MyDynamicActivity.this, mData, commentHeader,DynamicMethod ,user,newsList);
                            dynamicRv.setAdapter(commentAdapter);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }else {
                    }
                }
            });
        }
    }

    private void getNewsList(){
        for (int i = 0; i <mData.size() ; i++) {
            newsList.add(null);
            BmobQuery<News> query = new BmobQuery<>();
            query.addWhereEqualTo("title",mData.get(i).getNewsTitle());
            final int finalI = i;
            query.findObjects(new FindListener<News>() {
                @Override
                public void done(List<News> list, BmobException e) {
                    if(e==null){
                        if (list.size()>0){
                            newsList.set(finalI,list.get(0));
                            if(!newsList.contains(null)){
                               /* if(commentAdapter  == null){
                                    commentAdapter = new CommentAdapter(MyDynamicActivity.this, mData, commentHeader,DynamicMethod,user,newsList );
                                    dynamicRv.setAdapter(commentAdapter);
                                }*/
                                if(commentAdapter  == null){
                                    commentAdapter = new CommentAdapter(MyDynamicActivity.this, mData, commentHeader,DynamicMethod ,user,newsList);
                                    dynamicRv.setAdapter(commentAdapter);
                                }
                                getCommentHeader();
                                //commentAdapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            noContentLayout.setVisibility(View.VISIBLE);
                        }
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

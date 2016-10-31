package com.invok.newsapi;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.invok.newsapi.adapter.CommentAdapter;
import com.invok.newsapi.bean.Comment;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class NewsContentActivity extends AppCompatActivity {

    private static final String TAG = "NewsContentActivity";


    private TextView titleTv, channelNameTv, pubDateTv, contentTv, sourceTv;
    private News mNews;
    private LinearLayout contentLayout;
    private LinearLayout commentLayout;
    private LinearLayout commentCountLayout;//评论和评论数
    private TextView commentCountTv;
    private CardView commentEtLayout;//编辑框布局
    private TextView cancelTv, pubTv;//编辑框的两个选项
    private EditText commentEt;
    private LinearLayout commentBarLayout;//评论条

    private LinearLayout hitCommentLayout;//热门评论布局
    private List<Comment> hitComments;
    //private  int comCount = 0;//读取现有评论后数目，不用Newscount+1，怕更新到数目时网络出错，暂时做不到事务性

    private RecyclerView commentRv;
    private List<Comment> mData = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private  LinearLayoutManager mLayoutManager;

    private ImageView collect;//收藏按钮

    private User user;

    private List<String> commentHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        mNews = (News) getIntent().getSerializableExtra("news");
        user = (User) getIntent().getSerializableExtra("user");
        initView();
        initData();
        if(user != null) {
            record();//用来记录足迹
        }
    }

    private void initData() {
        titleTv.setText(mNews.getTitle());
        channelNameTv.setText(mNews.getChannelName());
        pubDateTv.setText(mNews.getPubDate());
        //contentTv.setText(mNews.getAllList());
        sourceTv.setText("本文来自："+mNews.getSource());

        String allist = mNews.getAllList();
        List<String> imageUrls = mNews.getImageurls();
        LinearLayout.LayoutParams lp;
        String str = "";
        boolean isRead = true;
        int j = 0; //记录图片序号
        //对alllist的图文进行分离处理
        //从1开始到长度-1，不读取‘[’,']'
        for (int i = 1; i < allist.length()-1; i++) {
            if(i!= allist.length()-2){
                //排除转译字符
                if(allist.charAt(i) == '\\'){
                    continue;
                }

                if (allist.charAt(i) == ',' && allist.charAt(i - 1) == '"' && allist.charAt(i + 1) == '"') {
                    str += "\n";
                }
            }
            if(allist.charAt(i) == '{'){
                //创建一段一段的文字
                if(str != "") {
                    TextView tv = new TextView(NewsContentActivity.this);
                    tv.setText(str);
                    tv.setTextSize(13);
                    contentLayout.addView(tv);
                }
                //创建图片夹杂在文字中间
                ImageView iv = new ImageView(NewsContentActivity.this);
                Glide.with(NewsContentActivity.this).load(imageUrls.get(j++)).into(iv);
                contentLayout.addView(iv);
                lp = (LinearLayout.LayoutParams) iv.getLayoutParams();
                lp.weight = 700;
                lp.height = 500;
                iv.setLayoutParams(lp);
                isRead = false;
            }
            if(isRead){
                str += allist.charAt(i);
            }
            if(allist.charAt(i) == '}') {
                isRead = true;
                str = "";
            }
            if(i == allist.length()-2 && allist.charAt(i) != '{' && str!=""){
                    TextView tv = new TextView(NewsContentActivity.this);
                    tv.setText(str);
                    tv.setTextSize(13);
                    contentLayout.addView(tv);
            }
        }

        downLoadHitComment();

        commentHeader = new ArrayList<>();
        mData = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(NewsContentActivity.this);
        commentRv.setLayoutManager(mLayoutManager);
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.news_title_tv);
        channelNameTv = (TextView) findViewById(R.id.news_channelName_tv);
        pubDateTv = (TextView) findViewById(R.id.news_time_tv);
        contentTv = (TextView) findViewById(R.id.news_content_tv);
        sourceTv = (TextView) findViewById(R.id.news_source_tv);
        contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
        commentCountTv = (TextView) findViewById( R.id.commentCount_tv);
        commentCountTv.setText(mNews.getCommentCount()+"");
        commentLayout =  (LinearLayout)findViewById(R.id.commentLayout);
        commentCountLayout = (LinearLayout) findViewById(R.id.commentCountLayout);
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    commentBarLayout.setVisibility(View.GONE);
                    commentEtLayout.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(NewsContentActivity.this, "评论功能需要登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        commentEtLayout = (CardView) findViewById(R.id.comment_et_layout);
        commentBarLayout = (LinearLayout) findViewById(R.id.commentBarLayout);
        cancelTv = (TextView) findViewById(R.id.cancel_tv);
        pubTv = (TextView) findViewById(R.id.pub_tv);
        commentEt = (EditText) findViewById(R.id.comment_et);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEtLayout.setVisibility(View.GONE);
                commentBarLayout.setVisibility(View.VISIBLE);
                commentEt.setText("");
            }
        });
        pubTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(commentEt.getText().toString())){
                    //发送评论到服务器
                   uploadCommentstoServer();
                }else{
                    Toast.makeText(NewsContentActivity.this, "评论不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hitCommentLayout = (LinearLayout) findViewById(R.id.hitCommentLayout);

        commentRv = (RecyclerView) findViewById(R.id.comment_rv);

        collect = (ImageView) findViewById(R.id.collect);
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    collectNews();
                    collect.setImageResource(R.drawable.save1);
                }else {
                    Toast.makeText(NewsContentActivity.this, "收藏功能需要登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initAppTitle();
    }

    private void collectNews() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    List<String> collection = user.getCollection();
                    if(collection!=null){
                        if(collection.contains(mNews.getObjectId())){
                            Toast.makeText(NewsContentActivity.this,"该新闻已收藏",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else {
                        collection = new ArrayList<>();
                    }
                    collection.add(mNews.getObjectId());
                    user.setCollection(collection);
                    user.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e== null){
                                Toast.makeText(NewsContentActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                            } else{
                                Log.d(TAG, "done: "+"收藏失败"+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public void uploadCommentstoServer(){
        final Comment comment = new Comment();
        comment.setUserName(user.getUserName());
        comment.setContent(commentEt.getText().toString());
        comment.setNewsTitle(mNews.getTitle());
        comment.setUserObjId(user.getObjectId());
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    final News news = mNews;
                    //mNews.setCommentCount(mNews.getCommentCount()+1);
                    news.setCommentCount(news.getCommentCount()+1);
                    news.update(mNews.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e== null){
                                Toast.makeText(NewsContentActivity.this, "新闻评论数更新成功！", Toast.LENGTH_SHORT).show();
                                commentCountTv.setText(news.getCommentCount()+"");
                                mData.add(comment);
                                commentHeader.add(user.getHeaderUrl());
                                //commentAdapter = new CommentAdapter(NewsContentActivity.this,mData);
                                //commentRv.setAdapter(commentAdapter);
                                if(commentAdapter == null) {
                                    commentAdapter = new CommentAdapter(NewsContentActivity.this, mData, commentHeader);
                                    commentRv.setAdapter(commentAdapter);
                                }
                                commentAdapter.notifyDataSetChanged();
                                hitCommentLayout.setVisibility(View.GONE);
                            } else{
                                Toast.makeText(NewsContentActivity.this, "新闻评论数更新失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(NewsContentActivity.this, "评论上传失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentEtLayout.setVisibility(View.GONE);
        commentBarLayout.setVisibility(View.VISIBLE);
        commentEt.setText("");
    }

    public void downLoadHitComment(){
        if (mNews.getCommentCount() == 0){
            TextView tv = new TextView(getApplicationContext());
            tv.setText("暂无评论");
            tv.setTextColor(Color.parseColor("#000000"));
            hitCommentLayout.addView(tv);
        }else{
            BmobQuery<Comment> query = new BmobQuery<>();
            query.addWhereEqualTo("newsTitle",mNews.getTitle());
            query.order("-up,-createdAt");
            query.findObjects(new FindListener<Comment>() {
                @Override
                public void done(List<Comment> list, BmobException e) {
                    if(e == null){
                       /* for (int i = 0; i <list.size() ; i++) {
                            Comment comment = list.get(i);
                            TextView tv = new TextView(getApplicationContext());
                            tv.setText(comment.getUserName()+" :"+comment.getContent() +"  赞："+comment.getUp()+" 时间"+comment.getCreatedAt());
                            hitCommentLayout.addView(tv);
                        }*/
                        mData = list;
                        getCommentHeader();
                        /*commentAdapter = new CommentAdapter(NewsContentActivity.this,mData,commentHeader);
                        commentRv.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();*/
                    }else{
                        TextView tv = new TextView(getApplicationContext());
                        tv.setText("数据读取超时，请检查网络后，刷新");
                        hitCommentLayout.addView(tv);
                    }
                }
            });
        }
    }

    private void record() {
       BmobQuery<User> query = new BmobQuery<>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    User user1 = user;
                    List<String> recordList;
                    if(user1.getRecord() != null) {
                       recordList = user1.getRecord();
                    }else{
                        recordList = new ArrayList<String>() ;
                    }

                    //防止多次浏览留下多次痕迹
                    if(!recordList.contains(mNews.getObjectId())) {
                        recordList.add(0, mNews.getObjectId());
                        user1.setRecord(recordList);
                        user1.update(user1.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    Log.d(TAG, "done: " + "记录足迹出错");
                                }
                            }
                        });
                    }
                    //把已有删除，移至最新
                    else {
                        recordList.remove(mNews.getObjectId());
                        recordList.add(0, mNews.getObjectId());
                        user1.setRecord(recordList);
                        user1.update(user1.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    Log.d(TAG, "done: " + "记录足迹出错");
                                }
                            }
                        });
                    }
                }
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
                        Log.d(TAG, "载入头像数据： URL："+user.getHeaderUrl());
                        //commentHeader.add(user.getHeaderUrl());
                        commentHeader.set(finalI,user.getHeaderUrl());//按照i值固定顺序插入头像，防止异步线程造成的顺序错乱
                        if(commentAdapter == null) {
                            commentAdapter = new CommentAdapter(NewsContentActivity.this, mData, commentHeader);
                            commentRv.setAdapter(commentAdapter);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }else {
                        Log.d(TAG, "getCommentHeader error: "+e.getMessage());
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
        if(user!=null && user.getHeaderUrl()!=null) {
            Glide.with(getApplicationContext()).load(user.getHeaderUrl()).asBitmap().into(topMe);
        }
        topMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),MeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewsContentActivity.this);
                    builder.setMessage("是否跳转到登录界面");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(NewsContentActivity.this,LoginActivity.class));
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            }
        });
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

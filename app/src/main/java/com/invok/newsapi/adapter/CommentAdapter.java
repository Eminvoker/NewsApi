package com.invok.newsapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.invok.newsapi.NewsContentActivity;
import com.invok.newsapi.R;
import com.invok.newsapi.bean.Comment;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/10/8.
 */
public class CommentAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Comment> mData;
    private LayoutInflater inflater;
    private List<String> commentHeader;

    private int method;
    private final static int DynamicMethod = 1;

    private User user;
    private List<News> newsList;

    public CommentAdapter(Context context, List<Comment> mData,List<String> commentHeader) {
        this.context = context;
        this.mData = mData;
        this.inflater = LayoutInflater.from(context);
        this.commentHeader = commentHeader;
    }

    public CommentAdapter(Context context, List<Comment> mData,List<String> commentHeader,int method,User user,List<News> newsList) {
        this.context = context;
        this.mData = mData;
        this.inflater = LayoutInflater.from(context);
        this.commentHeader = commentHeader;
        this.method = method;
        this.user = user;
        this.newsList = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(this.method == DynamicMethod  ){
            view = inflater.inflate(R.layout.dynamic_item, parent, false);
        }else {
            view = inflater.inflate(R.layout.comment_item, parent, false);
        }
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.upIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = mData.get(myViewHolder.getAdapterPosition());
                comment.setUp(comment.getUp()+1);
                comment.update(comment.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            myViewHolder.upTv.setText(Integer.parseInt(myViewHolder.upTv.getText().toString())+1+"");
                            myViewHolder.upIv.setImageResource(R.drawable.zan1);
                            myViewHolder.upIv.setClickable(false);
                        }
                    }
                });
            }
        });
        myViewHolder.downIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = mData.get(myViewHolder.getAdapterPosition());
                comment.setDown(comment.getDown()+1);
                comment.update(comment.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            myViewHolder.downTv.setText(Integer.parseInt(myViewHolder.downTv.getText().toString())+1+"");
                            myViewHolder.downIv.setImageResource(R.drawable.cai1);
                            myViewHolder.downIv.setClickable(false);
                        }
                    }
                });
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Comment comment = mData.get(position);
        final MyViewHolder mViewHolder = ((MyViewHolder)holder);
        mViewHolder.commentUserNameTv.setText(comment.getUserName());
        mViewHolder.commentTimeTv.setText(comment.getCreatedAt());
        mViewHolder.upTv.setText(comment.getUp()+"");
        mViewHolder.downTv.setText(comment.getDown()+"");
        mViewHolder.commentTv.setText(comment.getContent());
        String headerUrl = commentHeader.get(position);
        Log.d("headerUrl","序号："+position +" +姓名: "+comment.getUserName()+"头像："+headerUrl);
        if(headerUrl != null) {
            Glide.with(context).load(headerUrl).asBitmap().into(mViewHolder.commentHeader);
        }else {
            Glide.with(context).load(R.drawable.header).asBitmap().into(mViewHolder.commentHeader);
        }

        if(this.method == DynamicMethod  ){
            mViewHolder.newsTitleTv.setText(comment.getNewsTitle());
            mViewHolder.readNewsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, NewsContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", newsList.get(mViewHolder.getAdapterPosition()));
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentHeader.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView upIv,downIv;
        private TextView commentUserNameTv,commentTimeTv,upTv,downTv,commentTv;
        private CircleImageView commentHeader;

        private TextView newsTitleTv,readNewsTv;//动态：新闻的标题和点击查看原文

        public MyViewHolder(View itemView) {
            super(itemView);
            upIv = (ImageView) itemView.findViewById(R.id.up_iv);
            downIv = (ImageView) itemView.findViewById(R.id.down_iv);
            commentUserNameTv = (TextView) itemView.findViewById(R.id.comment_userName_tv);
            commentTimeTv = (TextView) itemView.findViewById(R.id.comment_time_tv);
            upTv = (TextView) itemView.findViewById(R.id.up_tv);
            downTv = (TextView) itemView.findViewById(R.id.down_tv);
            commentTv = (TextView) itemView.findViewById(R.id.comment_tv);
            commentHeader = (CircleImageView) itemView.findViewById(R.id.comment_header);
            if(method == DynamicMethod){
                newsTitleTv = (TextView) itemView.findViewById(R.id.newstitle);
                readNewsTv = (TextView) itemView.findViewById(R.id.readNews);
            }
        }
    }
}

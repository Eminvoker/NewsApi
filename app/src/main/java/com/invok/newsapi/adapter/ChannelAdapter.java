package com.invok.newsapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.invok.newsapi.ChannelListActivity;
import com.invok.newsapi.NewsListActivity;
import com.invok.newsapi.R;
import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.bean.User;

import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */
public class ChannelAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Channel> mData;
    private LayoutInflater inflater;
    private User user;

    public ChannelAdapter(Context context, List<Channel> mData,User user) {
        this.context = context;
        this.mData = mData;
        this.inflater = LayoutInflater.from(context);
        this.user = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.channel_item,parent,false);
        final MyViewHolder mViewHolder = new MyViewHolder(view);
        mViewHolder.channelNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, NewsListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("channel",mData.get(mViewHolder.getAdapterPosition()));
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder)holder).channelNameTv.setText(mData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView channelNameTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            channelNameTv = (TextView) itemView.findViewById(R.id.channelName);
        }
    }
}

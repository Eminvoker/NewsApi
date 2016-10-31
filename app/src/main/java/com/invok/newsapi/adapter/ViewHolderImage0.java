package com.invok.newsapi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.invok.newsapi.R;

/**
 * Created by Administrator on 2016/10/16.
 */
public class ViewHolderImage0 extends RecyclerView.ViewHolder{
    public TextView titleTv;
    public TextView timeTv;
    public TextView channelTv;
    public TextView commentCountTv;
    public ViewHolderImage0(View itemView) {
        super(itemView);
        titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
        timeTv = (TextView) itemView.findViewById(R.id.item_time_tv);
        channelTv = (TextView) itemView.findViewById(R.id.item_channel_tv);
        commentCountTv = (TextView) itemView.findViewById(R.id.commentCount_tv);
    }
}

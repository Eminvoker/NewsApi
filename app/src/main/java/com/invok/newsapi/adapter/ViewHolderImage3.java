package com.invok.newsapi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.invok.newsapi.R;

/**
 * Created by Administrator on 2016/10/16.
 */
public class ViewHolderImage3 extends RecyclerView.ViewHolder{
    public TextView titleTv;
    public TextView timeTv;
    public TextView channelTv;
    public TextView commentCountTv;
    public ImageView imageIv1,imageIv2,imageIv3;
    public ViewHolderImage3(View itemView) {
        super(itemView);
        imageIv1 = (ImageView) itemView.findViewById(R.id.item_image_iv1);
        imageIv2 = (ImageView) itemView.findViewById(R.id.item_image_iv2);
        imageIv3 = (ImageView) itemView.findViewById(R.id.item_image_iv3);
        titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
        timeTv = (TextView) itemView.findViewById(R.id.item_time_tv);
        channelTv = (TextView) itemView.findViewById(R.id.item_channel_tv);
        commentCountTv = (TextView) itemView.findViewById(R.id.commentCount_tv);
    }
}

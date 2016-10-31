package com.invok.newsapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.invok.newsapi.NewsContentActivity;
import com.invok.newsapi.R;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NewsAdapter";
    private Context context;
    private List<News> mData;
    private LayoutInflater inflater;

    private User user;//用来评论


    private static final int TYPE_IMAGE0= 0;
    private static final int TYPE_IMAGE1 = 1;
    private static final int TYPE_IMAGE3 = 3;

    public NewsAdapter(Context context, List<News> mData, User user) {
        this.context = context;
        this.mData = mData;
        this.inflater = LayoutInflater.from(context);
        this.user = user;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE1) {
            View view = inflater.inflate(R.layout.news_item_one, parent, false);
            final ItemViewHolder itemViewHolder = new ItemViewHolder(view);

            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, NewsContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", mData.get(itemViewHolder.getAdapterPosition()));
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
            return itemViewHolder;
        }
        if (viewType == TYPE_IMAGE0) {
            View view = inflater.inflate(R.layout.news_item_none, parent, false);
            final ViewHolderImage0 viewHolderImage0 = new ViewHolderImage0(view);
            viewHolderImage0.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, NewsContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", mData.get(viewHolderImage0.getAdapterPosition()));
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
            return viewHolderImage0;
        }
        if (viewType == TYPE_IMAGE3) {
            View view = inflater.inflate(R.layout.news_item_more, parent, false);
            final ViewHolderImage3 viewHolderImage3 = new ViewHolderImage3(view);
            viewHolderImage3.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, NewsContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("news", mData.get(viewHolderImage3.getAdapterPosition()));
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
            return viewHolderImage3;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            //holder.imageIv.setImageResource();
            News news = mData.get(holder.getAdapterPosition());
                ItemViewHolder ivholder = (ItemViewHolder) holder;
            Glide.with(context).load(news.getImageurls().get(0)).into(ivholder.imageIv);
            ivholder.titleTv.setText(news.getTitle());
            ivholder.timeTv.setText(news.getPubDate());
            ivholder.channelTv.setText(news.getChannelName());
            ivholder.commentCountTv.setText(news.getCommentCount() + "");
        }
        else if (holder instanceof ViewHolderImage0) {
            News news = mData.get(holder.getAdapterPosition());
            ViewHolderImage0 viewHolderImage0 = (ViewHolderImage0) holder;
            viewHolderImage0.titleTv.setText(news.getTitle());
            viewHolderImage0.timeTv.setText(news.getPubDate());
            viewHolderImage0.channelTv.setText(news.getChannelName());
            viewHolderImage0.commentCountTv.setText(news.getCommentCount() + "");
        }
        else if (holder instanceof ViewHolderImage3) {
            News news = mData.get(holder.getAdapterPosition());
            ViewHolderImage3 viewHolderImage3 = (ViewHolderImage3) holder;
            viewHolderImage3.titleTv.setText(news.getTitle());
            viewHolderImage3.timeTv.setText(news.getPubDate());
            viewHolderImage3.channelTv.setText(news.getChannelName());
            viewHolderImage3.commentCountTv.setText(news.getCommentCount() + "");
            //原来是ImageView的scaleType的问题，当设置为fitXY时，虽然ImageView显示那么点尺寸
            // 但是，但是Glide加载图片时，却是以全分辨率加载的，于是加载几张，就OOM了。
            Glide.with(context).load(news.getImageurls().get(0)).into(viewHolderImage3.imageIv1);
            Glide.with(context).load(news.getImageurls().get(1)).into(viewHolderImage3.imageIv2);
            Glide.with(context).load(news.getImageurls().get(2)).into(viewHolderImage3.imageIv3);
        }
    }

    @Override
    public int getItemCount() {
        // return mData.size()+1;// RecyclerView的count设置为数据总条数+ 1（LoadView）
        return mData.size(); //暂时不使用底部Loadbar
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.get(position).isHavePic() == false){
            return TYPE_IMAGE0;
        }else {
            if(mData.get(position).getImageurls().size()<3)
                return TYPE_IMAGE1;
            else
                return TYPE_IMAGE3;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageIv;
        private TextView titleTv;
        private TextView timeTv;
        private TextView channelTv;
        private TextView commentCountTv;


        public ItemViewHolder(View itemView) {
            super(itemView);
            imageIv = (ImageView) itemView.findViewById(R.id.item_image_iv);
            titleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            timeTv = (TextView) itemView.findViewById(R.id.item_time_tv);
            channelTv = (TextView) itemView.findViewById(R.id.item_channel_tv);
            commentCountTv = (TextView) itemView.findViewById(R.id.commentCount_tv);
        }
    }

   /* class LoadViewHolder extends RecyclerView.ViewHolder {
        private ImageView loadIv;
        private TextView loadTv;

        public LoadViewHolder(View itemView) {
            super(itemView);
            loadIv = (ImageView) itemView.findViewById(R.id.load_iv);
            loadTv = (TextView) itemView.findViewById(R.id.load_tv);
        }
    }*/
}

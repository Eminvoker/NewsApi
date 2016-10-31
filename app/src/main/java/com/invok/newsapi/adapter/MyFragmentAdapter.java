package com.invok.newsapi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.framgent.NewsListFramgent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Administrator on 2016/10/23.
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Channel> mChannels;
    private List<Fragment> mFragments = new ArrayList<>();

    //private Queue<Fragment> mFragmentQueue = new ArrayDeque<>();

    /*
    public MyFragmentAdapter(FragmentManager fm, Context mContext, List<Channel> mChannels, List<Fragment> mFragments) {
        super(fm);
        this.mContext = mContext;
        this.mChannels = mChannels;
        this.mFragments = mFragments;
    }*/

    public MyFragmentAdapter(FragmentManager fm, Context mContext, List<Channel> mChannels) {
        super(fm);
        this.mContext = mContext;
        this.mChannels = mChannels;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("channel",mChannels.get(position));
        NewsListFramgent newsListFramgent = new NewsListFramgent();
        newsListFramgent.setArguments(bundle);
        /*if(!mFragments.contains(newsListFramgent)){
            mFragments.add(newsListFramgent);
        }*/
        return newsListFramgent;
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }
}

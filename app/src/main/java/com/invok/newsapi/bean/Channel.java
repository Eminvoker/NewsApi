package com.invok.newsapi.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */
public class Channel implements Serializable{
    private String channelId;
    private String name;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

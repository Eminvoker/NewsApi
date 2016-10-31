package com.invok.newsapi.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/29.
 */
public class Comment extends BmobObject{

    private String newsTitle;
    private String userName;
    private String content;
    private String creatAt;//服务器记录的创建时间
    private int  up = 0;//点赞数
    private int down = 0;//踩数
    private int level = 0;//等级，越高越精华
    private String userObjId;

    public String getUserObjId() {
        return userObjId;
    }

    public void setUserObjId(String userObjId) {
        this.userObjId = userObjId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}

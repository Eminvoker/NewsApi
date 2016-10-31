package com.invok.newsapi.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/10/17.
 */
public class IMessage extends BmobObject{
    private String senderObjId;
    private String receiverObjId;
    private String content;
    private boolean isRead;

    public String getSenderObjId() {
        return senderObjId;
    }

    public void setSenderObjId(String senderObjId) {
        this.senderObjId = senderObjId;
    }

    public String getReceiverObjId() {
        return receiverObjId;
    }

    public void setReceiverObjId(String receiverObjId) {
        this.receiverObjId = receiverObjId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}

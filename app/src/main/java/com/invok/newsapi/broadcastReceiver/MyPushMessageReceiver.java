package com.invok.newsapi.broadcastReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.invok.newsapi.MainActivity;
import com.invok.newsapi.R;
import com.invok.newsapi.bean.IMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2016/10/16.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    private final static int FLAT_LUNACH = 1;
    /*{
"senderObjId":"86af4b6cf0",
"receiverObjId":"all" ,
"content":"小伙子，你好久没来看新闻了！"
}*/

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("msg"));
                Log.d("bmob", "发送者："+object.getString("senderObjId")+"  接收者："+object.getString("receiverObjId")
                +"  \n内容： "+object.getString("content"));
                /*IMessage iMessage = new IMessage();
                iMessage.setSenderObjId(object.getString("senderObjId"));
                iMessage.setReceiverObjId(object.getString("receiverObjId"));
                iMessage.setContent(object.getString("content"));
                iMessage.setRead(false);*/

                Intent intent1 = new Intent(context, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(context,0,intent1,0);

                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify = new Notification.Builder(context)
                        .setAutoCancel(true)//设置打开该通知自动消失
                        .setTicker("有新消息")//设置显示在状态栏的通知提示信息
                        .setSmallIcon(R.mipmap.ic_launcher)//通知图标
                        .setContentTitle("一条新的通知")//标题
                        .setContentText(object.getString("content"))//内容
                        .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)//默认的LED灯、声音
                        .setWhen(System.currentTimeMillis())
                        .setVibrate(new long[]{0,50,100,150})
                        .setContentIntent(pi)//启动程序的Intent
                        .build();
                nm.notify(FLAT_LUNACH,notify);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

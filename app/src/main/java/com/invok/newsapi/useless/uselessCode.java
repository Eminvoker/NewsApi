package com.invok.newsapi.useless;

/**
 * Created by Administrator on 2016/10/25.
 */
public class uselessCode {
    /*
    *  <service
            android:name="cn.bmob.push.lib.service.PushService"
            android:exported="true"
            android:label="PushService"
            android:process=":bmobpush">
            <intent-filter>
                <action android:name="cn.bmob.push.lib.service.PushService" />
            </intent-filter>
        </service>

        <!-- 用于进程保活 -->
        <service
            android:name="cn.bmob.push.lib.service.PushNotifyService"
            android:process=":bmobpush"></service>

        <receiver android:name="cn.bmob.push.PushReceiver">
            <intent-filter>

                <!-- 系统启动完成后会调用 -->
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <!-- 解锁完成后会调用 -->
                <action android:name="android.intent.action.USER_PRESENT" />
                <!-- 监听网络连通性 -->
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>

        <!-- 第四部中创建的消息接收器，在这里进行注册 -->
        <receiver android:name=".broadcastReceiver.MyPushMessageReceiver">
            <intent-filter>
                <action android:name="cn.bmob.push.action.MESSAGE" />
            </intent-filter>
        </receiver>

        <!-- 接收心跳和唤醒的广播，要和PushService运行在同个进程 -->
        <receiver
            android:name="cn.bmob.push.PushNotifyReceiver"
            android:process=":bmobpush">
            <intent-filter>

                <!-- 接收心跳广播的action -->
                <action android:name="cn.bmob.push.action.HEARTBEAT" />
                <!-- 接收唤醒广播的action -->
                <action android:name="cn.bmob.push.action.NOTIFY" />
            </intent-filter>
        </receiver>
    *
    * */
}

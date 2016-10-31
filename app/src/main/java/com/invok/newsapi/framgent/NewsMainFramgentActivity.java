package com.invok.newsapi.framgent;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.invok.newsapi.ChannelListActivity;
import com.invok.newsapi.LoginActivity;
import com.invok.newsapi.MeActivity;
import com.invok.newsapi.MyApplication;
import com.invok.newsapi.R;
import com.invok.newsapi.adapter.MyFragmentAdapter;
import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.tool.DepthPageTransformer;
import com.invok.newsapi.view.CircleImageView;
import com.invok.newsapi.view.ViewPagerIndicate;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsMainFramgentActivity extends FragmentActivity {

    private List<Channel> channelList = new ArrayList<>();
    private String URL = "http://apis.baidu.com/showapi_open_bus/channel_news/channel_news";
    private List<Fragment> mFragments;
    private ViewPager vp;
    private ViewPagerIndicate indicate;
    private MyFragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main_framgent);
        RequestByVolley(URL);
        initAppTitle();
    }

    //读取频道
    public void RequestByVolley(String URL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray ja = response.getJSONObject("showapi_res_body").getJSONArray("channelList");
                    int length = ja.length();
                    for (int i = 0; i <length ; i++) {
                        Channel channel = new Channel();
                        JSONObject jo = (JSONObject) ja.get(i);
                        channel.setChannelId(jo.getString("channelId"));
                        channel.setName(jo.getString("name"));
                        channelList.add(channel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(channelList.size()>0){
                    //读取完数据再开始设置
                    initViewPgaer();
                    initViewPagerIndicate();
                    mFragmentAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(),"无法连接到服务器",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header =  new HashMap<>();
                header.put("apikey","317097383946af5304db9828d7c2ff99");
                return header;
            }
        };
        MyApplication.getHttpQueues().add(request);
    }

    private void initViewPgaer() {
        vp = (ViewPager) findViewById(R.id.vp);
        /*mFragments = new ArrayList<>();
        for (Channel channel:channelList) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("channel",channel);
            NewsListFramgent newsListFramgent = new NewsListFramgent();
            newsListFramgent.setArguments(bundle);
            mFragments.add(newsListFramgent);
        }*/
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentAdapter = new MyFragmentAdapter(fragmentManager,this,channelList);
        vp.setAdapter(mFragmentAdapter);
        DepthPageTransformer dp = new DepthPageTransformer();
        vp.setPageTransformer(true,dp);
        vp.setOffscreenPageLimit(1);
    }

    private void initViewPagerIndicate() {
        int highlightColor = Color.parseColor("#FF6A6A");
        int normalColor = Color.parseColor("#E6E6FA");
        int[] i = {normalColor,highlightColor};
        int underlineColor = Color.parseColor("#000000");
        int underlineSize = 4;
        indicate = (ViewPagerIndicate) findViewById(R.id.indicate);
        indicate.resetText(R.layout.indicate_item,channelList,normalColor,highlightColor);
        indicate.resetUnderline(underlineSize,underlineColor);
        indicate.resetViewPager(vp);
        indicate.setOk();
    }

    public void initAppTitle(){
        CircleImageView topMe;
        ImageView topBack;
        topBack = (ImageView) findViewById(R.id.top_back);
        topMe = (CircleImageView) findViewById(R.id.top_me);
        /*if(user!=null && user.getHeaderUrl()!=null) {
            Glide.with(getApplicationContext()).load(user.getHeaderUrl()).asBitmap().into(topMe);
        }
        topMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),MeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("是否跳转到登录界面");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            }
        });*/
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

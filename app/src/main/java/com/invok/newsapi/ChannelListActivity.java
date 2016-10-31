package com.invok.newsapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.invok.newsapi.adapter.ChannelAdapter;
import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelListActivity extends AppCompatActivity {

    private static final String TAG = "ChannelListActivity";

    private List<Channel> channelList = new ArrayList<>();
    private String URL = "http://apis.baidu.com/showapi_open_bus/channel_news/channel_news";

    private RecyclerView channelRv;
    private ChannelAdapter channelAdapter;
    private GridLayoutManager gridLayoutManager;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        RequestByVolley(URL);
        user = (User) getIntent().getSerializableExtra("user");
        initView();
        channelAdapter = new ChannelAdapter(ChannelListActivity.this,channelList,user);
        channelRv.setAdapter(channelAdapter);
        gridLayoutManager = new GridLayoutManager(ChannelListActivity.this,4);
        channelRv.setLayoutManager(gridLayoutManager);
    }

    private void initView() {
        channelRv = (RecyclerView) findViewById(R.id.channel_rv);
        initAppTitle();
    }

    public void RequestByVolley(String URL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray ja = response.getJSONObject("showapi_res_body").getJSONArray("channelList");
                    for (int i = 0; i <ja.length() ; i++) {
                        JSONObject jo = (JSONObject) ja.get(i);
                        //Log.d(TAG, "onResponse: "+i+"  : " +jo.getString("channelId")+"  "+jo.getString("name"));
                        Channel channel = new Channel();
                        channel.setChannelId(jo.getString("channelId"));
                        channel.setName(jo.getString("name"));
                        channelList.add(channel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(channelList.size()>0){
                    channelAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ChannelListActivity.this,"无法连接到服务器",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
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

    public void initAppTitle(){
        CircleImageView topMe;
        ImageView topBack;
        topBack = (ImageView) findViewById(R.id.top_back);
        topMe = (CircleImageView) findViewById(R.id.top_me);
        if(user!=null && user.getHeaderUrl()!=null) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChannelListActivity.this);
                    builder.setMessage("是否跳转到登录界面");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ChannelListActivity.this,LoginActivity.class));
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
        });
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

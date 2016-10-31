package com.invok.newsapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.invok.newsapi.adapter.NewsAdapter;
import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.invok.newsapi.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class NewsListActivity extends AppCompatActivity {

    private static final String TAG = "NewsListActivity";

   /* String url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news";
    String arg = "channelId=5572a109b3cdc86cf39001db&needContent=1";
    String URL = url + "?" + arg;*/
    private String url;
    private String arg;
    private String URL;

    private RequestQueue requestQueue;

    private List<News> mData;

    private RecyclerView rv;

    private NewsAdapter newsAdapter;

    private SwipeRefreshLayout srl;

    private LinearLayoutManager mLayoutManager;

    private int lastVisibleItem;

    private int loadCount = 4;//记录加载次数，用来跳过前面加载过的数据,排除一开始加载的,从2开始，不能保证不重复
    private boolean isLoad = false;//记录是否已经在加载，是的话则不进行多次加载，防止重复

    private ImageView loadIv;
    private TextView loadTv;
    private LinearLayout loadBar;

    private boolean firstVolley = true; //是不是第一次读API的数据，避免刷新时重复加载
    private int refreshCount = 0;//刷新条数

    private  User user;

    private Channel channel;//本次载入频道

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        //requestQueue = Volley.newRequestQueue(NewsListActivity.this);//在MyApplication中创建

        if(getIntent().getSerializableExtra("user") != null) {
            user = (User) getIntent().getSerializableExtra("user");
        }

        channel = (Channel) getIntent().getSerializableExtra("channel");
        //Log.d(TAG, "xyxy: "+ channel.getName()+"  "+ channel.getChannelId());

        url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news";
        arg = "channelId="+channel.getChannelId()+"&needContent=1";//查指定频道
        URL = url + "?" + arg;

        mData = new ArrayList<>();
        initView();

        RequestByVolley(URL);
        //newsAdapter = new NewsAdapter(NewsListActivity.this,mData);
        // rv.setAdapter(newsAdapter);
        // 设置布局
        mLayoutManager = new LinearLayoutManager(NewsListActivity.this);
        rv.setLayoutManager(mLayoutManager);



        srl.setColorSchemeColors(Color.BLACK, Color.BLUE, Color.GREEN);
        //srl.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) NewsListActivity.this);

        refreshAndLoad();

    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);

        loadBar = (LinearLayout) findViewById(R.id.loadBar);
        loadIv = (ImageView) findViewById(R.id.load_iv);
        loadTv = (TextView) findViewById(R.id.load_tv);

        rv.setItemAnimator(new DefaultItemAnimator());

        initAppTitle();

    }

    public void RequestByVolley(String URL) {
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //初始化数据
                initData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("apikey", "317097383946af5304db9828d7c2ff99");
                return headers;
            }
        };
        //requestQueue.add(request1);
        MyApplication.getHttpQueues().add(request1);
    }

    private void initData(JSONObject response) {
        try {
            //获得新闻数组
            JSONArray jsonArray = response.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("contentlist");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                News news = new News();
                news.setChannelId(jo.getString("channelId"));
                news.setChannelName(jo.getString("channelName"));
                news.setContent(jo.getString("content"));
                news.setDesc(jo.getString("desc"));
                news.setHavePic(jo.getBoolean("havePic"));
                if (jo.getBoolean("havePic")) {
                    List<String> imgUrls = new ArrayList<>();
                    JSONArray jsonArray1 = jo.getJSONArray("imageurls");
                    for (int j = 0; j < Math.min(50,jsonArray1.length()); j++) {
                        JSONObject jo2 = (JSONObject) jsonArray1.get(j);
                        String imgUrl = jo2.getString("url");
                        imgUrls.add(imgUrl);
                    }
                    news.setImageurls(imgUrls);
                }
                news.setLink(jo.getString("pubDate"));
                news.setPubDate(jo.getString("pubDate"));
                news.setTitle(jo.getString("title"));
                news.setLink(jo.getString("link"));
                news.setSource(jo.getString("source"));
                news.setAllList(jo.getString("allList"));
                //Log.d(TAG, "i: " + news.getTitle());
                QueryAndInsert(news);
                //mData.add(news);不使用API数据，去云服务器读取，保证数据能在服务器中找到(这样要读的太多，会卡住，把连接到数据库的加入)
                //先添加一次数据创建表
               /* news.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            Log.d(TAG, "done: "+"数据添加成功！");
                        }else{
                            Log.d(TAG, "done: "+"数据添加失败！");
                        }
                    }
                });*/
            }


            } catch (JSONException e) {
            e.printStackTrace();
        }
        if(newsAdapter == null) {
            newsAdapter = new NewsAdapter(NewsListActivity.this, mData,user);
            rv.setAdapter(newsAdapter);
        }
    }

    private void QueryAndInsert(final News news) {
        final BmobQuery<News> bmobQuery = new BmobQuery<News>();
        bmobQuery.addWhereEqualTo("title", news.getTitle());
        bmobQuery.findObjects(new FindListener<News>() {
            @Override
            public void done(List<News> list, BmobException e) {
                if (e == null) {
                    //数据库中不存在，则存入
                    if (list.size() <= 0) {
                        News news1 = news;
                        news1.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "done: " + "数据添加成功！");
                                    mData.add(0,news);
                                    newsAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "done: " + "数据添加失败！"+e.getLocalizedMessage());
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "done: " + "数据已存在！");
                        if(firstVolley && !mData.contains(list.get(0))){
                            //Log.d(TAG, "done: " + "添加标题"+list.get(0).getTitle());
                            mData.add(list.get(0));
                            newsAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.d(TAG, "done: " + e.getMessage().toString());
                }
                srl.setRefreshing(false);//取消刷新标志
            }
        });
    }

    private void refreshAndLoad() {

        //下拉刷新

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firstVolley = false;
                RequestByVolley(URL);
            }
        });

        //上拉加载
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == newsAdapter.getItemCount() && !isLoad) {

                    loadBar.setVisibility(View.VISIBLE);
                    loadIv.setImageResource(R.drawable.load);
                    loadTv.setText("加载中...");

                    srl.setRefreshing(true);
                    isLoad = true;
                    Log.d(TAG, "onScrollStateChanged: "+"开始加载更多");


                    BmobQuery<News> bmobQuery = new BmobQuery<>("News");
                    bmobQuery.setLimit(10);// 限制最多10条数据结果作为一页
                    bmobQuery.setSkip(loadCount * 10); // 忽略前10条数据（即第一页数据结果）
                    bmobQuery.addWhereEqualTo("channelId",channel.getChannelId());
                    bmobQuery.order("-createdAt");
                    bmobQuery.findObjects(new FindListener<News>() {
                        @Override
                        public void done(List<News> list, BmobException e) {
                            if(e==null){
                                Log.d(TAG, "done: "+"加载查询成功");
                                for(News news : list){
                                    news.setTitle(news.getTitle());
                                    mData.add(news);
                                }
                                newsAdapter.notifyDataSetChanged();
                                loadCount++;

                                loadIv.setImageResource(R.drawable.complete);
                                loadTv.setText("加载完成");
                                if(list.size() == 0){
                                    loadTv.setText("已加载完所有数据");
                                }
                            }else {
                                Log.d(TAG, "done: "+"加载查询失败");
                                loadIv.setImageResource(R.drawable.sad);
                                loadTv.setText("加载失败,请重试");
                            }
                            isLoad = false;
                            srl.setRefreshing(false);
                        }
                    });
                }else if( lastVisibleItem + 1 < newsAdapter.getItemCount()){
                    loadBar.setVisibility(View.GONE);
                }
            }
        });

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
                    intent.setClass(NewsListActivity.this,MeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewsListActivity.this);
                    builder.setMessage("是否跳转到登录界面");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(NewsListActivity.this,LoginActivity.class));
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



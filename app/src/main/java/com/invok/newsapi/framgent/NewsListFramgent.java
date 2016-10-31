package com.invok.newsapi.framgent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.invok.newsapi.MyApplication;
import com.invok.newsapi.R;
import com.invok.newsapi.adapter.NewsAdapter;
import com.invok.newsapi.bean.Channel;
import com.invok.newsapi.bean.News;
import com.invok.newsapi.bean.User;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/10/23.
 */
public class NewsListFramgent extends Fragment {

     String TAG = "NewsListFramgent";

    private String URL;


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

    private User user;

    private Channel channel;//本次载入频道

    int x = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_news_list, container, false);
        channel = (Channel) getArguments().getSerializable("channel");

        String url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news";
        String arg = "channelId=" + channel.getChannelId() + "&needContent=1";//查指定频道
        URL = url + "?" + arg;

        x = 1;

        mData = new ArrayList<>();
        initView(view);

        RequestByVolley(URL);
        // 设置布局
        mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);


        srl.setColorSchemeColors(Color.BLACK, Color.BLUE, Color.GREEN);

        refreshAndLoad();

        /*
        //检查内存泄露
        RefWatcher refWatcher = MyApplication.getRefWatcher(getContext());
        refWatcher.watch(this);*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv);
        srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);

        loadBar = (LinearLayout) view.findViewById(R.id.loadBar);
        loadIv = (ImageView) view.findViewById(R.id.load_iv);
        loadTv = (TextView) view.findViewById(R.id.load_tv);

        rv.setItemAnimator(new DefaultItemAnimator());

        if(newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), mData,user);
        }
        rv.setAdapter(newsAdapter);
    }

    public void RequestByVolley(String URL) {
        final String urlString = URL;
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
                RequestByVolley(urlString);
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("apikey", "317097383946af5304db9828d7c2ff99");
                return headers;
            }
        };
        //requestQueue.add(request1);
        request1.setTag(channel.getName());
        request1.setRetryPolicy(new DefaultRetryPolicy(5000,5,2));
        MyApplication.getHttpQueues().add(request1);
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause() called with: " + "");
        MyApplication.getHttpQueues().cancelAll(channel.getName());
        super.onPause();
    }

    private void initData(JSONObject response) {
        try {
            //获得新闻数组
            JSONArray jsonArray = response.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("contentlist");
            JSONObject jo;
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                jo = (JSONObject) jsonArray.get(i);
                News news = new News();
                news.setChannelId(jo.getString("channelId"));
                news.setChannelName(jo.getString("channelName"));
                news.setContent(jo.getString("content"));
                news.setDesc(jo.getString("desc"));
                news.setHavePic(jo.getBoolean("havePic"));
                if (jo.getBoolean("havePic")) {
                    List<String> imgUrls = new ArrayList<>();
                    JSONArray jsonArray1 = jo.getJSONArray("imageurls");
                    JSONObject jo2;
                    int jaLength = jsonArray1.length();
                    for (int j = 0; j < jaLength; j++) {
                        jo2 = (JSONObject) jsonArray1.get(j);
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
       /* if(newsAdapter == null) {
            newsAdapter = new NewsAdapter(getContext(), mData,user);
        }
        rv.setAdapter(newsAdapter);*/
    }

    private void QueryAndInsert(News news1) {
        final News news = news1;
        BmobQuery<News> bmobQuery = new BmobQuery<News>();
        bmobQuery.addWhereEqualTo("title", news.getTitle());
        bmobQuery.findObjects(new FindListener<News>() {
            @Override
            public void done(List<News> list, BmobException e) {
                if (e == null) {
                    //数据库中不存在，则存入
                    if (list.size() <= 0) {
                        //News news1 = news;
                        news.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    //Log.d(TAG, "done: " + "数据添加成功！");
                                    mData.add(0,news);
                                    newsAdapter.notifyDataSetChanged();
                                } else {
                                   // Log.d(TAG, "done: " + "数据添加失败！"+e.getLocalizedMessage());
                                }
                            }
                        });
                    } else {
                      //  Log.d(TAG, "done: " + "数据已存在！");
                        if(firstVolley && !mData.contains(list.get(0))){
                            //Log.d(TAG, "done: " + "添加标题"+list.get(0).getTitle());
                            mData.add(list.get(0));
                            newsAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.d(TAG, "done: 加载网络数据失败："+channel.getName()  + e.getMessage().toString());
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

}

package com.gmail.avereshchaga.mylenta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.avereshchaga.mylenta.model.News;
import com.gmail.avereshchaga.mylenta.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends Activity {

    private ListView dataListView;
    private NewsItemAdapter dataAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<News> listNews;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<News> news = null;
            if (intent != null && intent.getParcelableArrayListExtra(Utils.KEY_LIST) != null) {
                news = intent.getParcelableArrayListExtra(Utils.KEY_LIST);
            }
            if (news != null && !news.isEmpty()) {
                Utils.sortAndTrimList(listNews, news);
                dataAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoader();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null || !savedInstanceState.containsKey(Utils.KEY_SAVE_LIST)) {
            listNews = new ArrayList<>();
        } else {
            listNews = savedInstanceState.getParcelableArrayList(Utils.KEY_SAVE_LIST);
        }
        dataListView = (ListView) this.findViewById(R.id.postListView);
        dataAdapter = new NewsItemAdapter(this, R.layout.news_line_item, listNews);
        dataListView.setAdapter(dataAdapter);
        dataListView.setOnItemClickListener(new MyOnItemClickListener());
        initSwipeRefreshLayout();
        //Subscribes to the events of our service
        registerReceiver(receiver, new IntentFilter(DownloadService.CHANNEL));
        if (listNews == null || listNews.isEmpty()) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setRefreshing(true);
            getNews();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Utils.KEY_SAVE_LIST, listNews);
        super.onSaveInstanceState(outState);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.red);
    }

    private void getNews() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            DownloadService.startActionDownload(this, Utils.URL_LENTA_RU);
            DownloadService.startActionDownload(this, Utils.URL_GAZETA_RU);
        } else {
            Crouton.makeText(this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView descriptionView = (TextView) arg1.findViewById(R.id.tv_description_label);
            descriptionView.setVisibility(descriptionView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
}

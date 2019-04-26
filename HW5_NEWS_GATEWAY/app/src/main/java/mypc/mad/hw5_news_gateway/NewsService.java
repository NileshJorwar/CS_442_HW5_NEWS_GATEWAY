package mypc.mad.hw5_news_gateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver serviceReceiver;
    private ArrayList<NewsArticleBean> articleList = new ArrayList<>();
    public static final String ACTION_MSG_TO_SVC = "BROADCAST TO NEWS SERVICE";
    public static final String ACTION_NEWS_STORY = "BROADCAST FROM NEWS SERVICE";
    public static final String MSG_FROM_NS = "MSG_FROM_NS";

    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        //Creating new thread for my service
        //ALWAYS write your long running tasks in a separate thread, to avoid ANR
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SVC);
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendBroadcastToMain();
                }
                Log.d(TAG, "run: Ending loop");
            }
        }).start();
        return Service.START_STICKY;
    }

    private void sendBroadcastToMain() {
        Log.d(TAG, "sendBroadcastToMain: " + articleList.size());
        try {
            if (articleList.isEmpty()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //Creating Intent for MainActivity
                Log.d(TAG, "run: Intent");
                Intent broadCastIntent = new Intent();
                broadCastIntent.setAction(ACTION_NEWS_STORY);
                broadCastIntent.putExtra(MSG_FROM_NS, articleList);
                sendBroadcast(broadCastIntent);
                //articleList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<NewsArticleBean> listIn) {
        Log.d(TAG, "setArticles: " + listIn.size());
        articleList.clear();
        articleList.addAll(listIn);
    }

    ////////////////////////////////////////////////////////////////////
    public class ServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SVC:
                    NewsBean newsBeanfromMain = null;
                    if (intent.hasExtra(MainActivity.MSG_DATA)) {
                        newsBeanfromMain = (NewsBean) intent.getSerializableExtra(MainActivity.MSG_DATA);
                        new NewsArticleDownloader(NewsService.this, newsBeanfromMain).execute();
                    }
                    Log.d(TAG, "onReceive: " + newsBeanfromMain.getName());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }

    }
}

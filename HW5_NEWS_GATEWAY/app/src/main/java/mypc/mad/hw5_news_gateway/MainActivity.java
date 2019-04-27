package mypc.mad.hw5_news_gateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Menu opt_menu;
    private ArrayList<NewsBean> newsSrcList = new ArrayList<>();
    private ArrayList<NewsArticleBean> articleListFromNS = new ArrayList<>();
    private HashMap<String, ArrayList<NewsBean>> newsData = new HashMap<>();
    public static final String ACTION_MSG_TO_SVC = "BROADCAST TO NEWS SERVICE";
    public static final String ACTION_NEWS_STORY = "BROADCAST FROM NEWS SERVICE";
    static final String MSG_DATA = "MSG_DATA";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver newsReceiver;
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start service for getting News Sources
        Intent intent = new Intent(this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();
        IntentFilter filterNews = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filterNews);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackground(null);
                        NewsBean news = newsSrcList.get(position);
                        //Creating Intent for NewsService
                        Intent broadCastIntent = new Intent();
                        broadCastIntent.setAction(ACTION_MSG_TO_SVC);
                        broadCastIntent.putExtra(MSG_DATA, news);
                        sendBroadcast(broadCastIntent);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );


        //View Pager
        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.container);
        pager.setAdapter(pageAdapter);
        // Add a background image to the view pager
        //pager.setBackground(getResources().getDrawable(R.mipmap.news_background, this.getTheme()));

        // Load the data
        if (newsData.isEmpty())
            new NewsSourceDownloader(this).execute();


    }


    public void updateNewsData(ArrayList<NewsBean> listIn) {
        Log.d(TAG, "updateNewsData: ");
        for (NewsBean n : listIn) {
            if (n.getCategory().trim().isEmpty()) {
                n.setCategory("Unspecified");
            }
            if (!newsData.containsKey(n.getCategory())) {
                newsData.put(n.getCategory(), new ArrayList<NewsBean>());
            }
            newsData.get(n.getCategory()).add(n);
        }

        newsData.put("All", listIn);

        ArrayList<String> tempList = new ArrayList<>(newsData.keySet());
        Collections.sort(tempList);
        for (String s : tempList) {
            opt_menu.add(s);
        }

        /*
         * Setting Options Menu Item Text Color
         **/
        String colorCodes[] = {"#000000", "#f9d418", "#838fea", "#158c13", "#f9042d", "#6fbdf2", "#242b60", "#f435ce"};
        Log.d(TAG, "updateNewsData: Size" + opt_menu.size());
        if (opt_menu.size() != 0) {
            for (int i = 0; i < opt_menu.size(); i++) {
                MenuItem item = opt_menu.getItem(i);
                SpannableString s = new SpannableString(item.getTitle());
                s.setSpan(new ForegroundColorSpan(Color.parseColor(colorCodes[i])), 0, s.length(), 0);
                item.setTitle(s);
            }
        }

        newsSrcList.addAll(listIn);
        mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList));
        /*mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                NewsBean newsBean = newsSrcList.get(position);
                TextView text = view.findViewById(android.R.id.text1);
                Log.d(TAG, "getView: " + text);
                SpannableString s = new SpannableString(newsBean.getName());
                s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                Log.d(TAG, "getView: ");
                text.setText("" + s);
                return view;
            }
        });*/

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item.toString());
            return true;
        }

        setTitle(item.getTitle().toString());
        newsSrcList.clear();
        if (newsData != null) {
            newsSrcList.addAll(newsData.get(item.getTitle().toString()));

        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        return true;
    }

    ////////////////////////////////////////////////////////////////////
    public class NewsReceiver extends BroadcastReceiver {
        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case NewsService.ACTION_NEWS_STORY:
                    if (intent.hasExtra(NewsService.MSG_FROM_NS)) {
                        articleListFromNS = (ArrayList<NewsArticleBean>) intent.getSerializableExtra(NewsService.MSG_FROM_NS);
                        displayPager();
                    }
                    Log.d(TAG, "onReceive: " + articleListFromNS.size());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }

    }

    public void displayPager() {

        Log.d(TAG, "displayPager: ");
        for (int i = 0; i < articleListFromNS.size(); i++) {
            if (articleListFromNS.get(i).getSrcName() != null) {
                setTitle(articleListFromNS.get(i).getSrcName());
                break;
            }
        }

        // Tell page adapter that all pages have changed
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        // Clear the fragments list
        fragments.clear();

        // Create "n" new fragments where "n" is the menu selection "item" passed in.
        for (int i = 0; i < articleListFromNS.size(); i++) {
            fragments.add(NewsFragment.newInstance(articleListFromNS.get(i), i + 1, articleListFromNS.size()));
        }

        // Tell the page adapter that the list of fragments has changed
        pageAdapter.notifyDataSetChanged();

        // Set the first fragment to display
        pager.setCurrentItem(0);
        //pager.setBackground(null);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private static final String TAG = "MyPageAdapter";
        private long baseId = 0;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsArticleBean.class);
        stopService(intent);
        super.onDestroy();
    }
}

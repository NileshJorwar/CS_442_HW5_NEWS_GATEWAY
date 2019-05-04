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
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Menu opt_menu;
    private boolean flag = false;
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
    private final ArrayList<String> tempCopyList = new ArrayList<>();
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private String colorCodes[] = {"#000000", "#f9d418", "#838fea", "#158c13", "#f9042d", "#6fbdf2", "#242b60", "#f435ce", "#3d1b1b", "#ef550e", "#3bef0e", "#0eefdc", "#0e55ef", "#ef0e91", "#330101", "#776767"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: restoreState" + newsData.size() + " /" + newsSrcList.size() + " / flag " + flag);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start service for getting News Sources
        Intent intent = new Intent(this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: ");
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


        if (newsData.isEmpty()) {
            new NewsSourceDownloader(this, "").execute();
        }


    }


    public void setSources(ArrayList<NewsBean> listIn) {
        Log.d(TAG, "onCreate: restoreState setSources" + newsData.size() + " / " + newsSrcList.size() + " /flag" + flag);
        if (!flag) {
            if (newsData.isEmpty()) {
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
                tempCopyList.addAll(tempList);
                for (String s : tempList) {
                    opt_menu.add(s);
                }

                /*
                 * Setting Options Menu Item Text Color
                 **/

                if (opt_menu.size() != 0) {
                    for (int i = 0; i < opt_menu.size(); i++) {
                        MenuItem item = opt_menu.getItem(i);
                        SpannableString s = new SpannableString(item.getTitle());
                        s.setSpan(new ForegroundColorSpan(Color.parseColor(colorCodes[i])), 0, s.length(), 0);
                        item.setTitle(s);
                    }
                }

                newsSrcList.addAll(listIn);
                mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        NewsBean newsBean = newsSrcList.get(position);
                        TextView text = view.findViewById(R.id.text_view);
                        text.setText(newsBean.toString());
                        text.setTextColor(Color.parseColor(newsBean.getColor(tempCopyList.indexOf(newsBean.getCategory()))));
                        return view;
                    }
                });

            } else {

                newsSrcList.clear();
                newsSrcList.addAll(listIn);
                mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        NewsBean newsBean = newsSrcList.get(position);
                        TextView text = view.findViewById(R.id.text_view);
                        text.setText(newsBean.toString());
                        text.setTextColor(Color.parseColor(newsBean.getColor(tempCopyList.indexOf(newsBean.getCategory()))));
                        return view;
                    }
                });

                ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }// flag===false

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

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreate: restoreState options");
        getMenuInflater().inflate(R.menu.opt_menu, menu);

        opt_menu = menu;
        opt_menu.clear();
        return true;
    }

    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onCreate: restoreState Item" + " /" + flag);
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        setTitle(item.getTitle().toString());
        if (!item.getTitle().toString().trim().equalsIgnoreCase("All")) {
            Log.d(TAG, "onOptionsItemSelected: inside not all");
            new NewsSourceDownloader(this, item.getTitle().toString()).execute();
        } else {
            if (newsData != null) {
                Log.d(TAG, "onOptionsItemSelected: inside all");
                newsSrcList.clear();
                newsSrcList.addAll(newsData.get(item.getTitle().toString()));
                ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////
    private int count = 0;

    public class NewsReceiver extends BroadcastReceiver {
        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Jorwar_onReceive: " + count++);
            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case NewsService.ACTION_NEWS_STORY:
                    articleListFromNS.clear();
                    if (intent.hasExtra(NewsService.MSG_FROM_NS)) {
                        articleListFromNS.addAll((ArrayList<NewsArticleBean>) intent.getSerializableExtra(NewsService.MSG_FROM_NS));
                    }
                    MainActivity.this.reDoFragments();
                    Log.d(TAG, "onReceive: " + articleListFromNS.size());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }
    }

    public void reDoFragments() {

        Log.d(TAG, "reDoFragments: ");
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
    public void onStop() {
        unregisterReceiver(newsReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        IntentFilter filterNews = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filterNews);
        super.onResume();
    }

    //Extra Functionality
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onCreate: restoreState save");
        Log.d(TAG, "onSaveInstanceState: " + articleListFromNS.size());
        outState.putSerializable("DRAWERLIST", newsSrcList);
        outState.putSerializable("NEWSDATA", newsData);
        outState.putBoolean("FLAGV", true);
        outState.putSerializable("ARTICLELIST", articleListFromNS);
        outState.putString("TITLE", getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: restoreState restore");
        super.onRestoreInstanceState(savedInstanceState);
        //flag = savedInstanceState.getBoolean("FLAGV");
        HashMap<String, ArrayList<NewsBean>> restoredHmap;
        restoredHmap = (HashMap<String, ArrayList<NewsBean>>) savedInstanceState.getSerializable("NEWSDATA");
        newsData.putAll(restoredHmap);
        ArrayList<NewsBean> restoredNewsSrcList;
        restoredNewsSrcList = (ArrayList<NewsBean>) savedInstanceState.getSerializable("DRAWERLIST");
        newsSrcList.addAll(restoredNewsSrcList);
        setOrientationChanges();
        ArrayList<NewsArticleBean> restoredArticleList;
        restoredArticleList = (ArrayList<NewsArticleBean>) savedInstanceState.getSerializable("ARTICLELIST");
        articleListFromNS.addAll(restoredArticleList);
        if (articleListFromNS.isEmpty())
            setTitle(savedInstanceState.getString("TITLE"));
        else
            reDoFragments();
    }

    public void setOrientationChanges() {
        Log.d(TAG, "setOrientationChanges: ");
        ArrayList<String> tempList = new ArrayList<>(newsData.keySet());
        Collections.sort(tempList);
        tempCopyList.addAll(tempList);
        //newsSrcList.addAll(listIn);
        Log.d(TAG, "setSources: srcl " + newsSrcList.size());
        mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                NewsBean newsBean = newsSrcList.get(position);
                TextView text = view.findViewById(R.id.text_view);
                text.setText(newsBean.toString());
                text.setTextColor(Color.parseColor(newsBean.getColor(tempCopyList.indexOf(newsBean.getCategory()))));
                return view;
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}

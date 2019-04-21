package mypc.mad.hw5_news_gateway;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Menu opt_menu;
    private ArrayList<NewsBean> newsSrcList = new ArrayList<>();
    private HashMap<String, ArrayList<NewsBean>> newsData = new HashMap<>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        NewsBean news = newsSrcList.get(position);
                        Intent intent = new Intent(MainActivity.this, NewsInfoActivity.class);
                        intent.putExtra(NewsBean.class.getName(), news);
                        startActivity(intent);
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
        mDrawerList.setAdapter(new ArrayAdapter<NewsBean>(this, R.layout.drawer_item, newsSrcList) {
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
        });

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

}

package mypc.mad.hw5_news_gateway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    public static final String NEWS_ARTICLE = "NEWS_ARTICLE";

    public NewsFragment() {
    }

    public static final NewsFragment newInstance(NewsArticleBean newsArticleBean, int index, int max) {
        NewsFragment f = new NewsFragment();
        //Constructs a new, empty Bundle sized to hold the given number of elements. (1)
        Bundle bdl = new Bundle(1);
        bdl.putSerializable(NEWS_ARTICLE, newsArticleBean);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the string to display from the arguments bundle
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        final NewsArticleBean newsArticleBean = (NewsArticleBean) getArguments().getSerializable(NEWS_ARTICLE);
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");

        Log.d(TAG, "onCreateView: Image" + newsArticleBean.getUrlToImage());
        TextView headLineView = v.findViewById(R.id.headLineView);
        headLineView.setText(newsArticleBean.getTitle());
        headLineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToOpenBrowser(newsArticleBean.getUrl());
            }
        });

        TextView dateView = v.findViewById(R.id.dateView);
        if (!newsArticleBean.getPublishedAt().equalsIgnoreCase("null")) {
            dateView.setText(newsArticleBean.getPublishedAt());
        } else {
            dateView.setVisibility(View.GONE);
        }

        TextView authorView = v.findViewById(R.id.authorView);
        if (!newsArticleBean.getAuthor().equalsIgnoreCase("null")) {
            authorView.setText(newsArticleBean.getAuthor());
        } else {
            authorView.setVisibility(View.GONE);
        }

        if (!newsArticleBean.getDescription().equalsIgnoreCase("null")) {

            TextView descriptionView = v.findViewById(R.id.articleTextView);
            descriptionView.setMovementMethod(new ScrollingMovementMethod());
            descriptionView.setText(newsArticleBean.getDescription());
            descriptionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickToOpenBrowser(newsArticleBean.getUrl());
                }
            });
        }

        TextView pageNum = v.findViewById(R.id.countView);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

        Picasso picasso = new Picasso.Builder(getContext()).build();
        ImageView imgView = v.findViewById(R.id.imgView);
        if (!newsArticleBean.getUrlToImage().equalsIgnoreCase("null")) {
            try {
                picasso.load(newsArticleBean.getUrlToImage().trim())
                        .error(R.mipmap.no_image)
                        .placeholder(R.drawable.placeholder)
                        .into(imgView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToOpenBrowser(newsArticleBean.getUrl());
            }
        });

        return v;
    }

    public void clickToOpenBrowser(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
        startActivity(intent);
    }
}

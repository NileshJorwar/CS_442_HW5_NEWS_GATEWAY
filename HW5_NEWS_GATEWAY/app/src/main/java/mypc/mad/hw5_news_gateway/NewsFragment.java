package mypc.mad.hw5_news_gateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class NewsFragment extends Fragment {

    public static final String NEWS_ARTICLE = "NEWS_ARTICLE";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final NewsArticleBean newsArticleBean = (NewsArticleBean) getArguments().getSerializable(NEWS_ARTICLE);
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");

        // Get the string to display from the arguments bundle
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        TextView headLineView = v.findViewById(R.id.headLineView);
        headLineView.setText(newsArticleBean.getTitle());
        headLineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlag(newsArticleBean.getUrl());
            }
        });

        TextView dateView = v.findViewById(R.id.dateView);
        dateView.setText(newsArticleBean.getPublishedAt());

        TextView authorView = v.findViewById(R.id.authorView);
        authorView.setText(newsArticleBean.getAuthor());

        TextView descriptionView = v.findViewById(R.id.articleTextView);
        descriptionView.setText(newsArticleBean.getDescription());
        descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlag(newsArticleBean.getUrl());
            }
        });

        TextView pageNum = v.findViewById(R.id.countView);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));
        return v;
    }

    public void clickFlag(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
        startActivity(intent);
    }
}

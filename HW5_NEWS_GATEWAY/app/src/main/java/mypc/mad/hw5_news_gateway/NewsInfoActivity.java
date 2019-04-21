package mypc.mad.hw5_news_gateway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NewsInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);
        Intent intent = getIntent();
        if (intent.hasExtra(NewsBean.class.getName())) {
            NewsBean c = (NewsBean) intent.getSerializableExtra(NewsBean.class.getName());
            TextView newsChannel = findViewById(R.id.newsChannel);
            newsChannel.setText(c.getName());
        }

    }
}

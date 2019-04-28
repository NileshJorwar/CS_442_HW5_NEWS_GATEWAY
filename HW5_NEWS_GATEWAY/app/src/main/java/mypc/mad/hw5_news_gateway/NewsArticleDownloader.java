package mypc.mad.hw5_news_gateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class NewsArticleDownloader extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsArticleDownloader";
    private NewsBean newsBean;
    private NewsService newsService;
    private static final String dataURL = "https://newsapi.org/v2/everything?sources=";
    private static final String dataURL2 = "&language=en&pageSize=100&apiKey=3aeab6df76cb4d9e9f7baa24357d0a93";


    NewsArticleDownloader(NewsService newsService, NewsBean newsBean) {
        this.newsService = newsService;
        this.newsBean = newsBean;
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<NewsArticleBean> newsStoryList = parseJSON(s);
        if (newsStoryList != null) {
            Log.d(TAG, "onPostExecute: " + newsStoryList.size());
            newsService.setArticles(newsStoryList);
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String formedUrl = dataURL + newsBean.getId().trim() + dataURL2;
        Uri dataUri = Uri.parse(formedUrl);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception " + e);
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }


    private ArrayList<NewsArticleBean> parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");
        ArrayList<NewsArticleBean> newsStoryList = new ArrayList<>();
        try {
            JSONObject jNewsSrc = new JSONObject(s);
            String source = null;
            String srcName = null;
            String status = jNewsSrc.getString("status");
            if (status.trim().equalsIgnoreCase("ok")) {
                String sources = jNewsSrc.getString("articles");
                if (sources != null) {
                    JSONArray jObjMain = new JSONArray(sources);
                    for (int i = 0; i < jObjMain.length(); i++) {
                        JSONObject jSource = (JSONObject) jObjMain.get(i);
                        if (source == null) {
                            source = jSource.getString("source");
                            JSONObject srcJson = new JSONObject(source);
                            srcName = srcJson.getString("name");
                        }
                        String author = jSource.getString("author");
                        String title = jSource.getString("title");
                        String description = jSource.getString("description");
                        String url = jSource.getString("url");
                        String urlToImage = jSource.getString("urlToImage");
                        String publishedAt = jSource.getString("publishedAt");
                        String publishedDate="";
                        if (!publishedAt.trim().isEmpty()) {
                            SimpleDateFormat srcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                            Date srcDate = srcFmt.parse(publishedAt);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                            publishedDate = targetFormat.format(srcDate);
                        }
                        newsStoryList.add(new NewsArticleBean(srcName, author, title, description, url, urlToImage, publishedDate));
                    }
                }
            }
            return newsStoryList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

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
import java.util.ArrayList;

public class NewsSourceDownloader extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsSourceDownloader";
    private MainActivity mainActivity;

    private static final String dataURL = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String dataURL2 = "&apiKey=3aeab6df76cb4d9e9f7baa24357d0a93";
    private String category;

    NewsSourceDownloader(MainActivity ma, String category) {
        mainActivity = ma;
        this.category = category;
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<NewsBean> newsSrcList = parseJSON(s);

        if (newsSrcList != null) {
            Log.d(TAG, "onPostExecute: " + newsSrcList.size());
            mainActivity.setSources(newsSrcList);
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String finalUrl = dataURL + category + dataURL2;
        Log.d(TAG, "doInBackground: final URL" + finalUrl);
        Uri dataUri = Uri.parse(finalUrl);
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


    private ArrayList<NewsBean> parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");
        ArrayList<NewsBean> newsSrcList = new ArrayList<>();
        try {
            JSONObject jNewsSrc = new JSONObject(s);
            String status = jNewsSrc.getString("status");
            if (status.trim().equalsIgnoreCase("ok")) {
                String sources = jNewsSrc.getString("sources");
                if (sources != null) {
                    JSONArray jObjMain = new JSONArray(sources);
                    for (int i = 0; i < jObjMain.length(); i++) {
                        JSONObject jSource = (JSONObject) jObjMain.get(i);
                        String id = jSource.getString("id");
                        String name = jSource.getString("name");
                        String url = jSource.getString("url");
                        String category = jSource.getString("category");
                        newsSrcList.add(new NewsBean(id, name, url, category));
                    }
                }
            }
            return newsSrcList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}


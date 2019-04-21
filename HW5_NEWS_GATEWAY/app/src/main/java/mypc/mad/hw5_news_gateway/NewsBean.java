package mypc.mad.hw5_news_gateway;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class NewsBean implements Serializable {
    private String id;
    private String name;
    private String category;
    private String url;

    public NewsBean(String id, String name, String url,String category) {
        this.id = id;
        this.name = name;
        this.url=url;
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @NonNull
    public String toString() {
        return name;
    }
}

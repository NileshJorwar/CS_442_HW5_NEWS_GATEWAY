package mypc.mad.hw5_news_gateway;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class NewsBean implements Serializable {
    private String id;
    private String name;
    private String category;
    private String url;
    private String colorCodes[] = {"#000000", "#f9d418", "#838fea", "#158c13", "#f9042d", "#6fbdf2", "#242b60", "#f435ce","#3d1b1b","#ef550e","#3bef0e","#0eefdc","#0e55ef","#ef0e91","#330101","#776767"};

    public NewsBean(String id, String name, String url, String category) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getColor(int i) {

        if(i<colorCodes.length)
            return colorCodes[i];
        return "#FFFFFF";
        /*
        if (category.equalsIgnoreCase("all")) {
            color = colorCodes[0];
        }
        else if (category.equalsIgnoreCase("business")) {
            color = colorCodes[1];
        }
        else if (category.equalsIgnoreCase("entertainment")) {
            color = colorCodes[2];
        }
        else if (category.equalsIgnoreCase("general")) {
            color = colorCodes[3];
        }
        else if (category.equalsIgnoreCase("health")) {
            color = colorCodes[4];
        }
        else if (category.equalsIgnoreCase("science")) {
            color = colorCodes[5];
        }
        else if (category.equalsIgnoreCase("sports")) {
            color = colorCodes[6];
        }
        else if (category.equalsIgnoreCase("technology")) {
            color = colorCodes[7];
        }
        else
        {
            color="#FFFFFF";
        }
        return color;*/
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

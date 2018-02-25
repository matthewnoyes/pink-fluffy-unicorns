package virtualassistant.data.news;

import java.util.Calendar;

public class NewsObj{
    private Calendar DateTime;
    private String title;
    private String impact;
    private String url;
    private String source;

    public NewsObj(Calendar time, String title, String impact, String url,String source){ // for Rns news
        this.DateTime = time;
        this.title = title;
        this.impact = impact;
        this.url = url;
        this.source = source;
    }

    public NewsObj(Calendar time, String title, String url,String source){ // for other types of news articles
        this.DateTime = time;
        this.title = title;
        this.url = url;
        this.source = source;
    }

    public Calendar getDateTime(){
        return this.DateTime;
    }
    public String getTitle(){
        return this.title;
    }
    public String getImpact(){
        return this.impact;
    }
    public String getUrl(){
        return this.url;
    }
    public String getSource(){
        return this.source;
    }
}

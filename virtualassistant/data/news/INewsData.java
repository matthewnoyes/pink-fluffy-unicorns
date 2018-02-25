package virtualassistant.data.news;
import java.util.ArrayList;

//Manages all the news data
  public interface INewsData {
    public ArrayList<NewsObj> getRnsNews(String company);
    public ArrayList<NewsObj> getAllianceNews(String company);
    public ArrayList<NewsObj> getYahooNews(String comapny);
    public ArrayList<NewsObj> sectorNews(String sector);
}

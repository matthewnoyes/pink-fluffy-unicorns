package virtualassistant.data.news;
import java.util.ArrayList;

import java.io.IOException;
import java.text.ParseException;

//Manages all the news data
  public interface INewsData {
    public ArrayList<NewsObj> getRnsNews(String company) throws IOException, ParseException;
    public ArrayList<NewsObj> getAllianceNews(String company) throws IOException, ParseException;
    public ArrayList<NewsObj> getYahooNews(String comapny) throws IOException, ParseException;
    public ArrayList<NewsObj> sectorNews(String sector) throws IOException, ParseException;
}

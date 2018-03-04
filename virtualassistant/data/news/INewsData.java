package virtualassistant.data.news;
import java.util.LinkedList;

import java.io.IOException;
import java.text.ParseException;

//Manages all the news data
  public interface INewsData {
    
    public LinkedList<NewsObj> getAllNews(String company) throws IOException, ParseException;
    public LinkedList<NewsObj> getRnsNews(String company) throws IOException, ParseException;
    public LinkedList<NewsObj> getAllianceNews(String company) throws IOException, ParseException;
    public LinkedList<NewsObj> getYahooNews(String comapny) throws IOException, ParseException;
    public LinkedList<NewsObj> sectorNews(String sector) throws IOException, ParseException;
}

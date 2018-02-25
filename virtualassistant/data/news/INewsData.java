package virtualassistant.data.news;

//Manages all the news data
  public interface INewsData {
    public ArrayList<newsObj> getRnsNews(String company);
    public ArrayList<newsObj> getAllianceNews(String company);
    public ArrayList<newsObj> getYahooNews(String comapny);
    public ArrayList<newsObj> sectorNews(String sector);
}

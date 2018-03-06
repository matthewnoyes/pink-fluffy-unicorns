package virtualassistant.ai;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Calendar;
//For testing
import java.util.Arrays;

import virtualassistant.data.stocks.IStockData;
import virtualassistant.data.stocks.ICompany;
import virtualassistant.data.news.*;

import java.io.IOException;
import java.text.ParseException;

public class LearningAgent implements ILearningAgent {

  private Favourites<String, Integer> favouriteStocks;
  private HashMap<String, Calendar> stockNotifications;
  private Calendar lastNewsUpdateTime;

  private IStockData stocks;
  private INewsData news;

  private final double minStockImpact = 2.5;
  private final double minNewsImpact = 2.5;

  public LearningAgent(IStockData stocks, INewsData news) {
    favouriteStocks = new Favourites<String, Integer>();
    stockNotifications = new HashMap<String, Calendar>();

    this.stocks = stocks;
    this.news = news;
  }

  public LearningAgent(IStockData stocks, INewsData news, Favourites<String, Integer> favouriteStocks) {

    if(favouriteStocks == null) {
        favouriteStocks = new Favourites<String, Integer>();
    }
    stockNotifications = new HashMap<String, Calendar>();

    this.favouriteStocks = favouriteStocks;

    this.stocks = stocks;
    this.news = news;

  }

  public void analyzeInput(String name) {

    System.out.println("Analyze input name: " + name);
    favouriteStocks.addToBegining(name, 1);
  }

  //Upate to have number be order - high last element
  public Favourites<String, Integer> getFavouriteStocks() {

    int place = favouriteStocks.size() - 1;

    //Iterates through oldest to newest
    for (String ticker : favouriteStocks.keySet()) {
      favouriteStocks.put(ticker, place);
      place--;
    }

    return favouriteStocks;
  }

  public void bookmarkStock(String ticker) {

  }

  /**
   * Gets the number of suggested queries
   *
   * If the user has less favourites than the requested amount, the function
   * will only return a array of size of the number of favourites
   */
  public String[] suggestQueries(int count) {

    Set<String> tickers = favouriteStocks.keySet();

    String[] queries;
    //Check that there is enough tickers to fill array
    if (tickers.size() < count) {
      queries = new String[tickers.size()];
    } else {
      queries = new String[count];
    }

    int i = 0;
    ListIterator<String> iterator = new ArrayList<String>(tickers).listIterator(favouriteStocks.size());

    while (iterator.hasPrevious()) {
      String ticker = iterator.previous();
      //Search for something interesting

      String query = "What is the current price of " + ticker;

      queries[i] = query;
      i++;

      //If we have enough queries, finish
      if (i >= queries.length) {
        break;
      }

    }

    return queries;
  }

  public String searchForStockEvent() {

    String alerts = "";

    Calendar removeTime = Calendar.getInstance();
    //Send notification every 3 hours
    removeTime.add(Calendar.HOUR, -3);

    //Check sectors
    for (String sector : stocks.getSectors()) {

      //Make sure the user has not been notified recently
      if (!stockNotifications.containsKey(sector)) {

        if (stocks.getSectorPercentageChange(sector) > minStockImpact) {
          alerts += sector + " is gaining price quickly\n";
          stockNotifications.put(sector, Calendar.getInstance());
        } else if (stocks.getSectorPercentageChange(sector) < -minStockImpact) {
          alerts += sector + " is losing price quickly\n";
          stockNotifications.put(sector, Calendar.getInstance());
        }
      } else {
        if (stockNotifications.get(sector).before(removeTime)) {
          stockNotifications.remove(sector);
        }
      }
    }

    for (ICompany com : stocks.getAllCompanies()) {

      if (!stockNotifications.containsKey(com.getTicker()) && !stockNotifications.containsKey(com.getSector())) {

        //Check the favourite stocks contain this stock
        if (favouriteStocks.containsKey(com.getTicker())) {
          if (com.getPercentageChange() > minStockImpact) {
            alerts += com.getName() + " is gaining price quickly\n";
            stockNotifications.put(com.getTicker(), Calendar.getInstance());
          } else if (com.getPercentageChange() < -minStockImpact) {
            alerts += com.getName() + " is losing price quickly\n";
            stockNotifications.put(com.getTicker(), Calendar.getInstance());
          }
        }

      } else {
        if (stockNotifications.containsKey(com.getTicker()) && stockNotifications.get(com.getTicker()).before(removeTime)) {
          stockNotifications.remove(com.getTicker());
        }
      }
    }

    return alerts;

  }

  public String searchForNewsEvent() throws IOException, ParseException {

    if (lastNewsUpdateTime == null) {
      //If first check, get data from past day
      lastNewsUpdateTime = Calendar.getInstance();
      lastNewsUpdateTime.add(Calendar.DAY_OF_YEAR, -5);
    }

    String alerts = "";
    for (ICompany com : stocks.getAllCompanies()) {

      //Test if the company is in the users favourites
      if (favouriteStocks.containsKey(com.getTicker())) {

        //Get news for company
        for (NewsObj article : news.getRnsNews(com.getTicker())) {
          //Check that the news has not just been flagged
          if (article.getDateTime().after(lastNewsUpdateTime)) {
            try {
              String impact = article.getImpact();
              if (impact.length() > 1) {
                if (Math.abs(Double.parseDouble(impact.substring(0, impact.length() - 1))) > minNewsImpact) {
                  //Send a alert
                  String[] title = article.getTitle().split(" ", 3);

                  alerts += "There is significant news on " + title[0] + ": " + title[2] + "\n";
                  alerts += article.getUrl() + "\n";
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    // for (String sector : stocks.getSectors()) {
    //   for (NewsObj article : news.sectorNews(sector)) {
    //     try {
    //       String impact = article.getImpact();
    //       if (impact.length() > 1) {
    //         if (Math.abs(Double.parseDouble(impact.substring(0, impact.length() - 1))) > minNewsImpact) {
    //         //Send a alert
    //         alerts += "There is significant news: " + article.getTitle() + "\n";
    //       }
    //     }
    //     } catch (Exception e) {
    //       //Do nothing
    //     }
    //   }
    //
    // }

    lastNewsUpdateTime = Calendar.getInstance();

    return alerts;

  }

  /*public static void main(String[] args) {
    // Favourites<String, Integer> test = new Favourites<String, Integer>();
    // String[] values = new String[]{"a", "b", "c", "d", "a", "e", "f", "g", "g", "f"};
    // for (int i = 0; i < values.length; i++) {
    //   test.addToBegining(values[i], 1);
    //   //Search for something interesting
    //
    // }
    // Set<String> tickers = test.keySet();
    // for (String ticker : tickers) {
    //   System.out.println(ticker);
    //   //Search for something interesting
    //
    // }

    // LearningAgent test = new LearningAgent();
    // String[] values = new String[]{"a", "b", "c", "d", "a", "e", "f", "g", "g", "f"};
    //
    // test.analyzeInput(Arrays.asList(values), null);
    // String[] output = test.suggestQueries(2);
    // for (int i = 0; i < output.length; i++) {
    //   System.out.println(output[i]);
    // }

  }*/
    public boolean unitTest(){
        return false;
    }

}

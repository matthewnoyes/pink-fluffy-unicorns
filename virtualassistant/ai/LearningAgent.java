package virtualassistant.ai;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Collection;
//For testing
import java.util.Arrays;

import virtualassistant.data.stocks.IStockData;
import virtualassistant.data.stocks.ICompany;
import virtualassistant.data.news.*;

import java.io.IOException;
import java.text.ParseException;

public class LearningAgent implements ILearningAgent {

  private Favourites<String, Integer> favouriteStocks;

  private IStockData stocks;
  private INewsData news;

  private final double minStockImapact = 20.0;
  private final double minNewsImapact = 20.0;

  public LearningAgent(IStockData stocks, INewsData news) {
    favouriteStocks = new Favourites<String, Integer>();

    this.stocks = stocks;
    this.news = news;
  }

  public LearningAgent(IStockData stocks, INewsData news, Favourites<String, Integer> favouriteStocks) {

    if(favouriteStocks == null) {
        this.favouriteStocks = new Favourites<String, Integer>();
    } else {
        this.favouriteStocks = favouriteStocks;
    }

    this.stocks = stocks;
    this.news = news;

  }

  public void analyzeInput(String ticker) {

    System.out.println("Analyze input name: " + ticker);
    favouriteStocks.addToBegining(ticker, 1);
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

    for (ICompany com : stocks.getAllCompanies()) {
      if (com.getPercentageChange() > minStockImapact && favouriteStocks.containsKey(com.getTicker())) {
        //Send a alert
        alerts += com.getName() + " is changing price quickly\n";
      }
    }

    //Check sectors

    return alerts;

  }

  public String searchForNewsEvent() throws IOException, ParseException {

    String alerts = "";
    for (ICompany com : stocks.getAllCompanies()) {
      for (NewsObj article : news.getRnsNews(com.getTicker())) {
        try {
          if (Integer.parseInt(article.getImpact()) > minNewsImapact) {
            //Send a alert
            alerts += "There is significant news: " + article.getTitle() + "\n";
          }
        } catch (Exception e) {
          //Do nothing
        }
      }

      for (NewsObj article : news.getAllianceNews(com.getTicker())) {
        try {
          if (Integer.parseInt(article.getImpact()) > minNewsImapact) {
            //Send a alert
            alerts += "There is significant news: " + article.getTitle() + "\n";
          }
        } catch (Exception e) {
          //Do nothing
        }
      }

      for (NewsObj article : news.getYahooNews(com.getTicker())) {
        try {
          if (Integer.parseInt(article.getImpact()) > minNewsImapact) {
            //Send a alert
            alerts += "There is significant news: " + article.getTitle() + "\n";
          }
        } catch (Exception e) {
          //Do nothing
        }
      }

      //Check more sources
    }

    for (String sector : stocks.getSectors()) {
      for (NewsObj article : news.sectorNews(sector)) {
        try {
          if (Integer.parseInt(article.getImpact()) > minNewsImapact) {
            //Send a alert
            alerts += "There is significant news: " + article.getTitle() + "\n";
          }
        } catch (Exception e) {
          //Do nothing
        }
      }
    }



    return alerts;

  }

  public static void main(String[] args) {
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

  }


}

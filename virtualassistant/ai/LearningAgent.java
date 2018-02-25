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
import virtualassistant.data.news.INewsData;

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
    this.favouriteStocks = favouriteStocks;

    this.stocks = stocks;
    this.news = news;
  }

  public void analyzeInput(List<String> tokenized, List<String> patternized) {

    for (String item : tokenized) {

      //See if item is a company
      if (stocks.getCompanyTickers().contains(item)) {
        favouriteStocks.addToBegining(item,1);
        continue;
      }
      if (stocks.getCompanyNames().contains(item)) {
        favouriteStocks.addToBegining(stocks.getCompanyForName(item).getTicker(),1);
        continue;
      }
    }

  }

  public Set<String> getFavouriteStocks() {
    return favouriteStocks.keySet();
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

    //Note: Goes backwards
    //for (String ticker : tickers) {
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



    for (ICompany com : stocks.getAllCompanies()) {
      if (com.getPercentageChange() > minStockImapact && favouriteStocks.containsKey(com.getTicker())) {
        //Send a alert
        //xxx.alert(com.getName() + " is changing price quickly");
      }
    }

    return "";

  }

  public String searchForNewsEvent() {

    // for (NewsObj article : new news) {
    //   if (article.getImapct() > minNewsImapact && favourites.containsKey(article.com)) {
    //     //Send a alert
    //     //xxx.alert("There is significant news on: " + article.com);
    //   }
    // }

    return "";

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

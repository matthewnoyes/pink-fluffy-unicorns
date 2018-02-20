package virtualassistant.ai;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.ListIterator;
//For testing
import java.util.Arrays;

import virtualassistant.data.stocks.IStockData;
import virtualassistant.data.news.INewsData;

public class LearningAgent implements ILearningAgent {

  private Favourites<String, Integer> favourites;

  private IStockData stocks;
  private INewsData news;

  public LearningAgent() {
    favourites = new Favourites<String, Integer>();
  }

  public void analyzeInput(List<String> tokenized, List<String> patternized) {

    for (String item : tokenized) {

      //See if item is a company
      if (stocks.getCompanyTickers().contains(item)) {
        favourites.addToBegining(item,1);
        continue;
      }
      if (stocks.getCompanyNames().contains(item)) {
        favourites.addToBegining(stocks.getCompanyForName(item).getTicker(),1);
        continue;
      }
    }

  }

  public Set<String> getFavourites() {
    return favourites.keySet();
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

    Set<String> tickers = favourites.keySet();

    String[] queries;
    //Check that there is enough tickers to fill array
    if (tickers.size() < count) {
      queries = new String[tickers.size()];
    } else {
      queries = new String[count];
    }

    int i = 0;
    ListIterator<String> iterator = new ArrayList<String>(tickers).listIterator(favourites.size());
    while (iterator.hasPrevious()) {
      String ticker = iterator.previous();
    //Note: Goes backwards
    //for (String ticker : tickers) {
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

  public void searchForStockEvent() {

  }

  public void searchForNewsEvent() {

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

    LearningAgent test = new LearningAgent();
    String[] values = new String[]{"a", "b", "c", "d", "a", "e", "f", "g", "g", "f"};

    test.analyzeInput(Arrays.asList(values), null);
    String[] output = test.suggestQueries(2);
    for (int i = 0; i < output.length; i++) {
      System.out.println(output[i]);
    }

  }


}

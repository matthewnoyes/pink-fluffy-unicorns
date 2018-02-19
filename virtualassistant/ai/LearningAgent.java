package virtualassistant.ai;

import java.util.List;
import java.util.Set;

public class LearningAgent implements ILearningAgent {

  private Favourites<String, Integer> favourites;

  public LearningAgent() {
    favourites = new Favourites<String, Integer>();
  }

  public void analyzeInput(List<String> tokenized, List<String> patternized) {

  }

  public Set<String> getFavourites() {
    return favourites.keySet();
  }

  public void bookmarkStock(String ticker) {

  }

  public String[] suggestQueries(int count) {

    String[] queries = new String[count];

    Set<String> tickers = favourites.keySet();

    //Note: Goes backwards
    for (String ticker : tickers) {
      //Search for something interesting

    }

    return queries;

  }

  public void searchForStockEvent() {

  }

  public void searchForNewsEvent() {

  }

  public static void main(String[] args) {
    Favourites<String, Integer> test = new Favourites<String, Integer>();
    String[] values = new String[]{"a", "b", "c", "d", "a", "e", "f", "g", "g", "f"};
    for (int i = 0; i < values.length; i++) {
      test.addToBegining(values[i], 1);
      //Search for something interesting

    }
    Set<String> tickers = test.keySet();
    for (String ticker : tickers) {
      System.out.println(ticker);
      //Search for something interesting

    }
  }


}

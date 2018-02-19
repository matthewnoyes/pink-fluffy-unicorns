package virtualassistant.ai;

import java.util.List;

public class LearningAgent implements ILearningAgent {

  private Favourites favourites;

  public void analyzeInput(List<String> tokenized, List<String> patternized) {

  }

  public String[] getFavourites() {
    return null;
  }

  public void bookmarkStock(String ticker) {

  }

  public String[] suggestQueries(int count) {

    String[] queries = new String[count];

    favourites.startIterating();

    for (int i = 0; i < count; i++) {
      String ticker = favourites.nextIterate();

      //Search for something interesting

    }

    return queries;

  }

  public void searchForStockEvent() {

  }

  public void searchForNewsEvent() {

  }

  public static void main(String[] args) {
    Favourites test = new Favourites();
    String[] values = new String[]{"a", "b", "c", "d", "a", "e", "f"};
    for (int i = 0; i < values.length; i++) {
      test.addToFavourites(values[i]);
      //Search for something interesting

    }
    test.startIterating();
    for (int i = 0; i < 6; i++) {
      String ticker = test.nextIterate();
      System.out.println(ticker);
      //Search for something interesting

    }
  }


}

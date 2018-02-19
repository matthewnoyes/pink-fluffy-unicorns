package virtualassistant.ai;

import java.util.List;
import java.util.Set;

//The AI section
public interface ILearningAgent {

  // Take tokenized string and patternized string and compare one against the other
  public void analyzeInput(List<String> tokenized, List<String> patternized);

  //?
  public Set<String> getFavourites();
  //Lets the AI know the user has bookmarked the stock
  public void bookmarkStock(String ticker);

  public String[] suggestQueries(int count);

  //For notifications (when the data gets updated):

  //Constantly search for stock anomalies
  public void searchForStockEvent();
  //Constantly search for news anomalies
  public void searchForNewsEvent();
}

package virtualassistant.ai;

//The AI section
public interface ILearningAgent {
  
  // Take tokenized string and patternized string and compare one against the other
  public void analyzeInput(List<String> tokenized, List<String> patternized);
  
  // 
  public void getFavourites();
  
  // Constantly search for anomalies
  public static void main(String[] args);
}

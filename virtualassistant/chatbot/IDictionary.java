package virtualassistant.chatbot;

// Keywords store
public interface IDictionary {
  
  // Loads keywords from memory
  private void load();
  
  // Returns keyword of string s
  public String getKeyword(String s);
  
}

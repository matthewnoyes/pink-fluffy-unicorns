package virtualassistant.chatbot;

// Keywords store
public interface IKeyword {
  
  // Loads keywords from memory
  public void load();
  
  // Returns keyword of string s
  public String getKeyword(String s);
  
}

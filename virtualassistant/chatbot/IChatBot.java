package virtualassistant.chatbot;
import  virtualassistant.misc.*;

public interface IChatBot {
  
  // return action and parsed string as tokens
  public String getResponse(String query);
}

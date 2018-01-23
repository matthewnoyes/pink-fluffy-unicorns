package virtualassistant.chatbot;
import  virtualassistant.misc.*;

//The NLP
public interface IChatBot {
  
  // return action and parsed string as tokens
  public Pair<Integer, List<String>> parse(String s);
}

package virtualassistant.chatbot;
import  virtualassistant.misc.*;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public interface IChatBot {

  // return action and parsed string as tokens
  public String getResponse(String query) throws IOException, ParseException;
}

package virtualassistant;

import java.util.List;

public interface IVirtualAssistant {

  // Startup & update memory
  public void start();
  public void decideAction(int action, List<String> parameters);

}

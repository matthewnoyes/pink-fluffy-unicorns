package virtualassistant;

public interface IVirtualAssistant {
  
  public static void main(String args[]);
  
  // Startup & update memory
  public void start();
  public void decideAction(Action action, List<String> parameters);
 
}

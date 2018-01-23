package virtualassistant;

public interface IVirtualAssistant {
  
  public static void main(String args[]);
  
  // Startup & update memory
  public static void load();
  public static void updateMemory();
  
  // I/O
  public static Pair<String, List<String, Int>> processInput();
  
  //Actions
  public static String action1();
}

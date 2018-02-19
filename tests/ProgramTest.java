package virtualassistant.tests;

// Add tests here
public class ProgramTest {

  private AI ai;
  private NPLParser npl;
  private Dictionary dictionary;
 
  public static void main(String[] args){
  
    // INSTANTIATION   
    System.out.println("Instantiation phase...\n");
    System.out.print("Instantiating Dictionary... ");
    try {
      dictionary = new Dictionary();
      
      if(dictionary != null)
        System.out.println("SUCCESS");
      else 
        System.out.println("FAIL");
    } catch(Exception e) {
      System.out.println(e.toString());
    } 
   
    System.out.print("Instantiating NPLParser... ");
    try {
      npl = new NPLParser();
      
      if(npl != null)
        System.out.println("SUCCESS");
      else 
        System.out.println("FAIL");
    } catch(Exception e) {
      System.out.println(e.toString());
    } 
    
    System.out.print("Instantiating AI... ");
    try {
      ai = new AI();
      
      if(ai != null)
        System.out.println("SUCCESS");
      else 
        System.out.println("FAIL");
    } catch(Exception e) {
      System.out.println(e.toString());
    } 
    
    // TEST SYSTEM FUNCTIONS  
    System.out.println("Methods testing...\n");
    System.out.println("Testing Dictionary...\n");
    
    System.out.print("Testing 'dictionary.getKeyword()'... ");
    try {
      if(dictionary.getKeyword().equals(???));
        System.out.println("SUCCESS");
      else
        System.out.println("FAIL");
    } catch(Exception) {
      System.out.println(e.toString());
    }
  }
}

import java.util.*;

public class Dictionary implements IDictionary {
  
  // have some store
  
  public Dictionary() {
    load();
  }
  
  public String getKeyword(String s) {
    return null;
  }
  
  private void load(){
    JSONArray a = (JSONArray) parser.parse(new FileReader("patterns.json"));
    
    foreach(Object o : a){
      JSONObject entry = (JSONObject) o;
      
      // save entry in local memory
    }
  }
}

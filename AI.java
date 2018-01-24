import java.util.*;


// Example of AI part that parses a string and returns information

public class AI implements IAI {
    
    NLPParser parser;
    
    // Constructor
    public AI(){
        parser = new NLPParser();
    }
    
    // Call action functions
    public String parse(String s){
        
       Pair<Integer, List<String>> action =  parser.parse(s);
        
       switch(action.getSecond()){
           case 1:
                updateFavourites(action.getSecond());
                return function1(action.getSecond());  
           case 2:
                updateFavourites(action.getSecond());
                return function2(action.getSecond());  
        }
    }
    
    // Analyse ( try to make it act constantly )
    private Object analyse() {
        return null;
    }
    
    // Actions
    private String function1(List<String> tokens) {
        
        // Example function1
        
        String company = tokens.remove();
        String timestamp = tokens.remove();
        
        int stockPrice = something.getStockPrice(company);
        
        return "Stock price for " + company + " today is " + stockPrice;
    }
    
    // Favourites
    private void updateFavourites(List<String> list){
        
        foreach(String item : list) {
            // do something
        }
    }
}

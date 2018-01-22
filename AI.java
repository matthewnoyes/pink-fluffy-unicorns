import java.util.*;


// Example of AI part that parses a string and returns information

public class AI(){
    
    NLPParser parser;
    
    // Constructor
    public AI(){
        parser = new NLPParser();
    }
    
    // String parser
    public String parse(String s){
        
        Pair<Integer, List<String>> action =  parser.parse(s);
        
        if(action.getFirst() == 1) {
            
            makeFavourite(action.getSecond());
            return function1(action.getSecond());  
        } else if(action.getFirst() == 2) {
            
            makeFavourite(action.getSecond());
            return function2(action.getSecond());  
        }
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
    private void makeFavourite(List<String> list){
        
        foreach(String item : list) {
            // do something
        }
    }
}

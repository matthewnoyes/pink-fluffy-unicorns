import java.util.*;

public class NLPParser {
    
    Set<String> sectors;
    Set<String> companies;
    Set<String> timestamps;
    Map<String, Integer> patterns; 
    // Pattern -> Action 
    //(action should be an int, say "1")
    // Send requested action to AI, which decides which method to use
    
    
    // constructor, load data from some JSON file
    public NLPParser() {
        
        sectors = getJSON("sectors");
        companies = getJSON("companies");
        timestamps = getJSON("timestamps");
        
        patterns = getPatternsJSON();
    }
    
    // return action and relevant data (keywords)
    public Pair<Integer, List<String>> parse(String s){ 
        
       Pair<String, List<String>> pk = patternize(s); // pattern + keywords
       String sentencePattern = pk.getFirst();
        
       return new Pair(patterns.get(sentencePattern), pk.getSecond() );
    }
    
    // replace tokens with keyword
    private Pair<String, List<String>> patternize(String s){
        
        List<String> sentence = tokenize(s);
        List<String> keywords = new LinkedList();
        String pattern = "";
        
        foreach(String token : sentence) {
            String keyword = getKeyword(token);
            if(keyword != null) {
                keywords.add(token);
                token = keyword;
            }
            
            pattern += token;
        }
        
        return new Pair(pattern, keywords);
    }
    
    //return tokenized string
    private List<String> tokenize(String s){
        
        return new LinkedList();
    }
    
    // Return keyword
    private getKeyword(String token) {
        
        if(sectors.contains(token))
            return "*sector*";
        
        if(sectors.contains(token))
            return "*company*";
        
        if(sectors.contains(token))
            return "*timestamp*";
        
        return null;
    }
    
    // Load data into memory
    private Set<String> getJSON(String s){
        
        if(s.equals("sectors")){
            
            return new HashSet();
        } else if(s.equals("companies")){
            
            return new HashSet();
        } else if(s.equals("timestamps")){
            
            return new HashSet();
        } else if(s.equals("patterns")){
            
            return new HashSet();
        }
    }
    
    private Map<String, Integer> getPatternsJSON(){
        
        return new HashMap();
    }
     
}

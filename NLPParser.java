import java.util.*;

public class NLPParser {
    
    Keyword keywords;
    Map<String, Integer> patterns; 
    // Pattern -> Action 
    //(action should be an int, say "1")
    // Send requested action to AI, which decides which method to use
    
    
    // constructor, load data from some JSON file
    public NLPParser() {

        patterns = getPatternsJSON();
        keyword = new Keyword();
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
        StringBuilder pattern = new StringBuilder();
        
        foreach(String token : sentence) {
            String keyword = getKeyword(token);
            if(keyword != null) {
                keywords.add(token);
                token = keyword;
            }
            
            pattern.append(token);
        }
        
        return new Pair(pattern.toString(), keywords);
    }
    
    //return tokenized string
    private List<String> tokenize(String s){
        
        List<String> tokens = String.split("[.,;:?!]");
        return tokens;
    }
    
    // Load patterns from permanent memory
    private HashMap<String, Integer> getPatternsJSON(){
        
        JSONArray a = (JSONArray) parser.parse(new FileReader("patterns.json"));
        Map map = new HashMap(a.size());
        
        foreach(Object o : a) {
            
            JSONObject patternObj = (JSONObject) o;
            
            String pattern = (String) patternObj.get("pattern");
            String action = (Integer) patternObj.get("action");
            
            map.add(pattern, action);
        }
        
        return map;
    }
     
}

package virtualassistant.data.datastore;

import virtualassistant.data.stocks.*;
import virtualassistant.ai.Favourites;
import virtualassistant.misc.Pair;

import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.*;


public class Loader {

    JSONParser parser;

    public Loader(){

        parser = new JSONParser();
    }


    public JSONObject parseJSON(String str) throws ParseException {
      System.out.println(str);

        return (JSONObject)parser.parse(str);
    }

    public JSONObject parseJSONFile(String str) throws ParseException {
      //System.out.println(str);
      try {
        return (JSONObject)parser.parse(new FileReader(str));
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }
    
    public void writeFavourites(LinkedHashMap<String, Integer> favourites){
    
        JSONArray list = new JSONArray();

        for(String str : favourites.keySet()){

            JSONObject cObj = new JSONObject();

            cObj.put("name", str);
            cObj.put("integer", favourites.get(str));

            list.add(cObj);
        }
        
        // Write to file
        try (FileWriter file = new FileWriter("favourites.json")) {

            file.write(list.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    // Load high first
    public Favourites<String, Integer> readFavourites(){
     
        Favourites<String, Integer> favourites = new Favourites();
        ArrayList<Pair<String, Integer>> arrayList = new ArrayList(Favourites.maxFavourites);
        
        try{
            Object obj = parser.parse(new FileReader("favourites.json"));
            
            JSONArray list = (JSONArray) obj;
            
            for(Object o : list) {
                
                JSONObject jo = (JSONObject) o;
                
                Pair<String, Integer> p = new Pair((String) jo.get("name"), (Integer) jo.get("integer"));
                
                arrayList.add(p);
            }
            
            // Sort arrayList
            arrayList.sort(); 
            
            for(Pair<String,Integer> p : arrayList) {
                favourites.put(p.getFirst(), p.getSecond());
            }
            
            return favourites;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
        
    }
    
}

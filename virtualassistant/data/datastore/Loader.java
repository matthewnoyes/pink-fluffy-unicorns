package virtualassistant.data.datastore;

import virtualassistant.data.stocks.*;
import virtualassistant.ai.Favourites;
import virtualassistant.misc.Pair;
import virtualassistant.data.system.SystemStatus;

import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Loader {
    
    private boolean verbose = false;
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
    
    public void writeSystemStatus(SystemStatus status){
        
        JSONObject obj = new JSONObject();
        
        obj.put("soundEnabled", status.getSoundEnabled());
        obj.put("speechEnabled", status.getSpeechEnabled());
        obj.put("volume", status.getVolume());
        
        //Write
        try (FileWriter file = new FileWriter("virtualassistant/data/datastore/systemStatus.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } 
        
    }
    
    public SystemStatus readSystemStatus(){
        
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("virtualassistant/data/datastore/systemStatus.json"));
            
            boolean soundEnabled = (boolean) obj.get("soundEnabled");
            boolean speechEnabled = (boolean) obj.get("speechEnabled");
            double volume = (double) obj.get("volume");
            volume = (double)1.0;
            return new SystemStatus(soundEnabled, speechEnabled, volume);
        
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
        try (FileWriter file = new FileWriter("virtualassistant/data/datastore/favourites.json")) {

            file.write(list.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    
    public Favourites<String, Integer> readFavourites(){
        // IMPORTANT : Load high first
        Favourites<String, Integer> favourites = new Favourites();
        ArrayList<Pair<String, Integer>> arrayList = new ArrayList(Favourites.maxFavourites);
        
        try{
            Object obj = parser.parse(new FileReader("virtualassistant/data/datastore/favourites.json"));
            
            JSONArray list = (JSONArray) obj;
            
            if(verbose) System.out.println("Read array obj...");
            
            for(Object o : list) {
                
                JSONObject jo = (JSONObject) o;
                
                Pair<String, Integer> p = new Pair((String) jo.get("name"), Math.toIntExact((Long) jo.get("integer")));
                
                if(verbose) System.out.println("Loading favourite: " + jo.get("name"));
                
                arrayList.add(p);
            }
            
            // Sort arrayList
            arrayList.sort(new SortBySecond()); 
            
            if(verbose) System.out.println("Sorted array...");
            
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
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
        
    }
    
    class SortBySecond implements Comparator<Pair<String, Integer>> {
        // Used for sorting in descending order 
        public int compare(Pair<String, Integer> a, Pair<String, Integer> b)
        {
            return b.getSecond() - a.getSecond();
        }
    }
    
}

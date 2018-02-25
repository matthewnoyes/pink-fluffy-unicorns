package virtualassistant.data.datastore;

import virtualassistant.data.stocks.*;

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

        return (JSONObject)parser.parse(new FileReader(str));
    }
    
    /*public StockData readStocks(){

        Map<String, Company> companies = new HashMap(100);
        Map<String, Set<Company>> sectors = new HashMap(20);

        try{

            Object obj = parser.parse(new FileReader("stockData.json"));

            JSONObject stockData = (JSONObject) obj;
            //System.out.println(jsonObject);

            // Companies
            JSONArray comp = (JSONArray) stockData.get("companies");

            for (Object o : comp) {

                JSONObject jo = (JSONObject) o;
                //System.out.println(jo);

                Company c = new Company((String) jo.get("ticker"), (String) jo.get("company"), (String) jo.get("sector"));
                c.open = (double) jo.get("open");
                c.high = (double) jo.get("high");
                c.low = (double) jo.get("low");
                c.vol = (double) jo.get("vol");
                c.pe = (double) jo.get("pe");
                c.mktCap = (double) jo.get("mktCap");
                c.yearHigh = (double) jo.get("yearHigh");
                c.yearLow =(double)  jo.get("yearLow");
                c.avgVol = (double) jo.get("avgVol");
                c.yield = (double) jo.get("yield");

                companies.put(c.ticker, c);

                // Sectors
                if(sectors.get(c.sector) == null){

                    sectors.put(c.sector, new HashSet(20));
                }

                sectors.get(c.sector).add(c);
            }



            return new StockData(companies, sectors);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writeStocks(StockData stockData){

        JSONObject obj = new JSONObject();

        // Companies
        JSONArray companies = new JSONArray();

        for(Company c : stockData.companies.values()){

            JSONObject cObj = new JSONObject();

            cObj.put("ticker", c.ticker);
            cObj.put("sector", c.sector);
            cObj.put("company", c.company);
            cObj.put("open", c.open);
            cObj.put("high", c.high);
            cObj.put("low", c.low);
            cObj.put("vol", c.vol);
            cObj.put("pe", c.pe);
            cObj.put("mktCap", c.mktCap);
            cObj.put("yearHigh", c.yearHigh);
            cObj.put("yearLow", c.yearLow);
            cObj.put("avgVol", c.avgVol);
            cObj.put("yield", c.yield);

            companies.add(cObj);
        }

        obj.put("companies", companies);

        // Sectors
        JSONObject sectors = new JSONObject();

        for(Set<Company> set : stockData.sectors.values()){

            JSONArray sArray = new JSONArray();
            String sectorName = new String();

            for(Company c : set) {

                sArray.add(c.ticker);
                sectorName = c.sector;
            }

            sectors.put(sectorName, sArray);
        }

        obj.put("sectors", sectors);

        // Write to file
        try (FileWriter file = new FileWriter("stockData.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.print(obj);

    }*/
    /*
    private StockData createDummyStockData(){

        Map<String, Company> companies = new HashMap(100);
        Map<String, Set<Company>> sectors = new HashMap(20);

        // Sectors
        for(int i = 1; i < 4; i++){
            sectors.put("Sector " + i, new HashSet(3));
        }

        // Companies:
        for(int i = 1; i < 10; i++) {
            Company c = new Company("C" + i, "Company " + i);

            if(i < 4) {

                c.sector = "Sector 1";
            } else if(i < 7) {

                c.sector = "Sector 2";
            } else {

                c.sector = "Sector 3";
            }

            sectors.get(c.sector).add(c);
            companies.put(c.ticker, c);
        }

        return new StockData(companies, sectors);
    }*/

   /* private void read(){


        try {

            Object obj = parser.parse(new FileReader("database.json"));

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);

            String name = (String) jsonObject.get("name");
            System.out.println(name);

            long age = (Long) jsonObject.get("age");
            System.out.println(age);

            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void write(){

        JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", new Integer(100));

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);

        try (FileWriter file = new FileWriter("database.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);

    } */
}

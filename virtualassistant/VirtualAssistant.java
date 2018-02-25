package virtualassistant;

//import json.simple.JSONArray;
//import json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import virtualassistant.data.stocks.*;
import virtualassistant.data.stocks.StockData;
import virtualassistant.data.datastore.Loader;
import virtualassistant.chatbot.Chatbot;
import virtualassistant.ai.LearningAgent;
import virtualassistant.data.system.SystemStatus;
import virtualassistant.misc.Pair;
import virtualassistant.data.news.*;

public class VirtualAssistant {

    private StockData stockData;
    private SystemStatus systemStatus;
    private Loader loader;
    private LearningAgent learningAgent;
    //private NewsProcessor newsProcessor;
    private Chatbot chatbot;


    public VirtualAssistant(){
        //Instantiate everything

        loader = new Loader();
        try {
          stockData = new StockData();// Sloader.readStocks();
        } catch (Exception e) {
          System.out.println(e);
        }
        learningAgent = new LearningAgent(stockData, null);
        //systemStatus = loader.readSystemStatus();
        //newsProcessor = new NewsProcessor();
        chatbot = new Chatbot();

    }

    private void scan() {

        // Try to update data, if working, fire learning agent
        // if(loader.updateData(stockData)) {
        //
        //     learningAgent.searchForStockEvent();
        //     learningAgent.searchForNewsEvent();
        // }
    }

    // Decide action type based on action type decided by chatbot?
    public Pair<String, ArrayList<NewsObj>> getResponse(String query) throws IOException, java.text.ParseException, ParseException {

        // Uncomment to link to chatbot
        
        String response = chatbot.getResponse(query);
        // Convert to JsonObject
        JSONObject obj = loader.parseJSON(response);
        
        //JSONObject obj = loader.parseJSONFile("tests/test.json");




        switch(Math.toIntExact((long)obj.get("action"))){

            case Action.COMPANY_DATA:  return getCompanyData(obj);

            case Action.SECTOR_DATA:   return getSectorData(obj);

            case Action.COMPARE_COMPANIES: //return compareCompanies(obj);
                                    break;

            case Action.COMPARE_SECTORS:   //return compareSectors(obj);
                                    break;

            case Action.ALERT:             //return alert(obj);
                                    break;

            default:                return new Pair("Undefined action!", null);
        }
        return null;

    }

    /* Company data
    */

    private Pair<String, ArrayList<NewsObj>> getCompanyData(JSONObject parameters) throws IOException, java.text.ParseException {

        ICompany company = stockData.getCompanyForTicker((String)parameters.get("company1"));
        INewsData news = new NewsData();


        switch((String)parameters.get("data1")) {

            case "open":
                return new Pair("" + company.getOpen(), null);

            case "news":
                return new Pair("Here is the news that you wanted", news.getAllianceNews((String)parameters.get("company1")));

            case "high":
                return new Pair("" + company.getHigh(), null);

            case "low":
                return new Pair("" + company.getLow(), null);

            case "vol":
                return new Pair("" + company.getVolume(), null);

            case "PercentageChange":
                return new Pair("" + company.getPercentageChange(), null);

            case "CurrentPrice":
                return new Pair("" + company.getCurrentPrice(), null);

	    case "Change":
                return new Pair("" + company.getChange(), null);

	    case "AverageClose":
                return new Pair("" + company.yearAverageClose(), null);

            // case "PastData":
            //     return new Pair("" + company.getPastData(), null);


            case "yearHigh":
                return new Pair("" + company.yearHigh(), null);

            case "yearLow":
                return new Pair("" + company.yearLow(), null);

            case "avgVol":
                return new Pair("" + company.yearAverageVolume(), null);

           /* case "news":
                Arraylist news = ...
                return company.yield();
                break;
            */
        }

        return null;

    }

    /* Sector data
    */

    private Pair<String, ArrayList<NewsObj>> getSectorData(JSONObject parameters) throws IOException, ParseException, java.text.ParseException {

        String sector = (String)parameters.get("sector");
        INewsData news = new NewsData();

        switch((String)parameters.get("data")) {

            /*case "price":
                chatbot.output(stockData.getSectorCurrentPrice(sector));
                break;
            */
            case "change":
                return new Pair("" + stockData.getSectorChange(sector), null);

            case "percentageChange":
                return new Pair("" + stockData.getSectorPercentageChange(sector), null);

            case "news":
		return new Pair("Here is the news that you wanted", news.sectorNews(sector));

            case "yearHigh":
                return new Pair("" + stockData.sectorYearHigh(sector), null);

            case "yearLow":
                return new Pair("" + stockData.sectorYearLow(sector), null);

            case "yearAverageClose":
                return new Pair("" + stockData.sectorYearAverageClose(sector), null);

            case "closePriceOn":
                // return stockData.sectorYearLow(sector);
                break;
        }

        return null;
    }


    class Action {
    static final short COMPANY_DATA = 0,
        SECTOR_DATA = 1,
        COMPARE_COMPANIES = 2,
        COMPARE_SECTORS = 3,
        ALERT = 4;
    }

}

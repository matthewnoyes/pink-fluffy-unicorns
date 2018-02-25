package virtualassistant;

//import json.simple.JSONArray;
//import json.simple.JSONObject;

import java.io.IOException;

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
    public String getResponse(String query) throws IOException, ParseException {

        String response = chatbot.getResponse(query);

        // Convert to JsonObject
        JSONObject obj = loader.parseJSON(response);

        switch((int)obj.get("action")){

            case Action.COMPANY_DATA:  return getCompanyData(obj);

            case Action.SECTOR_DATA:   return getSectorData(obj);

            case Action.COMPARE_COMPANIES: //return compareCompanies(obj);
                                    break;

            case Action.COMPARE_SECTORS:   //return compareSectors(obj);
                                    break;

            case Action.ALERT:             //return alert(obj);
                                    break;

            default:                return "Undefined action!";
        }
        return null;

    }

    /* Company data
    */

    private String getCompanyData(JSONObject parameters){

        ICompany company = stockData.getCompanyForName((String)parameters.get("company1"));


        switch((String)parameters.get("data1")) {

            case "open":
                return "" + company.getOpen();

            case "high":
                return "" + company.getHigh();

            case "low":
                return "" + company.getLow();

            case "vol":
                return "" + company.getVolume();

            /*case "pe":
              return company.getPe();
              break;

            /*case "mktCap":
                return company.getMktCap();
                break;
            */
            case "yearHigh":
                return "" + company.yearHigh();

            case "yearLow":
                return "" + company.yearLow();

            case "avgVol":
                return "" + company.yearAverageVolume();

           /* case "yield":
                return company.yield();
                break;
            */
        }

        return "";

    }

    /* Sector data
    */

    private String getSectorData(JSONObject parameters){

        String sector = (String)parameters.get("sector");

        switch((String)parameters.get("data")) {

            /*case "price":
                chatbot.output(stockData.getSectorCurrentPrice(sector));
                break;
            */
            case "change":
                return "" + stockData.getSectorChange(sector);

            case "percentageChange":
                return "" + stockData.getSectorPercentageChange(sector);

            case "yearHigh":
                return "" + stockData.sectorYearHigh(sector);

            case "yearLow":
                return "" + stockData.sectorYearLow(sector);

            case "yearAverageClose":
                return "" + stockData.sectorYearAverageClose(sector);

            case "closePriceOn":
                // return stockData.sectorYearLow(sector);
                break;
        }

        return "";
    }


    class Action {
    static final short COMPANY_DATA = 0,
        SECTOR_DATA = 1,
        COMPARE_COMPANIES = 2,
        COMPARE_SECTORS = 3,
        ALERT = 4;
    }

}

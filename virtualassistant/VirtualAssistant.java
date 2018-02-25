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
    public void getResponse(String query) throws IOException, ParseException {

        String response = chatbot.getResponse(query);

        // Convert to JsonObject
        JSONObject obj = loader.parseJSON(response);

        switch((int)obj.get("action")){

            case Action.COMPANY_DATA:  getCompanyData(obj);
                                    break;

            case Action.SECTOR_DATA:   getSectorData(obj);
                                    break;

            case Action.COMPARE_COMPANIES: //compareCompanies(obj);
                                    break;

            case Action.COMPARE_SECTORS:   //compareSectors(obj);
                                    break;

            case Action.ALERT:             //alert(obj);
                                    break;

            default:                System.out.println("Undefined action!");
                                    break;
        }


    }

    /* Company data
    */

    private void getCompanyData(JSONObject parameters){

        ICompany company = stockData.getCompanyForName((String)parameters.get("company1"));


        switch((String)parameters.get("data1")) {

            case "open":
                chatbot.output(company.getOpen());
                break;

            case "high":
                chatbot.output(company.getHigh());
                break;

            case "low":
                chatbot.output(company.getLow());
                break;

            case "vol":
                chatbot.output(company.getVolume());
                break;

            /*case "pe":
              chatbot.output(company.getPe());
              break;

            /*case "mktCap":
                chatbot.output(company.getMktCap());
                break;
            */
            case "yearHigh":
                chatbot.output(company.yearHigh());
                break;

            case "yearLow":
                chatbot.output(company.yearLow());
                break;

            case "avgVol":
                chatbot.output(company.yearAverageVolume());
                break;

           /* case "yield":
                chatbot.output(company.yield());
                break;
            */
        }

    }

    /* Sector data
    */

    private void getSectorData(JSONObject parameters){

        String sector = (String)parameters.get("sector");

        switch((String)parameters.get("data")) {

            /*case "price":
                chatbot.output(stockData.getSectorCurrentPrice(sector));
                break;
            */
            case "change":
                chatbot.output(stockData.getSectorChange(sector));
                break;

            case "percentageChange":
                chatbot.output(stockData.getSectorPercentageChange(sector));
                break;

            case "yearHigh":
                chatbot.output(stockData.sectorYearHigh(sector));
                break;

            case "yearLow":
                chatbot.output(stockData.sectorYearLow(sector));
                break;

            case "yearAverageClose":
                chatbot.output(stockData.sectorYearAverageClose(sector));
                break;

            case "closePriceOn":
                // ??? chatbot.output(stockData.sectorYearLow(sector));
                break;
        }
    }


    class Action {
    static final short COMPANY_DATA = 0,
        SECTOR_DATA = 1,
        COMPARE_COMPANIES = 2,
        COMPARE_SECTORS = 3,
        ALERT = 4;
    }

}

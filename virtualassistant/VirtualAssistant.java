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
import virtualassistant.misc.Pair;
import virtualassistant.data.news.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class VirtualAssistant {

    private StockData stockData;
    private SystemStatus systemStatus;
    private Loader loader;
    private LearningAgent learningAgent;
    private INewsData news;
    private Chatbot chatbot;
    private Calendar calDate;


    // Set this for debugging
    private boolean verbose = false;

    public VirtualAssistant(){
        //Instantiate everything

        loader = new Loader();


        // Try to load stockData again if this fails. We have no program without it
        boolean loaded = false;
        while(!loaded) {
            try {
                stockData = new StockData(true);// Sloader.readStocks();
                loaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load stock data... Retrying...");
                e.printStackTrace();
            }
        }

        System.out.println("Loading favourites...");
        learningAgent = new LearningAgent(stockData, null, loader.readFavourites());

        System.out.println("Loading system status...");
        systemStatus = loader.readSystemStatus();

        news = new NewsData();

        System.out.println("Setting up chatbot connection...");
        chatbot = new Chatbot();
        calDate  = Calendar.getInstance();

    }

    public void saveStatus(){
        loader.writeFavourites(learningAgent.getFavouriteStocks());
        loader.writeSystemStatus(systemStatus);
    }

    public void scan() {

        // Try to update data, if working, fire learning agent
        //if(loader.updateData(stockData)) {
        StockData newStockData = null;
        boolean loaded = false, updated = false;
        while(!loaded) {
            try {
                newStockData = new StockData(false);//stockData.clone();
                updated = true; //updateCurrentData(newStockData.tickerToCompany);
                loaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load stock data... Retrying...");
                e.printStackTrace();
            }
        }

        if(updated) {
            stockData = newStockData;

            /*learningAgent.searchForStockEvent();
            learningAgent.searchForNewsEvent();
            */
        }
    }

    // Decide action type based on action type decided by chatbot?
    public Pair<String, LinkedList<NewsObj>> getResponse(String query) throws IOException, java.text.ParseException, ParseException {

        JSONObject response = chatbot.getResponse(query);

        // Check if ALERT or DATA_REQUEST
        if((long)response.get("action") == Action.ALERT)
            return new Pair((String)response.get("message"), null);

        if((long)response.get("action") != Action.DATA_REQUEST)
            return null;//((String)response.get("message"), null);


        /// ADDD SPLITTING LOGICC HEREE

        Pair result = new Pair("", new LinkedList<NewsObj>());

        String names = (String) response.get("company1");

        if(verbose) {
            System.out.println("VirtualAssistant.getResponse(): Names = " + names);
        }

        String[] namesList = names.split(" and ");

        for(String name : namesList){

            if(verbose) {
                System.out.println("VirtualAssistant.getResponse(): Company name = " + name);
            }

            // Check whether company or sector
            ICompany company = stockData.getCompanyForTicker(name);
            if(company != null) {

                result = Pair.merge(result, getCompanyData(name, response));
                learningAgent.analyzeInput(name);
            } else if (stockData.isSector(name)){

                result = Pair.merge(result, getSectorData(name, response));
                learningAgent.analyzeInput(name);
            }
        }

        // Return
        return result;
    }

    /* Company data
    */

    private Pair<String, LinkedList<NewsObj>> getCompanyData(String name, JSONObject parameters) throws IOException, java.text.ParseException {

        ICompany company = stockData.getCompanyForTicker(name);
        // Add split logic on and, use a for loop to return


        // Try to get date
        try {
            if(parameters.get("date") != null){
                DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                calDate.setTime(df.parse((String)parameters.get("date")));
            }
        } catch (Exception e) {
            e.toString();
        }

        StringBuilder sb = new StringBuilder(name);
        sb.append(", ");

        switch((String)parameters.get("data1")) {

            case "currentPrice":
                sb.append("current price: ");
                sb.append("£");
                sb.append(company.getCurrentPrice());
                return new Pair(sb.toString(), null);

            case "ClosePriceOnDate": //ALL DATES FUNCTIONS ARE NOT ON DIALOGFLOW
                sb.append("closing price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append("£");
                sb.append(company.getClosePriceOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "OpenPriceOnDate":
                sb.append("opening price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append("£");
                sb.append(company.getOpenPriceOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "HighPriceOnDate":
                sb.append("highest price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append("£");
                sb.append(company.getHighPriceOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "LowPriceOnDate":
                sb.append("lowest price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append("£");
                sb.append(company.getLowPriceOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "VolumeOnDate":
                sb.append("volume on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append(company.getVolumeOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "Open":
                sb.append("opening price: ");
                sb.append("£");
                sb.append(company.getOpen());
                return new Pair(sb.toString(), null);

            case "news":
                sb.append("news:");
                return new Pair(sb.toString(), news.getAllianceNews(name));

            case "High":
                sb.append("highest price: ");
                sb.append("£");
                sb.append(company.getHigh());
                return new Pair(sb.toString(), null);

            case "Low":
                sb.append("lowest price: ");
                sb.append("£");
                sb.append(company.getLow());
                return new Pair(sb.toString(), null);

            case "Volume":
                sb.append("volume: ");
                sb.append(company.getVolume());
                return new Pair(sb.toString(), null);

            case "PercentageChange":
                sb.append("percentage change: ");
                sb.append(company.getPercentageChange());
                sb.append("%");
                return new Pair(sb.toString(), null);

            case "CurrentPrice":
                sb.append("current price: ");
                sb.append("£");
                sb.append(company.getCurrentPrice());
                return new Pair(sb.toString(), null);

            case "Change":
                sb.append("current change: ");
                sb.append("£");
                sb.append(company.getChange());
                return new Pair(sb.toString(), null);

            case "yearAverageClose":
                sb.append("year average close: ");
                sb.append("£");
                sb.append(company.yearAverageClose());
                return new Pair(sb.toString(), null);

            case "yearHigh":
                sb.append("year high: ");
                sb.append("£");
                sb.append(company.yearHigh());
                return new Pair(sb.toString(), null);

            case "yearLow":
                sb.append("year low: ");
                sb.append("£");
                sb.append(company.yearLow());
                return new Pair(sb.toString(), null);

            case "yearAverageVolume":
                sb.append("year average volume: ");
                sb.append(company.yearAverageVolume());
                return new Pair(sb.toString(), null);

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

    private Pair<String, LinkedList<NewsObj>> getSectorData(String sector, JSONObject parameters) throws IOException, ParseException, java.text.ParseException {

        INewsData news = new NewsData();
        Calendar calDate  = Calendar.getInstance();

        try{
            if(parameters.get("date") != null){
                DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                calDate.setTime(df.parse((String)parameters.get("date")));
            }
        } catch (Exception e) {
            e.toString();
        }

         StringBuilder sb = new StringBuilder(sector);
        sb.append(", ");


        switch((String)parameters.get("data1")) {

            /*case "price":
                chatbot.output(stockData.getSectorCurrentPrice(sector));
                break;
            */
            case "Open":
                sb.append("opening price: ");
                sb.append("£");
                sb.append(stockData.getSectorOpen(sector));
                return new Pair(sb.toString(), null);

            case "High":
                sb.append("highest price: ");
                sb.append("£");
                sb.append(stockData.getSectorHigh(sector));
                return new Pair(sb.toString(), null);

            case "Low":
                sb.append("lowest price: ");
                sb.append("£");
                sb.append(stockData.getSectorLow(sector));
                return new Pair(sb.toString(), null);


            case "currentPrice":
                sb.append("current price: ");
                sb.append("£");
                sb.append(stockData.getCurrentSectorPrice(sector));
                return new Pair(sb.toString(), null);

            case "Change":
                sb.append("change in price: ");
                sb.append("£");
                sb.append(stockData.getSectorChange(sector));
                return new Pair(sb.toString(), null);

            case "PercentageChange":
                sb.append("percentage change: ");
                sb.append("£");
                sb.append(stockData.getSectorPercentageChange(sector));
                return new Pair(sb.toString(), null);

            case "Volume":
                sb.append("volume: ");
                sb.append(stockData.getSectorVolume(sector));
                return new Pair(sb.toString(), null);

            case "news":
                sb.append("news: ");
                return new Pair(sb.toString(), news.sectorNews(sector));

            case "yearHigh":
                sb.append("year high: ");
                sb.append("£");
                sb.append(stockData.sectorYearHigh(sector));
                return new Pair(sb.toString(), null);

            case "yearLow":
                sb.append("year low: ");
                sb.append("£");
                sb.append(stockData.sectorYearLow(sector));
                return new Pair(sb.toString(), null);

            case "yearAverageClose":
                sb.append("year average close: ");
                sb.append("£");
                sb.append(stockData.sectorYearAverageClose(sector));
                return new Pair(sb.toString(), null);

            case "yearAverageVolume":
                sb.append("year average volume: ");
                sb.append(stockData.sectorAverageVolume(sector));
                return new Pair(sb.toString(), null);

            case "closePriceOn":  // not on  dialogflow
                // return stockData.sectorYearLow(sector);
                break;

            case "SectorClosePriceOnDate":  // not on dialogflow
                sb.append("close price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append(stockData.getSectorClosePriceOnDate(sector,calDate));
                return new Pair(sb.toString(), null);

        }

        return null;
    }


    class Action {
    static final short DATA_REQUEST = 0,
        ALERT = 1;
    }

}

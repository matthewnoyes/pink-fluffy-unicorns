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
                stockData = new StockData();// Sloader.readStocks();
                loaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load stock data... Retrying...");
                e.printStackTrace();
            }
        }
        
        learningAgent = new LearningAgent(stockData, null, loader.readFavourites());
        //systemStatus = loader.readSystemStatus();
        news = new NewsData();
        chatbot = new Chatbot();
        calDate  = Calendar.getInstance();

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
            } else if (stockData.isSector(name)){

                result = Pair.merge(result, getSectorData(name, response));
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
                sb.append(company.getCurrentPrice());
                sb.append("£.");
                return new Pair(sb.toString(), null);

            case "ClosePriceOnDate":
                sb.append("closing price on ");
                sb.append(calDate.toString());
                sb.append(": ");
                sb.append(company.getClosePriceOnDate(calDate));
                sb.append("£.");
                return new Pair(sb.toString(), null);
                
            case "OpenPriceOnDate":
                return new Pair("" + company.getOpenPriceOnDate(calDate), null);

            case "HighPriceOnDate":
                return new Pair("" + company.getHighPriceOnDate(calDate), null);

            case "LowPriceOnDate":
                return new Pair("" + company.getLowPriceOnDate(calDate), null);

            case "VolumeOnDate":
                return new Pair("" + company.getVolumeOnDate(calDate), null);

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

            case "SectorClosePriceOnDate":
                return new Pair("" + stockData.getSectorClosePriceOnDate(sector,calDate), null);
        }

        return null;
    }


    class Action {
    static final short DATA_REQUEST = 0,
        ALERT = 1;
    }

}

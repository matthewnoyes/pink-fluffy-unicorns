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
import java.util.Comparator;
import java.util.Collections;
import java.util.Set;

public class VirtualAssistant {
    
    public LearningAgent learningAgent;
    public SystemStatus systemStatus;
    
    private StockData stockData;
    private Loader loader;
    private INewsData newsData;
    private Chatbot chatbot;


    // Set this for debugging
    private boolean verbose = false;

    public VirtualAssistant(){
        //Instantiate everything

        loader = new Loader();

        System.out.println("Loading system status...");
        systemStatus = loader.readSystemStatus();

        newsData = new NewsData();

        System.out.println("Setting up chatbot connection...");
        chatbot = new Chatbot();


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
        learningAgent = new LearningAgent(stockData, newsData, loader.readFavourites());

        try {
          System.out.println(learningAgent.searchForStockEvent());
          for (String query : learningAgent.suggestQueries(5)) {
            System.out.println(query);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
            //learningAgent.searchForNewsEvent();
        System.out.println("Finished");

    }

    public void saveStatus(){
        loader.writeFavourites(learningAgent.getFavouriteStocks());
        loader.writeSystemStatus(systemStatus);
    }

    public void scan() {

        // Try to update data, if working, fire learning agent
        //if(loader.updateData(stockData)) {
        System.out.println("Running");

        StockData newStockData = null;
        boolean loaded = false, updated = false;
        while(!loaded) {
            try {
                newStockData = new StockData(true);//stockData.clone();
                updated = true; //updateCurrentData(newStockData.tickerToCompany);
                loaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load stock data... Retrying...");
                e.printStackTrace();
            }
        }

        if(updated) {
            stockData = newStockData;

        System.out.println(learningAgent.searchForStockEvent());
            //learningAgent.searchForNewsEvent();
        System.out.println("Finished");

        }
    }

    // Decide action type based on action type decided by chatbot?
    public Pair<String, LinkedList<NewsObj>> getResponse(String query) throws IOException, java.text.ParseException, ParseException {

        JSONObject response = chatbot.getResponse(query);

        // Check if ALERT or DATA_REQUEST
        if((long)response.get("action") == Action.ALERT)
            return new Pair((String)response.get("message"), null);

        if((long)response.get("action") == Action.DATA_REQUEST)
            return getDataRequest(response);

        if((long)response.get("action") == Action.SECTOR_COMPARISON)
            return getSectorComparison(response);

        return null;
    }
    // =========================  DATA REQUEST =================================
    private Pair<String, LinkedList<NewsObj>> getDataRequest(JSONObject response) throws IOException, java.text.ParseException, ParseException {

        Pair result = new Pair("", new LinkedList<NewsObj>());

        Calendar calDate = Calendar.getInstance();

        String names = (String) response.get("company");
        String datas = (String) response.get("data");
        String[] namesList = names.split("\\sand\\s|,\\s");
        String[] dataList = datas.split("\\sand\\s|,\\s");

        // Try to get date
        try {
            if(response.get("date") != null){
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                calDate.setTime(df.parse((String)response.get("date")));
            }
        } catch (Exception e) {
            e.toString();
        }

        for(String name : namesList){


            //Treat edge case "RDS" separately
            if(name.equals("RDS")){

                result = Pair.merge(result, new Pair("RDSA", null));
                result = Pair.merge(result, formatCompanyData((Company) stockData.getCompanyForTicker("RDSA"), dataList, calDate));
                result = Pair.merge(result, new Pair(".\n", null));

                result = Pair.merge(result, new Pair("RDSB", null));
                result = Pair.merge(result, formatCompanyData((Company) stockData.getCompanyForTicker("RDSB"), dataList, calDate));
                result = Pair.merge(result, new Pair(".\n", null));

                learningAgent.analyzeInput("RDS");

            } else { // Company not "RDS"

                // Check whether company or sector
                ICompany company = stockData.getCompanyForTicker(name);
                if(company != null) {

                    result = Pair.merge(result, new Pair(company.getTicker(), null));
                    result = Pair.merge(result, formatCompanyData((Company) company, dataList, calDate));
                     result = Pair.merge(result, new Pair(".\n", null));
                    learningAgent.analyzeInput(name);
                } else if (stockData.isSector(name)){

                    result = Pair.merge(result, new Pair(name, null));
                    result = Pair.merge(result, formatSectorData(name, dataList, calDate));
                     result = Pair.merge(result, new Pair(".\n", null));
                    learningAgent.analyzeInput(name);
                }
            }
        }

        // Return
        Collections.sort((LinkedList<NewsObj>)result.getSecond(), new SortByDate());
        return result;
    }

    /* Company data
    */
    private Pair<String, LinkedList<NewsObj>> formatCompanyData(Company company, String[] data, Calendar calDate) throws IOException, java.text.ParseException {
        Pair result = new Pair("", new LinkedList<NewsObj>());

        for(String d : data){
            result = Pair.merge(result, new Pair(", ", null));
            result = Pair.merge(result, getCompanyData(company, d, calDate));
        }

        return result;
    }

    private Pair<String, LinkedList<NewsObj>> getCompanyData(Company company, String data ,Calendar calDate) throws IOException, java.text.ParseException {

        StringBuilder sb = new StringBuilder(" ");

        switch(data) {

            case "CurrentPrice":
                sb.append("current price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getCurrentPrice()));
                return new Pair(sb.toString(), null);

            case "OnDateClosePrice": //ALL DATES FUNCTIONS ARE NOT ON DIALOGFLOW
                sb.append("closing price on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getClosePriceOnDate(calDate)));
                return new Pair(sb.toString(), null);

            case "OnDateOpenPrice":
                sb.append("opening price on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getOpenPriceOnDate(calDate)));
                return new Pair(sb.toString(), null);

            case "OnDateHighPrice":
                sb.append("highest price on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getHighPriceOnDate(calDate)));
                return new Pair(sb.toString(), null);

            case "OnDateLowPrice":
                sb.append("lowest price on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getLowPriceOnDate(calDate)));
                return new Pair(sb.toString(), null);

            case "OnDateVolume":
                sb.append("volume on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append(company.getVolumeOnDate(calDate));
                return new Pair(sb.toString(), null);

            case "OpenPrice":
                sb.append("opening price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getOpen()));
                return new Pair(sb.toString(), null);

            case "ClosePrice":
                sb.append("closing price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getClose()));
                return new Pair(sb.toString(), null);

            case "News":
                sb.append("news:");
                return new Pair(sb.toString(), newsData.getAllianceNews(company.getTicker()));

            case "HighPrice":
                sb.append("highest price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getHigh()));
                return new Pair(sb.toString(), null);

            case "LowPrice":
                sb.append("lowest price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getLow()));
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

            case "Change":
                sb.append("current change: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.getChange()));
                return new Pair(sb.toString(), null);

            case "YearAverageClose":
                sb.append("year average close: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.yearAverageClose()));
                return new Pair(sb.toString(), null);

            case "YearHigh":
                sb.append("year high: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.yearHigh()));
                return new Pair(sb.toString(), null);

            case "YearLow":
                sb.append("year low: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", company.yearLow()));
                return new Pair(sb.toString(), null);

            case "YearAverageVolume":
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
    private Pair<String, LinkedList<NewsObj>> formatSectorData(String sector, String[] data, Calendar calDate) throws IOException, ParseException, java.text.ParseException {
        Pair result = new Pair("", new LinkedList<NewsObj>());

        for(String d : data){
            result = Pair.merge(result, new Pair(", ", null));
            result = Pair.merge(result, getSectorData(sector, d, calDate));
        }

        return result;
    }

    private Pair<String, LinkedList<NewsObj>> getSectorData(String sector, String data, Calendar calDate) throws IOException, ParseException, java.text.ParseException {

        StringBuilder sb = new StringBuilder(" ");

        switch(data) {

            /*case "price":
                chatbot.output(stockData.getSectorCurrentPrice(sector));
                break;
            */
            case "OpenPrice":
                sb.append("opening price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorOpen(sector)));
                return new Pair(sb.toString(), null);

            case "ClosePrice":
                sb.append("closing price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorClose(sector)));
                return new Pair(sb.toString(), null);

            case "HighPrice":
                sb.append("highest price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorHigh(sector)));
                return new Pair(sb.toString(), null);

            case "LowPrice":
                sb.append("lowest price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorLow(sector)));
                return new Pair(sb.toString(), null);


            case "CurrentPrice":
                sb.append("current price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getCurrentSectorPrice(sector)));
                return new Pair(sb.toString(), null);

            case "Change":
                sb.append("change in price: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorChange(sector)));
                return new Pair(sb.toString(), null);

            case "PercentageChange":
                sb.append("percentage change: ");
                sb.append("\u00A3");
                sb.append(stockData.getSectorPercentageChange(sector));
                return new Pair(sb.toString(), null);

            case "Volume":
                sb.append("volume: ");
                sb.append(stockData.getSectorVolume(sector));
                return new Pair(sb.toString(), null);

            case "News":
                sb.append("news: ");
                return new Pair(sb.toString(), newsData.sectorNews(sector));

            case "YearHigh":
                sb.append("year high: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.sectorYearHigh(sector)));
                return new Pair(sb.toString(), null);

            case "YearLow":
                sb.append("year low: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.sectorYearLow(sector)));
                return new Pair(sb.toString(), null);

            case "YearAverageClose":
                sb.append("year average close: ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.sectorYearAverageClose(sector)));
                return new Pair(sb.toString(), null);

            case "YearAverageVolume":
                sb.append("year average volume: ");
                sb.append(stockData.sectorAverageVolume(sector));
                return new Pair(sb.toString(), null);

            //case "OnDateClosePrice":  // not on  dialogflow
                // return stockData.sectorYearLow(sector);
                //break;

            case "OnDateClosePrice":  // not on dialogflow
                sb.append("close price on ");
                sb.append(String.format("%02d", calDate.get(Calendar.DAY_OF_MONTH)));
                sb.append("/");
                sb.append(String.format("%02d", (calDate.get(Calendar.MONTH) + 1)));
                sb.append("/");
                sb.append(calDate.get(Calendar.YEAR));
                sb.append(": ");
                sb.append("\u00A3");
                sb.append(String.format("%.2f", stockData.getSectorClosePriceOnDate(sector,calDate)));
                return new Pair(sb.toString(), null);

        }

        return null;
    }
    // ========================= DATA REQUEST END ======================================


    // ========================= SECTOR COMPARISON =====================================
    private Pair<String, LinkedList<NewsObj>> getSectorComparison(JSONObject json) {

        String sector = (String) json.get("sector");

        Pair<String, LinkedList<NewsObj>> result = new Pair("", null);

        Set<Company> companies = null;

        System.out.println((String) json.get("data"));

        switch((String) json.get("data")){

            case "Up" :
                System.out.println("Case Up");
                companies = stockData.getRisingInSector(sector);
                if(companies.isEmpty()){
                    result = Pair.merge(result, new Pair("No rising companies in " + sector + ".", null));
                } else {
                    result = Pair.merge(result, new Pair("Rising companies: ", null));
                }
                break;

            case "Down" :
                System.out.println("Case down");
                companies = stockData.getFallingInSector(sector);
                if(companies.isEmpty()){
                    result = Pair.merge(result, new Pair("No falling companies in " + sector + ".", null));
                } else {
                    result = Pair.merge(result, new Pair("Falling companies: ", null));
                }
                break;
        }

        if(companies == null) {
            System.out.println("is null");
            return null;
        }

        if(companies.isEmpty()) return result;

        boolean ok = false;

        for(Company c : companies){
            System.out.println("Company: " + c.getName());
            if(ok) result = Pair.merge(result, new Pair(", ", null));
            ok = true;
            result = Pair.merge(result, new Pair(c.getName(), null));
        }

        result = Pair.merge(result, new Pair(".", null));

        return result;
    }
    // ========================= SECTOR COMPARISON END =================================


    class SortByDate implements Comparator<NewsObj> {
        // Used for sorting in descending order of date
        public int compare(NewsObj a, NewsObj b)
        {
            return b.getDateTime().compareTo(a.getDateTime());
        }
    }

    class Action {
    static final short DATA_REQUEST = 0,
        SECTOR_COMPARISON = 1,
        ALERT = 2;
    }

}

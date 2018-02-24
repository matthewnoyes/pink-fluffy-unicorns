package virtualassistant;

import data.datastore.org.json.simple.JSONArray;
import data.datastore.org.json.simple.JSONObject;

public class VirtualAssistant {

    private StockData stockdata;
    private SystemStatus systemStatus;
    private Loader loader;
    private LearningAgent learningAgent;
    //private NewsProcessor newsProcessor;
    private Chatbot chatbot;
    
    
    public VirtualAssistant(){
        //Instantiate everything

        loader = new Loader();
        stockData = loader.readStocks();
        learningAgent = loader.readLearningAgent();
        systemStatus = loader.readSystemStatus();
        //newsProcessor = new NewsProcessor();
        chatbot = new Chatbot();
        
    }
        
    public void startScanning(String[] args) {

        
        int timepassed = 0;
        //Try to update data.

        for(;;){
            //Wait 10 seconds
            TimeUnit.SECONDS.sleep(10);

            // Try to update data, if working, fire learning agent
            if(loader.updateData(stockData)) {

                learningAgent.searchForStockEvent();
                learningAgent.searchForNewsEvent();
            }
            
            // Autosave every 5 min?
            timePassed += 10;
            if(timePassed >= 300) {
                
                // Autosave smth
                timePassed = 0;
            }
      
        }
    }

    // Decide action type based on action type decided by chatbot?
    public void decideAction(String response){
        
        // Convert to JsonObject
        JSONObject obj = loader.parseJSON(response);
        
        switch(obj.get("action")){

            case Action.GET_COMPANY_DATA:  getCompanyData(obj);
                                    break;

            case Action.GET_SECTOR_DATA:   getSectorData(obj);
                                    break;

            case Action.COMPARE_COMPANIES: compareCompanies(obj);
                                    break;

            case Action.COMPARE_SECTORS:   compareSectors(obj);
                                    break;

            case Action.ALERT:             alert(obj);
                                    break;

            default:                System.out.println("Undefined action!");
                                    break;
        }


    }

    /* Company data
    */

    private void getCompanyData(JSONObject parameters){

        Company company = stockData.companyForName(parameters.get("company1"));
        
        
        switch(parameters.get("data1")) {

            case "open":
                chatbot.output(company.open);
                break;
            
            case "high":
                chatbot.output(company.high);
                break;
            
            case "low":
                chatbot.output(company.low);
                break;
            
            case "vol":
                chatbot.output(company.vol);
                break;

            case "pe":
              chatbot.output(company.pe);
              break;
                                      
            case "mktCap":
                chatbot.output(company.mktCap);
                break;
            
            case "yearHigh":
                chatbot.output(company.yearHigh);
                break;
            
            case "yearLow":
                chatbot.output(company.yearLow);
                break;

            case "avgVol":
                chatbot.output(company.avgVol);
                break;                

            case "yield":
                chatbot.output(company.yield);
                break;

        }
        
    }
    
    /* Sector data
    */

    private void getSectorData(JSONObject parameters){

        String sector = parameters.get("sector");

        switch(parameters.get("data")) {

            case "price":
                chatbot.output(stockData.sectorPrice(sector));
                break;
            
            case "change":
                chatbot.output(stockData.sectorChange(sector));
                break;
                
            case "percentageChange":
                chatbot.output(stockData.sectorPercentageChange(sector));
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
    
}

public enum Action {
    GET_COMPANY_DATA,
    GET_SECTOR_DATA,
    COMPARE_COMPANIES,
    COMPARE_SECTORS,
    ALERT
}

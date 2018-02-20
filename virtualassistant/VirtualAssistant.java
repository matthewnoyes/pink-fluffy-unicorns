package virtualassistant;

public class VirtualAssistant {

    private StockData stockdata;
    private SystemStatus systemStatus;
    private Loader loader;
    private LearningAgent learningAgent;
    private NewsProcessor newsProcessor;
    private Chatbot chatbot;

    public static void main(String[] args) {

        //Instntiate everything

        loader = new Loader();
        stockData = loader.readStocks();
        learningAgent = loader.readLearningAgent();
        systemStatus = loader.readSystemStatus();
        newsProcessor = new NewsProcessor();
        chatbot = new Chatbot();
        
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
    public static void decideAction(Action actionType, List<String> parameters){

        switch(actionType){

            case Action.GET_COMPANY_DATA:  getCompanyData(parameters);
                                    break;

            case Action.GET_SECTOR_DATA:   getSectorData(parameters);
                                    break;

            case Action.COMPARE_COMPANIES: compareCompanies(parameters);
                                    break;

            case Action.COMPARE_SECTORS:   compareSectors(parameters);
                                    break;

            case Action.ALERT:             alert(parameters);
                                    break;

            default:                System.out.println("Undefined action!");
                                    break;
        }


    }

    /* Company data
    */

    public static void getCompanyData(List<String> parameters){

        Company company = stockData.companyForName(parameters.remove(0));

        for(String str : parameters) {

            switch(str) {

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
    }
    
    /* Sector data
    */

    public static void getSectorData(List<String> parameters){

        String sector = parameters.remove(0);

        for(String str : parameters) {

            switch(str) {

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

}

public enum Action {
    GET_COMPANY_DATA,
    GET_SECTOR_DATA,
    COMPARE_COMPANIES,
    COMPARE_SECTORS,
    ALERT
}

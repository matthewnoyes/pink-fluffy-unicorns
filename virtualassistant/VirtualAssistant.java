package virtualassistant;

public class VirtualAssistant {
    
    private StockData stockdata;
    private Loader loader;
    private LearningAgent learningAgent;
    private NewsProcessor newsProcessor;
    private Chatbot chatbot;
    
    public static void main(String[] args) {
        
        //Instntiate everything
        
        loader = new Loader();
        stockData = loader.readStocks();
        learningAgent = loader.readLearningAgent();
        newsProcessor = new NewsProcessor();
        chatbot = new Chatbot();
        
        //Try to update data.
        
        for(;;){
            //Wait a bit
            TimeUnit.SECONDS.sleep(10);
            
            // Try to update data, if working, fire learning agent
            if(loader.updateData(stockData)) {
                
                learningAgent.searchForStockEvent();
                learningAgent.searchForNewsEvent();
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
    
    /* Example action
    *
    *   For this action, the first parameter should always be the company name, followed by
    *   type of data requested.
    */
    
    public static void getCompanyData(List<String> parameters){
        
        Company company = stockData.companyForName(parameters.remove(0));
        
        for(String str : parameters) {
            
            switch(str) {
                
                case "open":
                    chatbot.output(company.open);
                    break;
                
                case ...
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

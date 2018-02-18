import java.util.*;

public class StockData {

    public Map<String, Company> companies;
    public Map<String, Set<String>> sectors;
    
    public StockData(Map<String, Company> companies, Map<String,Set<String>> sectors){
        this.companies = companies;
        this.sectors = sectors;
    }
    
    //Company getters
    public ICompany getCompanyForName(String company){
        return null;
    }
    
    public ICompany getCompanyForTicker(String ticker){
        return null;
    }

    public String[] getSectors(){
        return null;
    }

    public ICompany[] getCompaniesInSector(String sector){
        return null;
    }

    //Sector info
    public double getCurrentSectorPrice(String sector){
        return 0.0;
    }
    
    public double getSectorChange(String sector){
        return 0.0;
    }
    
    public double getSectorPercentageChange(String sector){
        return 0.0;
    }

    public double sectorYearHigh(){
        return 0.0;
    }
    
    public double sectorYearLow(){
        return 0.0;
    }
    
    public double sectorYearAverageClose(){
        return 0.0;
    }

    public double getSectorClosePriceOnDate(Date date){
        return 0.0;
    }
 

}
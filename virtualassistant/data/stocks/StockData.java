package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.Set;

public class StockData implements IStockData {

  //<Ticker, Company class>
  private HashMap<String, Company> tickerToCompany;

  //<Company Name, Ticker>
  private HashMap<String, Company> nameToCompany;

  //<Sector, Set of tickers>
  private HashMap<String, Set<Company>> companiesInSector;

  public StockData() throws IOException {

    //Get the cookie and crumb for yahoo
    Scrapper.setupYahoo();

    tickerToCompany = new HashMap<String, Company>();
    companiesInSector = new HashMap<String, Set<Company>>();
    nameToCompany = new HashMap<String, Company>();

    HashMap<String, Integer> sectors = Scrapper.getSectors();

    for (Map.Entry<String, Integer> sector : sectors.entrySet()) {

      Set<Company> comInCurrSector = Scrapper.getSector(sector.getKey(), sector.getValue());

      companiesInSector.put(sector.getKey(), comInCurrSector);

      for (Company com : comInCurrSector) {
        tickerToCompany.put(com.getTicker(), com);
        nameToCompany.put(com.getName(), com);
      }
    }

  }

  public ICompany getCompanyForName(String company) {
    return nameToCompany.get(company);
  }
  public ICompany getCompanyForTicker(String ticker) {
    return tickerToCompany.get(ticker);
  }

  public Set<String> getSectors() {
    return companiesInSector.keySet();
  }

  public Set<Company> getCompaniesInSector(String sector) {
    return companiesInSector.get(sector);
  }

  public double getCurrentSectorPrice(String sector) {
    return 0.0;
  }
  public double getSectorChange(String sector) {
    return 0.0;
  }
  public double getSectorPercentageChange(String sector) {
    return 0.0;
  }

  public double sectorYearHigh() {
    return 0.0;
  }
  public double sectorYearLow() {
    return 0.0;
  }
  public double sectorYearAverageClose() {
    return 0.0;
  }

  public double getSectorClosePriceOnDate(Calendar date) {
    return 0.0;
  }

}

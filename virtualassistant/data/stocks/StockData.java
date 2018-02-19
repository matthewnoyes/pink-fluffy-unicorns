package virtualassistant.data.stocks;

import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.Set;

public class StockData implements IStockData {

  //<Ticker, Company class>
  private HashMap<String, Company> tickerToCompany;

  //<Sector, Set of tickers>
  private HashMap<String, Set<Company>> companiesInSector;

  //<Company Name, Ticker>
  private HashMap<String, Company> nameToCompany;

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
    /*
    Iterator<Map.Entry<String, Integer>> it = sectors.entrySet().iterator();

    int j = 0;

    while (it.hasNext()) {
      Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();

      Company[] sectorCom = Scrapper.getSector(pair.getKey(), pair.getValue());

      j += sectorCom.length;

      companies.put(pair.getKey(), sectorCom);

      for (int i = 0; i < sectorCom.length; i++) {
        Company com = sectorCom[i];

        companySectors.put(com.getName(), pair.getKey());
      }

    }*/

  }

  public ICompany getCompanyForName(String company) {
    return null;
  }
  public ICompany getCompanyForTicker(String ticker) {
    return null;
  }

  public String[] getSectors() {
    return null;
  }

  public ICompany[] getCompaniesInSector(String sector) {
    return null;
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

  public double getSectorClosePriceOnDate(Date date) {
    return 0.0;
  }

}

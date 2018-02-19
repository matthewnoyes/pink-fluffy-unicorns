package virtualassistant.data.stocks;

import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class StockData implements IStockData {

  private HashMap<String, Company[]> companies;

  //<Company name, Sector> -- for quicker accessing
  private HashMap<String, String> companySectors;

  public StockData() throws IOException {

    //Get the cookie and crumb for yahoo
    Scrapper.setupYahoo();

    companies = new HashMap<String, Company[]>();
    companySectors = new HashMap<String, String>();

    HashMap<String, Integer> sectors = Scrapper.getSectors();

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

    }

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

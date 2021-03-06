package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.Collection;

import java.io.IOException;
import java.io.BufferedWriter;

public class StockData implements IStockData {

  //<Ticker, Company class>
  private HashMap<String, Company> tickerToCompany;

  //<Company Name, Ticker>
  private HashMap<String, Company> nameToCompany;

  //<Sector, Set of tickers>
  private HashMap<String, Set<Company>> companiesInSector;

  public StockData(boolean verbose) throws IOException {

    tickerToCompany = new HashMap<String, Company>();
    companiesInSector = new HashMap<String, Set<Company>>();
    nameToCompany = new HashMap<String, Company>();

    System.out.println("Downloading sector data...");

    HashMap<String, Integer> sectors = Scrapper.getSectors();
    int index = 1;

    for (Map.Entry<String, Integer> sector : sectors.entrySet()) {
      if (verbose) {
        System.out.println("Loading " + sector.getKey() + " data (" + index++ + "/41)...");
      }
      Set<Company> comInCurrSector = Scrapper.getSector(sector.getKey(), sector.getValue());

      companiesInSector.put(sector.getKey(), comInCurrSector);

      for (Company com : comInCurrSector) {
        tickerToCompany.put(com.getTicker(), com);
        nameToCompany.put(com.getName(), com);
      }
    }
  }

  public static StockData reloadData(StockData old) throws IOException, Exception {

    if (!Scrapper.updateCurrentData(old.tickerToCompany)) {
      throw new Exception("Companies have changed");
    }

    for (Company com : old.tickerToCompany.values()) {
      com.updatePastData();
    }

    return old;
  }

  public StockData(Set<Company> companies) throws IOException {

    tickerToCompany = new HashMap<String, Company>();
    companiesInSector = new HashMap<String, Set<Company>>();
    nameToCompany = new HashMap<String, Company>();

    HashMap<String, Integer> sectors = Scrapper.getSectors();

    for (String sector : sectors.keySet()) {
      companiesInSector.put(sector, new HashSet<Company>());
    }

    for (Company com : companies) {

      if (companiesInSector.containsKey(com.getSector())) {
        companiesInSector.get(com.getSector()).add(com);
      } else {
        //error
      }

      tickerToCompany.put(com.getTicker(), com);
      nameToCompany.put(com.getName(), com);
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

  public Set<String> getCompanyNames() {
    return nameToCompany.keySet();
  }

  public Set<String> getCompanyTickers() {
    return tickerToCompany.keySet();
  }

  public boolean isSector(String sector) {
    return companiesInSector.containsKey(sector);
  }

  public Set<Company> getCompaniesInSector(String sector) {
    return companiesInSector.get(sector);
  }

  public Set<Company> getRisingInSector(String sector) {
    HashSet<Company> rising = new HashSet<Company>();

    for (Company com : getCompaniesInSector(sector)) {
      if (com.getChange() > 0) {
        rising.add(com);
      }
    }

    return rising;
  }
  public Set<Company> getFallingInSector(String sector) {
    HashSet<Company> falling = new HashSet<Company>();

    for (Company com : getCompaniesInSector(sector)) {
      if (com.getChange() < 0) {
        falling.add(com);
      }
    }

    return falling;
  }

  public Collection<Company> getAllCompanies() {
    return tickerToCompany.values();
  }

  public double getCurrentSectorPrice(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getCurrentPrice();
    }

    return total;
  }

  public double getSectorChange(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getChange();
    }

    return total;
  }

  public double getSectorPercentageChange(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    //Protect against divide by 0 errors
    if (companiesInSector.get(sector).size() == 0) {
      return 0.0;
    }

    return 100.0 * (getSectorChange(sector) / getCurrentSectorPrice(sector));
  }

  public double getSectorOpen(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getOpen();
    }

    return total;
  }

  public double getSectorHigh(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getHigh();
    }

    return total;
  }

  public double getSectorLow(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getLow();
    }

    return total;
  }

  public int getSectorVolume(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1;

    int total = 0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getVolume();
    }

    return total;
  }

  public double getSectorClose(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getClose();
    }

    return total;
  }

  public double sectorYearHigh(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    double high = 0.0;
    for (Calendar date = Calendar.getInstance(); date.after(lastYear); date.add(Calendar.DAY_OF_YEAR, -1)) {
      double day = getSectorHighOnDate(sector, date);
      if (day > high) {
        high = day;
      }
    }

    return high;
  }

  public double sectorYearLow(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    double low = 1000000.0;
    for (Calendar date = Calendar.getInstance(); date.after(lastYear); date.add(Calendar.DAY_OF_YEAR, -1)) {
      double day = getSectorLowOnDate(sector, date);
      if (day > 0 && day < low) {
        low = day;
      }
    }

    return low;
  }

  public double sectorYearAverageClose(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    double count = 0.0;
    for (Company com : getCompaniesInSector(sector)) {
      total += com.yearAverageClose();
      count++;
    }

    return total / count;
  }

  public double sectorAverageVolume(String sector) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    double count = 0.0;
    for (Company com : getCompaniesInSector(sector)) {
      total += com.yearAverageVolume();
      count++;
    }

    return total / count;
  }

  public double getSectorClosePriceOnDate(String sector, Calendar date) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getClosePriceOnDate(date);
    }

    return total;
  }

  public double getSectorOpenPriceOnDate(String sector, Calendar date) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getOpenPriceOnDate(date);
    }

    return total;
  }

  public double getSectorHighOnDate(String sector, Calendar date) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getHighPriceOnDate(date);
    }

    return total;
  }

  public double getSectorLowOnDate(String sector, Calendar date) {
    if (!companiesInSector.containsKey(sector))
      return -1.0;

    double total = 0.0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getLowPriceOnDate(date);
    }

    return total;
  }

  public int getSectorVolumeOnDate(String sector, Calendar date) {
    if (!companiesInSector.containsKey(sector))
      return -1;

    int total = 0;
    for (Company company : companiesInSector.get(sector)) {
      total += company.getVolumeOnDate(date);
    }

    return total;
  }

  public boolean unitTest(BufferedWriter logger) throws IOException {

    int sum = 0;
    for (Set com : companiesInSector.values()) {
      sum += com.size();
    }

    if (sum != tickerToCompany.size() || sum != nameToCompany.size()) {
      logger.write("Company stores inconsistent\n");
      return false;
    }
    logger.write("Company stores consistent\n");

    if (!isSector("Construction & Materials")) {
      logger.write("Missing random sector\n");
      return false;
    }
    logger.write("Random sector present\n");

    if (sectorYearLow("Pharmaceuticals & Biotechnology") == -1.0) {
      logger.write("Sector year low fails\n");
      return false;
    }
    logger.write("Sector year low succeeds\n");

    if (sectorYearHigh("Software & Computer Services") == -1.0) {
      logger.write("Sector year high fails\n");
      return false;
    }
    logger.write("Sector year high succeeds\n");




    return true;
  }
}

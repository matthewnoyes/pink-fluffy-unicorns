package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.Set;
import java.util.Collection;

//Manages all the stocks data
public interface IStockData {

  //Company getters
  public ICompany getCompanyForName(String company);
  public ICompany getCompanyForTicker(String ticker);

  public Set<String> getSectors();
  public Set<String> getCompanyNames();
  public Set<String> getCompanyTickers();

  public Set<Company> getCompaniesInSector(String sector);
  public Collection<Company> getAllCompanies();

  //Sector info
  public double getCurrentSectorPrice(String sector);
  public double getSectorChange(String sector);
  public double getSectorPercentageChange(String sector);

  public double sectorYearHigh();
  public double sectorYearLow();
  public double sectorYearAverageClose();

  public double getSectorClosePriceOnDate(Calendar date);

}

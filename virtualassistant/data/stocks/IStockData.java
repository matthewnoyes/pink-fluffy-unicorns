package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.Set;

//Manages all the stocks data
public interface IStockData {

  //Company getters
  public ICompany getCompanyForName(String company);
  public ICompany getCompanyForTicker(String ticker);

  public Set<String> getSectors();

  public Set<Company> getCompaniesInSector(String sector);

  //Sector info
  public double getCurrentSectorPrice(String sector);
  public double getSectorChange(String sector);
  public double getSectorPercentageChange(String sector);

  public double sectorYearHigh();
  public double sectorYearLow();
  public double sectorYearAverageClose();

  public double getSectorClosePriceOnDate(Calendar date);

}

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

  public boolean isSector(String sector);

  public Set<Company> getCompaniesInSector(String sector);
  public Set<Company> getRisingInSector(String sector);
  public Set<Company> getFallingInSector(String sector);
  public Collection<Company> getAllCompanies();

  //Sector info
  public double getCurrentSectorPrice(String sector);
  public double getSectorChange(String sector);
  public double getSectorPercentageChange(String sector);

  public double getSectorOpen(String sector);
  public double getSectorHigh(String sector);
  public double getSectorLow(String sector);
  public int getSectorVolume(String sector);
  public double getSectorClose(String sector);

  public double sectorYearHigh(String sector);
  public double sectorYearLow(String sector);
  public double sectorYearAverageClose(String sector);
  public double sectorAverageVolume(String sector);

  public double getSectorClosePriceOnDate(String sector, Calendar date);
  public int getSectorVolumeOnDate(String sector, Calendar date);
  public double getSectorLowOnDate(String sector, Calendar date);
  public double getSectorHighOnDate(String sector, Calendar date);

}

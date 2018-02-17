package virtualassistant.data.stocks;

import java.util.Date;

//Manages all the stocks data
public interface IStockData {

  //Company getters
  public ICompany getCompanyForName(String company);
  public ICompany getCompanyForTicker(String ticker);

  public String[] getSectors();

  public ICompany[] getCompaniesInSector(String sector);

  //Sector info
  public double getCurrentSectorPrice(String sector);
  public double getSectorChange(String sector);
  public double getSectorPercentageChange(String sector);

  public double sectorYearHigh();
  public double sectorYearLow();
  public double sectorYearAverageClose();

  public double getSectorClosePriceOnDate(Date date);

}

package virtualassistant.data.stocks;

//Manages all the stocks data
public interface IStockData {

  public String getTicker(String company);

  public String getSector(String ticker);
  public String[] getTickersInSector(String sector);

  public double getCurrentPrice(String ticker);
  public double getChange(String ticker);
  public double getPercentageChange(String ticker);

  public double getCurrentSectorPrice(String sector);
  public double getSectorChange(String sector);
  public double getSectorPercentageChange(String sector);

}

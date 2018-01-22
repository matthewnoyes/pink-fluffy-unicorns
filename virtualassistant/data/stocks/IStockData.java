package virtualassistant.data.stocks;

//Manages all the stocks data
public interface IStockData {

  public String getTicker(String company);

  public String getSector(String ticker);
  public String getTickersInSector(String sector);

  public double getCurrentPrice(String ticker);

}

package virtualassistant.data.stocks;

import java.util.Calendar;

public class Company implements ICompany {

  private String ticker;
  private String name;
  private String sector;

  private double currentPrice;
  private double currentChange;
  private double currentPercentChange;

  private double yearHigh;
  private double yearLow;
  private double averageClose;

  private HistoricalData[] pastData;

  public Company(String ticker, String name, String sector) {
    this.ticker = ticker;
    this.name = name;
    this.sector = sector;
    try {
      pastData = Scrapper.getPastData(ticker);

      //Do something with the data
    } catch (Exception e) {
      System.out.println(ticker);
      System.out.println(e);
    }
  }

  public Company(String ticker, String name, String sector, HistoricalData[] pastData) {

  }

  public String getName() {
    return name;
  }

  public String getTicker() {
    return ticker;
  }

  public String getSector() {
    return sector;
  }

  //-------------------- Current data -----------------

  public void updatePrice(double price) {
    currentPrice = price;
  }

  public void updatePercentageChange(double change) {
    currentChange = change;
  }

  public void updateChange(double change) {
    currentPercentChange = change;
  }

  public double getCurrentPrice() {
    return currentPrice;
  }
  public double getChange() {
    return currentChange;
  }
  public double getPercentageChange() {
    return currentPercentChange;
  }

  public double getOpen() {
    return getOpenPriceOnDate(Calendar.getInstance());
  }

  public double getHigh() {
    return getHighPriceOnDate(Calendar.getInstance());
  }

  public double getLow() {
    return getLowPriceOnDate(Calendar.getInstance());
  }

  public double getVolume() {
    return getVolumeOnDate(Calendar.getInstance());
  }

  //----------------- Past data ----------------

  public HistoricalData[] getPastData() {
    return pastData;
  }

  public double yearHigh() {
    return yearHigh;
  }

  public double yearLow() {
    return yearLow;
  }

  public double yearAverageClose() {
    return averageClose;
  }

  public double yearAverageVolume() {
    return 0.0;
  }

  public double getClosePriceOnDate(Calendar day) {
    return 0.0;
  }

  public double getOpenPriceOnDate(Calendar day) {
    return 0.0;
  }

  public double getHighPriceOnDate(Calendar day) {
    return 0.0;
  }

  public double getLowPriceOnDate(Calendar day) {
    return 0.0;
  }

  public double getVolumeOnDate(Calendar day) {
    return 0.0;
  }


}

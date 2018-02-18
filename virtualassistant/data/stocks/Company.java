package virtualassistant.data.stocks;

import java.util.Date;

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

  public Company(String ticker, String name, String sector) {
    this.ticker = ticker;
    this.name = name;
    this.sector = sector;
    try {
      Double[][] histData = Scrapper.getPastData(ticker);

      //Do something with the data
    } catch (Exception e) {
      System.out.println(ticker);
      System.out.println(e);
    }
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

  //----------------- Past data ----------------

  public double yearHigh() {
    return yearHigh;
  }

  public double yearLow() {
    return yearLow;
  }

  public double yearAverageClose() {
    return averageClose;
  }

  public double getClosePriceOnDate(Date date) {
    return 0.0;
  }


}

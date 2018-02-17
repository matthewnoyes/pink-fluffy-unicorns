package virtualassistant.data.stocks;

import java.util.Date;

public class Company implements ICompany {

  private String ticker;
  private String name;
  private String sector;

  public Company(String ticker, String name, String sector) {
    this.ticker = ticker;
    this.name = name;
    this.sector = sector;
  }

  public String getName() {
    return name;
  }

  public String getTicker() {
    return ticker;
  }

  public double getCurrentPrice() {
    return 0.0;
  }
  public double getChange() {
    return 0.0;
  }
  public double getPercentageChange() {
    return 0.0;
  }

  public String getSector() {
    return "Unknown";
  }

  public void updatePrice(double price) {

  }

  public void updatePercentageChange(double change) {

  }

  public void updateChange(double change) {

  }

  public double yearHigh() {
    return 0.0;
  }

  public double yearLow() {
    return 0.0;
  }

  public double yearAverageClose() {
    return 0.0;
  }

  public double getClosePriceOnDate(Date date) {
    return 0.0;
  }


}

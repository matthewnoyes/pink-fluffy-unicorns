package virtualassistant.data.stocks;

//A store for each company
public interface ICompany {

  //Company infomation
  public String getName();
  public String getTicker();

  public String getSector();

  //Current data
  public double getCurrentPrice();
  public double getChange();
  public double getPercentageChange();

  //Data from last year
  public double yearHigh();
  public double yearLow();
  public double yearAverageClose();

  public double getClosePriceOnDate(Date date);


}

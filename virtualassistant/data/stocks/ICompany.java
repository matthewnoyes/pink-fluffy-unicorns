package virtualassistant.data.stocks;

import java.util.Calendar;

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

  public double getOpen();
  public double getHigh();
  public double getLow();
  public double getVolume();
  public double getClose();

  //Data from last year
  public double yearHigh();
  public double yearLow();
  public double yearAverageClose();
  public double yearAverageVolume();

  public double getClosePriceOnDate(Calendar day);
  public double getOpenPriceOnDate(Calendar day);
  public double getHighPriceOnDate(Calendar day);
  public double getLowPriceOnDate(Calendar day);
  public int getVolumeOnDate(Calendar day);


}

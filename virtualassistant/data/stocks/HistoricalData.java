package virtualassistant.data.stocks;

import java.util.Calendar;

public class HistoricalData {

  public Calendar date;
  public double open;
  public double close;
  public double high;
  public double low;
  public int volume;

  public HistoricalData(Calendar date) {
    this.date = date;
  }

}

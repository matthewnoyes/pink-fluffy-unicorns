package virtualassistant.data.stocks;

import java.util.Calendar;
import java.util.HashMap;

public class HistoricalData extends HashMap<Calendar, HistoricalData.Record> {

  @Override
  public Record put(Calendar key, Record value) {
    Record previous = super.put(key, value);

    //Check for dates from more than a year ago.

    return previous;
  }

  public static class Record {
    public double open;
    public double close;
    public double high;
    public double low;
    public int volume;
  }

}

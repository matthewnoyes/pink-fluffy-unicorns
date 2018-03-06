package virtualassistant.data.stocks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;

import java.util.HashMap;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.util.Set;
import java.util.HashSet;

public class Scrapper {

  //Bits of lse website
  private static String lse1 = "http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=";
  private static String lse2 = "&page=";

  //Bits of yahoo website
  private static String yahoo1 = "https://query1.finance.yahoo.com/v7/finance/download/";
  private static String yahoo2 = "?period1=";
  private static String yahoo3 = "&period2=";
  private static String yahoo4 = "&interval=1d&events=history&crumb=";

  private static String crumb;
  private static String cookie;

  /**
   * Gets a list of all the companies in the given sector.
   *
   */
  public static Set<Company> getSector(String sectorName, int value) throws IOException {
    HashSet<Company> companies = new HashSet<Company>();

    //Get the page for the sector
    Document doc = Jsoup.connect(lse1 + String.format("%04d", value) + lse2 + "1").get();

    Elements table = doc.select("[summary=\"Companies and Prices\"]");

    //If no companies in the sector return.
    if (table.size() == 0) {
      return companies;
    }


    Elements tbody = table.first().select("tbody");

    //Iterate over all companies
    for (Element row : tbody.first().select("tr")) {

      Elements boxes = row.select("td");

      //Setup company with ticker, name and sector
      Company company = new Company(boxes.get(0).html(), boxes.get(1).child(0).html(), sectorName);

      //Update prices
      company.updatePrice(Double.parseDouble(boxes.get(3).html().replace(",", "")));
      company.updateChange(Double.parseDouble(boxes.get(4).html().split("<")[0]));
      try {
        //Sometimes fails - don't know why
        company.updatePercentageChange(Double.parseDouble(boxes.get(5).html()));
      } catch (Exception e) {
        company.updatePercentageChange(0.0);
      }

      companies.add(company);
    }

    return companies;
  }

  /**
   * Updates the company stock prices.
   *
   * Returns true if all updated, false means list has changed
   */
  public static boolean updateCurrentData(HashMap<String, Company> companies) throws IOException {
    int count = 0;
    for (int page = 1; page < 7; page++) {

      Document doc = Jsoup.connect(lse1+lse2+page).get();

      Elements table = doc.select("[summary=\"Companies and Prices\"]");


      Elements tbody = table.first().select("tbody");

      for (Element row : tbody.first().select("tr")) {

        Elements boxes = row.select("td");

        Company com = companies.get(boxes.get(0).html());
        if (com == null) {
          //Unknown company -- list has changed
          return false;
        }

        // Company company = new Company(boxes.get(0).html(), boxes.get(1).child(0).html(), "");
        // //System.out.println(boxes.get(3).html().replace(",", ""));
        //

        com.updatePrice(Double.parseDouble(boxes.get(3).html().replace(",", "")));
        com.updateChange(Double.parseDouble(boxes.get(4).html().split("<")[0]));
        try {
          //Sometimes fails - jsoup not parsing correctly?
          com.updatePercentageChange(Double.parseDouble(boxes.get(5).html()));
        } catch (Exception e) {
          com.updatePercentageChange(0.0);
          System.out.println(e);
        }
        //
        // companies[position] = company;
        // position++;
        count++;
      }
    }

    if (count == companies.size()) {
      return true;
    }
    return false;

  }

  public static void updateHistoricalData(HistoricalData pastData, String ticker) throws IOException {

    if (crumb == null || cookie == null) {
      setupYahoo();
    }

    //weird regex
    ticker = ticker.replaceAll("\\Q.\\E", "-");

    //Remove trailing dashes
    if (ticker.charAt(ticker.length() - 1) == '-') {
      ticker = ticker.substring(0,ticker.length() - 1);
    }

    ticker += ".L";

    //Setup the date range

    //From 4 days from before newest entry - to get weekends
    Calendar cal = (Calendar)pastData.getLatestEntryDate().clone();

    cal.add(Calendar.DAY_OF_YEAR, -4);
    long pastDay = (long)cal.getTime().getTime()/1000;
    cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, -1);
    long currentDay = (long)cal.getTime().getTime()/1000;

    //Get csv file
    Document doc = Jsoup.connect(yahoo1 + ticker + yahoo2 + pastDay + yahoo3 + currentDay + yahoo4 + crumb).userAgent("Mozilla").cookie("B", cookie).get();

    //Split up the CSV file
    String[] rawData = doc.html().split("[ ,]");

    //Ignore the start and end of the CSV file (not data)
    for (int i = 12; i < rawData.length-2; i += 7) {

      //Date
      String strDate = rawData[i];

      Calendar date = Calendar.getInstance();
      date.set(Integer.parseInt(strDate.substring(0, 4)), Integer.parseInt(strDate.substring(5, 7)) - 1, Integer.parseInt(strDate.substring(8, 10)));
      date = Company.resetTime(date);
      HistoricalData.Record entry = new HistoricalData.Record();

      try {
        entry.open = Double.parseDouble(rawData[i+1]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.open = -1.0;
      }
      try {
        entry.high = Double.parseDouble(rawData[i+2]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.high = -1.0;
      }
      try {
        entry.low = Double.parseDouble(rawData[i+3]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.low = -1.0;
      }
      try {
        entry.close = Double.parseDouble(rawData[i+4]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.close = -1.0;
      }
      // 4 is ajusted close
      try {
        entry.volume = Integer.parseInt(rawData[i+6]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.volume = -1;
      }

      pastData.put(date, entry);
    }
  }

  /**
   * Returns all the sectors and the corresponding number for LSE's website
   *
   * @return A map of sectorsas the key and their number for the value
   */
  public static HashMap<String, Integer> getSectors() throws IOException {

    HashMap<String, Integer> sectors = new HashMap<String, Integer>();

    //Get the webpage
    Document doc = Jsoup.connect(lse1 + lse2 + "1").get();

    Element selector = doc.select("[id=\"industrySector\"]").first();

    //Iterate through all sectors
    for (int i = 1; i < selector.children().size(); i++) {
      //Add the sector to the map
      sectors.put(Jsoup.parse(selector.child(i).html()).text(), Integer.parseInt(selector.child(i).attr("value")));
    }

    return sectors;

  }


  private static void setupYahoo() throws IOException {
    //Setup date range
    Calendar cal = Calendar.getInstance();

    cal.add(Calendar.MINUTE, -1);
    long currentDay = (long)cal.getTime().getTime()/1000;
    cal.add(Calendar.YEAR, -1);
    long pastDay = (long)cal.getTime().getTime()/1000;

    Connection.Response testSend = Jsoup.connect("https://uk.finance.yahoo.com/quote/BT-A.L/history?period1="+pastDay+"&period2="+currentDay+"&interval=1d&filter=history&frequency=1d").execute();

    //Extract crumb
    Document page = testSend.parse();
    int cs = page.html().indexOf("CrumbStore");
    int cr = page.html().indexOf("crumb", cs + 10);
	  int cl = page.html().indexOf(":", cr + 5);
	  int q1 = page.html().indexOf("\"", cl + 1);
	  int q2 = page.html().indexOf("\"", q1 + 1);
    String test = page.html().substring(q1+1,q2);

    test = test.replaceAll("\\\\u002F", "/");
    crumb = Jsoup.parse(new String(test.getBytes(), "UTF-8")).text();

    // System.out.println(Jsoup.parse("h0J\u002FSVFBI2l").text());

    //Store corresponding cookie
    cookie = testSend.cookie("B");

  }

  /**
   * Gets the historical data for a stock
   *
   * NOTE: return type may change.
   */
  public static HistoricalData getPastData(String ticker) throws IOException {

    if (crumb == null || cookie == null) {
      setupYahoo();
    }

    //weird regex
    ticker = ticker.replaceAll("\\Q.\\E", "-");

    //Remove trailing dashes
    if (ticker.charAt(ticker.length() - 1) == '-') {
      ticker = ticker.substring(0,ticker.length() - 1);
    }

    ticker += ".L";

    //Setup the date range
    Calendar cal = Calendar.getInstance();

    cal.add(Calendar.MINUTE, -1);
    long currentDay = (long)cal.getTime().getTime()/1000;
    cal.add(Calendar.YEAR, -1);
    long pastDay = (long)cal.getTime().getTime()/1000;

    //Get csv file
    Document doc = Jsoup.connect(yahoo1 + ticker + yahoo2 + pastDay + yahoo3 + currentDay + yahoo4 + crumb).userAgent("Mozilla").cookie("B", cookie).get();

    //Split up the CSV file
    String[] rawData = doc.html().split("[ ,]");

    //Put the data into the array
    HistoricalData data = new HistoricalData();

    //Ignore the start and end of the CSV file (not data)
    for (int i = 12; i < rawData.length-2; i += 7) {

      //Date
      String strDate = rawData[i];

      Calendar date = Calendar.getInstance();
      date.set(Integer.parseInt(strDate.substring(0, 4)), Integer.parseInt(strDate.substring(5, 7)) - 1, Integer.parseInt(strDate.substring(8, 10)));
      date = Company.resetTime(date);
      HistoricalData.Record entry = new HistoricalData.Record();

      try {
        entry.open = Double.parseDouble(rawData[i+1]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.open = -1.0;
      }
      try {
        entry.high = Double.parseDouble(rawData[i+2]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.high = -1.0;
      }
      try {
        entry.low = Double.parseDouble(rawData[i+3]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.low = -1.0;
      }
      try {
        entry.close = Double.parseDouble(rawData[i+4]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.close = -1.0;
      }
      // 4 is ajusted close
      try {
        entry.volume = Integer.parseInt(rawData[i+6]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        entry.volume = -1;
      }

      data.put(date, entry);
    }

    return data;
  }


  public static void main(String[] args) throws IOException, ParseException {
    //getPastData("BT-A");
    StockData data = new StockData(true);

    System.out.println(data.getCompanyForTicker("BT.A").getOpen());
  }
  
  public boolean unitTest(){
        return false;
    }
}

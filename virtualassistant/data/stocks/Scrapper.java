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

  private static String lse1 = "http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=";
  private static String lse2 = "&page=";

  private static String yahoo1 = "https://query1.finance.yahoo.com/v7/finance/download/";
  private static String yahoo2 = "?period1=";
  private static String yahoo3 = "&period2=";
  private static String yahoo4 = "&interval=1d&events=history&crumb=";

  /*
   *TEST
   */
  public static Company[] allCompanies() throws IOException {

    //100 companies in FTSE100  -- X actually 101 constituents
    Company[] companies = new Company[101];
    int position = 0;

    for (int page = 1; page < 7; page++) {

      Document doc = Jsoup.connect(lse1+lse2+page).get();

      Elements table = doc.select("[summary=\"Companies and Prices\"]");


      Elements tbody = table.first().select("tbody");

      for (Element row : tbody.first().select("tr")) {

        Elements boxes = row.select("td");

        Company company = new Company(boxes.get(0).html(), boxes.get(1).child(0).html(), "");
        //System.out.println(boxes.get(3).html().replace(",", ""));

        company.updatePrice(Double.parseDouble(boxes.get(3).html().replace(",", "")));
        company.updateChange(Double.parseDouble(boxes.get(4).html().split("<")[0]));
        company.updatePercentageChange(Double.parseDouble(boxes.get(5).html()));

        companies[position] = company;
        position++;


      }
    }

    return companies;


  }

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
      company.updatePercentageChange(Double.parseDouble(boxes.get(5).html()));

      companies.add(company);
    }

    return companies;
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
      sectors.put(selector.child(i).html(), Integer.parseInt(selector.child(i).attr("value")));
    }

    return sectors;

  }

  /*
   *TEST
   */
  public static void histData() throws ParseException {
    String dateString = "27 01 2017 12:00:00";
    DateFormat dateFormat = new SimpleDateFormat("dd mm yyyy hh:mm:ss");
    Date date = dateFormat.parse(dateString);
    long unixTime = (long)date.getTime()/1000;
    System.out.println(unixTime);
    String loc = "https://query1.finance.yahoo.com/v7/finance/download/SKY.L?period1=1485388800&period2=1516924800&interval=1d&events=history&crumb=oGI5lMzxt0P";
  }

  private static String crumb;
  private static String cookie;


  public static void setupYahoo() throws IOException {
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
    crumb = page.html().substring(q1+1,q2);

    //Store corresponding cookie
    cookie = testSend.cookie("B");

  }

  /**
   * Gets the historical data for a stock
   *
   * NOTE: return type may change.
   */
  public static Double[][] getPastData(String ticker) throws IOException, ParseException {

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

    //Not working properly
    String[] rawData = doc.html().split("[\n,]");
    int currRow = 0;

    //Put the data into the array
    Double[][] data = new Double[6][366];
    for (int i = 10; i < rawData.length-2; i += 6) {

      try {
        data[0][currRow] = Double.parseDouble(rawData[i]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        data[0][currRow] = -1.0;
      }
      try {
        data[1][currRow] = Double.parseDouble(rawData[i+1]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        data[1][currRow] = -1.0;
      }
      try {
        data[2][currRow] = Double.parseDouble(rawData[i+2]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        data[2][currRow] = -1.0;
      }
      try {
        data[3][currRow] = Double.parseDouble(rawData[i+3]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        data[3][currRow] = -1.0;
      }
      try {
        data[4][currRow] = Double.parseDouble(rawData[i+4]);
      } catch (NumberFormatException e) {
        //Mark that there is no data for this day
        data[4][currRow] = -1.0;
      }
      // data[1][currRow] = Double.parseDouble(rawData[i+1]);
      // data[2][currRow] = Double.parseDouble(rawData[i+2]);
      // data[3][currRow] = Double.parseDouble(rawData[i+3]);
      // data[4][currRow] = Double.parseDouble(rawData[i+4]);

      //Volume - not working
      // data[5][currRow] = Double.parseDouble(rawData[i+5]);

      currRow++;
    }

    return data;
  }

  public static void main(String[] args) throws IOException, ParseException {
    //getPastData("BT-A");
    StockData data = new StockData();
  }
}

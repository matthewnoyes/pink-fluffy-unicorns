package virtualassistant.data.stocks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;

public class Scrapper {

  private static String lse1 = "http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=";
  private static String lse2 = "&page=";

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
  public static Company[] getSector(String sectorName, int value) throws IOException {
    ArrayList<Company> companies = new ArrayList<Company>();

    //Get the page for the sector
    Document doc = Jsoup.connect(lse1 + String.format("%04d", value) + lse2 + "1").get();

    Elements table = doc.select("[summary=\"Companies and Prices\"]");

    //If no companies in the sector return.
    if (table.size() == 0) {
      return new Company[0];
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

    //Convert to array
    Company[] result = new Company[companies.size()];
    companies.toArray(result);

    return result;
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

  public static void main(String[] args) throws IOException, ParseException {

    StockData data = new StockData();
  }
}

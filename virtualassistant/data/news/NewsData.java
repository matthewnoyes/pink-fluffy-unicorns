package virtualassistant.data.news;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;


public class NewsData implements INewsData{
	private DateFormat multiUseFormat = new SimpleDateFormat("dd MMM yyyy HH:mm"); // date format for multiple news
	private DateFormat rnsNewsDateFormat = new SimpleDateFormat("HH:mm dd-MMM-yyyy"); // date format for rns londonstockexchange news
	private DateFormat yahooNewFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"); // Date format for yahoo news
	private DateFormat sectorNewsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // Date format for the sectors



	public LinkedList<NewsObj> getRnsNews(String company) throws IOException, ParseException {
		LinkedList<NewsObj> articlesObjs = new LinkedList<NewsObj>(); // arraylist of news articles objects
		for(int i=1;i<=6;i++){ // loop to go through each page and get the isin number of the company wanted.
			final Document doc = Jsoup.connect("http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=&page=" + i).get();
				for(Element li: doc.select(".table_dati tr")){ // going through each list item and adding the article into the arraylist
					if(li.select(".name:eq(0)").text().equals(company)){
						String kk = li.select(".name:eq(1)").first().select(".name a").attr("href");
						return rnsNewsArticleGetter(isinNumberGetter(kk),company); // We've got the isin number, now get all the news from it
					}
				}
			}
			return articlesObjs;
		}

    private LinkedList<NewsObj> rnsNewsArticleGetter(String webpage,String company) throws IOException, ParseException {
            LinkedList<NewsObj> tempArticlesObjs = new LinkedList<NewsObj>();
        final Document doc = Jsoup.connect(webpage).get(); // all the articles from lse new analysis is gotten.
        for(Element row: doc.select(".table_datinews tr")){ // loop of articles in the table row is done
            Calendar calDate = Calendar.getInstance();
            final String title =  company + " - "+ row.select(".text-left a").text();
            calDate.setTime(rnsNewsDateFormat.parse((row.select(".datetime.nowrap").text()))); // with the date formatting
            final String source = row.select(".nowrap.text-left").get(1).text();
            final String impact = row.select(".nowrap.text-left").get(2).text();
            final String relHref = row.select(".text-left a").first().attr("href");
            final String absurl=javascriptLinkToUrl(relHref); // html link is a javascript script so need to use regex to get the link out.
            tempArticlesObjs.add(newsArrayAdder(calDate,title,impact,absurl,source)); // news article object added to arraylist
        }
        return tempArticlesObjs;
    }

	private String isinNumberGetter(String webPageLink){	 // used to get the isin Number of the company and return the webpage for that company
		final Pattern pattern = Pattern.compile("/summary/company-summary/(.*?).html"); // regex used to get the part of link needed (the isin number).
		final Matcher matcher = pattern.matcher(webPageLink);
		String absoluteUrl = "";
		while(matcher.find()){
			absoluteUrl = "http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/exchange-insight/news-analysis.html?fourWayKey=" + matcher.group(1);
		}
		return absoluteUrl; // returns the url needed
	}

	private String javascriptLinkToUrl(String jsUrl){	 // used to convert a javascript link into an actual usable url
		final Pattern pattern = Pattern.compile("'/exchange/news/(.*?).html"); // regex used to get the part of link needed.
		final Matcher matcher = pattern.matcher(jsUrl);
		String absurl = "";
		while(matcher.find()){
			absurl = "http://www.londonstockexchange.com/exchange/news/" + matcher.group(1) +".html";
		}
		return absurl;
	}

	public LinkedList<NewsObj> getAllianceNews(String company) throws IOException, ParseException { // COMPANY OFFICIAL NAME E.G BARCLAYS = BARC MUST BE IN CAPITALS LETTERS
		LinkedList<NewsObj> articlesObjs = new LinkedList<NewsObj>();
		Date date = new Date();		
		String todaysDate= new SimpleDateFormat("dd MMM yyyy").format(date);

		final Document doc = Jsoup.connect("http://www.londonstockexchange.com/exchange/news/alliance-news/company-news.html?tidm=" + company).get();
			for(Element li: doc.select("ul li")){ // going through each list item and adding the article into the arraylist
				Calendar calDate = Calendar.getInstance();
				final String title = company + " - " + (li.select("a").text());
				final String url = "http://www.londonstockexchange.com" + li.select("a").first().attr("href");
				final String dateGrabbed = li.select(".hour").text();
				calDate.setTime(multiUseFormat.parse(dateGrabbed.length() == 5 ? todaysDate + " " + dateGrabbed : dateGrabbed)); // at times the article may be posted today, which will only contain the time need to then add the date to it
				articlesObjs.add(newsArrayAdder(calDate,title,url,"London stock exchange - Alliance News"));  //articles are added to the array list as objects.
			}
			return articlesObjs;
		}

    public LinkedList<NewsObj> getYahooNews(String company) throws IOException, ParseException { // works by getting recent 20 news articles of a company.
        //This method is only compatible with companies now.
        String companyIncorectFormat = company;
        if(company.equals("BT.A")){ // BT'S name is different on yahoo
            companyIncorectFormat = "BT-A";
            }
        
        if(company.charAt(company.length() -1) == '.'){ // This is needed as the company name must end with .l e.g. barc = barc.l
            companyIncorectFormat = company + "L";		
        }else{
            companyIncorectFormat = company + ".L";	
        }
        LinkedList<NewsObj> tempArticlesObjs = new LinkedList<NewsObj>();
        Document doc = Jsoup.parse(new URL("https://feeds.finance.yahoo.com/rss/2.0/headline?s="+company+"&region=US&lang=en-US").openStream(), "UTF-8", "", Parser.xmlParser()); // xml retrieved to be parsed
        for(Element li: doc.select("item")){
            Calendar calDate = Calendar.getInstance(); 
            calDate.setTime(yahooNewFormat.parse(li.select("pubDate").text()));
            final String title = company + " - " + li.select("title").text();
            final String url = li.select("link").text();
            tempArticlesObjs.add(newsArrayAdder(calDate,title,url,"Yahoo Finance News"));
        }
        return tempArticlesObjs;
    }

    private LinkedList<NewsObj> sectorNewsGrabber(String sector, String companies) throws IOException, ParseException { // works by getting recent 20 news articles of companies in a sector				
        //This method is only compatible with sectors now. Give users choice between alliance news and RNS news. 
        LinkedList<NewsObj> tempArticlesObjs = new LinkedList<NewsObj>();
        Document doc = Jsoup.parse(new URL("https://feeds.finance.yahoo.com/rss/2.0/headline?s="+companies+"&region=US&lang=en-US").openStream(), "UTF-8", "", Parser.xmlParser()); // xml retrieved to be parsed
        for(Element li: doc.select("item")){
            Calendar calDate = Calendar.getInstance(); 
            calDate.setTime(yahooNewFormat.parse(li.select("pubDate").text()));
            String title = li.select("title").text();
            if(title.substring(0,4).equals("[$$]")){
                title = sector + " - " + title.substring(4);
            }else{
                title = sector + " - " + title;
            
            }
            final String url = li.select("link").text();
            tempArticlesObjs.add(newsArrayAdder(calDate,title,url,"Yahoo Finance News"));
        }
        return tempArticlesObjs;
    }
	
    public LinkedList<NewsObj> sectorNews(String sector) throws IOException, ParseException {			
        final Document Ftse100 = Jsoup.connect("https://en.wikipedia.org/wiki/FTSE_100_Index").get(); // used to get all companies in a sector
        String allCompaniesInSector = "";
        String sectorWanted = sector;
        int count = 0; // only matters if 0 as no company has been added.

        switch(sector) // Some sector names on chatbot are different to wikipedias version
        {
            case "General Financial":
            sectorWanted = "financial services";
            break;
            case "Household Goods":
            sectorWanted = "Household Goods & Home Construction";
            break;
            case "Industrial Metals":
            sectorWanted = "Industrial Metals & Mining";
            break;
            case "Oil Equipment Services":
            sectorWanted = "Oil Equipment, Services & Distribution";
            break;
        }
        
        for(Element li: Ftse100.select(".wikitable.sortable tbody tr")){ // going through each company and adding its name to a string if its in the sector.
            if(sectorWanted.equals(li.select("td:eq(2)").text())){
                String company = li.select("td:eq(1)").text();

                if(company.equals("BT.A")){ // BT'S name is different on yahoo
                    company = "BT-A";
                }

                if(company.charAt(company.length() -1) == '.'){ // This is needed as the company name must end with .l e.g. barc = barc.l
                    company = company + "L";		
                }else{
                    company = company + ".L";	
                }

                if(count == 0){  // add to the string, each company is to be seperated with commas.
                    allCompaniesInSector = allCompaniesInSector + company;
                    count++;
                }else{
                    allCompaniesInSector = allCompaniesInSector + "," + company;
                }
            }
        }
        return sectorNewsGrabber(sector,allCompaniesInSector);
    }

	private NewsObj newsArrayAdder(Calendar Datetime, String title, String url,String source){ // used to make objects of news articles.
		NewsObj c = new NewsObj(Datetime,title,url,source);
		return c;
	}

	private NewsObj newsArrayAdder(Calendar Datetime, String title, String impact, String url,String source){ // creates objects for each rns news article
		NewsObj c = new NewsObj(Datetime,title,impact,url,source);
		return c;
	}
}

public class Stock {

private String ticker; // "ABF"
private String sector; // "Food producers"
private String company; // "Associated British Foods"

// industrial average
private double open; // opening price
private double high; // today's high
private double low; // today's low
private double vol; // volume - “the number of shares"
private double pe; // price earning
private double mktCap; // market cap
private double yearHigh; // high from 52 weeks ago
private double yearLow; // low from 52 weeks ago
private double avgVol;  // average volume
private double yield; // stock yield

public Stock(String ticker, String company) {
				this.ticker = ticker;
				this.company = company;
				//this.sector = findSector(ticker);
				updateValues();
}
/* NEEDED */
public void updateValues() {
				sector = "random sector";
				open = 0;
				high = 0;
				low = 0;
				vol = 0;
				pe = 0;
				mktCap = 0;
				yearHigh = 0;
				yearLow = 0;
				avgVol = 0;
				yield = 0;
				// somehow update all values
}
public News[] getNews() {
				// gets all news related to this stock
				return null;
}

/* getters */
public String getTicker() {
				return ticker;
}
public String getSector() {
				return sector;
}
public String getCompany() {
				return company;
}
public double getOpen() {
				return open;
}
public double getHigh() {
				return high;
}
public double getLow() {
				return low;
}
public double getVol() {
				return vol;
}
public double getPE() {
				return pe;
}
public double getMktCap() {
				return mktCap;
}
public double getYearHigh() {
				return yearHigh;
}
public double getYearLow() {
				return yearLow;
}
public double getAvgVol() {
				return avgVol;
}
public double getYield() {
				return yield;
}

}

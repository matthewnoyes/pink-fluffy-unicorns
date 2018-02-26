package virtualassistant.data.system;

import java.util.Calendar;
import java.util.Date;

public class SystemStatus {

    private static Date lastUpdatedNews;
    private static Date lastUpdatedStocks;

    private static boolean soundEnabled;
    private static boolean speechEnabled;
    private static double volume; // [0.0, 1.0]

    public SystemStatus(boolean soundEnabled, boolean speechEnabled, double volume, Date lastUpdatedStocks){
        this.soundEnabled = soundEnabled;
        this.speechEnabled = speechEnabled;
        this.volume = volume;
        this.lastUpdatedStocks = lastUpdatedStocks;
    }

    public static Date getLastUpdatedNews(){
        return lastUpdatedNews;
    }

    public static Date getLastUpdatedStocks(){
        return lastUpdatedStocks;
    }

    public static void setLastUpdatedNews(Date lastUpdatedNews){
        //this.lastUpdatedNews = lastUpdatedNews;
    }

    public static void setLastUpdatedStocks(Date lastUpdatedStocks){
        //this.lastUpdatedStocks = lastUpdatedStocks;
    }

    public static void toggleSound(){
        soundEnabled = !soundEnabled;
    }

    public static void toggleSpeech(){
        speechEnabled = !speechEnabled;
    }

    public static void setVolume(){

    }

    public static void setSoundEnabled(){

    }

    public static void setSpeechEnabled(){

    }

    public static boolean getSpeechEnabled(){
      return false;

    }

    public static boolean getSoundEnabled(){
      return false;
    }

    public double getVolume(){
      return 0.0;

    }
}

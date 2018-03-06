package tests;

import virtualassistant.*;
import virtualassistant.ai.*;
import virtualassistant.chatbot.*;
import virtualassistant.data.datastore.*;
import virtualassistant.data.news.*;
import virtualassistant.data.stocks.*;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

// Add tests here
public class UnitTest {

    private static BufferedWriter logger;
      
    public static void main(String[] args) throws IOException {
            
        logger = new BufferedWriter(new FileWriter("tests/logs_unitTesting.txt"));
         
        VirtualAssistant virtualAssistant = null;
        Scrapper scrapper = null;
        StockData stockData = null;
        NewsData newsData = null;
        Loader loader = null;
        Chatbot chatbot = null;
        LearningAgent learningAgent = null;
    
        // Start testing
        logger.write("Testing Virtual Assistant...");
        try {
            virtualAssistant = new VirtualAssistant();
            writeConclusion(virtualAssistant.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing Scrapper...");
        try {
            scrapper = new Scrapper();
            writeConclusion(scrapper.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing StockData...");
        try {          
            stockData = new StockData(true);
            writeConclusion(stockData.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing NewsData...");
        try {
            newsData = new NewsData();
            writeConclusion(newsData.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing Loader...");
        try {
            loader = new Loader();
            writeConclusion(loader.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing Chatbot...");
        try {
            chatbot = new Chatbot();
            writeConclusion(chatbot.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("Testing LearningAgent...");
        try {
            learningAgent = new LearningAgent(stockData, newsData);
            writeConclusion(learningAgent.unitTest(logger));
        } catch (Exception e) {
            writeConclusion(false);
            e.printStackTrace();
        }
         
        logger.write("\nTESTING COMPLETE!");
         
        logger.close();
         
    }
    
    private static void writeConclusion(boolean ok) throws IOException{
        
        if(ok)
            logger.write("SUCCESS\n");
        else 
            logger.write("FAILED\n");
         
    }
    
}

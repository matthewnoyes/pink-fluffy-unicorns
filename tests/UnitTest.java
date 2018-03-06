package virtualassistant.tests;

import virtualassistant.*;
import java.io.*;

// Add tests here
public class UnitTest {

    BufferedWriter logger;
    
    public static void main(String[] args) throws IOException {
            
         logger = new BufferedWriter(new FileWriter("tests/logs_unitTesting.txt"));
         
         logger.write("Testing Virtual Assistant...");
         VirtualAssistant virtualAssistant = new VirtualAssistant();
         writeConclusion(virtualAssistant.unitTest());
         
         logger.write("Testing Scrapper...");
         Scrapper scrapper = new Scrapper();
         writeConclusion(scrapper.unitTest(logger));
         
         logger.write("Testing StockData...");
         StockData stockData = new StockData();
         writeConclusion(stockData.unitTest(logger));
         
         logger.write("Testing NewsData...");
         NewsData newsData = new NewsData();
         writeConclusion(newsData.unitTest(logger));
         
         logger.write("Testing Loader...");
         Loader loader = new Loader();
         writeConclusion(loader.unitTest(logger));
         
         logger.write("Testing Chatbot...");
         Chatbot chatbot = new Chatbot();
         writeConclusion(chatbot.unitTest(logger));
         
         logger.write("Testing LearningAgent...");
         LearningAgent learningAgent = new LearningAgent();
         writeConclusion(learningAgent.unitTest(logger));
         
         logger.write("\nTESTING COMPLETE!");
         
         logger.close();
         
    }
    
    private static void writeConclusion(boolean ok){
        
        if(va.unitTest())
            logger.write("SUCCESS\n");
        else 
            logger.write("FAILED\n");
         
    }
}

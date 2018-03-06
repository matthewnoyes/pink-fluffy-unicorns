package virtualassistant.tests;

import java.io.*;

// Add tests here
public class UnitTest {

    BufferedWriter logger;
    
    public static void main(String[] args) throws IOException {
            
         logger = new BufferedWriter(new FileWriter("tests/logs_unitTesting"));
    }
}

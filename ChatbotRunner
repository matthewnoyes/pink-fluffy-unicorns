import java.lang.Runtime;
import java.lang.Process;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ChatbotRunner {

  static BufferedReader input;
  static BufferedWriter output;

  public static void main(String[] args) {

    try {
      Runtime rt = Runtime.getRuntime();
      Process p = rt.exec("./text-client.sh 953f52cffa2e4a369dd7f8f5fa7c4c6c");


      output = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
      input = new BufferedReader(new InputStreamReader(p.getInputStream()));


      //System.out.println(makeRequest("What is the price of BP"));

      Scanner s = new Scanner(System.in);
      String in = "";
      in = s.nextLine();
      while (in != null) {
        System.out.println(makeRequest(in));
        in = s.nextLine();
      }
    } catch (Exception e) {
      System.out.println("Error");
    } finally {
      try {
        output.close();
        input.close();
      } catch (Exception e) {

      }
    }
  }

  public static String makeRequest(String request) {
    try {

      output.write(request, 0, request.length());
      output.newLine();
      output.flush();


      //Wait for reponse -- probably can change
      TimeUnit.SECONDS.sleep(2);


      String line;
      while ((line = input.readLine()) != null) {
        return line;
      }
      //return input.readLine();

    } catch (Exception e) {
      System.out.println("Error");
    }

    return "";
  }

}

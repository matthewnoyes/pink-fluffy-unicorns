package virtualassistant.chatbot;

import java.io.IOException;
import org.jsoup.Jsoup;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

public class Chatbot implements IChatBot {
    private JSONParser parser = new JSONParser();
    private String authorization = "Bearer 953f52cffa2e4a369dd7f8f5fa7c4c6c";
    private String contentType = "application/json";

    public Chatbot(){

    }

    // Input: Query
    // Output: Response
    public String getResponse(String query) throws IOException, ParseException {
        String body = "{\n"
		+ "   \"lang\": \"en\",\n"
		+ "   \"query\": \""+query+" \",\n"  // query
		+ "   \"sessionId\": \"12345\",\n" //A string token up to 36 symbols long, used to identify the client and to manage session parameters (including contexts) per client.
		+ "   \"timezone\": \"Europe/Madrid\"\n"
		+ "}";

	    final String document = Jsoup.connect("https://api.dialogflow.com/v1/query?v=20150910") // Gets response from dialogflow
	    .requestBody(body)
	    .header("Authorization", authorization).header("Content-Type", contentType).ignoreContentType(true)
	    .postDataCharset("UTF-8")
	    .post().text();

        // Getting the response needed from the json retrieved.
        final JSONObject json = (JSONObject) parser.parse(document);
	    final JSONObject results =  (JSONObject) json.get("result");
	    final String response = ((JSONObject) results.get("fulfillment")).get("speech").toString();
        return response;
    }

    public void output(Double text) {

    }
}

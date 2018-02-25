package virtualassistant.gui;

import virtualassistant.VirtualAssistant;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.Separator;

import java.net.URL;
import java.util.*;

import java.io.IOException;

public class Controller implements Initializable {

@FXML
public VBox chatbot_container;
@FXML
public Label update_time;
@FXML
public TextField query_text_field;
@FXML
public ImageView wifi_image_view;
@FXML
public Button round_mic_button;

private boolean listening;
private boolean onHelp;
private List<Message> chatbot_message_list;
private List<String> helptext_list;

private VirtualAssistant virtualAssistant;

@Override
public void initialize(URL location, ResourceBundle resources) {
		listening = false;
		onHelp = false;
		chatbot_message_list = new ArrayList<>();
		helptext_list = new ArrayList<>();
		helptext_list.add("How are the banks doing?");
		helptext_list.add("Any news on Coca Cola?");
		helptext_list.add("What is the high price of Just Eat?");
		helptext_list.add("Is LLoyds positive?");
		helptext_list.add("Open price of Barclays");
		helptext_list.add("How do you feel about construction?");

		chatbot_message_list.add(new Response("Hi, ask me anything!", null));

		virtualAssistant = new VirtualAssistant();

		openHelp();
}

/* ============ METHODS YOU NEED TO KNOW ============ */
/*
	void changeUpdateTime(String time) :
		changes displayed update time
*/
/* void changeWifiAccess(boolean access) :
	chnages displayed wifi access true = have connection
*/

// make a query
public void makeQuery(String text) {
		if(onHelp) {
			closeHelp();
		}

		Message query = new Query(text);
		chatbot_message_list.add(query);
		addMessage(query);



		String responseStr = "An error occured";

		try {
    	responseStr = virtualAssistant.getResponse(query.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}

		News[] news = null; // this should be the news to be displayed with the response
		Message response = new Response(responseStr, news);
		chatbot_message_list.add(response);
		addMessage(response);
}

public void startListening() {

}
public void stopListening() {
		String text = "";
		makeQuery(text);
}

// handle when the mic button is clicked
@FXML
public void handleMicButtonClick(ActionEvent e) {
		if(!listening) {
				listening = true;
				round_mic_button.getStyleClass().remove("not_listening");
				round_mic_button.getStyleClass().add("listening");
				startListening();
		} else {
				listening = false;
				round_mic_button.getStyleClass().remove("listening");
				round_mic_button.getStyleClass().add("not_listening");
				stopListening();
		}
}

@FXML
public void handleSendQueryButtonClick(ActionEvent e) {
		String typed_query = query_text_field.getText();

		if(typed_query != "") {
				makeQuery(typed_query);
		}
		query_text_field.setText("");
}

@FXML
public void handleHelpButtonClick(ActionEvent e) {
		if(onHelp) {
				closeHelp();
		} else {
				openHelp();
		}
}

// open help screen
public void openHelp() {
		onHelp = true;
		chatbot_container.getChildren().clear();

		Label helpTitle = new Label("Some questions you can ask:");
		helpTitle.setPrefWidth(Main.WIDTH);
		helpTitle.setAlignment(Pos.CENTER);
		helpTitle.setId("help_title");
		chatbot_container.getChildren().add(helpTitle);

		for(int i = 0; i < 4 && i < helptext_list.size(); i++) {
				Label text_label = new Label('"'+helptext_list.get(i)+'"');
				text_label.setPrefWidth(Main.WIDTH);
				text_label.setAlignment(Pos.CENTER);
				text_label.setId("help_text");
				chatbot_container.getChildren().add(text_label);
		}

		Label ellipsis = new Label("...");
		ellipsis.setPrefWidth(Main.WIDTH);
		ellipsis.setAlignment(Pos.CENTER);
		ellipsis.setId("help_text");
		chatbot_container.getChildren().add(ellipsis);
}

// close help screen
public void closeHelp() {
		onHelp = false;
		chatbot_container.getChildren().clear();
		for(int i = 0; i < chatbot_message_list.size(); i++) {
				addMessage(chatbot_message_list.get(i));
		}
}

public void changeUpdateTime(String time) {
		update_time.setText("Last updated: " + time);
}

public void changeWifiAccess(boolean access) {
		if(access) {
				Image image = new Image(getClass().getResourceAsStream("images/wifi_access.png"));
				wifi_image_view.setImage(image);
		} else {
				Image image = new Image(getClass().getResourceAsStream("images/wifi_no_access.png"));
				wifi_image_view.setImage(image);
		}
}

public void addMessage(Message message) {
		// container of message
		HBox container = new HBox(0);

		// fill one side of message to decrease it's size
		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		// contains the content in the message
		VBox content_contain = new VBox(5);
		content_contain.getStyleClass().add("chat_bubble");

		// the main label for the message
		Label label = new Label(message.getMessage());
		label.setWrapText(true);

		// set specifics of containers depending on query or response
		if(message instanceof Query) {
				label.setTextAlignment(TextAlignment.RIGHT);

				content_contain.setAlignment(Pos.CENTER_RIGHT);
				content_contain.setId("query_send");
				content_contain.getChildren().add(label);
				container.setPadding(new Insets(0,0,0,80));
				container.getChildren().addAll(region, content_contain);
		} else if(message instanceof Response) {
				label.setTextAlignment(TextAlignment.LEFT);

				content_contain.setAlignment(Pos.CENTER_LEFT);
				content_contain.setId("query_recieve");
				content_contain.getChildren().add(label);

				// if the response contains news to display
				News[] news = message.getNews();
				if(news != null) {
						// set to max width for message
						content_contain.setPrefWidth(Main.WIDTH);

						for(int x = 0; x < 3 && x < news.length; x++) {
								Separator separator = new Separator();
								separator.setMaxWidth(200);

								Label heading = new Label(news[x].getTitle());
								heading.setId("news_heading");

								VBox newsContain = new VBox(5);
								newsContain.setAlignment(Pos.CENTER_RIGHT);
								newsContain.getChildren().addAll(separator, heading);

								content_contain.getChildren().add(newsContain);
						}

				}
				container.setPadding(new Insets(0,80,0,0));
				container.getChildren().addAll(content_contain, region);

		}
		chatbot_container.getChildren().add(container);
}

}

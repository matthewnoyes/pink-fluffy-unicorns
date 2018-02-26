package virtualassistant.gui;

import virtualassistant.VirtualAssistant;
import virtualassistant.misc.Pair;
import virtualassistant.data.news.NewsObj;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.Separator;
import javafx.animation.*;
import javafx.util.Duration;

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
public Timeline mic_button_timeline;

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

		generateHelpText();
		generateAnimations();

		chatbot_message_list.add(new Response("Hi, ask me anything!", null));
        
        System.out.println("Downloading data...");
		virtualAssistant = new VirtualAssistant();
        
	// 	final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
 // -            executorService.scheduleAtFixedRate(App::virtualAssistant.scan, 0, 15, TimeUnit.SECONDS);
 // -        }
        System.out.println("Launching interface...");
		openHelp();
        
        System.out.println("Success!");
        System.out.println("===================================\n\n");
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
				responseStr = virtualAssistant.getResponse(query.getMessage()).getFirst();
		} catch (Exception e) {
				e.printStackTrace();
		}

		NewsObj[] news = null; // this should be the news to be displayed with the response
		Message response = new Response(responseStr, news);
		chatbot_message_list.add(response);
		addMessage(response);
}

public void startListening() {
}

public void stopListening() {
		String text = "";
		//makeQuery(text);
}

private void generateHelpText() {
		helptext_list.add("How are the banks doing?");
		helptext_list.add("Any news on Coca Cola?");
		helptext_list.add("What is the high price of Just Eat?");
		helptext_list.add("Is LLoyds positive?");
		helptext_list.add("Open price of Barclays");
		helptext_list.add("How do you feel about construction?");
}
private void generateAnimations() {
		mic_button_timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt->round_mic_button.setStyle("-fx-background-color: #FF4339;")),
		                                   new KeyFrame(Duration.seconds(1), evt->round_mic_button.setStyle("-fx-background-color: #a9a9a9;")));
		mic_button_timeline.setCycleCount(Animation.INDEFINITE);
}

// handle when the mic button is clicked
@FXML
private void handleMicButtonClick(ActionEvent e) {
		if(!listening) {
				listening = true;
				mic_button_timeline.play();
				startListening();
		} else {
				listening = false;
				mic_button_timeline.stop();
				round_mic_button.setStyle("-fx-background-color: #a9a9a9;");
				stopListening();
		}
}

@FXML
private void handleSendQueryButtonClick(ActionEvent e) {
		String typed_query = query_text_field.getText();

		if(typed_query != null && !typed_query.trim().isEmpty()) {
				makeQuery(typed_query);
		}
		query_text_field.setText("");
}

@FXML
private void handleHelpButtonClick(ActionEvent e) {
		if(onHelp) {
				closeHelp();
		} else {
				openHelp();
		}
}

// open help screen
private void openHelp() {
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
private void closeHelp() {
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

private void addMessage(Message message) {
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
				NewsObj[] news = message.getNews();
				if(news != null) {
						// set to max width for message
						//content_contain.setPrefWidth(Main.WIDTH);

						for(int x = 0; x < 3 && x < news.length; x++) {
								Separator separator = new Separator();
								separator.setMaxWidth(200);

								Label heading = new Label(news[x].getTitle());
								heading.setId("news_heading");
								Label time = new Label(news[x].getDateTime().toString());
								time.setId("news_data");
								Label url = new Label(news[x].getUrl());
								url.setId("news_data");

								VBox newsContain = new VBox(0);
								newsContain.setAlignment(Pos.CENTER_RIGHT);
								newsContain.getChildren().addAll(separator, heading, time,url);

								content_contain.getChildren().add(newsContain);
						}

				}
				container.setPadding(new Insets(0,80,0,0));
				container.getChildren().addAll(content_contain, region);

		}
		chatbot_container.getChildren().add(container);
}

}

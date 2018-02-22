import java.util.*;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.control.Separator;

public class Interface extends Application {
// width and height of application
public static final int WIDTH = 500;
public static final int HEIGHT = 700;

// needed global variables
private boolean listening; 						// if current listening to voice
private boolean onHelp;								// if help screen is being displayed
private ArrayList<Message> queries;		// list of all queries and responses made
private ArrayList<String> helptext;		// list of all the 'help' queries

// gui global nodes
private Stage window;
private VBox root;
private ScrollPane scroll;
private VBox chatbot;
private HBox textInput;
private HBox buttons;
private Label update_time;
private TextField textField;

// method to change the update time (stocks last updated)
public void changeUpdateTime(String time) {
				update_time.setText("Last updated: " + time);
}

// start listening to voice input
public void startListening() {
				// ...
}

// stop listening to voice input
public void stopListening() {
				// ...
				//String query = "";
				// makeQuery(query);
}

// make a query
public void makeQuery(String text) {
				if(onHelp) {
					closeHelp();
				}

				Message query = new Query(text);
				queries.add(query);
				addMessage(query);

				//
				// process message here
				//

				News[] news = null;
				Message response = new Response("Response", news);
				queries.add(response);
				addMessage(response);
}

private void closeProgram() {

}


/* ================================================== */
/* ================= GUI CODE ======================= */
/* ================================================== */

// start the gui
@Override
public void start(Stage primaryStage) throws Exception {
				window = primaryStage;
				init(window);

				root = new VBox(10);
				root.setId("root");

				// chatbot container in scrollable pane, scrolled to bottom
				makeChatbotContainer();

				// text input area for user to input queries
				makeTextInput();

				// control buttons
				makeButtons();

				// put chat bot in scrollable pane
				scroll = new ScrollPane(chatbot);
				scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setFitToWidth(true);

				// filler so chat bot doesn't scroll past buttons
				Region filler = new Region();
				VBox.setVgrow(filler, Priority.ALWAYS);

				// add to container
				root.getChildren().addAll(scroll, filler, textInput, buttons);

				// start with help screen open
				openHelp();

				// create the scene
				Scene scene = new Scene(root, WIDTH, HEIGHT);
				scene.getStylesheets().add("styles/stylesheet.css");

				window.setScene(scene);
				window.show();
}

// initialize the window and variables
private void init(Stage window) {
				window.setTitle("Trader's Assistant");
				window.setResizable(false);
				window.setOnCloseRequest(e->{
												e.consume(); // stop window closing automatically
												closeProgram();
								});
				listening = false;
				onHelp = false;
				queries = new ArrayList<Message>();
				helptext = new ArrayList<String>();

				queries.add(new Query("Hello"));
				queries.add(new Response("Good morning", null));
				queries.add(new Query("Any news on Barclays"));
				News[] news = new News[4];
				news[0] = new News("Barclays kills children");
				news[1] = new News("Appointed Hitler as CEO");
				news[2] = new News("Stocks rise drastically");
				news[3] = new News("Barclays buys childhood dog");
				queries.add(new Response("This is what I found:", news));

				helptext.add("How are the banks doing?");
				helptext.add("Any news on Coca Cola?");
				helptext.add("What is the high price of Just Eat?");
				helptext.add("Is LLoyds positive?");
				helptext.add("Open price of Barclays");
				helptext.add("How do you feel about construction?");
}

// open help screen
public void openHelp() {
				onHelp = true;
				chatbot.getChildren().clear();

				Label helpTitle = new Label("Some questions you can ask:");
				helpTitle.setPrefWidth(WIDTH);
				helpTitle.setAlignment(Pos.CENTER);
				helpTitle.setId("help_title");
				chatbot.getChildren().add(helpTitle);

				for(int i = 0; i < 4 && i < helptext.size(); i++) {
								Label text_label = new Label('"'+helptext.get(i)+'"');
								text_label.setPrefWidth(WIDTH);
								text_label.setAlignment(Pos.CENTER);
								text_label.setId("help_text");
								chatbot.getChildren().add(text_label);
				}

				Label ellipsis = new Label("...");
				ellipsis.setPrefWidth(WIDTH);
				ellipsis.setAlignment(Pos.CENTER);
				ellipsis.setId("help_text");
				chatbot.getChildren().add(ellipsis);
}

// close help screen
public void closeHelp() {
				onHelp = false;
				chatbot.getChildren().clear();
				for(int i = 0; i < queries.size(); i++) {
								addMessage(queries.get(i));
				}
}

// add message to screen
public void addMessage(Message message) {
				// container of message
				HBox container = new HBox(0);

				// fill one side of message to decrease it's size
				Region region = new Region();
				HBox.setHgrow(region, Priority.ALWAYS);

				// contains the content in the message
				VBox contentContain = new VBox(5);
				contentContain.getStyleClass().add("chat_bubble");

				// the main label for the message
				Label label = new Label(message.getMessage());
				label.setWrapText(true);

				// set specifics of containers depending on query or response
				if(message instanceof Query) {
								// if a query
								container.setPadding(new Insets(0,0,0,80));
								contentContain.setAlignment(Pos.CENTER_RIGHT);

								label.setTextAlignment(TextAlignment.RIGHT);
								contentContain.setId("query_send");
								contentContain.getChildren().add(label);

								container.getChildren().addAll(region, contentContain);
				} else if(message instanceof Response) {
								// if a response
								container.setPadding(new Insets(0,80,0,0));
								contentContain.setAlignment(Pos.CENTER_LEFT);

								label.setTextAlignment(TextAlignment.LEFT);
								contentContain.setId("query_recieve");
								contentContain.getChildren().add(label);

								// if the response contains news to display
								News[] news = message.getNews();
								if(news != null) {
												// set to max width for message
												contentContain.setPrefWidth(WIDTH);

												for(int x = 0; x < 3 && x < news.length; x++) {
																// container for the news displayed
																VBox newsContain = new VBox(5);
																newsContain.setAlignment(Pos.CENTER_RIGHT);

																// horizontal separator
																Separator separator = new Separator();
																separator.setMaxWidth(200);

																Label heading = new Label(news[x].getTitle());
																heading.setId("news_heading");
																newsContain.getChildren().addAll(separator, heading);

																contentContain.getChildren().add(newsContain);
												}

								}

								container.getChildren().addAll(contentContain, region);

				}
				chatbot.getChildren().add(container);
}

// make the chatbot
private void makeChatbotContainer() {
				// container for the chatbot i.e the messages and replies
				chatbot = new VBox(20);
				chatbot.setId("chatbot_container");
				//chatbot.setPrefHeight(HEIGHT);
				chatbot.setPrefWidth(WIDTH);
				chatbot.setAlignment(Pos.TOP_CENTER);
}
// make input
private void makeTextInput() {
				textInput = new HBox(0);
				textInput.setAlignment(Pos.BOTTOM_CENTER);

				textField = new TextField();
				textField.setPromptText("Input a query here");
				textField.setPrefWidth(WIDTH);
				textField.setId("text_field");

				Button confirm = new Button("Send");
				confirm.setPrefWidth(200);
				confirm.setId("send_query_button");

				confirm.setOnAction(e->{
												makeQuery(textField.getText());
												textField.setText("");
								});

				textInput.getChildren().addAll(textField, confirm);
}
// make buttons
private void makeButtons() {
				buttons = new HBox(5);

				StackPane helpContain = new StackPane();
				Button helpButton = new Button();
				helpButton.setText("?");
				helpButton.setId("help_button");
				helpButton.setOnAction(e->{
					if(onHelp) {
						closeHelp();
					} else {
						openHelp();
					}
				});
				helpContain.getChildren().add(helpButton);
				helpContain.setAlignment(Pos.BOTTOM_LEFT);
				helpContain.setPrefWidth(Math.floor(WIDTH/2));

				StackPane button = new StackPane();
				Image image = new Image(getClass().getResourceAsStream("images/microphone.png"));
				ImageView imageView = new ImageView(image);
				imageView.setPreserveRatio(false);
				imageView.setFitHeight(45);
				imageView.setFitWidth(34);

				Button roundButton = new Button("",imageView);
				roundButton.setId("round_button");
				roundButton.getStyleClass().add("not_listening");
				roundButton.setOnAction(e->{
												if(!listening) {
												        listening = true;
												        roundButton.getStyleClass().remove("not_listening");
												        roundButton.getStyleClass().add("listening");
												        startListening();
												} else {
												        listening = false;
												        roundButton.getStyleClass().remove("listening");
												        roundButton.getStyleClass().add("not_listening");
												        stopListening();
												}
								});
				button.getChildren().add(roundButton);
				button.setPrefWidth(Math.floor(WIDTH/4));

				StackPane updateContain = new StackPane();
				update_time = new Label("Last updated: ...");
				update_time.setId("update_time");
				updateContain.getChildren().add(update_time);
				updateContain.setAlignment(Pos.BOTTOM_RIGHT);
				updateContain.setPrefWidth(Math.floor(WIDTH/2));


				buttons.getChildren().addAll(helpContain, button, updateContain);
}

}

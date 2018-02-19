package virtualassistant.gui;

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

public static final int WIDTH = 500;
public static final int HEIGHT = 700;

Stage window;
boolean listening = false;

static ArrayList<Message> queries;
VBox root;

@Override
public void start(Stage primaryStage) {
				window = primaryStage;
				init(window);

				root = new VBox(10);

				root.setId("root");
				root.setPadding(new Insets(20, 10, 20, 10));

				ScrollPane scroll = makeChatbotContainer();

				HBox textInput = makeTextInput();

				HBox buttons = makeButtons();

				scroll.setVvalue(1.0);
				root.getChildren().addAll(scroll, textInput, buttons);

				Scene scene = new Scene(root, WIDTH, HEIGHT);
				scene.getStylesheets().add("styles/stylesheet.css");
				window.setScene(scene);
				window.show();
}

private ScrollPane makeChatbotContainer() {
				// container for the chatbot i.e the messages and replies
				VBox chatbotContain = new VBox(20);
				chatbotContain.setId("chatbot_container");
				chatbotContain.setPrefHeight(HEIGHT);
				chatbotContain.setPrefWidth(WIDTH);
				chatbotContain.setAlignment(Pos.TOP_CENTER);

				// user questions and responses
				for(int i = 0; i < queries.size(); i++) {
								// contain the message
								HBox container = new HBox(0);

								// fill one side of message to decrease it's size
								Region region = new Region();
								HBox.setHgrow(region, Priority.ALWAYS);

								// contains the content in the message
								VBox contentContain = new VBox(5);
								contentContain.getStyleClass().add("chat_bubble");

								// the main label for the message
								Label label = new Label(queries.get(i).getMessage());
								label.setWrapText(true);

								// set specifics of containers depending on query or response
								if(queries.get(i) instanceof Query) {
												// if a query
												container.setPadding(new Insets(0,0,0,80));
												contentContain.setAlignment(Pos.CENTER_RIGHT);

												label.setTextAlignment(TextAlignment.RIGHT);
												contentContain.setId("query_send");
												contentContain.getChildren().add(label);

												container.getChildren().addAll(region, contentContain);
								} else if(queries.get(i) instanceof Response) {
												// if a response
												container.setPadding(new Insets(0,80,0,0));
												contentContain.setAlignment(Pos.CENTER_LEFT);

												label.setTextAlignment(TextAlignment.LEFT);
												contentContain.setId("query_recieve");
												contentContain.getChildren().add(label);

												// if the response contains news to display
												News[] news = queries.get(i).getNews();
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

								chatbotContain.getChildren().addAll(container);
				}

				Region filler = new Region();
				VBox.setVgrow(filler, Priority.ALWAYS);
				chatbotContain.getChildren().add(filler);

				ScrollPane scroll = new ScrollPane(chatbotContain);
				scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scroll.setFitToWidth(true);

				return scroll;
}

private HBox makeTextInput() {
				HBox textInputContain = new HBox(0);
				textInputContain.setAlignment(Pos.BOTTOM_CENTER);
				TextField textField = new TextField();
				textField.setId("text_field");
				textField.setPrefWidth(WIDTH);
				textField.setPromptText("Input a query here");
				Button confirm = new Button("Send");
				confirm.setPrefWidth(200);
				confirm.setId("send_query_button");
				textInputContain.getChildren().addAll(textField, confirm);

				return textInputContain;
}

private HBox makeButtons() {
				HBox buttonsContain = new HBox(5);
				buttonsContain.setAlignment(Pos.BOTTOM_RIGHT);

				Button helpButton = new Button();
				helpButton.setText("?");
				helpButton.setId("help_button");

				Region filler1 = new Region();
				HBox.setHgrow(filler1, Priority.ALWAYS);

				StackPane button = new StackPane();
				Image image = new Image(getClass().getResourceAsStream("Images/microphone.png"));
				ImageView imageView = new ImageView(image);
				imageView.setPreserveRatio(false);
				imageView.setFitHeight(45);
				imageView.setFitWidth(34);
				Button roundButton = new Button("",imageView);
				roundButton.setId("round_button");
				roundButton.getStyleClass().add("not_listening");
				roundButton.setOnAction(e->{
												queries.add(new Query("..."));
												if(!listening) {
												        listening = true;
												        roundButton.getStyleClass().remove("not_listening");
												        roundButton.getStyleClass().add("listening");
												} else {
												        listening = false;
												        roundButton.getStyleClass().remove("listening");
												        roundButton.getStyleClass().add("not_listening");
												}
								});
				button.getChildren().add(roundButton);


				Label update_time = new Label("Last updated: 12:07");
				update_time.setId("update_time");
				buttonsContain.getChildren().addAll(helpButton, filler1, button, update_time);

				return buttonsContain;
}

private void init(Stage window) {
				window.setTitle("Trader's Assistant");
				window.setResizable(false);
				window.setOnCloseRequest(e->{
												e.consume(); // stop window closing automatically
												closeProgram();
								});
  
        News[] amazingnews = new News[3];
				amazingnews[0] = new News("Title 1");
				amazingnews[1] = new News("Title 2");
				amazingnews[2] = new News("Title 3");

				queries = new ArrayList<Message>();
				queries.add(new Query("Hello"));
				queries.add(new Response("Good afternoon", null));
				queries.add(new Query("What is barcelays open price today"));
				queries.add(new Response("Barclays open price is £180.89", null));
				queries.add(new Query("Any news on bitcoin"));
				queries.add(new Response("I found this news on bitcoin", amazingnews));
}

private void updateUI() {

}

private void closeProgram() {

}

}

class News {

private String title;

public News(String title) {
				this.title = title;
}

public String getTitle() {
				return title;
}

}

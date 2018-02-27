package virtualassistant.gui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.control.Separator;
import javafx.geometry.*;
import javafx.event.*;

import java.awt.Desktop;
import java.net.URI;
import java.util.*;
import virtualassistant.data.news.NewsObj;

public class Response extends Message {

private LinkedList<NewsObj> news;

public Response(String response, LinkedList<NewsObj> news) {
		super(response);
		this.news = news;
}

public LinkedList<NewsObj> getNews() {
		return news;
}

// get the container used to display the message
public HBox getDisplay() {
		// container of message
		HBox container = new HBox(0);

		// fill one side of message to decrease it's size
		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		// contains the content in the message
		VBox content_contain = new VBox(5);
		content_contain.getStyleClass().add("chat_bubble");
		content_contain.setAlignment(Pos.CENTER_LEFT);
		content_contain.setId("query_recieve");

		// the main label for the message
		Label label = new Label(this.getMessage());
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.LEFT);

		content_contain.getChildren().add(label);

		VBox news_articles_contain = new VBox(0);
		// if the response contains news to display
		if(news != null && news.size() > 0) {
				// set to max width for message
				//content_contain.setPrefWidth(Main.WIDTH);

				for(NewsObj x : news) {
						Separator separator = new Separator();
						separator.setMaxWidth(200);

						Label heading = new Label(x.getTitle());
						heading.setWrapText(true);
						heading.setId("news_heading");
						//Label time = new Label(news.get(x).getDateTime().toString());
						String strtime = "";
						Calendar date = x.getDateTime();

                        // Beautify string
                        StringBuilder sb = new StringBuilder();
						sb.append(date.get(Calendar.HOUR));
                        sb.append(":");
						sb.append(date.get(Calendar.MINUTE));
						sb.append(" ");
						sb.append(date.get(Calendar.DAY_OF_MONTH));
						sb.append("/");
						sb.append(date.get(Calendar.MONTH));
						sb.append("/");
						sb.append(date.get(Calendar.YEAR));
						Label time = new Label(sb.toString());


						time.setId("news_data");
						Hyperlink url = new Hyperlink(x.getUrl());
						url.setWrapText(true);
						url.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								try {
									Desktop.getDesktop().browse(new URI(url.getText()));
								} catch(Exception ex) {
									System.out.println("Error opening webpage");
								}
							}
						});
						url.setId("news_url");

						VBox newsContain = new VBox(0);
						newsContain.setAlignment(Pos.CENTER_RIGHT);
						newsContain.getChildren().addAll(separator, heading, time,url);

						news_articles_contain.getChildren().add(newsContain);
				}
				ScrollPane news_scroll = new ScrollPane(news_articles_contain);
				news_scroll.setFitToWidth(true);
				news_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				news_scroll.setStyle("-fx-background: transparent");
				news_scroll.setPrefHeight(250);
				content_contain.getChildren().add(news_scroll);
		} else {
			container.setPadding(new Insets(0,80,0,0));
		}
		container.getChildren().addAll(content_contain, region);
		return container;
}

}

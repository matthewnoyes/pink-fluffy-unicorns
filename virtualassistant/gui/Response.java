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
		container.setPadding(new Insets(0,80,0,0));

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

		// if the response contains news to display
		if(news != null) {
				// set to max width for message
				//content_contain.setPrefWidth(Main.WIDTH);

				for(int x = 0; x < 3 && x < news.size(); x++) {
						Separator separator = new Separator();
						separator.setMaxWidth(200);

						Label heading = new Label(news.get(x).getTitle());
						heading.setId("news_heading");
						Label time = new Label(news.get(x).getDateTime().toString());
						time.setId("news_data");
						Hyperlink url = new Hyperlink(news.get(x).getUrl());
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

						content_contain.getChildren().add(newsContain);
				}

		}
		container.getChildren().addAll(content_contain, region);
		return container;
}

}

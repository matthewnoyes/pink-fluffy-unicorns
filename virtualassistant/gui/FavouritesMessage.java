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

public class FavouritesMessage extends Message {

  private String[] faves;

  public FavouritesMessage(String message, String[] faves) {
    super(message);
    this.faves = faves;
  }

  public String[] getFaves() {
    return faves;
  }

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

		VBox faves_contain = new VBox(0);
		// if the response contains news to display
		if(faves != null && faves.length > 0) {

				for(String x : faves) {
						Separator separator = new Separator();

						Label heading = new Label(x);
						heading.setWrapText(true);
						heading.setId("fave_heading");

						VBox queryContain = new VBox(0);
						queryContain.setAlignment(Pos.CENTER_RIGHT);
						queryContain.getChildren().addAll(separator, heading);

						faves_contain.getChildren().add(queryContain);
				}
				ScrollPane fave_scroll = new ScrollPane(faves_contain);
				fave_scroll.setFitToWidth(true);
				fave_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				fave_scroll.setStyle("-fx-background: transparent");
				fave_scroll.setPrefHeight(130);
				content_contain.getChildren().add(fave_scroll);
		} else {
			container.setPadding(new Insets(0,80,0,0));
		}
		container.getChildren().addAll(content_contain, region);
		return container;
}

}

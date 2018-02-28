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

public class URLMessage extends Message {

  private String url;

  public URLMessage(String response, String url) {
  		super(response);
      this.url = url;
  }

  public String getUrl() {
    return url;
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

    Hyperlink url_hyperlink = new Hyperlink(this.getUrl());
    url_hyperlink.setWrapText(true);
		url_hyperlink.setTextAlignment(TextAlignment.LEFT);
    url_hyperlink.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        try {
          Desktop.getDesktop().browse(new URI(url_hyperlink.getText()));
        } catch(Exception ex) {
          System.out.println("Error opening webpage");
        }
      }
    });

		content_contain.getChildren().addAll(label, url_hyperlink);
		container.getChildren().addAll(content_contain, region);
		return container;
  }

}

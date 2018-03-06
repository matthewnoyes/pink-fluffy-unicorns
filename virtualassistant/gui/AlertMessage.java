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

public class AlertMessage extends Message {

  private String[] alerts;

  public AlertMessage(String message, String[] alerts) {
    super(message);
    this.alerts = alerts;
  }

  public String[] getAlerts() {
      return alerts;
  }

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

    for(String alert : getAlerts()) {
      Label alert_message = new Label(alert);
      alert_message.setId("alert_message");
      alert_message.setWrapText(true);
  		alert_message.setTextAlignment(TextAlignment.LEFT);
      content_contain.getChildren().add(alert_message);
    }

		container.getChildren().addAll(content_contain, region);
		return container;
  }

}

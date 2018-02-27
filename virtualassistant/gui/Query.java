package virtualassistant.gui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.control.Separator;
import javafx.geometry.*;

public class Query extends Message {

public Query(String question) {
				super(question);
}

public HBox getDisplay() {
	// container of message
	HBox container = new HBox(0);
	container.setPadding(new Insets(0,0,0,80));

	// fill one side of message to decrease it's size
	Region region = new Region();
	HBox.setHgrow(region, Priority.ALWAYS);

	// contains the content in the message
	VBox content_contain = new VBox(5);
	content_contain.getStyleClass().add("chat_bubble");
	content_contain.setAlignment(Pos.CENTER_RIGHT);
	content_contain.setId("query_send");

	// the main label for the message
	Label label = new Label(this.getMessage());
	label.setWrapText(true);
	label.setTextAlignment(TextAlignment.RIGHT);

	content_contain.getChildren().add(label);
	container.getChildren().addAll(region, content_contain);

	return container;
}
}

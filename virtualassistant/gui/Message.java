package virtualassistant.gui;

import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.text.*;

import java.util.*;
import virtualassistant.data.news.NewsObj;

public class Message {

protected String message;

public Message(String message) {
				this.message = message;
}

public String getMessage() {
				return message;
}

public LinkedList<NewsObj> getNews() {
				return null;
}

public HBox getDisplay() {
	return null;
}

}

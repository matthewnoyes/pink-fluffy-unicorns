package virtualassistant.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

public static float WIDTH = 500;
public static float HEIGHT = 700;

public static void main(String[] args) {
		launch(args);
}

@Override
public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Trader's Assistant");
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(e->{
						e.consume();
						closeProgram();
				});

		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		scene.getStylesheets().add("virtualassistant/gui/styles/stylesheet.css");

		primaryStage.setScene(scene);
		primaryStage.show();

}

public void closeProgram() {
		// save any data before if closes
		System.exit(0);
}


}

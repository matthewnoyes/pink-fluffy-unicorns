package virtualassistant.gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.Separator;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.JetPilotEffect;
import marytts.signalproc.effects.LpcWhisperiserEffect;
import marytts.signalproc.effects.RobotiserEffect;
import marytts.signalproc.effects.StadiumEffect;
import marytts.signalproc.effects.VocalTractLinearScalerEffect;
import marytts.signalproc.effects.VolumeEffect;
import model.TextToSpeech;
import virtualassistant.VirtualAssistant;
import virtualassistant.chatbot.SpeechRecognizerMain;
import virtualassistant.data.news.NewsObj;
import virtualassistant.misc.Pair;

public class Controller implements Initializable {

@FXML
public ScrollPane scrollpane;
@FXML
public VBox chatbot_container;
@FXML
public Label update_time;
@FXML
public TextField query_text_field;
@FXML
public Button send_query_button;
@FXML
public ImageView wifi_image_view;
@FXML
public ImageView mute_control_image_view;
@FXML
public Button round_mic_button;
public Timeline mic_button_timeline;

private boolean listening;
private boolean onHelp;
private boolean ready; // used to see if ready to recieve messages

// IMPORTANT: Set to false only if it slows down your internet connection too much
private boolean autoUpdate = true;
private boolean virtualAssistantFinished = false;
private boolean sstFinished = false;
private final int dataUpdatePeriod = 60; // seconds

private List<Message> chatbot_message_list;
private List<String> helptext_list;

private static VirtualAssistant virtualAssistant;
private static SpeechRecognizerMain stt;
private static TextToSpeech tts;

@Override
public void initialize(URL location, ResourceBundle resources) {
	init_variables();

	// Run download of data in background
	Task task1 = new Task<Void>() {
		@Override
		public Void call() {
			System.out.println("Starting text to speech...");
			tts = new TextToSpeech();
			tts.setVoice("dfki-poppy-hsmm");

			System.out.println("Downloading data...");
			virtualAssistant = new VirtualAssistant();

			ready = true;
			// Change mute button
			changeMuteButtonIcon();
			changeWifiAccess(true);

			System.out.println("Success!");
			System.out.println("===================================\n\n");

			virtualAssistantFinished = true;

			if(sstFinished)
				round_mic_button.setDisable(false);

			return null;
		}
	};
	task1.setOnSucceeded(e -> {
		changeUpdateTime(getTimeNow());
	});
	new Thread(task1).start();

	// Run initialization of speech-to-text in background
	Task task2 = new Task<Void>() {
		@Override
		public Void call() {
			stt = new SpeechRecognizerMain(Controller.this);

			//stt.startSpeechRecognition();
			stt.ignoreSpeechRecognitionResults();
			System.out.println("Speech to text... complete");

			sstFinished = true;

			if(virtualAssistantFinished)
				round_mic_button.setDisable(false);

			return null;
		}
	};
	new Thread(task2).start();


	if(autoUpdate) {
		ScheduledService<String> svc = new ScheduledService<String>() {
			protected Task<String> createTask() {
				return new Task<String>() {
					protected String call() {
						virtualAssistant.scan();
						return getTimeNow();
          }
        };
			}
		};
		svc.setPeriod(Duration.seconds(dataUpdatePeriod));
		svc.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
      public void handle(WorkerStateEvent t) {
				String time = (String)t.getSource().getValue();
        changeUpdateTime(time);
      }
		});
		svc.start();
	}

	System.out.println("Launching interface...");
	openHelp();
}

/* =============================================== */
/* =============== Initialisation ================ */
/* =============================================== */

// Initialise variables
public void init_variables() {
	listening = false;
	onHelp = false;
	ready = false;
	chatbot_message_list = new LinkedList<>();
	helptext_list = new ArrayList<>();

	generateHelpText();
	generateAnimations();

	chatbot_message_list.add(new Response("Hi, ask me anything!", null));
}

// Add help text to list
private void generateHelpText() {

	helptext_list.add("Current price of BT?");
	helptext_list.add("Coca Cola close price yesterday?");
	helptext_list.add("BP open price yesterday?");
	helptext_list.add("What was the highest price of barc yesterday?");
	helptext_list.add("Lowest price of Sainsbury's?");
	helptext_list.add("BATS volume 1 feb?");
	helptext_list.add("BT's open price today?");
	helptext_list.add("Any news on BKG?");
	helptext_list.add("What is the high price of barc?");
	helptext_list.add("Tesco low?");
	helptext_list.add("Volume of Glencore?");
	helptext_list.add("Vodafone percentage change?");
	helptext_list.add("How much has ITV changed in price?");
	helptext_list.add("CNA year average close?");
	helptext_list.add("GKN year high?");
	helptext_list.add("What was RBS lowest price this year?");
	helptext_list.add("Average volume for RTO last year?");
	helptext_list.add("Open prices of automobile?");
	helptext_list.add("High prices of aerospace?");
	helptext_list.add("Banks lowest prices?");
	helptext_list.add("Current prices of oil?");
	helptext_list.add("How much has chemicals changed?");
	helptext_list.add("Percentage change of gas?");
	helptext_list.add("Volume of media?");
	helptext_list.add("News on chemicals?");
	helptext_list.add("Yearly highs of tobacco?");
	helptext_list.add("Yearly lows of tobacco?");
	helptext_list.add("2017 average close real estate?");
	helptext_list.add("2018 average volume of banks?");
	helptext_list.add("Closing price on banks?");
	helptext_list.add("Closing price of tobacco yesterday?");
	helptext_list.add("Any companies falling in automobiles?");
	helptext_list.add("Which chemicals are falling?");
	helptext_list.add("Which aerospace companies are rising?");
	helptext_list.add("Which tobacco companies are falling right now?");

}

private void generateAnimations() {
	mic_button_timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt->round_mic_button.setStyle("-fx-background-color: #FF4339;")),
	                                   new KeyFrame(Duration.seconds(1), evt->round_mic_button.setStyle("-fx-background-color: #a9a9a9;")));
	mic_button_timeline.setCycleCount(Animation.INDEFINITE);
}

/* =============================================== */
/* =============== ChatBot queries =============== */
/* =============================================== */

public void makeSystemQuery(String message){
	Message system = new Response(message, null);
	chatbot_message_list.add(system);
	addMessage(system);
}

// Make a query to the chatbot
public void makeQuery(String text) {
	// if we are on the help page, close it
	if(onHelp) {
		closeAll();
	}

	// add the query to the UI
	Message query = new Query(text);
	chatbot_message_list.add(query);
	addMessage(query);

	// if the connection is established
	if(ready) {
		// Get response in extra task
		Task task = new Task<Message>() {
			@Override
			public Message call() {

				Message response;

				try {

					Pair<String, LinkedList<NewsObj> > responsePair =
						virtualAssistant.getResponse(query.getMessage());

					String responseStr = responsePair.getFirst();

					LinkedList<NewsObj> responseNews = responsePair.getSecond();

					if(responseStr != null && !responseStr.equals("") && virtualAssistant.systemStatus.getSoundEnabled())
						tts.speak(responseStr, 1.0f, false, false);

					response = new Response(responseStr, responseNews);

				} catch (Exception e) {

					//e.printStackTrace();

					String message = "Sorry, we couldn't find that. Use the help button for queries you can ask. Try google meanwhile!";
					String url = "https://www.google.co.uk/search?q=" + query.getMessage().replaceAll("\\s", "+");
					response = new URLMessage(message, url);
				}

				return response;
			}
		};

		// Once response is recieved add it to the UI
		task.setOnSucceeded(new EventHandler() {
				@Override
				public void handle(Event event) {
				        Platform.runLater(new Runnable() {
						@Override
						public void run() {

						        Message response = (Message)task.getValue();
						        chatbot_message_list.add(response);

						        addMessage(response);

						}
					});
				}
			});
		new Thread(task).start();

		// If there is no connection established
	} else {
		// Add error message as response
		Message error_response = new Response("Connection error. Please try again later.",null);
		chatbot_message_list.add(error_response);
		addMessage(error_response);

	}
}

public static void saveStatus(){
	if(virtualAssistant == null) return;

	virtualAssistant.saveStatus();
}

// Start listening to voice input
public void startListening() {

	System.out.println("start listening");
	stt.stopIgnoreSpeechRecognitionResults();

}

// End listening to voice input
public void stopListening() {

	stt.ignoreSpeechRecognitionResults();
	System.out.println("stop listening");
	//String text = "";
	//makeQuery(text);
}

/* =============================================== */
/* ============ Handle Button Clicks ============= */
/* =============================================== */

@FXML
private void handleMuteButtonClick() {
	virtualAssistant.systemStatus.toggleSound();
	changeMuteButtonIcon();
}

// handle when the mic button is clicked
@FXML
private void handleMicButtonClick(ActionEvent e) {
	if(!listening) {
		listening = true;
		mic_button_timeline.play();
		startListening();
	} else {
		listening = false;
		mic_button_timeline.stop();
		round_mic_button.setStyle("-fx-background-color: #a9a9a9;");
		stopListening();
	}
}

@FXML
private void handleSendQueryButtonClick(ActionEvent e) {
	String typed_query = query_text_field.getText();

	if(typed_query != null && !typed_query.trim().isEmpty()) {
		makeQuery(typed_query);
	}
	query_text_field.setText("");
}

@FXML
private void handleHelpButtonClick(ActionEvent e) {
	if(onHelp) {
		closeAll();
	} else {
		openHelp();
	}
}

/* =============================================== */
/* ============ Change User Inteface ============= */
/* =============================================== */

public void scrollToBottom() {
	Animation animation = new Timeline(
		new KeyFrame(Duration.seconds(1),
		             new KeyValue(scrollpane.vvalueProperty(), 1)));
	animation.play();

}
public String getTimeNow() {
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	String time = sdf.format(cal.getTime()).toString();
	return time;
}

public void changeUpdateTime(String time) {
	update_time.setText("Last updated: " + time);
}

public void changeWifiAccess(boolean access) {
	if(access) {
		ready = true;
		query_text_field.setPromptText("Input a query here");
		query_text_field.setDisable(false);
		send_query_button.setDisable(false);

		Image image = new Image(getClass().getResourceAsStream("images/wifi_access.png"));
		wifi_image_view.setImage(image);
	} else {
		ready = false;
		query_text_field.setPromptText("Connecting...");
		query_text_field.setDisable(true);
		send_query_button.setDisable(true);

		Image image = new Image(getClass().getResourceAsStream("images/wifi_no_access.png"));
		wifi_image_view.setImage(image);
	}
}

public void changeMuteButtonIcon(){
	if(virtualAssistant.systemStatus.getSoundEnabled()) {
		// un mute the voice
		Image image = new Image(getClass().getResourceAsStream("images/not_muted.png"));
		mute_control_image_view.setImage(image);
	} else {
		// mute the voice
		Image image = new Image(getClass().getResourceAsStream("images/muted.png"));
		mute_control_image_view.setImage(image);
	}
}

public void addMessage(Message message) {
	if(onHelp) {
		closeAll();
	}
	chatbot_container.getChildren().add(message.getDisplay());
	scrollToBottom();
}

// Display the help screen to the user
public void openHelp() {
	onHelp = true;
	chatbot_container.getChildren().clear();

	Label helpTitle;

	String[] suggested = null;

	if(!ready) {
		helpTitle = new Label("Some questions you can ask:");
	} else {
		suggested = virtualAssistant.learningAgent.suggestQueries(4);
		if(suggested.length > 0) {
			helpTitle = new Label("Suggested queries for you:");
		} else {
			helpTitle = new Label("Some questions you can ask:");
		}
	}

	helpTitle.setPrefWidth(Main.WIDTH);
	helpTitle.setAlignment(Pos.CENTER);
	helpTitle.setId("help_title");

	chatbot_container.getChildren().add(helpTitle);



	if(!ready || suggested.length == 0) {
		Set<Integer> stated = new HashSet<>();

		for(int i = 0; i < 4 && i < helptext_list.size(); i++) {

			int rand_index = (int)(Math.random() * helptext_list.size());

			while (stated.contains(rand_index)) {
				rand_index = (int)(Math.random() * helptext_list.size());
			}

			stated.add(rand_index);

			Label text_label = new Label('"'+helptext_list.get(rand_index)+'"');
			text_label.setPrefWidth(Main.WIDTH);
			text_label.setAlignment(Pos.CENTER);
			text_label.setId("help_text");

			chatbot_container.getChildren().add(text_label);
		}
	} else {

		for(String x : suggested) {
			Label text_label = new Label('"'+x+'"');
			text_label.setPrefWidth(Main.WIDTH);
			text_label.setAlignment(Pos.CENTER);
			text_label.setId("help_text");

			chatbot_container.getChildren().add(text_label);
		}
	}

	Label ellipsis = new Label("...");
	ellipsis.setPrefWidth(Main.WIDTH);
	ellipsis.setAlignment(Pos.CENTER);
	ellipsis.setId("help_text");

	chatbot_container.getChildren().add(ellipsis);
}

// close help screen
public void closeAll() {
	onHelp = false;
	chatbot_container.getChildren().clear();

	for(Message x : chatbot_message_list) {
		addMessage(x);
	}
}

}

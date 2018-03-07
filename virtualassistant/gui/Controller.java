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
private ScrollPane scrollpane;
@FXML
private VBox chatbot_container;
@FXML
private Label update_time;
@FXML
private TextField query_text_field;
@FXML
private Button send_query_button;
@FXML
private ImageView wifi_image_view;
@FXML
private ImageView mute_control_image_view;
@FXML
private Button round_mic_button;
private Timeline mic_button_timeline;

private boolean listening;
private boolean onHelp;
private boolean ready; // used to see if ready to recieve messages

// IMPORTANT: Set to false only if it slows down your internet connection too much
private boolean autoUpdate = true;
private boolean virtualAssistantFinished = false;
private boolean sstFinished = false;
private final static int dataUpdatePeriod = 60; // seconds

private List<Message> chatbot_message_list;
private List<String> helptext_list;

private static VirtualAssistant virtualAssistant;
private static SpeechRecognizerMain stt;
private static TextToSpeech tts;

public final boolean sttEnabled = false;

@Override
public void initialize(URL location, ResourceBundle resources) {
	init_variables();

	// Run download of data in background
	Task task1 = new Task<Void>() {
		@Override
		public Void call() {
			System.out.println("Starting text to speech...");
			tts = new TextToSpeech();
			//tts.setVoice("dfki-poppy-hsmm");
			tts.setVoice("cmu-rms-hsmm");


			System.out.println("Downloading data...");
			virtualAssistant = new VirtualAssistant(Controller.this);

			ready = true;
			// Change mute button
			changeMuteButtonIcon(virtualAssistant.systemStatus.getSoundEnabled());
			changeWifiAccess(true);

			System.out.println("Success!");
			System.out.println("===================================\n\n");

			virtualAssistantFinished = true;

			if(sstFinished)
				round_mic_button.setDisable(false);

			return null;
		}
	};

	// Run initialization of speech-to-text in background
	Task task2 = new Task<Void>() {
		@Override
		public Void call() {
			if (Controller.this.sttEnabled) {
				stt = new SpeechRecognizerMain(Controller.this);

				//stt.startSpeechRecognition();
				stt.ignoreSpeechRecognitionResults();
				System.out.println("Speech to text... complete");

				sstFinished = true;

				if(virtualAssistantFinished)
					round_mic_button.setDisable(false);
			}

			return null;
		}
	};

	task1.setOnSucceeded(e->{
			changeUpdateTime(getTimeNow());

			new Thread(task2).start();
		});
	new Thread(task1).start();


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
private void init_variables() {
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
	//helptext_list.add("BATS volume 1 feb?");
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
	//helptext_list.add("2017 average close real estate?");
	//helptext_list.add("2018 average volume of banks?");
	helptext_list.add("Closing price on banks?");
	helptext_list.add("Closing price of tobacco yesterday?");
	helptext_list.add("Any companies falling in automobiles?");
	helptext_list.add("Which chemicals are falling?");
	helptext_list.add("Which aerospace companies are rising?");
	helptext_list.add("Which tobacco companies are falling right now?");
	helptext_list.add("What are my favourite stocks?");
	helptext_list.add("How are my favourites doing?");
	helptext_list.add("Show my favourite stocks");
}

private void generateAnimations() {
	mic_button_timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt->round_mic_button.setStyle("-fx-background-color: #FF4339;")),
	                                   new KeyFrame(Duration.seconds(1), evt->round_mic_button.setStyle("-fx-background-color: #a9a9a9;")));
	mic_button_timeline.setCycleCount(Animation.INDEFINITE);
}

/* =============================================== */
/* =============== ChatBot queries =============== */
/* =============================================== */

/**
 * This method allows for a query to be made to the chatbot.
 * It will add both the query and the response the chatbot display.
 *
 * @param text {String} The query to be made to the chatbot
 */
public void makeQuery(String text) {
	// if we are on the help page, close it
	if(onHelp) {
		closeAll();
	}

	// add the query to the UI
	Message query = new Query(text);
	chatbot_message_list.add(query);
	addMessage(query);


	if(isFavouritesMessage(text)) {
		displayFavourites();
	} else {
		// if the connection is established
		if(ready) {
			// Get response in extra task to allow for better user experience
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
						if(virtualAssistant.systemStatus.getSoundEnabled())
							tts.speak("Sorry, we couldn't find that.", 1.0f, false, false);

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

}

/**
 * Make a system query to the UI.
 * Just displays the given data similar to a response
 *
 * @param message The message to be displayed with the alerts
 * @param alerts  Array of alerts that will be displayed with the message
 */
public void makeSystemQuery(String message, String[] alerts){
	Message system = new AlertMessage(message, alerts);
	chatbot_message_list.add(system);
	addMessage(system);
}

/**
 * [saveStatus description]
 */
public static void saveStatus(){
	if(virtualAssistant == null) return;

	virtualAssistant.saveStatus();
}

/**
 * Called when the microphone button is first clicked.
 * Used to make the application start listening to voice input.
 */
public void startListening() {

	System.out.println("start listening");
	stt.stopIgnoreSpeechRecognitionResults();

}

/**
 * Called when the microphone button is clicked while it is already listening.
 * Used to make the application stop listening to voice input.
 */
public void stopListening() {

	stt.ignoreSpeechRecognitionResults();
	System.out.println("stop listening");

}

/**
 * Changes what the UI displays for the last updated time
 *
 * @param time The time to be displayed
 */
public void changeUpdateTime(String time) {
	update_time.setText("Last updated: " + time);
}

private boolean isFavouritesMessage(String text1) {
	String text = text1.toLowerCase();

	return text.contains("favourites")
	|| text.contains("favourite's")
	|| text.contains("favourite")
	|| text.contains("favorites")
	|| text.contains("favorite's")
	|| text.contains("favorite")
	|| text.contains("favoured")
	|| text.contains("favored");
}

/* =============================================== */
/* ============ Handle Button Clicks ============= */
/* =============================================== */

@FXML
private void handleMuteButtonClick() {
	virtualAssistant.systemStatus.toggleSound();
	changeMuteButtonIcon(virtualAssistant.systemStatus.getSoundEnabled());
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

private void scrollToBottom() {
	scrollpane.setVvalue(1);
	Animation animation = new Timeline(
		new KeyFrame(Duration.seconds(0.5),
		             new KeyValue(scrollpane.vvalueProperty(), 1)));
	animation.play();

}
private String getTimeNow() {
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	String time = sdf.format(cal.getTime()).toString();
	return time;
}

private void changeWifiAccess(boolean access) {
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

private void changeMuteButtonIcon(boolean sound){
	if(sound) {
		// un mute the voice
		Image image = new Image(getClass().getResourceAsStream("images/not_muted.png"));
		mute_control_image_view.setImage(image);
	} else {
		// mute the voice
		Image image = new Image(getClass().getResourceAsStream("images/muted.png"));
		mute_control_image_view.setImage(image);
	}
}
private void displayFavourites() {
	if(ready) {
		Message favourites = new FavouritesMessage("Your favourites:", virtualAssistant.learningAgent.getUpdates(3));
		chatbot_message_list.add(favourites);
		addMessage(favourites);
	}
}

private void addMessage(Message message) {
	if(onHelp) {
		closeAll();
	}
	chatbot_container.getChildren().add(message.getDisplay());
	scrollToBottom();
}

// Display the help screen to the user
private void openHelp() {
	onHelp = true;
	chatbot_container.getChildren().clear();


	/* Black bar - not needed
	// Favourites bar at top of help page
	HBox favourites_contain = new HBox(15);
	favourites_contain.setPrefWidth(Main.WIDTH);
	favourites_contain.setId("favourites_contain");
	favourites_contain.setAlignment(Pos.CENTER);
	if(ready) {

		for(String x : virtualAssistant.learningAgent.getUpdates(3)) {
			Label label = new Label(x);
			label.setId("favourites_help_label");

			favourites_contain.getChildren().add(label);
		}

	} else {
		Label loading = new Label("Loading favourites...");
		loading.setId("favourites_help_label");
		favourites_contain.getChildren().add(loading);
	}
	chatbot_container.getChildren().add(favourites_contain);
	*/

	// Help page
	Label helpTitle;

	String[] suggested = null;

	helpTitle = new Label("Some questions you can ask:");

	helpTitle.setPrefWidth(Main.WIDTH);
	helpTitle.setAlignment(Pos.CENTER);
	helpTitle.setId("help_title");

	chatbot_container.getChildren().add(helpTitle);

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
		text_label.setTextAlignment(TextAlignment.CENTER);
		text_label.setWrapText(true);
		text_label.setId("help_text");

		chatbot_container.getChildren().add(text_label);
	}

	/*
	   for(String x : suggested) {
	        Label text_label = new Label('"'+x+'"');
	        text_label.setPrefWidth(Main.WIDTH);
	        text_label.setAlignment(Pos.CENTER);
	        text_label.setTextAlignment(TextAlignment.CENTER);
	        text_label.setWrapText(true);
	        text_label.setId("help_text");

	        chatbot_container.getChildren().add(text_label);
	   }
	 */


	Label ellipsis = new Label("...");
	ellipsis.setPrefWidth(Main.WIDTH);
	ellipsis.setAlignment(Pos.CENTER);
	ellipsis.setId("help_text");

	chatbot_container.getChildren().add(ellipsis);
}

// close help screen
private void closeAll() {
	onHelp = false;
	chatbot_container.getChildren().clear();

	for(Message x : chatbot_message_list) {
		addMessage(x);
	}
}

}

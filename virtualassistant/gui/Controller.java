package virtualassistant.gui;

// TODO

import virtualassistant.VirtualAssistant;
import virtualassistant.misc.Pair;
import virtualassistant.data.news.NewsObj;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.Separator;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.io.IOException;

// Text to speech
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.JetPilotEffect;
import marytts.signalproc.effects.LpcWhisperiserEffect;
import marytts.signalproc.effects.RobotiserEffect;
import marytts.signalproc.effects.StadiumEffect;
import marytts.signalproc.effects.VocalTractLinearScalerEffect;
import marytts.signalproc.effects.VolumeEffect;

import model.TextToSpeech;

import virtualassistant.chatbot.SpeechRecognizerMain;

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
private boolean muted;

// IMPORTANT: Set to false only if it slows down your internet connection too much
private boolean autoUpdate = true;

private List<Message> chatbot_message_list;
private List<String> helptext_list;

private static VirtualAssistant virtualAssistant;
private static SpeechRecognizerMain stt;
private static TextToSpeech tts;

@Override
public void initialize(URL location, ResourceBundle resources) {
		listening = false;
		onHelp = false;
		ready = false;
		muted = false;
		chatbot_message_list = new ArrayList<>();
		helptext_list = new ArrayList<>();

		generateHelpText();
		generateAnimations();

		chatbot_message_list.add(new Response("Hi, ask me anything!", null));

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
						changeWifiAccess(true);

						System.out.println("Success!");
						System.out.println("===================================\n\n");

						return null;
				}
		};
		new Thread(task1).start();

		// Run initialization of speech-to-text in background
		Task task2 = new Task<Void>() {
				@Override
				public Void call() {
			
                        stt = new SpeechRecognizerMain(Controller.this);
                        stt.startSpeechRecognition();
                        stt.ignoreSpeechRecognitionResults();
                        System.out.println("Speech to text... complete");

						round_mic_button.setDisable(false);

						return null;
				}
		};
		new Thread(task2).start();


		if(autoUpdate) {
				final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				executorService.scheduleAtFixedRate(new Runnable(){
								public void run() {
								        virtualAssistant.scan();
								}
						}, 60, 60, TimeUnit.SECONDS);
		}

		System.out.println("Launching interface...");
		openHelp();
}

public void makeSystemQuery(){

}

// make a query
public void makeQuery(String text) {
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
								Pair<String, LinkedList<NewsObj> > responsePair;
								String responseStr;
								LinkedList<NewsObj> responseNews;
								try {
										responsePair = virtualAssistant.getResponse(query.getMessage());
										responseStr = responsePair.getFirst();
										responseNews = responsePair.getSecond();
								} catch (Exception e) {
										responseStr = "An error occured";
										responseNews = null;
										e.printStackTrace();
								}

								tts.speak(responseStr, (float)virtualAssistant.systemStatus.getVolume(), false, false);

								Message response = new Response(responseStr, responseNews);
								return response;
						}
				};
				// once task completed add it to the UI
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
				Message error_response = new Response("Connection error. Please try again later.",null);
				chatbot_message_list.add(error_response);
				addMessage(error_response);
		}
}

public static void saveStatus(){
		if(virtualAssistant == null) return;

		virtualAssistant.saveStatus();
}

public void startListening() {

		stt.stopIgnoreSpeechRecognitionResults();

}

public void stopListening() {
		stt.ignoreSpeechRecognitionResults();
		//String text = "";
		//makeQuery(text);
}

private void generateHelpText() {
		helptext_list.add("How are the banks doing?");
		helptext_list.add("Any news on Coca Cola?");
		helptext_list.add("What is the high price of Just Eat?");
		helptext_list.add("Is LLoyds positive?");
		helptext_list.add("Open price of Barclays");
		helptext_list.add("How do you feel about construction?");
}

private void generateAnimations() {
		mic_button_timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt->round_mic_button.setStyle("-fx-background-color: #FF4339;")),
		                                   new KeyFrame(Duration.seconds(1), evt->round_mic_button.setStyle("-fx-background-color: #a9a9a9;")));
		mic_button_timeline.setCycleCount(Animation.INDEFINITE);
}

private void scrollToBottom() {
		Animation animation = new Timeline(
				new KeyFrame(Duration.seconds(1),
				             new KeyValue(scrollpane.vvalueProperty(), 1)));
		animation.play();
}

@FXML
private void handleMuteButtonClick() {
		if(muted) {
				// un mute the voice

				Image image = new Image(getClass().getResourceAsStream("images/not_muted.png"));
				mute_control_image_view.setImage(image);
				muted = false;
		} else {
				// mute the voice

				Image image = new Image(getClass().getResourceAsStream("images/muted.png"));
				mute_control_image_view.setImage(image);
				muted = true;
		}
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

private void addMessage(Message message) {
		chatbot_container.getChildren().add(message.getDisplay());
		scrollToBottom();
}

// open help screen
private void openHelp() {
		onHelp = true;
		chatbot_container.getChildren().clear();

		Label helpTitle = new Label("Some questions you can ask:");
		helpTitle.setPrefWidth(Main.WIDTH);
		helpTitle.setAlignment(Pos.CENTER);
		helpTitle.setId("help_title");
		chatbot_container.getChildren().add(helpTitle);

		for(int i = 0; i < 4 && i < helptext_list.size(); i++) {
				Label text_label = new Label('"'+helptext_list.get(i)+'"');
				text_label.setPrefWidth(Main.WIDTH);
				text_label.setAlignment(Pos.CENTER);
				text_label.setId("help_text");
				chatbot_container.getChildren().add(text_label);
		}

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
		for(int i = 0; i < chatbot_message_list.size(); i++) {
				addMessage(chatbot_message_list.get(i));
		}
}

}

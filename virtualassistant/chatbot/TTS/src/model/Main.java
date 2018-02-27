package model;

import java.util.Arrays;
import java.util.List;

import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.JetPilotEffect;
import marytts.signalproc.effects.LpcWhisperiserEffect;
import marytts.signalproc.effects.RobotiserEffect;
import marytts.signalproc.effects.StadiumEffect;
import marytts.signalproc.effects.VocalTractLinearScalerEffect;
import marytts.signalproc.effects.VolumeEffect;

public class Main {
	public static void main(String[] args) {
		//Create TextToSpeech
		TextToSpeech tts = new TextToSpeech();
		
		//print all available voices we have added on class path
		Voice.getAvailableVoices().stream().forEach(System.out::println);
		
		//setting the voice
		tts.setVoice("dfki-poppy-hsmm");
		
		//tts says what we tell it to say
		tts.speak("Sorry, I didn't catch that.", 1.0f, false, false);
		
		
	}
	
}

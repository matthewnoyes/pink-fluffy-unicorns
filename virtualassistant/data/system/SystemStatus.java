package virtualassistant.data.system;

import java.util.Calendar;
import java.util.Date;

public class SystemStatus {

    private boolean soundEnabled;
    private boolean speechEnabled;
    private double volume; // [0.0, 1.0]

    public SystemStatus(boolean soundEnabled, boolean speechEnabled, double volume){
        this.soundEnabled = soundEnabled;
        this.speechEnabled = speechEnabled;
        this.volume = volume;
    }

    public void toggleSound(){
        soundEnabled = !soundEnabled;
    }

    public void toggleSpeech(){
        speechEnabled = !speechEnabled;
    }

    public void setVolume(double volume){
        this.volume = volume;
    }

    public void setSoundEnabled(boolean soundEnabled){
        this.soundEnabled = soundEnabled;
    }

    public void setSpeechEnabled(boolean speechEnabled){
        this.speechEnabled = speechEnabled;
    }

    public boolean getSpeechEnabled(){
        return speechEnabled;
    }

    public boolean getSoundEnabled(){
        return soundEnabled;
    }

    public double getVolume(){
        return volume;
    }
}

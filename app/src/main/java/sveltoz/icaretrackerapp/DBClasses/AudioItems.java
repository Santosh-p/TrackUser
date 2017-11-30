package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 11/24/16.
 */

public class AudioItems {
    String audioName;


    public AudioItems(String audioName) {
        this.audioName = audioName;

    }

    public String get_audio_name() {
        return audioName;
    }

    public void set_audio_name(String audioName) {
        this.audioName = audioName;
    }


}

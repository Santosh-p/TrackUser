package sveltoz.icaretrackerapp.OtherClasses;

import android.app.Application;

/**
 * Created by apple on 9/23/16.
 */

public class AppStatus extends Application{
    public boolean isActivityVisible() {
        return activityVisible;
    }

    public void activityResumed() {
        activityVisible = true;
    }

    public void activityPaused() {
        activityVisible = false;
    }

    private boolean activityVisible;
}

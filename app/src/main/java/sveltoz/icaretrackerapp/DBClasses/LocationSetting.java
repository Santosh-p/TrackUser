package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 5/5/17.
 */

public class LocationSetting {
    int _trackerId;
    String _locSettingTime;

    public LocationSetting() {
    }

    public LocationSetting(int _trackerId, String _locSettingTime) {
        this._trackerId = _trackerId;
        this._locSettingTime = _locSettingTime;
    }

    public int get_trackerId() {
        return _trackerId;
    }

    public void set_trackerId(int _trackerId) {
        this._trackerId = _trackerId;
    }

    public String get_locSettingTime() {
        return _locSettingTime;
    }

    public void set_locSettingTime(String _locSettingTime) {
        this._locSettingTime = _locSettingTime;
    }
}

package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 8/17/16.
 */
public class Tracker {
    int _tracker_Id;
    String _user_Id;
    String _tracker_Name;
    String _tracker_Email;
    String _location_flag;
    String _history_flag;
    String _autocall_flag;
    String _imagecapture_flag;
    String _livetracking_flag;
    String _tracker_App_Status;


    public Tracker() {
    }


    public Tracker(String _user_Id, String _tracker_Name, String _tracker_Email, String _location_flag, String _history_flag, String _autocall_flag, String _imagecapture_flag, String _livetracking_flag, String _tracker_App_Status) {
        this._user_Id = _user_Id;
        this._tracker_Name = _tracker_Name;
        this._tracker_Email = _tracker_Email;
        this._location_flag = _location_flag;
        this._history_flag = _history_flag;
        this._autocall_flag = _autocall_flag;
        this._imagecapture_flag = _imagecapture_flag;
        this._livetracking_flag = _livetracking_flag;
        this._tracker_App_Status = _tracker_App_Status;
    }


    public Tracker(int _tracker_Id, String _user_Id, String _tracker_Name, String _tracker_Email, String _location_flag, String _history_flag, String _autocall_flag, String _imagecapture_flag, String _livetracking_flag, String _tracker_App_Status) {
        this._tracker_Id = _tracker_Id;
        this._user_Id = _user_Id;
        this._tracker_Name = _tracker_Name;
        this._tracker_Email = _tracker_Email;
        this._location_flag = _location_flag;
        this._history_flag = _history_flag;
        this._autocall_flag = _autocall_flag;
        this._imagecapture_flag = _imagecapture_flag;
        this._livetracking_flag = _livetracking_flag;
        this._tracker_App_Status = _tracker_App_Status;
    }

    public String get_tracker_App_Status() {
        return _tracker_App_Status;
    }

    public void set_tracker_App_Status(String _tracker_App_Status) {
        this._tracker_App_Status = _tracker_App_Status;
    }

    public String get_imagecapture_flag() {
        return _imagecapture_flag;
    }

    public void set_imagecapture_flag(String _imagecapture_flag) {
        this._imagecapture_flag = _imagecapture_flag;
    }

    public Tracker(String _user_Id, String _livetracking_flag) {
        this._user_Id = _user_Id;
        this._livetracking_flag = _livetracking_flag;
    }

    public String get_livetracking_flag() {
        return _livetracking_flag;
    }

    public void set_livetracking_flag(String _livetracking_flag) {
        this._livetracking_flag = _livetracking_flag;
    }

    public int get_tracker_Id() {
        return _tracker_Id;
    }

    public void set_tracker_Id(int _tracker_Id) {
        this._tracker_Id = _tracker_Id;
    }

    public String get_user_Id() {
        return _user_Id;
    }

    public void set_user_Id(String _user_Id) {
        this._user_Id = _user_Id;
    }

    public String get_tracker_Name() {
        return _tracker_Name;
    }

    public void set_tracker_Name(String _tracker_Name) {
        this._tracker_Name = _tracker_Name;
    }

    public String get_tracker_Email() {
        return _tracker_Email;
    }

    public void set_tracker_Email(String _tracker_Email) {
        this._tracker_Email = _tracker_Email;
    }

    public String get_location_flag() {
        return _location_flag;
    }

    public void set_location_flag(String _location_flag) {
        this._location_flag = _location_flag;
    }

    public String get_history_flag() {
        return _history_flag;
    }

    public void set_history_flag(String _history_flag) {
        this._history_flag = _history_flag;
    }

    public String get_autocall_flag() {
        return _autocall_flag;
    }

    public void set_autocall_flag(String _autocall_flag) {
        this._autocall_flag = _autocall_flag;
    }
}

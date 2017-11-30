package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 1/23/17.
 */

public class TrackerTrackeeDetails {
String _user_id;
    String _name;
    String _status;
    byte[] _bytes;


    public TrackerTrackeeDetails(String _user_id, String _name, String _status) {
        this._user_id=_user_id;
        this._name = _name;
        this._status = _status;

    }

    public TrackerTrackeeDetails(String _user_id, String _name, String _status, byte[] _bytes) {
        this._user_id = _user_id;
        this._name = _name;
        this._status = _status;
        this._bytes = _bytes;
    }

    public TrackerTrackeeDetails(String _user_id, String _status) {
        this._user_id = _user_id;
        this._status = _status;
    }

    public byte[] get_bytes() {
        return _bytes;
    }

    public void set_bytes(byte[] _bytes) {
        this._bytes = _bytes;
    }

    public String get_user_id() {
        return _user_id;
    }

    public void set_user_id(String _user_id) {
        this._user_id = _user_id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }
}

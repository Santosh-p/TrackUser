package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by Acer-Win7 on 10/18/2016.
 */
public class LocationClass {

    int _location_id;
    String _latitude;
    String _longitude;
    String _timestamp;
    String _user_Id;


    public LocationClass(int _location_id, String _latitude, String _longitude, String _timestamp, String _user_Id) {
        this._location_id = _location_id;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._timestamp = _timestamp;
        this._user_Id = _user_Id;
    }

    public int get_location_id() {
        return _location_id;
    }

    public LocationClass(String _latitude, String _longitude, String _timestamp) {
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._timestamp = _timestamp;
    }

    public LocationClass(int _location_id, String _latitude, String _longitude, String _timestamp) {
        this._location_id = _location_id;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._timestamp = _timestamp;
    }

    public LocationClass(String _user_Id) {
        this._user_Id = _user_Id;
    }

    public void set_location_id(int _location_id) {
        this._location_id = _location_id;
    }

    public String get_timestamp() {
        return _timestamp;
    }

    public void set_timestamp(String _timestamp) {
        this._timestamp = _timestamp;
    }

    public String get_longitude() {
        return _longitude;
    }

    public void set_longitude(String _longitude) {
        this._longitude = _longitude;
    }

    public String get_latitude() {
        return _latitude;
    }

    public void set_latitude(String _latitude) {
        this._latitude = _latitude;
    }

    public String get_user_Id() {
        return _user_Id;
    }

    public void set_user_Id(String _user_Id) {
        this._user_Id = _user_Id;
    }
}

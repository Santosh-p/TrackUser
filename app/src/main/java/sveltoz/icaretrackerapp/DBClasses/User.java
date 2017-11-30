package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 8/11/16.
 */

public class User {
    int _user_id;
    String _name;
    String _email;
    String _tracker_pin;
    String _trackee_pin;
    String _user_type;
    String _email_verify;
    public User() {
    }

    public User(String _email_verify, String _user_type, String _trackee_pin, String _tracker_pin, String _email, String _name) {
        this._email_verify = _email_verify;
        this._user_type = _user_type;
        this._trackee_pin = _trackee_pin;
        this._tracker_pin = _tracker_pin;

        this._email = _email;
        this._name = _name;
    }

    public User(int _user_id, String _name, String _email, String _tracker_pin, String _trackee_pin, String _user_type, String _email_verify) {
        this._user_id = _user_id;
        this._name = _name;
        this._email = _email;
        this._tracker_pin = _tracker_pin;
        this._trackee_pin = _trackee_pin;
        this._user_type = _user_type;
        this._email_verify = _email_verify;
    }

    public User(int _user_id, String _name, String _email) {
        this._user_id = _user_id;
        this._name = _name;
        this._email = _email;
    }

    public int get_user_id() {
        return _user_id;
    }

    public void set_user_id(int _user_id) {
        this._user_id = _user_id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_tracker_pin() {
        return _tracker_pin;
    }

    public void set_tracker_pin(String _tracker_pin) {
        this._tracker_pin = _tracker_pin;
    }

    public String get_trackee_pin() {
        return _trackee_pin;
    }

    public void set_trackee_pin(String _trackee_pin) {
        this._trackee_pin = _trackee_pin;
    }

    public String get_user_type() {
        return _user_type;
    }

    public void set_user_type(String _user_type) {
        this._user_type = _user_type;
    }

    public String get_email_verify() {
        return _email_verify;
    }

    public void set_email_verify(String _email_verify) {
        this._email_verify = _email_verify;
    }
}

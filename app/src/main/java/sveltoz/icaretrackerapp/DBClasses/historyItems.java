package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 11/24/16.
 */

public class historyItems {
    String _number;
    String _call_Type;
    String _call_Duration;
    String _call_Time;

    public historyItems(String _number, String _call_Type, String _call_Duration, String _call_Time) {
        this._number = _number;
        this._call_Type = _call_Type;
        this._call_Duration = _call_Duration;
        this._call_Time = _call_Time;
    }

    public String get_call_Time() {
        return _call_Time;
    }

    public void set_call_Time(String _call_Time) {
        this._call_Time = _call_Time;
    }

    public String get_number() {
        return _number;
    }

    public void set_number(String _number) {
        this._number = _number;
    }

    public String get_call_Type() {
        return _call_Type;
    }

    public void set_call_Type(String _call_Type) {
        this._call_Type = _call_Type;
    }

    public String get_call_Duration() {
        return _call_Duration;
    }

    public void set_call_Duration(String _call_Duration) {
        this._call_Duration = _call_Duration;
    }
}

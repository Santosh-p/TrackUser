package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 8/26/16.
 */
public class Trackee {
    int _trackee_Id;
    String _user_Id;
    String _trackee_Name;
    String _trackee_Email;
    String _trackee_Loc_Flag;
    String _trackee_His_Flag;
    String _trackee_Auto_Flag;
    String _trackee_ImageCapture_Flag;
    String _trackee_App_Status;

    public Trackee() {
    }


    public Trackee(int _trackee_Id, String _user_Id, String _trackee_Name, String _trackee_Email, String _trackee_Loc_Flag, String _trackee_His_Flag, String _trackee_Auto_Flag, String _trackee_ImageCapture_Flag, String _trackee_App_Status) {
        this._trackee_Id = _trackee_Id;
        this._user_Id = _user_Id;
        this._trackee_Name = _trackee_Name;
        this._trackee_Email = _trackee_Email;
        this._trackee_Loc_Flag = _trackee_Loc_Flag;
        this._trackee_His_Flag = _trackee_His_Flag;
        this._trackee_Auto_Flag = _trackee_Auto_Flag;
        this._trackee_ImageCapture_Flag = _trackee_ImageCapture_Flag;
        this._trackee_App_Status = _trackee_App_Status;
    }

    public Trackee(String _user_Id, String _trackee_Name, String _trackee_Email, String _trackee_Loc_Flag, String _trackee_His_Flag, String _trackee_Auto_Flag, String _trackee_ImageCapture_Flag, String _trackee_App_Status) {
        this._user_Id = _user_Id;
        this._trackee_Name = _trackee_Name;
        this._trackee_Email = _trackee_Email;
        this._trackee_Loc_Flag = _trackee_Loc_Flag;
        this._trackee_His_Flag = _trackee_His_Flag;
        this._trackee_Auto_Flag = _trackee_Auto_Flag;
        this._trackee_ImageCapture_Flag = _trackee_ImageCapture_Flag;
        this._trackee_App_Status = _trackee_App_Status;
    }



    public Trackee(String _user_Id, String _trackee_App_Status) {
        this._user_Id = _user_Id;
        this._trackee_App_Status = _trackee_App_Status;
    }

    public String get_trackee_App_Status() {
        return _trackee_App_Status;
    }

    public void set_trackee_App_Status(String _trackee_App_Status) {
        this._trackee_App_Status = _trackee_App_Status;
    }

    public String get_trackee_ImageCapture_Flag() {
        return _trackee_ImageCapture_Flag;
    }

    public void set_trackee_ImageCapture_Flag(String _trackee_ImageCapture_Flag) {
        this._trackee_ImageCapture_Flag = _trackee_ImageCapture_Flag;
    }

    public int get_trackee_Id() {
        return _trackee_Id;
    }

    public void set_trackee_Id(int _trackee_Id) {
        this._trackee_Id = _trackee_Id;
    }

    public String get_user_Id() {
        return _user_Id;
    }

    public void set_user_Id(String _user_Id) {
        this._user_Id = _user_Id;
    }

    public String get_trackee_Name() {
        return _trackee_Name;
    }

    public void set_trackee_Name(String _trackee_Name) {
        this._trackee_Name = _trackee_Name;
    }

    public String get_trackee_Email() {
        return _trackee_Email;
    }

    public void set_trackee_Email(String _trackee_Email) {
        this._trackee_Email = _trackee_Email;
    }

    public String get_trackee_Loc_Flag() {
        return _trackee_Loc_Flag;
    }

    public void set_trackee_Loc_Flag(String _trackee_Loc_Flag) {
        this._trackee_Loc_Flag = _trackee_Loc_Flag;
    }

    public String get_trackee_His_Flag() {
        return _trackee_His_Flag;
    }

    public void set_trackee_His_Flag(String _trackee_His_Flag) {
        this._trackee_His_Flag = _trackee_His_Flag;
    }

    public String get_trackee_Auto_Flag() {
        return _trackee_Auto_Flag;
    }

    public void set_trackee_Auto_Flag(String _trackee_Auto_Flag) {
        this._trackee_Auto_Flag = _trackee_Auto_Flag;
    }
}

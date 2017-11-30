package sveltoz.icaretrackerapp.DBClasses;

/**
 * Created by apple on 11/29/16.
 */

public class Request {
    int _request_id;
    String _req_user_id;
    String _req_user_type;
    String _req_user_name;
    String _req_user_emailid;

    public Request() {
    }

    public Request(String _req_user_id, String _req_user_type, String _req_user_name, String _req_user_emailid) {
        this._req_user_id = _req_user_id;
        this._req_user_type = _req_user_type;
        this._req_user_name = _req_user_name;
        this._req_user_emailid = _req_user_emailid;
    }

    public Request(int _request_id, String _req_user_id, String _req_user_type, String _req_user_name, String _req_user_emailid) {
        this._request_id = _request_id;
        this._req_user_id = _req_user_id;
        this._req_user_type = _req_user_type;
        this._req_user_name = _req_user_name;
        this._req_user_emailid = _req_user_emailid;
    }

    public int get_request_id() {
        return _request_id;
    }

    public void set_request_id(int _request_id) {
        this._request_id = _request_id;
    }

    public String get_req_user_id() {
        return _req_user_id;
    }

    public void set_req_user_id(String _req_user_id) {
        this._req_user_id = _req_user_id;
    }

    public String get_req_user_type() {
        return _req_user_type;
    }

    public void set_req_user_type(String _req_user_type) {
        this._req_user_type = _req_user_type;
    }

    public String get_req_user_name() {
        return _req_user_name;
    }

    public void set_req_user_name(String _req_user_name) {
        this._req_user_name = _req_user_name;
    }

    public String get_req_user_emailid() {
        return _req_user_emailid;
    }

    public void set_req_user_emailid(String _req_user_emailid) {
        this._req_user_emailid = _req_user_emailid;
    }
}

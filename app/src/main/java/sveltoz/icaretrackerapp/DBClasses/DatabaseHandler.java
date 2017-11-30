package sveltoz.icaretrackerapp.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static sveltoz.icaretrackerapp.Activity.DrawerActivity.context;
import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "iCare.db";
    // token table
    private static final String TABLE_TOKEN = "tbltoken";
    private static final String KEY_TOKEN_ID = "token_id";
    private static final String KEY_TOKEN = "token";
    // User Table
    private static final String TABLE_USERS = "registration";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL_ID = "email_id ";
    private static final String KEY_TRACKER_PIN = "trackerpin";
    private static final String KEY_TRACKEE_PIN = "trackeepin";
    private static final String KEY_USER_TYPE = "usertype";
    private static final String KEY_EMAIL_VERIFY = "emailverify";
    private static final String KEY_STORAGE= "storage";
    // Trackers
    private static final String TABLE_TRACKER_DETAILS = "trckerdetails";
    private static final String KEY_TRACKER_ID = "trackerid";
    private static final String KEY_TRACKER_NAME = "trackername";
    private static final String KEY_TRACKER_EMAIL = "trackeremail";
    private static final String KEY_LOCATION_FLAG = "locationflag";
    private static final String KEY_HISTORY_FLAG = "historyflag";
    private static final String KEY_AUTOCALL_FLAG = "autocallflag";
    private static final String KEY_IMAGECAPTURE_FLAG = "imagecaptureflag";
    private static final String KEY_LIVETRACKING_FLAG = "livetrackingflag";
    private static final String KEY_TRACKER_App_Status_FLAG = "trackerappstatusflag";
    // Trackees
    private static final String TABLE_TRACKEE_DETAILS = "trckeedetails";
    private static final String KEY_TRACKEE_ID = "trackeeid";
    private static final String KEY_TRACKEE_NAME = "trackeename";
    private static final String KEY_TRACKEE_EMAIL = "trackeeemail";
    private static final String KEY_TRACKEE_LOCATION_FLAG = "trackeelocationflag";
    private static final String KEY_TRACKEE_HISTORY_FLAG = "trackeehistoryflag";
    private static final String KEY_TRACKEE_AUTOCALL_FLAG = "trackeeautocallflag";
    private static final String KEY_TRACKEE_IMAGECAPTURE_FLAG = "trackeeimagecaptureflag";
    private static final String KEY_TRACKEE_App_Status_FLAG = "trackeeappstatusflag";

    private static final String TABLE_STORE_IMAGES = "imagestable";
    // Profile image
    public static final String IMAGE_ID = "id";
    public static final String IMAGE = "image";
    private SQLiteDatabase mDb;
   // private static final String IMAGES_TABLE = "ImagesTable";

    //table location
    private static final String TABLE_STORE_LOCATION = "storelocation";
    private static final String KEY_LOCATION_ID = "locationid";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIMESTAMP = "timestamp";

    //table pendingrequest
    private static final String TABLE_PENDING_REQUEST = "pendingrequest";
    private static final String KEY_REQUEST_ID = "reqid";
    private static final String KEY_REQ_USER_ID = "requserid";
    private static final String KEY_REQ_USER_TYPE = "requsertype";
    private static final String KEY_REQ_USER_NAME = "requsername";
    private static final String KEY_REQ_USER_EMAIL = "requseremail";
    //table trackertrackeeimages
    private static final String TABLE_TRACKER_TRACKEE_IMAGES = "trackertrackeeimages";
    public static final String TRACKER_TRACKEE_ID = "trackertrackeeid";
    public static final String TRACKER_TRACKEE_IMAGE = "trackertrackeeimage";


    //table LocationSetting
    private static final String TABLE_LOCATION_SETTING = "locationsetting";
    private static final String KEY_LOC_SETTING_TIME = "locsettingtime";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TOKEN_TABLE = "CREATE TABLE " + TABLE_TOKEN + "("
                + KEY_TOKEN_ID + " INTEGER,"
                + KEY_TOKEN + " TEXT"+ ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL_ID + " TEXT,"
                + KEY_TRACKER_PIN + " TEXT,"
                + KEY_TRACKEE_PIN + " TEXT,"
                + KEY_USER_TYPE + " TEXT,"
                + KEY_EMAIL_VERIFY + " TEXT"+ ")";

        String CREATE_TRACKERS_TABLE = "CREATE TABLE " + TABLE_TRACKER_DETAILS + "("
                + KEY_TRACKER_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " TEXT,"
                + KEY_TRACKER_NAME + " TEXT,"
                + KEY_TRACKER_EMAIL + " TEXT,"
                + KEY_LOCATION_FLAG + " TEXT,"
                + KEY_HISTORY_FLAG + " TEXT,"
                + KEY_AUTOCALL_FLAG + " TEXT,"
                + KEY_IMAGECAPTURE_FLAG + " TEXT,"
                + KEY_LIVETRACKING_FLAG + " TEXT,"
                + KEY_TRACKER_App_Status_FLAG + " TEXT"+ ")";


        String CREATE_TRACKEES_TABLE = "CREATE TABLE " + TABLE_TRACKEE_DETAILS + "("
                + KEY_TRACKEE_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " TEXT,"
                + KEY_TRACKEE_NAME + " TEXT,"
                + KEY_TRACKEE_EMAIL + " TEXT,"
                + KEY_TRACKEE_LOCATION_FLAG + " TEXT,"
                + KEY_TRACKEE_HISTORY_FLAG + " TEXT,"
                + KEY_TRACKEE_AUTOCALL_FLAG + " TEXT,"
                + KEY_TRACKEE_IMAGECAPTURE_FLAG + " TEXT,"
                + KEY_TRACKEE_App_Status_FLAG + " TEXT"+ ")";

        String CREATE_IMAGES_TABLE =
                "CREATE TABLE " + TABLE_STORE_IMAGES + " (" +
                        IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + IMAGE + " BLOB NOT NULL );";

        String CREATE_TABLE_STORE_LOCATION = "CREATE TABLE " + TABLE_STORE_LOCATION + "("
                + KEY_LOCATION_ID + " INTEGER PRIMARY KEY,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_TIMESTAMP + " TEXT" + ")";

        String CREATE_TABLE_PENDING_REQUEST = "CREATE TABLE " + TABLE_PENDING_REQUEST + "("
                + KEY_REQUEST_ID + " INTEGER PRIMARY KEY,"
                + KEY_REQ_USER_ID + " TEXT,"
                + KEY_REQ_USER_TYPE + " TEXT,"
                + KEY_REQ_USER_NAME + " TEXT,"
                + KEY_REQ_USER_EMAIL + " TEXT" + ")";

        String CREATE_TABLE_TRACKER_TRACKEE_IMAGES = "CREATE TABLE " + TABLE_TRACKER_TRACKEE_IMAGES + "("
                + TRACKER_TRACKEE_ID + " INTEGER,"
                + TRACKER_TRACKEE_IMAGE + " BLOB NOT NULL );";
        String CREATE_TABLE_LOCATION_SETTING = "CREATE TABLE " + TABLE_LOCATION_SETTING + "("
                + KEY_TRACKER_ID + " INTEGER,"
                + KEY_LOC_SETTING_TIME + " TEXT"+");";

        db.execSQL(CREATE_TOKEN_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TRACKERS_TABLE);
        db.execSQL(CREATE_TRACKEES_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_TABLE_STORE_LOCATION);
        db.execSQL(CREATE_TABLE_PENDING_REQUEST);
        db.execSQL(CREATE_TABLE_TRACKER_TRACKEE_IMAGES);
        db.execSQL(CREATE_TABLE_LOCATION_SETTING);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKEE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING_REQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKER_TRACKEE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_SETTING);

        // Create tables again

        onCreate(db);
    }
    // ############################# Token table ###########################

    public void addToken(Integer tokenID,String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOKEN_ID, tokenID);
        values.put(KEY_TOKEN, token);
        // Inserting Row
        db.insert(TABLE_TOKEN, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public int updateToken(Integer tokenID,String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOKEN, token);

        // updating row
        return db.update(TABLE_TOKEN, values, KEY_TOKEN_ID + " = ?",
                new String[]{String.valueOf(tokenID)});
    }
    public String getToken() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TOKEN;
        Cursor cursor = db.rawQuery(selectQuery, null);
        String strToken=null;
        if (cursor.moveToLast()) {
            strToken=cursor.getString(1);
        }
        return strToken;
    }

    // ############################# User table ###########################
    // code to add User
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.get_user_id());
        values.put(KEY_NAME, user.get_name());
        values.put(KEY_EMAIL_ID, user.get_email());
        values.put(KEY_TRACKER_PIN, user.get_tracker_pin());
        values.put(KEY_TRACKEE_PIN, user.get_trackee_pin());
        values.put(KEY_USER_TYPE, user.get_user_type());
        values.put(KEY_EMAIL_VERIFY, user.get_email_verify());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public User getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

        }
        User user = new User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
        return user;
    }

    // code to get all Users in a list view
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.set_user_id(cursor.getInt(0));
                user.set_name(cursor.getString(1));
                user.set_email(cursor.getString(2));
                user.set_tracker_pin(cursor.getString(3));
                // Adding contact to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userList;
    }

    // code to update the single User
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.get_name());
        values.put(KEY_EMAIL_ID, user.get_email());
        values.put(KEY_TRACKER_PIN, user.get_tracker_pin());
        values.put(KEY_TRACKEE_PIN, user.get_trackee_pin());
        values.put(KEY_USER_TYPE, user.get_user_type());
        values.put(KEY_EMAIL_VERIFY, user.get_email_verify());
        // updating row
        return db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.get_user_id())});
    }

    // code to update the single User
    public int updateUser1(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.get_name());
        values.put(KEY_EMAIL_ID, user.get_email());

        // updating row
        return db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.get_user_id())});
    }

// ############################# TrackerDetails table ###########################

    // code to add Tracker
    public void addTracker(Tracker tracker) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, tracker.get_user_Id());
            values.put(KEY_TRACKER_NAME, tracker.get_tracker_Name());
            values.put(KEY_TRACKER_EMAIL, tracker.get_tracker_Email());
            values.put(KEY_LOCATION_FLAG, tracker.get_location_flag());
            values.put(KEY_HISTORY_FLAG, tracker.get_history_flag());
            values.put(KEY_AUTOCALL_FLAG, tracker.get_autocall_flag());
            values.put(KEY_IMAGECAPTURE_FLAG, tracker.get_imagecapture_flag());
            values.put(KEY_LIVETRACKING_FLAG, tracker.get_livetracking_flag());
            values.put(KEY_TRACKER_App_Status_FLAG, tracker.get_tracker_App_Status());
           // KEY_LIVETRACKING_FLAG

            // Inserting Row
            db.insert(TABLE_TRACKER_DETAILS, null, values);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "1 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void updateTracker(Tracker tracker) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, tracker.get_user_Id());
            values.put(KEY_TRACKER_NAME, tracker.get_tracker_Name());
            values.put(KEY_TRACKER_EMAIL, tracker.get_tracker_Email());
            values.put(KEY_LOCATION_FLAG, tracker.get_location_flag());
            values.put(KEY_HISTORY_FLAG, tracker.get_history_flag());
            values.put(KEY_AUTOCALL_FLAG, tracker.get_autocall_flag());
            values.put(KEY_IMAGECAPTURE_FLAG, tracker.get_imagecapture_flag());
            values.put(KEY_LIVETRACKING_FLAG, tracker.get_livetracking_flag());
            values.put(KEY_TRACKER_App_Status_FLAG, tracker.get_tracker_App_Status());
            // Inserting Row
            db.update(TABLE_TRACKER_DETAILS, values, KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(tracker.get_user_Id())});
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "2 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // code to add Trackee
    public void updateTrackerAppStatusFlag(TrackerTrackeeDetails tracker) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TRACKER_App_Status_FLAG, tracker.get_status());
            // updating Row
            db.update(TABLE_TRACKER_DETAILS, values, KEY_TRACKER_ID + " = ?",
                    new String[]{String.valueOf(tracker.get_user_id())});

        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "3 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void updateLivetrackingStatusTracker(Tracker tracker) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, tracker.get_user_Id());
            values.put(KEY_LIVETRACKING_FLAG, tracker.get_livetracking_flag());

            // Inserting Row
            db.update(TABLE_TRACKER_DETAILS, values, KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(tracker.get_user_Id())});
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "4 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // code to get all Tracker in a list view
    public List<Tracker> getAllTrackers() {
        List<Tracker> trackerList = new ArrayList<Tracker>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACKER_DETAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tracker tracker = new Tracker();
                tracker.set_tracker_Id(Integer.parseInt(cursor.getString(0)));
                tracker.set_user_Id(cursor.getString(1));
                tracker.set_tracker_Name(cursor.getString(2));
                tracker.set_tracker_Email(cursor.getString(3));
                tracker.set_location_flag(cursor.getString(4));
                tracker.set_history_flag(cursor.getString(5));
                tracker.set_autocall_flag(cursor.getString(6));
                tracker.set_imagecapture_flag(cursor.getString(7));
                tracker.set_livetracking_flag(cursor.getString(8));
                tracker.set_tracker_App_Status(cursor.getString(9));

                // Adding tracker to list
                trackerList.add(tracker);
            } while (cursor.moveToNext());
        }
        // return Tracker list
        return trackerList;
    }

    // code to get all Tracker in a list view
    public List<Tracker> getTrackerDetails(String trackerid) {
        List<Tracker> trackerList = new ArrayList<Tracker>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACKER_DETAILS+ " WHERE " + KEY_USER_ID + "='" + trackerid + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tracker tracker = new Tracker();
                tracker.set_tracker_Id(Integer.parseInt(cursor.getString(0)));
                tracker.set_user_Id(cursor.getString(1));
                tracker.set_tracker_Name(cursor.getString(2));
                tracker.set_tracker_Email(cursor.getString(3));
                tracker.set_location_flag(cursor.getString(4));
                tracker.set_history_flag(cursor.getString(5));
                tracker.set_autocall_flag(cursor.getString(6));
                tracker.set_imagecapture_flag(cursor.getString(7));
                tracker.set_livetracking_flag(cursor.getString(8));
                tracker.set_tracker_App_Status(cursor.getString(9));

                // Adding tracker to list
                trackerList.add(tracker);
            } while (cursor.moveToNext());
        }
        // return Tracker list
        return trackerList;
    }

    //delete trackee
    public void deleteTracker(String trackerid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TRACKER_DETAILS + " WHERE " + KEY_USER_ID + "='" + trackerid + "'");
        db.close();
    }
// ############################# TrackeeDetails table ###########################

    // code to add Trackee
    public void addTrackee(Trackee trackee) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, trackee.get_user_Id());
            values.put(KEY_TRACKEE_NAME, trackee.get_trackee_Name());
            values.put(KEY_TRACKEE_EMAIL, trackee.get_trackee_Email());
            values.put(KEY_TRACKEE_LOCATION_FLAG, trackee.get_trackee_Loc_Flag());
            values.put(KEY_TRACKEE_HISTORY_FLAG, trackee.get_trackee_His_Flag());
            values.put(KEY_TRACKEE_AUTOCALL_FLAG, trackee.get_trackee_Auto_Flag());
            values.put(KEY_TRACKEE_IMAGECAPTURE_FLAG, trackee.get_trackee_ImageCapture_Flag());
            values.put(KEY_TRACKEE_App_Status_FLAG, trackee.get_trackee_App_Status());
            // Inserting Row
            db.insert(TABLE_TRACKEE_DETAILS, null, values);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "5 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to add Trackee
    public void updateTrackee(Trackee trackee) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, trackee.get_user_Id());
            values.put(KEY_TRACKEE_NAME, trackee.get_trackee_Name());
            values.put(KEY_TRACKEE_EMAIL, trackee.get_trackee_Email());
            values.put(KEY_TRACKEE_LOCATION_FLAG, trackee.get_trackee_Loc_Flag());
            values.put(KEY_TRACKEE_HISTORY_FLAG, trackee.get_trackee_His_Flag());
            values.put(KEY_TRACKEE_AUTOCALL_FLAG, trackee.get_trackee_Auto_Flag());
            values.put(KEY_TRACKEE_IMAGECAPTURE_FLAG, trackee.get_trackee_ImageCapture_Flag());
            values.put(KEY_TRACKEE_App_Status_FLAG, trackee.get_trackee_App_Status());
            // Inserting Row
            db.update(TABLE_TRACKEE_DETAILS, values, KEY_TRACKEE_ID + " = ?",
                    new String[]{String.valueOf(trackee.get_trackee_Id())});

        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "6 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // code to add Trackee
    public void updateTrackeeAppStatusFlag(Trackee trackee) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, trackee.get_user_Id());
            values.put(KEY_TRACKEE_App_Status_FLAG, trackee.get_trackee_App_Status());
            // updating Row
            db.update(TABLE_TRACKEE_DETAILS, values, KEY_TRACKEE_ID + " = ?",
                    new String[]{String.valueOf(trackee.get_trackee_Id())});

        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "7 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // code to get all Trackee in a list view
    public List<Trackee> getAllTrackees() {
        List<Trackee> trackeeList = new ArrayList<Trackee>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACKEE_DETAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Trackee trackee = new Trackee();
                trackee.set_trackee_Id(Integer.parseInt(cursor.getString(0)));
                trackee.set_user_Id(cursor.getString(1));
                trackee.set_trackee_Name(cursor.getString(2));
                trackee.set_trackee_Email(cursor.getString(3));
                trackee.set_trackee_Loc_Flag(cursor.getString(4));
                trackee.set_trackee_His_Flag(cursor.getString(5));
                trackee.set_trackee_Auto_Flag(cursor.getString(6));
                trackee.set_trackee_ImageCapture_Flag(cursor.getString(7));
                trackee.set_trackee_App_Status(cursor.getString(8));
                // Adding Trackee to list
                trackeeList.add(trackee);
            } while (cursor.moveToNext());
        }
        // return Trackee list
        return trackeeList;
    }

    public Trackee getTrackeeDetails(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Trackee trackee=null;
        Cursor cursor = db.rawQuery( "SELECT * FROM " + TABLE_TRACKEE_DETAILS + " WHERE " + KEY_USER_ID + "="+id, null);
        if (cursor.moveToFirst()) {

             trackee = new Trackee(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                     cursor.getString(7),
                     cursor.getString(8));
        }
        return trackee;
    }
    //delete trackee
    public void deleteTrackee(String trackeeid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TRACKEE_DETAILS + " WHERE " + KEY_USER_ID + "='" + trackeeid + "'");
        db.close();
    }
    // ############################# Images table ###########################

    public DatabaseHandler open() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        return this;
    }

    public void close() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }

    // Insert the image to the Sqlite DB
    public void insertImage(byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IMAGE, imageBytes);
        db.insert(TABLE_STORE_IMAGES, null, cv);
    }

    // Get the image from SQLite DB
    // We will just get the last image we just saved for convenience...
    public byte[] retreiveImageFromDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.query(true, TABLE_STORE_IMAGES, new String[]{IMAGE,},
                null, null, null, null,
                IMAGE_ID + " DESC", "1");
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(cur.getColumnIndex(IMAGE));
            cur.close();
            return blob;
        }
        cur.close();
        return null;
    }

    public void RemoveImage(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from "+ TABLE_STORE_IMAGES);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("delete exception", e.toString());
            e.printStackTrace();
            appendLog(context, "8 DatabaseHandler " + e.toString()+date);
        }
    }

    // ############################# LocationClass table ###########################
    // code to add LocationClass
    public void addlocation(LocationClass locationClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_ID, locationClass.get_location_id());
        values.put(KEY_LATITUDE, locationClass._latitude);
        values.put(KEY_LONGITUDE, locationClass._longitude);
        values.put(KEY_TIMESTAMP, locationClass._timestamp);

        // Inserting Row
        db.insert(TABLE_STORE_LOCATION, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    //get stored location
    public LocationClass getStoredLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STORE_LOCATION;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            return null;
        }else {
            if (cursor.moveToLast()) {
                Log.i("insert exception", "");
            }
            LocationClass locationClass = new LocationClass(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            return locationClass;
        }
    }

    //Update LocationClass in every half hour
    public void updateLocation(LocationClass locationClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LATITUDE, locationClass.get_latitude());
            values.put(KEY_LONGITUDE, locationClass.get_longitude());
            values.put(KEY_TIMESTAMP, locationClass.get_timestamp());

            // Inserting Row
            db.update(TABLE_STORE_LOCATION, values, KEY_LOCATION_ID + " = ?",
                    new String[]{String.valueOf(locationClass.get_location_id())});
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("insert exception", e.toString());
            e.printStackTrace();
            appendLog(context, "9 DatabaseHandler " + e.toString()+date);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // ############################# Pending_Request table ###########################
    // code to add Pending_Request
    public void addRequest(Request request) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REQ_USER_ID, request.get_req_user_id());
        values.put(KEY_REQ_USER_TYPE, request.get_req_user_type());
        values.put(KEY_REQ_USER_NAME, request.get_req_user_name());
        values.put(KEY_REQ_USER_EMAIL, request.get_req_user_emailid());
        // Inserting Row
        db.insert(TABLE_PENDING_REQUEST, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all Users in a list view
    public List<Request> getStoredRequests() {
        List<Request> reqList = new ArrayList<Request>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_REQUEST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Request req = new Request();
                req.set_request_id(cursor.getInt(0));
                req.set_req_user_id(cursor.getString(1));
                req.set_req_user_type(cursor.getString(2));
                req.set_req_user_name(cursor.getString(3));
                req.set_req_user_emailid(cursor.getString(4));

                //Adding request to list
                reqList.add(req);
            } while (cursor.moveToNext());
        }
        //Return contact list
        return reqList;
    }

    //delete Pending_Request
    public void deleteRequest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_PENDING_REQUEST + " WHERE " + KEY_REQUEST_ID + "='" + id + "'");
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("delete exception", e.toString());
            e.printStackTrace();
            appendLog(context, "10 DatabaseHandler " + e.toString()+date);
        }
        db.close();

        //Closing database connection
    }
    //delete AllPending_Request
    public void deleteAllRequest() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from "+ TABLE_PENDING_REQUEST);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("delete exception", e.toString());
            e.printStackTrace();
            appendLog(context, "11 DatabaseHandler " + e.toString()+date);
        }
        db.close(); // Closing database connection
    }

    // ############################# Commom code ###########################
    //delete AllPending_Request
    public void clearAppData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from "+ TABLE_USERS);
            db.execSQL("delete from "+ TABLE_TRACKER_DETAILS);
            db.execSQL("delete from "+ TABLE_TRACKEE_DETAILS);
            db.execSQL("delete from "+ TABLE_STORE_LOCATION);
            db.execSQL("delete from "+ TABLE_STORE_IMAGES);
            db.execSQL("delete from "+ TABLE_PENDING_REQUEST);
            db.execSQL("delete from " + TABLE_TRACKER_TRACKEE_IMAGES);
            db.execSQL("delete from " + TABLE_LOCATION_SETTING);

        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("delete exception", e.toString());
            e.printStackTrace();
            appendLog(context, "12 DatabaseHandler " + e.toString()+date);
        }
        db.close(); // Closing database connection
    }
    public void clearUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            //db.execSQL("delete from "+ TABLE_USERS);
            db.execSQL("delete from "+ TABLE_TRACKER_DETAILS);
            db.execSQL("delete from "+ TABLE_TRACKEE_DETAILS);
            db.execSQL("delete from "+ TABLE_STORE_LOCATION);
            db.execSQL("delete from "+ TABLE_STORE_IMAGES);
            db.execSQL("delete from "+ TABLE_PENDING_REQUEST);
            db.execSQL("delete from " + TABLE_TRACKER_TRACKEE_IMAGES);
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("delete exception", e.toString());
            e.printStackTrace();
            appendLog(context, "13 DatabaseHandler " + e.toString()+date);
        }
        db.close(); // Closing database connection
    }

    // ############################# TRACKER TRACKEE IMAGE TABLE ###########################
    // Insert the inserttrackertrackeeImage to the Sqlite DB
    public void inserttrackertrackeeImage(int uid,byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRACKER_TRACKEE_IMAGE, imageBytes);
        cv.put(TRACKER_TRACKEE_ID,uid);
        db.insert(TABLE_TRACKER_TRACKEE_IMAGES, null, cv);
    }
    public byte[] retrivetrackertrackeeimage(int uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.query(true, TABLE_TRACKER_TRACKEE_IMAGES, new String[]{TRACKER_TRACKEE_IMAGE,},
                null, null, null, null,
                uid + " DESC", "1");
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(cur.getColumnIndex(TRACKER_TRACKEE_IMAGE));
            cur.close();
            return blob;
        }
        cur.close();
        return null;
    }

    public byte[] getStoredImage(int uid) {

        // Select All Query
        String selectQuery = "SELECT " + TRACKER_TRACKEE_IMAGE + " FROM " + TABLE_TRACKER_TRACKEE_IMAGES +" WHERE " + TRACKER_TRACKEE_ID + "=" + uid;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            byte[] blob = cursor.getBlob(0);
            return blob;
        }
        // return contact list
        return null;
    }
// ############################# TRACKER location time table ###########################

    // code to add LocSetting
    public void addLocSetting(int userId, String timeToStop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRACKER_ID, userId);
        values.put(KEY_LOC_SETTING_TIME, timeToStop);


        // Inserting Row
        db.insert(TABLE_LOCATION_SETTING, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    // code to get all items from location setting
    public List<LocationSetting> getAlllocSettings() {
        List<LocationSetting> locSettingList = new ArrayList<LocationSetting>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_LOCATION_SETTING;

           cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    LocationSetting locationSetting = new LocationSetting();
                    locationSetting.set_trackerId(cursor.getInt(0));
                    locationSetting.set_locSettingTime(cursor.getString(1));

                    // Adding contact to list
                    locSettingList.add(locationSetting);
                } while (cursor.moveToNext());
            }
            
        }
        
        catch(Exception e){
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLog(context, "14 DatabaseHandler " + e.toString()+date);
        }
        finally {
            cursor.close();
        }
        // return contact list
        return locSettingList;
    }

    // code to update the single User
    public int updateLocSetting(LocationSetting locationSetting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOC_SETTING_TIME, locationSetting.get_locSettingTime());

        // updating row
        return db.update(TABLE_LOCATION_SETTING, values, KEY_TRACKER_ID + " = ?",
                new String[]{String.valueOf(locationSetting.get_trackerId())});
    }

    //delete trackee
    public void deleteLocSetting(int trackerid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOCATION_SETTING + " WHERE " + KEY_TRACKER_ID + "='" + trackerid + "'");
        db.close();
    }
}
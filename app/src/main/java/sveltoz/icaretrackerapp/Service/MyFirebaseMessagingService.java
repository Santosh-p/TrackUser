package sveltoz.icaretrackerapp.Service;

/**
 * Created by apple on 3/15/17.
 */

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import sveltoz.icaretrackerapp.Activity.AddTrackerActivity;
import sveltoz.icaretrackerapp.Activity.DrawerActivity;
import sveltoz.icaretrackerapp.Activity.LoginActivity;
import sveltoz.icaretrackerapp.Activity.MapsActivity;
import sveltoz.icaretrackerapp.Activity.PendingNotificationsActivity;
import sveltoz.icaretrackerapp.App.Config;
import sveltoz.icaretrackerapp.Camera.CameraPreview;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.LocationClass;
import sveltoz.icaretrackerapp.DBClasses.LocationSetting;
import sveltoz.icaretrackerapp.DBClasses.Request;
import sveltoz.icaretrackerapp.DBClasses.Trackee;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;
import sveltoz.icaretrackerapp.Util.NotificationUtils;

import static sveltoz.icaretrackerapp.Activity.DrawerActivity.context;
import static sveltoz.icaretrackerapp.Activity.LoginActivity.onActivity;
import static sveltoz.icaretrackerapp.Activity.PrivacySettingActivity.sdf;
import static sveltoz.icaretrackerapp.Constants.Constants.appendLogforService;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrlDownloadFile;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrlUploadFile;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    String responceType, responceVal2, responceVal3, batryLevel, timestamp, callHistory, trackeeid, from, from1;
    public static double latitude, longitude;


    //LocationUpdateService gps1;
    InputStream inputStream = null;
    // for update user
    String uUserId, uName, uEmail, settingkeyword;
    public static String userid, responceVal1;
    public static String message;
    //static String mobileno;
    // battery details
    public String status;
    public static int level;
    public static String PACKAGE_NAME;
    ArrayList<String> trackeeUserIdList = new ArrayList<String>();
    ArrayList<String> trackerUserIdList = new ArrayList<String>();

    ArrayList<ArrayList<String>> hisList = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<String>();
    ArrayList<String> calltype = new ArrayList<String>();
    ArrayList<String> callDayTime = new ArrayList<String>();
    ArrayList<String> callduration = new ArrayList<String>();

    // String timefromdatabase;
    DatabaseHandler db = new DatabaseHandler(this);
    Context myssplcontext;
    StringBuffer sbCallHistry = new StringBuffer();
    final Handler handler = new Handler();
    String apilevel;
    String formattedDate, formattedDate1;
    int locid = 1;
    String flag = "ON";
    String autoflag, hisflag, Locflag, imgflag, deletefilefrom;
    File audiofile;
    String filepath;
    File recordingfile;
    static File capturedfile;
    static String capturedImageName;

    Integer t;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".mp3";
    private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final int AUDIO_RECORDER_FOLDER1 = 0;
    String StoragePath;

    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP3,
            AUDIO_RECORDER_FILE_EXT_3GP};
    String LocSetting, HisSetting, BatterySetting, drawerTab;
    String trackeeLocFlag = "ON";
    String trackeeHisFlag = "ON";
    String trackeeAutoflag = "ON";
    String trackeeImageCaptureflag = "ON";

    String defaultFlag = "true";

    public static String liveTrackingStatus;
    public static String camType;
    String audiofilename;
    Uri path = Uri.parse("android.resource://sveltoz.icaretrackerapp/raw/notification");
    int trackee_Id;
    String trackee_user_Id, trackee_Name, trackee_Email, trackee_Loc_Flag, trackee_His_Flag, trackee_Auto_Flag, trackee_ImageCapture_Flag, trackee_App_Status, result = null, responceStatus;

    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected android.location.Location mCurrentLocation;
    private android.location.Location mLocation;

    boolean canGetLocation = false;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;

    String uUserId1, uName1, uEmail1, autoflag1, hisflag1, Locflag1, imgflag1, responceStatus1, appStatusFlag1, liveTrackingFlag1;
    List<LocationSetting> locationSetting;

    @Override
    public void onCreate() {
        PACKAGE_NAME = getApplicationContext().getPackageName();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (checkGPSService()) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // getLocation();

            //   new BatteryStatus().execute("");
        }
        callHistory = getCallDetails();
        // new BatteryStatus().execute("");

        User user1 = db.getUserDetails();
        userid = String.valueOf(user1.get_user_id());
        // getting GPS status

        final AccessibilityService myssplcontext;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                locationSetting = db.getAlllocSettings();
                String alertTime = "";
                String currentDateTimeString;

                for (LocationSetting cn : locationSetting) {
                    alertTime = cn.get_locSettingTime();
                    //11 Sep 2017 12:08:37 p.m.
                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    //11 Sep 2017 12:03:51 p.m.
                    //  alertTime=   18 Jul 2017 07:22:08 PM
                    // currentDateTimeString =18-Jul-2017 6:24:09 PM
                    //  sdf= dd-MMM-yyyy hh:mm:ss aa
                    try {
                        if (currentDateTimeString.contains("-")) {
                            sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                        } else if (Character.isLetter(currentDateTimeString.charAt(0))) {
                            sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
                        } else {
                            sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                        }
                        Date date1 = sdf.parse(currentDateTimeString);

                        if (alertTime.contains("-")) {
                            sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                        } else if (Character.isLetter(alertTime.charAt(0))) {
                            sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
                        } else {
                            sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                        }

                        Date date2 = sdf.parse(alertTime);
                        Long diffInMin = printDifference(date2, date1);

                        if (diffInMin < 1) {
                            int tID = cn.get_trackerId();
                            List<Tracker> tracker = db.getTrackerDetails(Integer.toString(tID));
                            for (Tracker cn1 : tracker) {
                                trackerUserIdList.add(cn1.get_user_Id());
                                uUserId1 = cn1.get_user_Id();
                                uName1 = cn1.get_tracker_Name();
                                uEmail1 = cn1.get_tracker_Email();
                                Locflag1 = cn1.get_location_flag();
                                hisflag1 = cn1.get_history_flag();
                                autoflag1 = cn1.get_autocall_flag();
                                imgflag1 = cn1.get_imagecapture_flag();
                                liveTrackingFlag1 = cn1.get_livetracking_flag();
                                appStatusFlag1 = cn1.get_tracker_App_Status();
                            }

                            if (isNetworkAvailable(MyFirebaseMessagingService.this)) {
                                Locflag = "OFF";
                                settingkeyword = "locoff";
                                new SendNotificationofSetting().execute();

                            }
                            db.deleteLocSetting(Integer.parseInt(uUserId));
                            updateTracker();
                        }
                    } catch (Exception e) {
                        //java.text.ParseException: Unparseable date: "13 Jul 2017 18:36:18" (at offset 20)
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        e.printStackTrace();
                        appendLogforService(getApplicationContext(), "1 MyFirebaseMessagingService " + e.toString() + date);

                    }
                }
                ha.postDelayed(this, 5000);
            }
        }, 5000);
    }

    public void updateTracker() {

        db.updateTracker(new Tracker(uUserId, uName, uEmail, Locflag, hisflag, autoflag, imgflag, liveTrackingFlag1, appStatusFlag1));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        } else {
            //Toast.makeText(this, "LocationClass not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        //Bundle[{google.sent_time=1489561773210, from=58701156251, google.message_id=0:1489561773222271%7060114af9fd7ecd, contentTitle=content title GCM, message=hii, tickerText=example test GCM}]
        if (remoteMessage == null)
            return;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                Map<String, String> params = remoteMessage.getData();
                JSONObject json = new JSONObject(params);

                message = json.getString("message");
                //frontimagereq/85/88/OFF
                //downloadimagefile/64 2017-01-30 12.54.01.jpg
                //downloadaudiofile/audio 603 2017-03-23 15.10.45.mp3
                //newtrackee/70/harish hiray/harishhiray@gmail.com
                //addtrackeeres/76/Android test/yes/dev.test.ndroid@gmail.com
                //hisres/917972286530Mx0qThursdayk29kJunek11:56:45kAMz917972286530Mx0qThursdayk29kJunek11:07:20kAMz19012676031Ix42qThursdayk29kJunek10:43:45kAMz7276890984Ox239qThursdayk29kJunek09:13:37kAMz917276890984Mx0qThursdayk29kJunek09:13:17kAMz61419941117Ix114qThursdayk29kJunek09:11:10kAMz61419941117Ix0qThursdayk29kJunek09:10:34kAMz7276890984Ox0qWednesdayk28kJunek23:26:45kPMz7276890984Ox0qWednesdayk28kJunek22:25:12kPMz918626093260Ox91qWednesdayk28kJunek22:18:46kPMz/
                //hisres/917756900423Ix11qTuesdayk20kJunek17:34:23kPMz917756900423Mx0qTuesdayk20kJunek13:09:31kPMz919028355897Ix3qThursdayk1kJunek14:51:12kPMz919028355897Mx0qThursdayk1kJunek14:49:27kPMz918669038406Ix8qTuesdayk30kMayk14:25:51kPMz918669038406nullx0qTuesdayk30kMayk14:24:52kPMz919658963154Mx0qFridayk5kMayk20:47:25kPMz919658963154Mx0qFridayk5kMayk20:46:25kPMz917972248523Ox0qWednesdayk12kAprilk17:19:26kPMz917972248523Mx0qFridayk31kMarchk10:07:42kAMz/ON
                //hisres/917972286530Mx0qThursdayk29kJunek11:56:45kAMz917972286530Mx0qThursdayk29kJunek11:07:20kAMz19012676031Ix42qThursdayk29kJunek10:43:45kAMz7276890984Ox239qThursdayk29kJunek09:13:37kAMz917276890984Mx0qThursdayk29kJunek09:13:17kAMz61419941117Ix114qThursdayk29kJunek09:11:10kAMz61419941117Ix0qThursdayk29kJunek09:10:34kAMz7276890984Ox0qWednesdayk28kJunek23:26:45kPMz7276890984Ox0qWednesdayk28kJunek22:25:12kPMz918626093260Ox91qWednesdayk28kJunek22:18:46kPMz/ON
                if (!(message == null)) {
                    if (message.contains("/")) {
                        String[] result = message.split("/");
                        responceType = result[0];
                        responceVal1 = result[1];

                        if (responceType.trim().equals("locreq")) {

                            mGoogleApiClient.connect();

                            liveTrackingStatus = result[3];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal1, liveTrackingStatus));

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new MyFirebaseMessagingService.SendNotificationResponseWithLocation().execute();
                                }
                            });
                            //**  new SendNotificationResponseWithLocation().execute();
                        } else if (responceType.trim().equals("hisreq")) {
                            liveTrackingStatus = result[3];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal1, liveTrackingStatus));

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new MyFirebaseMessagingService.SendNotificationResponseWithCallHistory().execute();
                                }
                            });
                            //** new SendNotificationResponseWithCallHistory().execute();
                        } else if (responceType.trim().equals("voicereq")) {
                            trackeeid = result[2];
                            liveTrackingStatus = result[3];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal1, liveTrackingStatus));

                            apilevel = String.valueOf(Build.VERSION.SDK_INT);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new MyFirebaseMessagingService.SendNotificationResponseWithRecordedVoice().execute();
                                }
                            });
                            //**   new SendNotificationResponseWithRecordedVoice().execute();
                        } else if (responceType.trim().equals("frontimagereq")) {
                            trackeeid = result[2];
                            liveTrackingStatus = result[3];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal1, liveTrackingStatus));
                            List<Tracker> hisflag1 = db.getTrackerDetails(responceVal1);
                            for (Tracker cn : hisflag1) {
                                imgflag = cn.get_imagecapture_flag();
                            }

                            if (imgflag.equals("ON")) {
                                camType = "front";

                                Intent service = new Intent(MyFirebaseMessagingService.this, DemoCamService.class);
                                startService(service);
                            }

                        } else if (responceType.trim().equals("rearimagereq")) {
                            trackeeid = result[2];
                            liveTrackingStatus = result[3];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal1, liveTrackingStatus));
                            List<Tracker> hisflag1 = db.getTrackerDetails(responceVal1);
                            for (Tracker cn : hisflag1) {
                                imgflag = cn.get_imagecapture_flag();
                            }
                            if (imgflag.equals("ON")) {
                                camType = "rear";
                                Intent service = new Intent(MyFirebaseMessagingService.this, DemoCamService.class);
                                startService(service);
                            }
                        } else if (responceType.trim().equals("batreq")) {

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new BatteryStatus().execute("");
                                }
                            });
                            //**   new SendNotificationResponseWithBatteryDetails().execute();
                        }
                        if (responceType.trim().equals("downloadaudiofile")) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new RecordedFileDownloading().execute();
                                }
                            });
                            //**   new RecordedFileDownloading().execute();
                        } else if (responceType.trim().equals("downloadimagefile")) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new CapturedImageFileDownloading().execute();
                                }
                            });
                            //**  new CapturedImageFileDownloading().execute();
                        }

                        if (responceType.trim().equals("stoplivetrackingstatus")) {
                            liveTrackingStatus = responceVal1;
                            responceVal2 = result[2];
                            db.updateLivetrackingStatusTracker(new Tracker(responceVal2, liveTrackingStatus));

                        }
                        // result of LOC req
                        if (responceType.trim().equals("locres")) {
                            responceVal2 = result[2];
                            timestamp = result[3];
                            LocSetting = result[4];
                            from1 = result[5];
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("mapsActivity")) {
                                    Intent i = new Intent(Config.PUSH_NOTIFICATION);
                                    i.putExtra("from", "serviceLoc");
                                    i.putExtra("lat", responceVal1);
                                    i.putExtra("long", responceVal2);
                                    i.putExtra("timestamp", timestamp);
                                    i.putExtra("from1", from1);
                                    i.putExtra("LocSetting", LocSetting);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                                }
                            }
                        } else if (responceType.trim().equals("hisres")) {
                            HisSetting = result[2];


                            if (isForeground(PACKAGE_NAME)) {
                                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                                pushNotification.putExtra("from", "serviceHis");
                                pushNotification.putExtra("his", responceVal1);
                                pushNotification.putExtra("HisSetting", HisSetting);
                                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                            }
                        } else if (responceType.trim().equals("batres")) {
                            // BatterySetting = result[2];
                            batryLevel = result[3];
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("mapsActivity")) {
                                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                                    pushNotification.putExtra("from", "serviceBattery");
                                    pushNotification.putExtra("battery_status", responceVal1);
                                    pushNotification.putExtra("battery_percentage", batryLevel);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                                }
                            }
                        }

                        if (responceType.trim().equals("loclivetracking")) {

                            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                            pushNotification.putExtra("from", "serviceLiveLoc");
                            pushNotification.putExtra("livelat", responceVal1);
                            pushNotification.putExtra("livelong", result[2]);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                        }

                        // trackee added from another tracker
                        if (responceType.trim().equals("newtrackee")) {
                            responceVal2 = result[2];
                            uEmail = result[3];
                            Boolean flag = true;
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            List<Trackee> trackee = db.getAllTrackees();
                            if (trackee.size() == 0) {
                                flag = true;
                            } else {
                                for (Trackee cn : trackee) {
                                    //  mylist.add(cn.get_tracker_Name());
                                    String t = cn.get_trackee_Email();
                                    if (uEmail.equals(cn.get_trackee_Email())) {
                                        flag = false;
                                    }
                                }
                            }
                            if (flag) {
                                db.addTrackee(new Trackee(responceVal1, responceVal2, uEmail, trackeeLocFlag, trackeeHisFlag, trackeeAutoflag, trackeeImageCaptureflag, defaultFlag));
                                db.addRequest(new Request(responceVal1, responceType, responceVal2, uEmail));

                            }
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("drawerActivity")) {
                                    Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Go_To_Fragment", "A");
                                    startActivity(i);
                                } else if (!onActivity.equals("loginActivity")) {
                                    drawerTab = "A";
                                    showNotificationToOpenDrawer(responceVal2, " Added as a Trackee");
                                } else {
                                    showNotificationToOpenLogin(responceVal2, " Added as a Trackee");
                                }
                            } else {
                                showNotificationToOpenLogin(responceVal2, " Added as a Trackee");
                            }
                        }

                        if (responceType.trim().equals("newtracker")) {
                            responceVal2 = result[2];
                            responceVal3 = result[3];
                            db.addRequest(new Request(responceVal1, responceType, responceVal2, responceVal3));

                            if (isForeground(PACKAGE_NAME)) {
                                if (!onActivity.equals("loginActivity")) {
                                    drawerTab = "A";
                                    //CustomNotificationTrackerReqDrawer();
                                    showNotificationToOpenNotificationView(responceVal2, " Requested as a Tracker");
                                } else {
                                    showNotificationToOpenLogin(responceVal2, " Requested as a Tracker");
                                    //CustomNotificationTrackerReqLogin();
                                }
                            } else {
                                showNotificationToOpenLogin(responceVal2, " Requested as a Tracker");
                                //CustomNotificationTrackerReqLogin();
                            }
                        }

                        if (responceType.trim().equals("addtrackeeres")) {
                            responceVal2 = result[2];
                            uEmail = result[4];
                            Boolean flag = false;
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            List<Trackee> trackee = db.getAllTrackees();
                            if (trackee.size() == 0) {
                                flag = true;
                            } else {
                                for (Trackee cn : trackee) {
                                    String t = cn.get_trackee_Email();
                                    if (uEmail.equals(cn.get_trackee_Email())) {
                                        flag = false;
                                    } else {
                                        flag = true;
                                    }
                                }
                            }

                            if (flag) {
                                db.addTrackee(new Trackee(responceVal1, responceVal2, uEmail, trackeeLocFlag, trackeeHisFlag, trackeeAutoflag, trackeeImageCaptureflag, defaultFlag));
                                db.addRequest(new Request(responceVal1, responceType, responceVal2, uEmail));
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("drawerActivity")) {
                                    Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Go_To_Fragment", "A");
                                    startActivity(i);
                                }
                                drawerTab = "A";
                                showNotificationToOpenDrawer(responceVal2, " Added as a Trackee");
                            } else {
                                showNotificationToOpenLogin(responceVal2, " Added as a Trackee");
                            }
                        }

                        if (responceType.trim().equals("locon")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = "ON";
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }

                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Enabled location");
                            } else {
                                SettingNotificationforlogin(" Enabled location");
                            }
                        }
                        if (responceType.trim().equals("locoff")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = "OFF";
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();

                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Disabled location");
                            } else {
                                SettingNotificationforlogin(" Disabled location");
                            }
                        }
                        if (responceType.trim().equals("hison")) {
                            responceVal2 = result[2];
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {

                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = "ON";
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();

                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Enabled call history");
                            } else {
                                SettingNotificationforlogin(" Enabled call history");
                            }
                        }
                        if (responceType.trim().equals("hisoff")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = "OFF";
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Disabled call history");
                            } else {
                                SettingNotificationforlogin(" Disabled call history");
                            }
                        }
                        if (responceType.trim().equals("autoon")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = "ON";
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();

                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Enabled voice recording");
                            } else {
                                SettingNotificationforlogin("Enabled voice recording");
                            }
                        }
                        if (responceType.trim().equals("autooff")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = "OFF";
                                    trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Disabled voice recording");
                            } else {
                                SettingNotificationforlogin(" Disabled voice recording");
                            }
                        }
                        if (responceType.trim().equals("imgon")) {
                            responceVal2 = result[2];
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = "ON";
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification(" Enabled image capture");
                            } else {
                                SettingNotificationforlogin("  Enabled image capture");
                            }
                        }
                        //imgoff/36/30
                        if (responceType.trim().equals("imgoff")) {
                            responceVal2 = result[2];
                            Integer trackeeId;
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                if (cn.get_user_Id().equals(responceVal2)) {
                                    trackee_Id = cn.get_trackee_Id();
                                    trackee_Name = cn.get_trackee_Name();
                                    trackee_Email = cn.get_trackee_Email();
                                    trackee_Loc_Flag = cn.get_trackee_Loc_Flag();
                                    trackee_His_Flag = cn.get_trackee_His_Flag();
                                    trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                                    trackee_ImageCapture_Flag = "OFF";
                                    trackee_App_Status = cn.get_trackee_App_Status();
                                    updateTrackee();
                                    db.addRequest(new Request(responceVal1, responceType, trackee_Name, cn.get_trackee_Email()));
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                SettingNotification("  Disabled image capture");
                            } else {
                                SettingNotificationforlogin("  Disabled image capture");
                            }
                        }

                        if (responceType.trim().equals("trackerdeletedyou")) {
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            List<Tracker> tracker = db.getTrackerDetails(responceVal1);
                            for (Tracker cn : tracker) {
                                trackerUserIdList.add(cn.get_user_Id());
                                uUserId = cn.get_user_Id();
                                uName = cn.get_tracker_Name();
                                uEmail = cn.get_tracker_Email();
                            }
                            db.deleteTracker(responceVal1);
                            showNotificationToOpenLogin("Tracker " + uName, "deleted you");
                            db.addRequest(new Request(responceVal1, responceType, uName, uEmail));
                            if (isForeground(PACKAGE_NAME)) {
                                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("Go_To_Fragment", "A");
                                startActivity(i);
                            }
                        }

                        if (responceType.trim().equals("trackeedeletedyou")) {
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            Trackee trackee = db.getTrackeeDetails(Integer.parseInt(responceVal1));

                            uName = trackee.get_trackee_Name();
                            uEmail = trackee.get_trackee_Email();

                            db.deleteTrackee(responceVal1);
                            showNotificationToOpenLogin("Trackee " + uName, "deleted you");
                            db.addRequest(new Request(responceVal1, responceType, uName, uEmail));
                            if (isForeground(PACKAGE_NAME) && !onActivity.equals("loginActivity")) {

                                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("Go_To_Fragment", "A");
                                startActivity(i);
                            }
                        }

                        if (responceType.trim().equals("userupdatedprofile")) {
                            String Username = null;
                            responceVal2 = result[2];// name
                            uEmail = result[3];
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                trackeeUserIdList.add(cn.get_user_Id());
                            }
                            List<Tracker> tracker = db.getAllTrackers();
                            for (Tracker cn : tracker) {
                                trackerUserIdList.add(cn.get_user_Id());
                            }

                            if (this.trackeeUserIdList.contains(responceVal1)) {
                                int trackeeId;
                                List<Trackee> trackee1 = db.getAllTrackees();
                                for (Trackee cn : trackee1) {
                                    if (cn.get_user_Id().equals(responceVal1)) {
                                        trackeeId = cn.get_trackee_Id();
                                        Username = cn.get_trackee_Name();
                                        defaultFlag = "true";
                                        db.updateTrackee(new Trackee(trackeeId, responceVal1, responceVal2, uEmail, trackeeLocFlag, trackeeHisFlag, trackeeAutoflag, trackeeImageCaptureflag, defaultFlag));
                                    }
                                }
                            }
                            if (this.trackerUserIdList.contains(responceVal1)) {
                                // this code will execute
                                defaultFlag = "true";
                                //    Username=cn.get_trackee_Name();
                                List<Tracker> tracker1 = db.getAllTrackers();
                                for (Tracker cn : tracker1) {
                                    if (cn.get_user_Id().equals(responceVal1)) {
                                        //trackeeId = cn.get_trackee_Id();
                                        Username = cn.get_tracker_Name();
                                        db.updateTracker(new Tracker(responceVal1, responceVal2, uEmail, flag, flag, flag, flag, AddTrackerActivity.liveTrackingFlag, defaultFlag));
                                    }
                                }
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("drawerActivity")) {
                                    Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Go_To_Fragment", "A");
                                    startActivity(i);
                                }
                                drawerTab = "A";
                                showNotificationToOpenDrawer(Username, "  Updated profile");
                            } else {
                                showNotificationToOpenLogin(Username, "  Updated profile");
                            }
                            db.addRequest(new Request(responceVal1, responceType, Username, uEmail));

                        }
                        if (responceType.trim().equals("DeletedAccount")) {
                            responceVal2 = result[2];// name
                            db.addRequest(new Request(responceVal1, responceType, responceVal2, "email"));
                            List<Trackee> trackee = db.getAllTrackees();
                            for (Trackee cn : trackee) {
                                trackeeUserIdList.add(cn.get_user_Id());
                            }
                            List<Tracker> tracker = db.getAllTrackers();
                            for (Tracker cn : tracker) {
                                trackerUserIdList.add(cn.get_user_Id());
                            }
                            if (this.trackeeUserIdList.contains(responceVal1)) {
                                db.deleteTrackee(responceVal1);
                            }
                            if (this.trackerUserIdList.contains(responceVal1)) {
                                db.deleteTracker(responceVal1);
                            }
                            if (isForeground(PACKAGE_NAME)) {
                                if (onActivity.equals("drawerActivity")) {
                                    Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("Go_To_Fragment", "A");
                                    startActivity(i);
                                }
                                drawerTab = "A";
                                showNotificationToOpenDrawer(responceVal2, "  Deleted iCareTracker Account");

                            } else {
                                showNotificationToOpenLogin(responceVal2, "  Deleted iCareTracker Account");
                            }
                        }

                        List<Tracker> tracker = db.getTrackerDetails(responceVal1);
                        for (Tracker cn : tracker) {
                            trackerUserIdList.add(cn.get_user_Id());
                            uUserId = cn.get_user_Id();
                            uName = cn.get_tracker_Name();
                            uEmail = cn.get_tracker_Email();
                            Locflag = cn.get_location_flag();
                            hisflag = cn.get_history_flag();
                            autoflag = cn.get_autocall_flag();
                        }
                    }
                }
            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.e(TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "2 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

    }

    private void handleNotification(String message) {

    }

    public void updateTrackee() {
        db.updateTrackee(new Trackee(trackee_Id, responceVal2, trackee_Name, trackee_Email, trackee_Loc_Flag, trackee_His_Flag, trackee_Auto_Flag, trackee_ImageCapture_Flag, trackee_App_Status));
    }


    // common notification to login
    private void showNotificationToOpenLogin(String title, String responceVal1) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(title);
        builder.setContentText(responceVal1);
        builder.setAutoCancel(true);
        builder.setSound(path);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    //common notification to Drawer
    private void showNotificationToOpenDrawer(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Go_To_Fragment", drawerTab);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(path);
        SharedPreferences.Editor editor = getSharedPreferences("addTrackerData", MODE_PRIVATE).edit();
        editor.putString("trackerid", responceVal1.toString());
        editor.putString("name", responceVal2.toString());
        editor.commit();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void showNotificationToOpenNotificationView(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        Intent intent = new Intent(this, PendingNotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Go_To_Fragment", drawerTab);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(path);
        SharedPreferences.Editor editor = getSharedPreferences("addTrackerData", MODE_PRIVATE).edit();
        editor.putString("trackerid", responceVal1.toString());
        editor.putString("name", responceVal2.toString());
        editor.commit();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    //This method is generating a notification and displaying the notification of privacy setting
    private void SettingNotification(String str) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(trackee_Name);
        builder.setContentText(str);
        builder.setSound(path);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());

    }

    //This method is generating a notification and displaying the notification of privacy setting
    private void SettingNotificationforlogin(String str) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(trackee_Name);
        builder.setContentText(str);
        builder.setSound(path);
        SharedPreferences.Editor editor = getSharedPreferences("addTrackerData", MODE_PRIVATE).edit();
        editor.putString("trackerid", responceVal1.toString());
        editor.putString("name", responceVal2.toString());
        editor.commit();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
        // notificationManager.cancelAll();
    }

    private void showNotificationToShowRecordingORCapturingImageCompleted(String title, String message, String from) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.applogolarge);
        builder.setSound(path);
        Intent intent = new Intent();
        if (isForeground(PACKAGE_NAME)) {
            if (!onActivity.equals("loginActivity")) {
                intent = new Intent(this, MapsActivity.class);
                intent.putExtra("from", from);

            } else {
                intent = new Intent(this, LoginActivity.class);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogolarge));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(false);
        builder.setSound(path);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


    public class SendNotificationResponseWithLocation extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                List<Tracker> Locflag1 = db.getTrackerDetails(responceVal1);
                for (Tracker cn : Locflag1) {
                    Locflag = cn.get_location_flag();
                }
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formattedDate = df.format(c.getTime());
                if (isGPSEnabled) {
                    if(mLocation !=null) {
                        latitude = mLocation.getLatitude();
                        longitude = mLocation.getLongitude();
                    }
                }

                String lat = String.valueOf(latitude);
                String lang = String.valueOf(longitude);
                from = "current";
                if (lat.equals("0.0")) {
                    LocationClass loca = db.getStoredLocation();
                    lat = loca.get_latitude();
                    lang = loca.get_longitude();
                    formattedDate = loca.get_timestamp();
                    Log.d("Updated LocationClass", "location not available");
                    from = "device";
                }
                if (Locflag != null && !Locflag.isEmpty()) {
                    if (Locflag.equals("OFF")) {
                        LocSetting = "OFF";
                    } else if (Locflag.equals("ON")) {
                        LocSetting = "ON";
                    }
                }

                LocationClass loc = db.getStoredLocation();
                if (loc == null) {
                    db.addlocation(new LocationClass(locid, lat, lang, formattedDate));
                } else {
                    db.updateLocation(new LocationClass(locid, lat, lang, formattedDate));
                }

                formattedDate1 = formattedDate.replaceAll("\\s+", "S");

                String url = baseUrl1 + "notificationwithlocation/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", responceVal1);
                jsonObject.accumulate("TrackeeId", userid);
                jsonObject.accumulate("Lattitude", lat);
                jsonObject.accumulate("Longitude", lang);
                jsonObject.accumulate("Time", formattedDate1);
                jsonObject.accumulate("OnOffSts", LocSetting);
                jsonObject.accumulate("LocFrom", from);

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    //  JSONObject mainObject = new JSONObject(result);
                    //   responceStatus = mainObject.getString("Status");
                } else
                    result = "Did not work!";

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.toString());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "3 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        protected String doInBackground(String... urls) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!isMyServiceRunning(LocationUpdateService.class)) {
                if (isGPSEnabled) {
                    Intent i = new Intent(MyFirebaseMessagingService.this, LocationUpdateService.class);
                    startService(i);
                }
            }
        }
    }

    class SendNotificationResponseWithCallHistory extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                List<Tracker> autoflag1 = db.getTrackerDetails(responceVal1);
                for (Tracker cn : autoflag1) {
                    autoflag = cn.get_location_flag();
                }
                List<Tracker> hisflag1 = db.getTrackerDetails(responceVal1);
                for (Tracker cn : hisflag1) {
                    hisflag = cn.get_history_flag();
                }
                if (hisflag != null && !hisflag.isEmpty()) {
                    if (hisflag.equals("OFF")) {
                        HisSetting = "OFF";
                    } else if (hisflag.equals("ON")) {
                        HisSetting = "ON";
                    }
                }

                String url = baseUrl1 + "NotificationWithCallHistory/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", responceVal1);
                jsonObject.accumulate("CallHistory", callHistory);
                jsonObject.accumulate("OnOffSts", HisSetting);


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                //{"TrackerId":"1302","CallHistory":"917972286530Mx0qThursdayk29kJunek11:56:45kAMz917972286530Mx0qThursdayk29kJunek11:07:20kAMz19012676031Ix42qThursdayk29kJunek10:43:45kAMz7276890984Ox239qThursdayk29kJunek09:13:37kAMz917276890984Mx0qThursdayk29kJunek09:13:17kAMz61419941117Ix114qThursdayk29kJunek09:11:10kAMz61419941117Ix0qThursdayk29kJunek09:10:34kAMz7276890984Ox0qWednesdayk28kJunek23:26:45kPMz7276890984Ox0qWednesdayk28kJunek22:25:12kPMz918626093260Ox91qWednesdayk28kJunek22:18:46kPMz","OnOffSts":"ON"}
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.toString());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "4 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        protected String doInBackground(String... urls) {
            return null;
        }
    }

    class SendNotificationResponseWithRecordedVoice extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            List<Tracker> hisflag1 = db.getTrackerDetails(responceVal1);
            for (Tracker cn : hisflag1) {
                autoflag = cn.get_autocall_flag();
            }

            if (autoflag.equals("ON")) {
                startRecording();
                long delayInMillis = 10000;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopRecording();
                        if (Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        new SendRecordedFileToServer().execute();

                    }
                }, delayInMillis);
            }
        }

        protected String doInBackground(String... urls) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    public class SendNotificationResponseWithBatteryDetails extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                //new BatteryStatus().execute("");
                String url = baseUrl1 + "NotificationWithBatteryDetails/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", responceVal1);
                jsonObject.accumulate("ChargingStatus", status);
                jsonObject.accumulate("OnOffSts", "ON");
                jsonObject.accumulate("BatteryPer", level);


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                // Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "5 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        protected String doInBackground(String... urls) {

            return null;
        }

    }

    public boolean checkGPSService() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLogforService(getApplicationContext(), "6 MyFirebaseMessagingService " + e.toString() + date);
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLogforService(getApplicationContext(), "7 MyFirebaseMessagingService " + e.toString() + date);
        }

        if (!gps_enabled || !network_enabled) {
            return false;
        } else
            return true;

    }

    private String getCallDetails() {
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        if (managedCursor.getCount() > 0) {
            int limit = 10;
            if (managedCursor.getCount() <= 10) {
                limit = managedCursor.getCount();
            }
            for (int i = 0; i <= limit - 1; i++) {
                managedCursor.moveToNext();
                String phNumber = managedCursor.getString(number);
                String callType1 = managedCursor.getString(type);
                String callDate1 = managedCursor.getString(date);
                Date Date = new Date(Long.valueOf(callDate1));
                String callDuration1 = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType1);

                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        // o for out going and S Split
                        dir = "O";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "I";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "M";
                        break;
                    default:
                        dir = "null";
                        break;
                }
                SimpleDateFormat formatter1 = new SimpleDateFormat("EEEE d MMMM HH:mm:ss aaa");
                String tDate2 = formatter1.format(Date);

                if(dir.equals("null")) {

                }else {
                    tDate2 = tDate2.replaceAll("\\s+", "k");
                    if (phNumber != null) {
                        sbCallHistry.append(phNumber + dir + "x" + callDuration1 + "q" + tDate2 + "z");
                    }
                }
            }
        } else {
            sbCallHistry.append("0");
        }
        hisList.add(numbers);
        hisList.add(calltype);
        hisList.add(callDayTime);
        hisList.add(callduration);

        managedCursor.close();

        String str = sbCallHistry.toString();
        str = str.replaceAll("[|?*<\">+\\[\\]/']", "");
        return str;
    }

    // code for getting battery status
    public class BatteryStatus extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            new SendNotificationResponseWithBatteryDetails().execute();

        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            try {

                level = getBatteryPercentage(getApplicationContext());

                if (isPhonePluggedIn(getApplicationContext()).compareToIgnoreCase("yes") == 0) {
                    status = "yes";// true (Charging)
                } else {
                    status = "no"; // False
                }
            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.i("exception", e.toString());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "11 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public static String isPhonePluggedIn(Context context) {
        boolean charging = false;
        String result = "No";
        try {
            final Intent batteryIntent = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;

            int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            if (batteryCharge)
                charging = true;
            if (usbCharge)
                charging = true;
            if (acCharge)
                charging = true;

            if (charging) {
                result = "Yes";
            }
        } catch (Exception e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Log.i("ACTION_BATTERY_CHANGED", e.toString());
            e.printStackTrace();
            appendLogforService(context, "8 MyFirebaseMessagingService " + e.toString() + date);
        }
        return result;
    }

    // send record file to server
    class SendRecordedFileToServer extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    "AudioRecorder/" + audiofilename + ".mp3");
            String path = file.toString();

            uploadFile(path);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            deleterecording();
        }

        protected String doInBackground(String... urls) {
            return null;
        }
    }

    public static void SendImageFileToServer() {
        new SendCapturedImageFileToServer().execute();
    }

    public static class SendCapturedImageFileToServer extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            deleteCapturedImage();
        }

        protected String doInBackground(String... urls) {
            try {
                capturedImageName = CameraPreview.imageFileName;
                // capturedImageName = capturedImageName.replace(".jpeg", "");
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "AudioRecorder/" + capturedImageName);

                String path = file.toString();

                uploadFile(path);

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                e.printStackTrace();
                appendLogforService(context, "12 MyFirebaseMessagingService " + e.toString() + date);
            }
            return null;
        }
    }


    class RecordedFileDownloading extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    "AudioRecorder/AudioFile.mp3");
            String path = file.toString();*/
            //String url = "http://202.88.154.118/FileUploadWCF/FileService.svc/DownloadFile/"+message;
            // String AudiofileName = responceVal1 + ".mp3"; //song name that will be stored in your device in case of song

            String url = baseUrlDownloadFile + responceVal1;
            // url = url.replaceAll(" ", "%20");
            boolean download = true;

            StoragePath = getStoragePath();
            File directory = new File(StoragePath, "/ICareTracker/"); //create directory to keep your downloaded file
            if (!directory.exists()) {
                directory.mkdir();
            }
            File directory1 = new File(directory, "/AudioFiles/");
            if (!directory1.exists()) {
                directory1.mkdir();
            }
            try {
                InputStream input = null;
                try {

                    URL url1 = new URL(url); // link of the song which you want to download like (http://...)
                    input = url1.openStream();
                    OutputStream output = new FileOutputStream(new File(directory1, responceVal1));
                    download = true;
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);
                            download = true;
                        }
                        output.close();
                    } catch (Exception e) {

                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        //MyUtility.showLog("output exception in catch....."+ exception + "");
                        download = false;
                        output.close();
                        e.printStackTrace();
                        appendLogforService(getApplicationContext(), "13 MyFirebaseMessagingService " + e.toString() + date);
                    }
                } catch (Exception e) {

                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    //MyUtility.showLog("input exception in catch....."+ exception + "");
                    download = false;
                    //progressDialog.dismiss();
                    e.printStackTrace();
                    appendLogforService(getApplicationContext(), "14 MyFirebaseMessagingService " + e.toString() + date);
                } finally {
                    input.close();
                }
            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                download = false;
                //progressDialog.dismiss();
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "15 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            deletefilefrom = "audiofolder";
            new DeleteFileOncedownloaded().execute();
        }

        protected String doInBackground(String... urls) {

            return null;
        }
    }


    public String getStoragePath() {
        //  String storageFlag;
        String StoragePath = null;
        User user = db.getUserDetails();


        StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return StoragePath;
    }

    public class CapturedImageFileDownloading extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //String AudiofileName = responceVal1 + ".jpeg";
            String url = baseUrlDownloadFile + responceVal1;

            // url = url.replaceAll(" ", "%20");
            boolean download = true;

            StoragePath = getStoragePath();
            File directory = new File(StoragePath, "/ICareTracker/"); //create directory to keep your downloaded file
            if (!directory.exists()) {
                directory.mkdir();
            }
            File directory1 = new File(directory, "/ImageFiles/");
            if (!directory1.exists()) {
                directory1.mkdir();
            }
            try {
                InputStream input = null;
                try {

                    URL url1 = new URL(url); // link of the song which you want to download like (http://...)
                    input = url1.openStream();
                    OutputStream output = new FileOutputStream(new File(directory1, responceVal1));
                    download = true;
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);
                            download = true;
                        }
                        output.close();
                    } catch (Exception e) {

                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        download = false;
                        e.printStackTrace();
                        appendLogforService(getApplicationContext(), "16 MyFirebaseMessagingService " + e.toString() + date);
                        output.close();
                    }
                } catch (Exception e) {

                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    download = false;
                    e.printStackTrace();
                    appendLogforService(getApplicationContext(), "17 MyFirebaseMessagingService " + e.toString() + date);
                } finally {
                    input.close();
                }
            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                download = false;
                //progressDialog.dismiss();
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "18 MyFirebaseMessagingService " + e.toString() + date);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            deletefilefrom = "imagefolder";
            new DeleteFileOncedownloaded().execute();

        }

        protected String doInBackground(String... urls) {

            return null;
        }
    }

    class DeleteFileOncedownloaded extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            try {
                String url = baseUrl1 + "DeleteFile/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("fileName", responceVal1);

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "19 MyFirebaseMessagingService " + e.toString() + "  " + date);
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                if (deletefilefrom.equals("audiofolder")) {
                    if (isForeground(PACKAGE_NAME)) {
                        if (onActivity.equals("mapsActivity")) {
                            Intent i = new Intent(Config.PUSH_NOTIFICATION);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("from", "serviceAuto");
                            i.putExtra("audioFileName", responceVal1);
                            i.putExtra("storagepath", StoragePath);
                            i.putExtra("long", responceVal2);
                            i.putExtra("timestamp", timestamp);
                            i.putExtra("from1", from1);
                            LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(i);

                        }
                        showNotificationToShowRecordingORCapturingImageCompleted("Recording", "Recording completed.", "serviceAuto");
                    } else {
                        showNotificationToOpenLogin(responceVal2, "Recording completed.");
                    }
                } else {
                    if (isForeground(PACKAGE_NAME)) {
                        if (onActivity.equals("mapsActivity")) {
                            Intent i = new Intent(Config.PUSH_NOTIFICATION);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("from", "serviceImage");
                            i.putExtra("imageFileName", responceVal1);
                            i.putExtra("storagepath", StoragePath);
                            i.putExtra("long", responceVal2);
                            i.putExtra("timestamp", timestamp);
                            i.putExtra("from1", from1);
                            LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(i);


                        }
                        showNotificationToShowRecordingORCapturingImageCompleted("Capturing image", "Image capture is complete.", "serviceImage");
                    } else {
                        showNotificationToOpenLogin(responceVal2, "Image capture is complete.");
                    }
                }
            } else if (responceStatus.equals("fail")) {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
            } else if (responceStatus.equals("useruninstalledapp")) {

            }
        }
    }

    // code for start audio recording in background
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLogforService(getApplicationContext(), "20 MyFirebaseMessagingService " + e.toString() + date);
        } catch (IOException e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLogforService(getApplicationContext(), "21 MyFirebaseMessagingService " + e.toString() + date);
        }
    }

    private String getFilename() {
        filepath = Environment.getExternalStorageDirectory().getPath();
        audiofile = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!audiofile.exists()) {
            audiofile.mkdirs();
        }
        StringBuffer sb = new StringBuffer("audio");
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
        formattedDate = formattedDate.replace(":", ".");
        formattedDate = formattedDate.replace(" ", "x");
        audiofilename = String.valueOf(sb.append("x" + userid + "x" + formattedDate + "x" + responceVal1));//
        return (audiofile.getAbsolutePath() + "/" + audiofilename + file_exts[currentFormat]);

    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.d("Error", "Error");
        }
    };
    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.d("Error", "Error");
        }
    };


    private void stopRecording() {
        try {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLogforService(getApplicationContext(), "22 MyFirebaseMessagingService " + e.toString() + date);
        }
    }


    public void deleterecording() {
        recordingfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecorder/" + audiofilename);
        // File recordingfile = new File(filepath, AUDIO_RECORDER_FOLDER);
        boolean deleted = recordingfile.delete();
    }

    public static void deleteCapturedImage() {
        capturedfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecorder/" + capturedImageName);
        boolean deleted = capturedfile.delete();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static int uploadFile(String selectedFilePath) {
        //selectedFilePath = "E:\\debit.png";
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);
        String[] parts = selectedFilePath.split("/");
        String fileName = parts[parts.length - 1];
        long length = selectedFile.length();
        length = length / 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            URL url = new URL(baseUrlUploadFile);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//Allow Inputs
            connection.setDoOutput(true);//Allow Outputs
            connection.setUseCaches(false);//Don't use a cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("UploadedImage", selectedFilePath);
            //connection.setRequestProperty("TrackerId", responceVal1);
            //creating new dataoutputstream
            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            //writing bytes to data outputstream
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            // fileName = fileName.replaceAll(" ", "x");
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"UploadedImage\";filename=\""
                    + fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            //returns no. of bytes present in fileInputStream
            bytesAvailable = fileInputStream.available();
            //selecting the buffer size as minimum of available bytes or 1 MB
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //setting the buffer as byte array of size of bufferSize
            buffer = new byte[bufferSize];
            //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            //loop repeats till bytesRead = -1, i.e., no bytes are left to read
            while (bytesRead > 0) {
                try {
                    dataOutputStream.write(buffer, 0, bufferSize);
                } catch (OutOfMemoryError e) {

                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    // Toast.makeText(myssplcontext, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    appendLogforService(context, "23 MyFirebaseMessagingService " + e.toString() + date);
                }
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            try {
                serverResponseCode = connection.getResponseCode();
            } catch (OutOfMemoryError e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                // Toast.makeText(MyFirebaseMessagingService.this, "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                appendLogforService(context, "24 MyFirebaseMessagingService " + e.toString() + date);
            }

            String serverResponseMessage = connection.getResponseMessage();

            InputStream is = connection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            is.close();
            try {
                JSONObject job = new JSONObject(responseStrBuilder.toString());

            } catch (JSONException e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                //e.printStackTrace();
                e.printStackTrace();
                appendLogforService(context, "25 MyFirebaseMessagingService " + e.toString() + date);
            }
        } catch (FileNotFoundException e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            // e.printStackTrace();
            e.printStackTrace();
            appendLogforService(context, "26 MyFirebaseMessagingService " + e.toString() + date);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Log.i("exception", "File Size Is Too Large");
                    // Toast.makeText(MyFirebaseMessagingService.this, "File Size Is Too Large", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MalformedURLException e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Log.i("exception", "URL Error!");
                    //  Toast.makeText(MyFirebaseMessagingService.this, "URL Error!", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
            appendLogforService(context, "27 MyFirebaseMessagingService " + e.toString() + date);
        } catch (IOException e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Log.i("exception", "Cannot Read/Write File");
                    // Toast.makeText(MyFirebaseMessagingService.this, "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
            appendLogforService(context, "28 MyFirebaseMessagingService " + e.toString() + date);
        }
        //dialog.dismiss();
        return serverResponseCode;
        //}
    }


    class SendNotificationofSetting extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {

            try {

                String url = baseUrl1 + "NotificationForSettings/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("OnOffKeyword", settingkeyword);
                jsonObject.accumulate("TrackerId", uUserId);
                jsonObject.accumulate("TrackeeName", userid);

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);
                se.setContentType("application/json");
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    // result = result.replaceAll("\","");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";

            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLogforService(getApplicationContext(), "29 MyFirebaseMessagingService " + "  " + settingkeyword + "  " + uUserId + "  " + userid + "  " + e.toString() + "  " + date);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (responceStatus != null) {
                if (responceStatus.equals("success")) {

                } else if (responceStatus.equals("fail")) {

                } else if (responceStatus.equals("useruninstalledapp")) {

                }
            }
        }
    }

    public long printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long min = TimeUnit.MILLISECONDS.toMinutes(different);

        return min;

    }

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }
}



package sveltoz.icaretrackerapp.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import sveltoz.icaretrackerapp.Adapter.AutoRecordAdapter;
import sveltoz.icaretrackerapp.Adapter.CaptureImageAdapter;
import sveltoz.icaretrackerapp.Adapter.HistoryAdapter;
import sveltoz.icaretrackerapp.App.Config;
import sveltoz.icaretrackerapp.DBClasses.AudioItems;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.LocationClass;
import sveltoz.icaretrackerapp.DBClasses.Trackee;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.DBClasses.historyItems;
import sveltoz.icaretrackerapp.R;
import sveltoz.icaretrackerapp.Util.NotificationUtils;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.checkGPSService;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.showAlertDialog;


public class MapsActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, OnMapReadyCallback, DirectionCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    TextView txtBatryStatus, txtBatryPer, setLocation;//tvlevel,tvAddress,tvTimestamp
    Button btnLocation, btnHistory, btnAutoCall, btnCaptureImage, flotingFront, flotingBack;
    RelativeLayout hisLayout, autoCalllayout, mapLayout, imageLayout;
    LinearLayout btnLayout;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    static double ulatitude, ulongitude;
    static Double latitude, longitude;
    public double oldlatitude, oldlongitude;
    LatLng oldLatLng;
    private Menu menu;
    ListView lvHis;
    String from, from1;

    // user details from dbF
    String userid;

    // trackee Details
    String trackeeUserID, trackeeName;
    ArrayList<historyItems> historyList;

    public ProgressDialog progress;
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public String Keyword;
    String history, autoRes;
    String trackerUserID, chargingStatus, batryLevel, dbfrom, timestamp, mobileNumber, apiLevel, responceStatus, adminPermission, address;
    int trackee_Id;
    DatabaseHandler db = new DatabaseHandler(this);

    ArrayList<HashMap<String, String>> resultList;
    String LocSetting, HisSetting, AutocallSetting, BatterySetting;
    String result = null;
    public HistoryAdapter itemAdapter;
    String locFlag, hisFlag, autoFlag, imgFlag, liveTrackingFlag;
    public static AutoRecordAdapter autoItemAdapter;
    public static CaptureImageAdapter imageItemAdapter;
    Boolean progressFlag = true;
    AudioItems item1, item2;

    String audiofileName, timerstatus, imageFileName, StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    ArrayList<AudioItems> lstArrAuto = new ArrayList<AudioItems>();
    ArrayList<AudioItems> lstArrImage = new ArrayList<AudioItems>();
    ArrayList<AudioItems> SequencedlstArrAutoImage = new ArrayList<AudioItems>();
    ListView lstAuto;
    GridView lstImage;
    FloatingActionButton btnFloatingAudio;
    // MyTimerTask myTimerTask;
    Timer timer1 = new Timer();
    Timer timer = new Timer();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Marker livemarker,currentmarker;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false, imageflag = true;
    ArrayList<AudioItems> SequencedlstArrAuto = new ArrayList<AudioItems>();
    long megAvailable;
    public Button btnRequestDirection;
    private String serverKey = "AIzaSyBJxPDUBRCeLUkEE0UrLV_jhQm56EDhB5o";// "AIzaSyCRk2RrpcRp09ZGrUOH4tGu77_Ly2N6m60";;
    private LatLng origin;// = new LatLng(13.7371063, 100.5642539);
    private LatLng destination;// = new LatLng(13.7604896, 100.5594266);
    LatLng startLatLng;
    String trackee_Name, trackee_Email, trackee_Loc_Flag, trackee_His_Flag, trackee_Auto_Flag, trackee_ImageCapture_Flag, trackee_App_Status;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    Boolean reqDirFlag = false;
    boolean MenuOption;
    InputStream inputStream = null;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LoginActivity.onActivity = "mapsActivity";
        this.menu = menu;
        lvHis = (ListView) findViewById(R.id.lvHistory);
        lstAuto = (ListView) findViewById(R.id.lstAuto);
        registerForContextMenu(lstAuto);

        lstImage = (GridView) findViewById(R.id.lstImage);
        registerForContextMenu(lstImage);

        btnLocation = (Button) findViewById(R.id.buttonLoc);
        btnHistory = (Button) findViewById(R.id.buttonHis);
        btnAutoCall = (Button) findViewById(R.id.buttonAuto);
        btnCaptureImage = (Button) findViewById(R.id.buttonImage);
        btnFloatingAudio = (FloatingActionButton) findViewById(R.id.fabAudio);

        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        hisLayout = (RelativeLayout) findViewById(R.id.historyLayout);
        autoCalllayout = (RelativeLayout) findViewById(R.id.autoCallLayout);
        imageLayout = (RelativeLayout) findViewById(R.id.captureImageLayout);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);

        txtBatryStatus = (TextView) findViewById(R.id.txtChargingStatus);
        txtBatryPer = (TextView) findViewById(R.id.txtChargingPer);
        setLocation = (TextView) findViewById(R.id.tvadress);
        setLocation.setVisibility(View.INVISIBLE);
        flotingFront = (Button) findViewById(R.id.fabFront);
        flotingBack = (Button) findViewById(R.id.fabBack);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        flotingFront.setOnClickListener(this);
        flotingBack.setOnClickListener(this);
        btnRequestDirection = (Button) findViewById(R.id.btn_request_direction);
        btnRequestDirection.setOnClickListener(this);
        progress = new ProgressDialog(MapsActivity.this);

        if (checkGPSService(MapsActivity.this)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else {
            displayLocationSettingsRequest(getApplicationContext());
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    try {
                        //  String message = intent.getStringExtra("message");
                        from = intent.getStringExtra("from");
                        if (from.equals("serviceLoc")) {
                            progressFlag = false;
                            if ((progress != null) && progress.isShowing())
                                progress.dismiss();
                            timestamp = intent.getStringExtra("timestamp");
                            dbfrom = intent.getStringExtra("from1");
                            LocSetting = intent.getStringExtra("LocSetting");
                            mapLayout.setVisibility(View.VISIBLE);
                            btnLayout.setVisibility(View.VISIBLE);

                            if (timestamp.contains("locationnotreceived")) {
                                showAlertDialog(MapsActivity.this, "Oops!", "Location not received");
                            } else {
                                String tempLat = intent.getStringExtra("lat");
                                if (tempLat != null) {
                                    latitude = Double.parseDouble(intent.getStringExtra("lat"));
                                    longitude = Double.parseDouble(intent.getStringExtra("long"));

                                    address = getCompleteAddressString(latitude, longitude);

                                    if (startLatLng == null) {
                                        startLatLng = new LatLng(latitude, longitude);
                                        origin = startLatLng;
                                        destination = startLatLng;
                                    }

                                    if (latitude != 0.0 || longitude != 0.0) {
                                        if (LocSetting.equals("OFF")) {
                                            showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to access location");
                                        } else {
                                            timestamp = timestamp.replaceAll("[S]", " ");

                                            if (timestamp != null && !timestamp.isEmpty()) {

                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


                                                String tDate2 = getCurrentTimeStamp();//format.format(Date.parse(currentDateTimeString));
                                                try {
                                                    Date newsDate = format.parse(timestamp);
                                                    Date currentDate = format.parse(tDate2);
                                                    timestamp = printDifference(newsDate, currentDate);

                                                } catch (ParseException e) {
                                                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                                    String date = df.format(Calendar.getInstance().getTime());
                                                    e.printStackTrace();
                                                    appendLog(MapsActivity.this, "1.1 MapsActivity " + e.toString() + date);
                                                }
                                            } else {
                                                timestamp = "";
                                            }


                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));

                                            if (dbfrom.equals("current")) {
                                                //updateMarker();
                                                setUpMap();
                                            }
                                            if (dbfrom.equals("device")) {
                                                showAlertDialog(MapsActivity.this, "Location!", "Last location detected");
                                                setUpMap();
                                            }
                                            if (dbfrom.equals("server")) {
                                                showAlertDialog(MapsActivity.this, "Location!", "Last location detected");
                                                setUpMap();
                                            }
                                            btnLocation.setBackgroundResource(R.drawable.selectedbutton);
                                            btnHistory.setBackgroundResource(R.drawable.mybutton);
                                            btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                                            btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
                                        }
                                    }
                                }
                            }
                        }
                        if (from.equals("serviceHis")) {
                            progressFlag = false;
                            history = intent.getStringExtra("his");
                            HisSetting = intent.getStringExtra("HisSetting");

                            mapLayout.setVisibility(View.INVISIBLE);
                            hisLayout.setVisibility(View.VISIBLE);
                            autoCalllayout.setVisibility(View.INVISIBLE);
                            autoCalllayout.setVisibility(View.INVISIBLE);
                            imageLayout.setVisibility(View.INVISIBLE);
                            btnLayout.setVisibility(View.VISIBLE);
                            if (history.equals("0")) {

                                showAlertDialog(MapsActivity.this, "Oops!", "Trackee cleared his call history!");
                            } else {
                                if (HisSetting.equals("OFF")) {
                                    showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to access call history!");
                                } else {
                                    if ((progress != null) && progress.isShowing())
                                        progress.dismiss();

                                    List<String> hisList = Arrays.asList(history.split("z"));
                                    historyList = new ArrayList<historyItems>();
                                    for (int hisItem = 0; hisItem <= hisList.size() - 1; hisItem++) {
                                        String t = hisList.get(hisItem);
                                        List<String> numTimeList = Arrays.asList(hisList.get(hisItem).split("x"));
                                        String num, cType, cDetails;
                                        num = numTimeList.get(0);
                                        cDetails = numTimeList.get(1);
                                        List<String> durationTimeList = Arrays.asList(cDetails.split("q"));
                                        String callTime, callDuration;

                                        callDuration = durationTimeList.get(0);
                                        callTime = durationTimeList.get(1);
                                        callTime = callTime.replaceAll("[k]", " ");

                                        callDuration = getDurationString(Integer.parseInt(callDuration));

                                        List<String> duration = Arrays.asList(callDuration.split(":"));
                                        int hrs, min, sec;
                                        hrs = Integer.parseInt(duration.get(0).trim());
                                        min = Integer.parseInt(duration.get(1).trim());
                                        sec = Integer.parseInt(duration.get(2).trim());
                                        StringBuilder SBDuration = new StringBuilder(100);
                                        if (hrs > 0) {
                                            SBDuration.append(hrs + " hrs " + min + " mins " + sec + " secs");
                                        } else if (min > 0) {
                                            SBDuration.append(min + " mins " + sec + " secs");
                                        } else if (sec >= 0) {
                                            SBDuration.append(sec + " secs");
                                        }

                                        char temp = num.charAt(num.length() - 1);
                                        num = num.substring(0, num.length() - 1);
                                        if (temp == 'O') {
                                            cType = "Outgoing";
                                        } else if (temp == 'I') {
                                            cType = "Incoming";
                                        } else if (temp == 'M') {
                                            cType = "Missed";
                                        } else {
                                            cType = "Other";
                                        }
                                        historyItems item1 = new historyItems(num, cType, SBDuration.toString(), callTime);
                                        historyList.add(item1);
                                    }
                                    if (historyList != null && !historyList.isEmpty()) {
                                        itemAdapter = new HistoryAdapter(MapsActivity.this,
                                                R.layout.his_list_row, historyList);
                                        lvHis.setAdapter(itemAdapter);
                                    }
                                    btnLocation.setBackgroundResource(R.drawable.mybutton);
                                    btnHistory.setBackgroundResource(R.drawable.selectedbutton);
                                    btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                                    btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
                                }
                            }
                        }

                        if (from.equals("serviceAuto")) {
                            mapLayout.setVisibility(View.INVISIBLE);
                            hisLayout.setVisibility(View.INVISIBLE);
                            autoCalllayout.setVisibility(View.VISIBLE);
                            imageLayout.setVisibility(View.INVISIBLE);
                            btnLayout.setVisibility(View.VISIBLE);
                            autoRes = intent.getStringExtra("res");
                            AutocallSetting = intent.getStringExtra("AutocallSetting");
                            audiofileName = intent.getStringExtra("audioFileName");
                            MenuOption = true;
                            // audiofileName = audiofileName + ".mp3";
                            StoragePath = intent.getStringExtra("storagepath");
                            String fileSplit[] = audiofileName.split("x");
                            // trackeeUserID = fileSplit[0];

                            File file = new File(StoragePath + "/ICareTracker/AudioFiles");
                            File list[] = file.listFiles();
                            lstArrAuto.clear();
                            for (int i = 0; i < list.length; i++) {
                                String filename = list[i].getName();
                                String[] fileSplit1 = filename.split("x");
                                String trackeeUserId1 = fileSplit1[0];
                                if (trackeeUserId1.equals(trackeeUserID) && list[i].getName().contains("mp3")) {
                                    item1 = new AudioItems(list[i].getName());
                                    lstArrAuto.add(item1);
                                } else {

                                }
                            }
                            SequencedlstArrAuto.clear();
                            for (int j = lstArrAuto.size() - 1; j >= 0; j--) {
                                String name = lstArrAuto.get(j).get_audio_name();
                                item2 = new AudioItems(name);
                                SequencedlstArrAuto.add(item2);
                            }

                            lstAuto.setAdapter(autoItemAdapter);
                            autoItemAdapter.notifyDataSetChanged();
                            btnAutoCall.setClickable(true);

                            btnLocation.setBackgroundResource(R.drawable.mybutton);
                            btnHistory.setBackgroundResource(R.drawable.mybutton);
                            btnAutoCall.setBackgroundResource(R.drawable.selectedbutton);
                            btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
                        }

                        if (from.equals("serviceImage")) {
                            imageflag = false;
                            mapLayout.setVisibility(View.INVISIBLE);
                            hisLayout.setVisibility(View.INVISIBLE);
                            autoCalllayout.setVisibility(View.INVISIBLE);
                            imageLayout.setVisibility(View.VISIBLE);
                            btnLayout.setVisibility(View.VISIBLE);
                            autoRes = intent.getStringExtra("res");
                            mobileNumber = intent.getStringExtra("mobileNumber");
                            apiLevel = intent.getStringExtra("apiLevel");
                            AutocallSetting = intent.getStringExtra("AutocallSetting");
                            StoragePath = intent.getStringExtra("storagepath");
                            imageFileName = intent.getStringExtra("imageFileName");
                            MenuOption = false;
                            //imageFileName = imageFileName;

                            String fileSplit[] = imageFileName.split("x");
                            // trackeeUserID = fileSplit[0];

                            File file = new File(StoragePath + "/ICareTracker/ImageFiles");

                            File list[] = file.listFiles();

                            lstArrImage.clear();
                            for (int i = 0; i < list.length; i++) {
                                String filename = list[i].getName();
                                String[] fileSplit2 = filename.split("x");
                                String trackeeUserId1 = fileSplit2[0];
                                if (trackeeUserId1.equals(trackeeUserID) && list[i].getName().contains("jpeg")) {
                                    item1 = new AudioItems(list[i].getName());
                                    lstArrImage.add(item1);
                                } else {

                                }
                            }
                            SequencedlstArrAutoImage.clear();
                            for (int j = lstArrImage.size() - 1; j >= 0; j--) {
                                String name = lstArrImage.get(j).get_audio_name();
                                item2 = new AudioItems(name);
                                SequencedlstArrAutoImage.add(item2);
                            }

                            lstImage.setAdapter(imageItemAdapter);
                            imageItemAdapter.notifyDataSetChanged();

                            btnLocation.setBackgroundResource(R.drawable.mybutton);
                            btnHistory.setBackgroundResource(R.drawable.mybutton);
                            btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                            btnCaptureImage.setBackgroundResource(R.drawable.selectedbutton);

                        }

                        if (from.equals("serviceBattery")) {

                            chargingStatus = intent.getStringExtra("battery_status");
                            batryLevel = intent.getStringExtra("battery_percentage");

                            MenuItem cPer = menu.findItem(R.id.action_charging_per);
                            MenuItem cState = menu.findItem(R.id.action_charging_state);
                            if (chargingStatus != null && !batryLevel.equals("0")) {
                                cPer.setTitle(batryLevel + "%");
                            } else {
                                cPer.setEnabled(false).setVisible(false);
                            }
                            if (chargingStatus != null && chargingStatus.equals("yes")) {
                                cState.setIcon(getResources().getDrawable(R.mipmap.ic_battery_charging_full_black_24dp));
                            } else {
                                cState.setIcon(getResources().getDrawable(R.mipmap.ic_battery_full_black_24dp));
                            }
                        }

                        if (from.equals("serviceLiveLoc")) {
                            String tempLat = intent.getStringExtra("livelat").toString();
                            if (tempLat != null) {

                                if (latitude != null) {

                                    latitude = Double.parseDouble(intent.getStringExtra("livelat"));
                                    longitude = Double.parseDouble(intent.getStringExtra("livelong"));


                                    address = getCompleteAddressString(latitude, longitude);


                                    new updateLiveTrackingMarker().execute();

                                }
                            }
                        }
                    } catch (Exception e) {
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        e.printStackTrace();
                        appendLog(MapsActivity.this, "1 MapsActivity " + e.toString() + date);
                    }
                }

            }
        };


        SharedPreferences prefs = getSharedPreferences("userDetails", MODE_PRIVATE);
        trackeeUserID = prefs.getString("selectedUserId", "No trackeeName defined");//"No trackeeName defined" is the default value.
        trackeeName = prefs.getString("name", "No trackeeName defined");

        trackeeName = StringUtils.capitalize(trackeeName);
        Trackee trackee = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
        if (trackee != null) {
            locFlag = trackee.get_trackee_Loc_Flag();
            hisFlag = trackee.get_trackee_His_Flag();
            autoFlag = trackee.get_trackee_Auto_Flag();
            autoFlag = trackee.get_trackee_ImageCapture_Flag();
        }

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle(trackeeName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme)));
        // status bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.Teal));
        }


        Bundle extra = getIntent().getExtras();
        from = extra.getString("from");
        User user1 = db.getUserDetails();
        userid = String.valueOf(user1.get_user_id());
        db.close();


        if (from.equals("serviceImage")) {
            imageflag = false;
            mapLayout.setVisibility(View.INVISIBLE);
            hisLayout.setVisibility(View.INVISIBLE);
            autoCalllayout.setVisibility(View.INVISIBLE);
            imageLayout.setVisibility(View.VISIBLE);
            btnLayout.setVisibility(View.VISIBLE);
            MenuOption = false;
            // StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(StoragePath + "/ICareTracker/ImageFiles");

            File list[] = file.listFiles();

            lstArrImage.clear();
            for (int i = 0; i < list.length; i++) {
                String filename = list[i].getName();
                String[] fileSplit2 = filename.split("x");
                String trackeeUserId1 = fileSplit2[0];
                if (trackeeUserId1.equals(trackeeUserID) && list[i].getName().contains("jpeg")) {
                    item1 = new AudioItems(list[i].getName());
                    lstArrImage.add(item1);
                } else {

                }
            }
            SequencedlstArrAutoImage.clear();
            for (int j = lstArrImage.size() - 1; j >= 0; j--) {
                String name = lstArrImage.get(j).get_audio_name();
                item2 = new AudioItems(name);
                SequencedlstArrAutoImage.add(item2);
            }
            lstImage.setAdapter(imageItemAdapter);
            imageItemAdapter.notifyDataSetChanged();

            btnLocation.setBackgroundResource(R.drawable.mybutton);
            btnHistory.setBackgroundResource(R.drawable.mybutton);
            btnAutoCall.setBackgroundResource(R.drawable.mybutton);
            btnCaptureImage.setBackgroundResource(R.drawable.selectedbutton);
        }

        if (from.equals("serviceAuto")) {
            mapLayout.setVisibility(View.INVISIBLE);
            hisLayout.setVisibility(View.INVISIBLE);
            autoCalllayout.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.INVISIBLE);
            btnLayout.setVisibility(View.VISIBLE);
            MenuOption = true;

            File file = new File(StoragePath + "/ICareTracker/AudioFiles");
            File list[] = file.listFiles();
            lstArrAuto.clear();
            for (int i = 0; i < list.length; i++) {
                String filename = list[i].getName();
                String[] fileSplit1 = filename.split("x");
                String trackeeUserId1 = fileSplit1[0];
                if (trackeeUserId1.equals(trackeeUserID) && list[i].getName().contains("mp3")) {
                    item1 = new AudioItems(list[i].getName());
                    lstArrAuto.add(item1);
                } else {

                }
            }
            SequencedlstArrAuto.clear();
            for (int j = lstArrAuto.size() - 1; j >= 0; j--) {
                String name = lstArrAuto.get(j).get_audio_name();
                item2 = new AudioItems(name);
                SequencedlstArrAuto.add(item2);
            }

            autoItemAdapter = new AutoRecordAdapter(MapsActivity.this,
                    R.layout.auto_list_row, SequencedlstArrAuto);

            lstAuto.setAdapter(autoItemAdapter);
            autoItemAdapter.notifyDataSetChanged();
            btnAutoCall.setClickable(true);

            btnLocation.setBackgroundResource(R.drawable.mybutton);
            btnHistory.setBackgroundResource(R.drawable.mybutton);
            btnAutoCall.setBackgroundResource(R.drawable.selectedbutton);
            btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        trackerUserID = userid;

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                hisLayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                imageLayout.setVisibility(View.INVISIBLE);
                btnLayout.setVisibility(View.VISIBLE);

                btnLocation.setBackgroundResource(R.drawable.selectedbutton);
                btnHistory.setBackgroundResource(R.drawable.mybutton);
                btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                btnCaptureImage.setBackgroundResource(R.drawable.mybutton);


                Trackee trackee = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
                if (trackee != null) {
                    locFlag = trackee.get_trackee_Loc_Flag();
                }

                if (locFlag.equals("OFF")) {

                    showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allow to access location");
                } else {
                    hisLayout.setVisibility(View.INVISIBLE);
                    autoCalllayout.setVisibility(View.INVISIBLE);
                    Keyword = "locreq";
                    if (isNetworkAvailable(MapsActivity.this)) {

                        progressDialog();
                        liveTrackingFlag = "ON";
                        progressFlag = true;
                        new SendNotificationForRequest().execute();
                    } else {
                        showAlertDialog(MapsActivity.this, "Network Error!", "Please check internet connection");
                    }
                }
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapLayout.setVisibility(View.INVISIBLE);
                hisLayout.setVisibility(View.VISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                imageLayout.setVisibility(View.INVISIBLE);
                btnLayout.setVisibility(View.VISIBLE);
                btnLocation.setBackgroundResource(R.drawable.mybutton);
                btnHistory.setBackgroundResource(R.drawable.selectedbutton);
                btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
                Trackee trackee = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
                if (trackee != null) {
                    hisFlag = trackee.get_trackee_His_Flag();
                }
                if (hisFlag.equals("OFF")) {
                    showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to access call history");
                } else {
                    if (timerstatus != null && timerstatus.equals("on")) {
                        // myTimerTask.cancel();
                    }
                    Keyword = "hisreq";
                    if (isNetworkAvailable(MapsActivity.this)) {
                        progressDialog();

                        liveTrackingFlag = "OFF";
                        progressFlag = true;
                        new SendNotificationForRequest().execute();
                    } else {
                        showAlertDialog(MapsActivity.this, "Network Error!", "Please check internet connection");
                    }
                }
            }
        });
        btnAutoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuOption = true;
                mapLayout.setVisibility(View.INVISIBLE);
                hisLayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(View.INVISIBLE);
                btnLayout.setVisibility(View.VISIBLE);
                btnLocation.setBackgroundResource(R.drawable.mybutton);
                btnHistory.setBackgroundResource(R.drawable.mybutton);
                btnAutoCall.setBackgroundResource(R.drawable.selectedbutton);
                btnCaptureImage.setBackgroundResource(R.drawable.mybutton);
                String root_sd = Environment.getExternalStorageDirectory().toString();
                File file = new File(root_sd + "/ICareTracker/AudioFiles");

                File list[] = file.listFiles();
                lstArrAuto.clear();
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        String filename = list[i].getName();
                        String[] fileSplit = filename.split("x");
                        String trackeeUserId = fileSplit[0];
                        if (trackeeUserId.equals(trackeeUserID) && list[i].getName().contains("mp3")) {
                            item1 = new AudioItems(list[i].getName());
                            lstArrAuto.add(item1);
                        } else {

                        }
                    }
                }
                SequencedlstArrAuto.clear();
                for (int j = lstArrAuto.size() - 1; j >= 0; j--) {
                    String name = lstArrAuto.get(j).get_audio_name();
                    item2 = new AudioItems(name);
                    SequencedlstArrAuto.add(item2);
                }
                autoItemAdapter = new AutoRecordAdapter(MapsActivity.this,
                        R.layout.auto_list_row, SequencedlstArrAuto);

                lstAuto.setAdapter(autoItemAdapter);
                autoItemAdapter.notifyDataSetChanged();
            }
        });
        btnFloatingAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                megAvailable = getFreeMemoryFromInternalStorage();
                if (megAvailable > 5) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

                    Trackee trackee = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
                    if (trackee != null) {
                        autoFlag = trackee.get_trackee_Auto_Flag();
                    }
                    if (autoFlag.equals("OFF")) {
                        showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to get voice");
                    } else {
                        mapLayout.setVisibility(View.INVISIBLE);
                        hisLayout.setVisibility(View.INVISIBLE);
                        autoCalllayout.setVisibility(View.VISIBLE);
                        btnLayout.setVisibility(View.VISIBLE);
                        Keyword = "voicereq";

                        if (isNetworkAvailable(MapsActivity.this)) {
                            liveTrackingFlag = "OFF";
                            new SendNotificationForRequest().execute();
                            alertDialogBuilder.setMessage("Recording will take around 20 seconds. Please wait !");
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    btnLocation.setClickable(true);
                                    btnHistory.setClickable(true);
                                    btnFloatingAudio.setClickable(false);
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnFloatingAudio.setClickable(true);
                                }
                            }, 20000);
                        } else {
                            showAlertDialog(MapsActivity.this, "Network Error!", "Please check internet connection");
                        }

                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Memory Space not available!", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuOption = false;
                animateFAB();
                mapLayout.setVisibility(View.INVISIBLE);
                hisLayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                autoCalllayout.setVisibility(View.INVISIBLE);
                imageLayout.setVisibility(View.VISIBLE);
                btnLayout.setVisibility(View.VISIBLE);
                btnLocation.setBackgroundResource(R.drawable.mybutton);
                btnHistory.setBackgroundResource(R.drawable.mybutton);
                btnAutoCall.setBackgroundResource(R.drawable.mybutton);
                btnCaptureImage.setBackgroundResource(R.drawable.selectedbutton);
                imageprocessing();
            }
        });

        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnLayout.getVisibility() == View.VISIBLE) {
                    btnLayout.setVisibility(View.INVISIBLE);
                } else {
                    btnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        if (mapLayout.getVisibility() == View.VISIBLE) {
            // Its visible

        } else {
            if (timerstatus != null && timerstatus.equals("on")) {
                // myTimerTask.cancel();
            }
            // Either gone or invisible
        }


        if (mapLayout.getVisibility() == View.VISIBLE) {
            if (livemarker != null) {
                btnLocation.setBackgroundResource(R.drawable.selectedbutton);
            }

        } else if (hisLayout.getVisibility() == View.VISIBLE) {
            btnHistory.setBackgroundResource(R.drawable.selectedbutton);
        } else if (autoCalllayout.getVisibility() == View.VISIBLE) {
            btnAutoCall.setBackgroundResource(R.drawable.selectedbutton);
        } else if (imageLayout.getVisibility() == View.VISIBLE) {
            btnCaptureImage.setBackgroundResource(R.drawable.selectedbutton);
        }
        resultList = new ArrayList<HashMap<String, String>>();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        if (checkGPSService(MapsActivity.this)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        AppIndex.AppIndexApi.start(client, getIndexApiAction());


    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        if (checkGPSService(MapsActivity.this)) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        if (checkGPSService(MapsActivity.this)) {
            mGoogleApiClient.connect();
        }
        if ((progress != null) && progress.isShowing())
            progress.dismiss();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        setUpMapIfNeeded();
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public void requestDirection() {
        Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.TRANSIT)
                .execute(this);

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
       // Snackbar.make(btnRequestDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
        if (direction.isOK()) {
            ArrayList<LatLng> sectionPositionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
            for (LatLng position : sectionPositionList) {
                // mMap.addMarker(new MarkerOptions().position(position));
            }

            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(this, stepList, 5, Color.GREEN, 3, Color.BLUE);
            for (PolylineOptions polylineOption : polylineOptionList) {
                mMap.addPolyline(polylineOption);
            }
            if (!reqDirFlag) {
                origin = destination;
            } else {
                polylineOptionList.clear();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Snackbar.make(btnRequestDirection, t.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            ulatitude = mLocation.getLatitude();
            ulongitude = mLocation.getLongitude();

            LocationClass loc = db.getStoredLocation();
            if (loc == null) {
                db.addlocation(new LocationClass(String.valueOf(ulatitude), String.valueOf(ulongitude), formattedDate));
            } else {
                db.updateLocation(new LocationClass(1, String.valueOf(ulatitude), String.valueOf(ulongitude), formattedDate));
            }
            if (mMap != null) {
                if (mapLayout.getVisibility() == View.VISIBLE) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ulatitude, ulongitude), 16.0f));
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (checkGPSService(MapsActivity.this)) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class updateLiveTrackingMarker extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Location locationA = new Location("point A");

            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);

            Location locationB = new Location("point B");

            locationB.setLatitude(oldlatitude);
            locationB.setLongitude(oldlongitude);

            float distance = locationA.distanceTo(locationB);

            if (distance > 3) {
                if ((progress != null) && progress.isShowing())
                    progress.dismiss();
                btnLocation.setClickable(true);
                btnHistory.setClickable(true);
                btnAutoCall.setClickable(true);
                if (livemarker != null) {
                    livemarker.remove();
                }
                LatLng mLatLng = new LatLng(latitude, longitude);
                btnRequestDirection.setVisibility(View.VISIBLE);
                livemarker = mMap.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        View v = getLayoutInflater().inflate(R.layout.marker, null);

                        TextView info = (TextView) v.findViewById(R.id.info);

                        info.setText(trackeeName + "\n" + address);

                        return v;
                    }
                });
                // Showing the current location in Google Map
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));
                destination = mLatLng;

                oldlatitude = latitude;
                oldlongitude = longitude;

                requestDirection();

                setLocation.setVisibility(View.VISIBLE);
                setLocation.setText(address);

                // Zoom in the Google Map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            }
        }

        protected String doInBackground(String... urls) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGPSService(MapsActivity.this)) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isNetworkAvailable(MapsActivity.this)) {

            new DeactivateLocationTracking().execute();

        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
        // Check if we were successful in obtaining the map.
    }


    public void setUpMap() {
        LatLng mLatLng = new LatLng(latitude, longitude);
        oldLatLng = mLatLng;
        oldlatitude = latitude;
        oldlongitude = longitude;
        final String address1 = getCompleteAddressString(latitude, longitude);
        setLocation.setVisibility(View.VISIBLE);
        btnRequestDirection.setVisibility(View.VISIBLE);
        setLocation.setText(address);
        if (currentmarker != null) {
            currentmarker.remove();
        }
        currentmarker=mMap.addMarker(new MarkerOptions().position(mLatLng).title(trackeeName));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.marker, null);

                TextView info = (TextView) v.findViewById(R.id.info);

                info.setText(trackeeName + "\n" + address1);

                return v;
            }
        });

        btnLocation.setBackgroundResource(R.drawable.selectedbutton);

    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            e.printStackTrace();
            appendLog(MapsActivity.this, "3 MapsActivity " + e.toString() + date);
        }
        return strAdd;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    class SendNotificationForRequest extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            try {
                String url = baseUrl1 + "notification/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Keyword", Keyword);
                jsonObject.accumulate("TrackeeUserId", trackeeUserID);
                jsonObject.accumulate("TrackerUserId", trackerUserID);
                jsonObject.accumulate("LiveTrackingsts", liveTrackingFlag);

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
                    adminPermission = mainObject.getString("AdminPermission");
                    //  {    "Status": "success",    "AdminPermission": "livetrackPermissionOFF"  }]
                } else
                    result = "Did not work!";
            } catch (Exception e) {

                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(MapsActivity.this, "4 MapsActivity " + e.toString() + date);

                //MapsActivity org.apache.http.conn.HttpHostConnectException: Connection to http://icare.sveltoz.com refused Mon, 10 Jul 2017, 12:19
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (responceStatus.equals("success")) {

                if (Keyword.equals("locreq")) {
                    btnLocation.setClickable(false);
                    btnHistory.setClickable(false);
                    db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "true"));
                    if (adminPermission.equals("livetrackPermissionOFF")) {
                        progressFlag = false;
                                if ((progress != null) && progress.isShowing())
                                    progress.dismiss();
                                btnLocation.setClickable(true);
                                btnHistory.setClickable(true);
                                btnAutoCall.setClickable(true);
                                new AlertDialog.Builder(MapsActivity.this)
                                        .setTitle("Subscribe")
                                        .setCancelable(true)
                                        .setMessage("Subscribe to use live tracking service")
                                        .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                    }
                }
            } else if (responceStatus.equals("fail")) {

               // voicereq,voicePermissionON
                if (Keyword.equals("voicereq")) {

                    db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "true"));
                    if (adminPermission.equals("voicePermissionOFF")) {
                        progressFlag = false;
                        if ((progress != null) && progress.isShowing())
                            progress.dismiss();

                        btnFloatingAudio.setClickable(true);

                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Subscribe")
                                .setCancelable(true)
                                .setMessage("Subscribe to get voice")
                                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }else if (Keyword.equals("hisreq")) {

                    db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "true"));
                    if (adminPermission.equals("historyPermissionOFF")) {
                        progressFlag = false;
                        if ((progress != null) && progress.isShowing())
                            progress.dismiss();
                        btnLocation.setClickable(true);
                        btnHistory.setClickable(true);
                        btnAutoCall.setClickable(true);
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Subscribe")
                                .setCancelable(true)
                                .setMessage("Subscribe to get call history")
                                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }else if (Keyword.equals("frontimagereq")) {

                    db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "true"));
                    if (adminPermission.equals("frontimagePermissionOFF")) {
                        progressFlag = false;
                        if ((progress != null) && progress.isShowing())
                            progress.dismiss();
                        btnLocation.setClickable(true);
                        btnHistory.setClickable(true);
                        btnAutoCall.setClickable(true);
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Subscribe")
                                .setCancelable(true)
                                .setMessage("Subscribe to get front image")
                                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }else if (Keyword.equals("rearimagereq")) {

                    db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "true"));
                    if (adminPermission.equals("rearimagePermissionOFF")) {
                        progressFlag = false;
                        if ((progress != null) && progress.isShowing())
                            progress.dismiss();
                        btnLocation.setClickable(true);
                        btnHistory.setClickable(true);
                        btnAutoCall.setClickable(true);
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Subscribe")
                                .setCancelable(true)
                                .setMessage("Subscribe to get rear image")
                                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                }

            } else if (responceStatus.equals("useruninstalledapp")) {
                progressFlag = false;
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "User uninstalled iCare Tracker App.", Toast.LENGTH_LONG).show();

                db.updateTrackeeAppStatusFlag(new Trackee(trackeeUserID, "false"));

                List<Trackee> trackee = db.getAllTrackees();
                for (Trackee cn : trackee) {
                    if (cn.get_user_Id().equals(trackeeUserID)) {
                        trackee_Id = cn.get_trackee_Id();
                        trackee_Name = cn.get_trackee_Name();
                        trackee_Email = cn.get_trackee_Email();
                        trackee_Loc_Flag = "ON";
                        trackee_His_Flag = cn.get_trackee_His_Flag();
                        trackee_Auto_Flag = cn.get_trackee_Auto_Flag();
                        trackee_ImageCapture_Flag = cn.get_trackee_ImageCapture_Flag();
                        trackee_App_Status = "false";
                        updateTrackee();
                    }
                }
            }
        }
    }

    public void updateTrackee() {

        db.updateTrackee(new Trackee(trackee_Id, trackeeUserID, trackee_Name, trackee_Email, trackee_Loc_Flag, trackee_His_Flag, trackee_Auto_Flag, trackee_ImageCapture_Flag, trackee_App_Status));
    }

    class DeactivateLocationTracking extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                String url = baseUrl1 + "StopLocationUpdate/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", trackerUserID);
                jsonObject.accumulate("TrackeeId", trackeeUserID);
                jsonObject.accumulate("LiveTrackingsts", "OFF");

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
                appendLog(MapsActivity.this, "5 MapsActivity " + e.toString() + date);

            }
            return null;
        }
    }


    public void showAlertDialogWithRetry(String title, String message) {
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        progressDialog();
                        new SendNotificationForRequest().execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
  /* Create the actionbar options menu */

    /*************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.maps_activity_menu, menu);
        this.menu = menu;

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.action_charging_state:
                break;
            case R.id.action_charging_per:
                break;
            default:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences batterydetails = getApplicationContext().getSharedPreferences("batterydetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = batterydetails.edit();
        editor.clear();
        editor.commit();
        if (timerstatus != null && timerstatus.equals("on")) {
            // myTimerTask.cancel();
        }
        if (isNetworkAvailable(MapsActivity.this)) {
            new DeactivateLocationTracking().execute();
        }
        Intent i = new Intent(MapsActivity.this, DrawerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("Go_To_Fragment", "A");
        startActivity(i);
    }

    public void progressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Please wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        long delayInMillis = 15000;


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((progress != null) && progress.isShowing())
                    progress.dismiss();
                btnLocation.setClickable(true);
                btnHistory.setClickable(true);
                btnAutoCall.setClickable(true);
            }
        }, delayInMillis);



        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (progressFlag) {
                    if (Keyword.equals("locreq")) {
                        showAlertDialogWithRetry("Error !!!", "1.Trackees network connection is poor! \n 2.You have logged onto another device");
                        // progressDialog();
                    } else {


                        showAlertDialog(MapsActivity.this, "Error !!!", "1.Trackees network connection is poor! \n 2.You have logged onto another device");
                        // showAlertDialog(MapsActivity.this, "Due to following Reasons!", "1.Trackees Network connection is poor! \n 2.You have logged onto another device");
                    }
                }
                btnLocation.setClickable(true);
                btnHistory.setClickable(true);
                btnAutoCall.setClickable(true);
            }
        });

    }

    public String printDifference(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long Days = different / daysInMilli;
        different = different % daysInMilli;

        long Hours = different / hoursInMilli;
        different = different % hoursInMilli;

        long Minutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long Seconds = different / secondsInMilli;

        String result;
        if (Days > 0) {
            result = Days + " Days ago ";
        } else if (Hours > 0) {
            result = Hours + " Hours ago ";
        } else if (Minutes > 0) {
            result = Minutes + " Min ago";
        } else {
            result = Seconds + " Sec ago ";
        }
        return result;
    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_request_direction:
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MapsActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to show direction from your location?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        reqDirFlag = true;
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        origin = new LatLng(ulatitude, ulongitude);
                        requestDirection();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                break;
            case R.id.fabFront:
                megAvailable = getFreeMemoryFromInternalStorage();
                if (megAvailable > 5) {
                    Trackee trackee = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
                    if (trackee != null) {
                        imgFlag = trackee.get_trackee_ImageCapture_Flag();
                    }
                    if (imgFlag.equals("OFF")) {

                        showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to take picture.");
                    } else {
                        Keyword = "frontimagereq";
                        if (isNetworkAvailable(MapsActivity.this)) {
                            liveTrackingFlag = "OFF";
                            new SendNotificationForRequest().execute();
                            showAlertDialog(MapsActivity.this, "Wait", "Image capture will take around 20 seconds. Please wait!");
                            animateFAB();
                            btnLocation.setClickable(true);
                            btnHistory.setClickable(true);
                        } else {
                            showAlertDialog(MapsActivity.this, "Network Error!", "Please check internet connection.");
                        }
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Memory Space not available!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.fabBack:
                megAvailable = getFreeMemoryFromInternalStorage();
                if (megAvailable > 5) {
                    Trackee trackee1 = db.getTrackeeDetails(Integer.parseInt(trackeeUserID));
                    if (trackee1 != null) {
                        imgFlag = trackee1.get_trackee_ImageCapture_Flag();
                    }
                    if (imgFlag.equals("OFF")) {
                        showAlertDialog(MapsActivity.this, "Access Denied!", "Trackee not allowed to take picture.");
                    } else {
                        Keyword = "rearimagereq";
                        if (isNetworkAvailable(MapsActivity.this)) {
                            liveTrackingFlag = "OFF";
                            new SendNotificationForRequest().execute();
                            showAlertDialog(MapsActivity.this, "Wait", " Image capture will take around 20 seconds. Please wait!");
                            animateFAB();
                            btnLocation.setClickable(true);
                            btnHistory.setClickable(true);
                        } else {
                            showAlertDialog(MapsActivity.this, "Network Error!", "Please check internet connection.");
                        }
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Memory Space not available!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {
            flotingFront.startAnimation(fab_close);
            flotingBack.startAnimation(fab_close);
            flotingFront.setClickable(false);
            flotingBack.setClickable(false);
            isFabOpen = false;
        } else {
            flotingFront.startAnimation(fab_open);
            flotingBack.startAnimation(fab_open);
            flotingFront.setClickable(true);
            flotingBack.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //  menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (MenuOption) {
            if (item.getTitle() == "Delete") {
                int pos = info.position;
                AudioItems itemname = SequencedlstArrAuto.get(pos);
                String filename = itemname.get_audio_name();
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICareTracker/AudioFiles/" + filename;
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                    SequencedlstArrAuto.remove(pos);
                    lstAuto.setAdapter(autoItemAdapter);
                    autoItemAdapter.notifyDataSetChanged();
                }
            }
        } else {
            int pos = info.position;
            AudioItems itemname = SequencedlstArrAutoImage.get(pos);
            String filename = itemname.get_audio_name();
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICareTracker/ImageFiles/" + filename;
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
                SequencedlstArrAutoImage.remove(pos);
                lstImage.setAdapter(imageItemAdapter);
                imageItemAdapter.notifyDataSetChanged();
            }
        }
        return super.onContextItemSelected(item);
    }

    public void imageprocessing() {

        if (imageflag) {
            String root_sd = Environment.getExternalStorageDirectory().toString();
            File file = new File(root_sd + "/ICareTracker/ImageFiles");

            File list[] = file.listFiles();
            lstArrImage.clear();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    String filename = list[i].getName();
                    String[] fileSplit2 = filename.split("x");
                    String trackeeUserId = fileSplit2[0];
                    if (trackeeUserId.equals(trackeeUserID) && list[i].getName().contains("jpeg")) {
                        item1 = new AudioItems(list[i].getName());
                        lstArrImage.add(item1);
                    } else {
                    }
                }
            }
            SequencedlstArrAuto.clear();
            for (int j = lstArrImage.size() - 1; j >= 0; j--) {
                String name = lstArrImage.get(j).get_audio_name();
                item2 = new AudioItems(name);
                SequencedlstArrAutoImage.add(item2);
            }

            imageItemAdapter = new CaptureImageAdapter(MapsActivity.this,
                    R.layout.auto_list_row, SequencedlstArrAutoImage);
            lstImage.setAdapter(imageItemAdapter);
            imageItemAdapter.notifyDataSetChanged();
        }
        imageflag = false;
        if ((progress != null) && progress.isShowing())
            progress.dismiss();
    }

    public long getFreeMemoryFromInternalStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        return megAvailable;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "LocationClass settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                            String date = df.format(Calendar.getInstance().getTime());
                            // Log.i(TAG, "PendingIntent unable to execute request.");
                            e.printStackTrace();
                            appendLog(MapsActivity.this, "6 MapsActivity " + e.toString() + date);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Log.i(TAG, "LocationClass settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}



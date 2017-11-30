package sveltoz.icaretrackerapp.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.LocationSetting;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;
import sveltoz.icaretrackerapp.Service.MyBroadcastReceiver;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.progressDialog;
import static sveltoz.icaretrackerapp.Constants.Constants.showAlertDialog;


public class PrivacySettingActivity extends AppCompatActivity {
    DatabaseHandler db = new DatabaseHandler(this);
    String UserId;
    String autoflag, hisflag, Locflag, imgflag, responceStatus, appStatusFlag, liveTrackingFlag;
    private Switch LocSwitch, HisSwitch, AutoSwitch, ImageSwitch;
    String uUserId, uName, uEmail;
    ArrayList<String> trackerUserIdList = new ArrayList<String>();
    String name, settingkeyword, userid;
    String result = null;
    String defaultFlag = "true";
    InputStream inputStream = null;
    ProgressDialog progress;
    Button btnIncr, btndecr;
    TextView txtTime, txtSetLimit;
    RadioGroup rgLocSetting;
    RadioButton rbChooseTime, rbFixTime;
    Boolean locSettingFlag = false;
    CheckBox cbAddLimit;
    LinearLayout llvalidTillLayout, llVilidTillLabel;
    List<LocationSetting> locationSetting;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    SimpleDateFormat simpleDateFormat;
    String time;
    Calendar calander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);
        LocSwitch = (Switch) findViewById(R.id.locswitch);
        HisSwitch = (Switch) findViewById(R.id.hisswitch);
        AutoSwitch = (Switch) findViewById(R.id.autoswitch);
        ImageSwitch = (Switch) findViewById(R.id.imageswitch);

        btndecr = (Button) findViewById(R.id.btnDecrease);
        btnIncr = (Button) findViewById(R.id.btnIncrease);

        txtTime = (TextView) findViewById(R.id.rbChooseTime);
        txtSetLimit = (TextView) findViewById(R.id.txtSetLimit);

        rgLocSetting = (RadioGroup) findViewById(R.id.rgLocSetting);
        rbChooseTime = (RadioButton) findViewById(R.id.rbChooseTime);
        rbFixTime = (RadioButton) findViewById(R.id.rbFixTime);

        cbAddLimit = (CheckBox) findViewById(R.id.cbAddLimit1);

        llvalidTillLayout = (LinearLayout) findViewById(R.id.llValidTill);
        llVilidTillLabel = (LinearLayout) findViewById(R.id.ll_valid_till_label);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        LoginActivity.onActivity = "privacySettingActivity";
        Bundle extra = getIntent().getExtras();
        UserId = extra.getString("UserID");
        progress = new ProgressDialog(PrivacySettingActivity.this);
        User user3 = db.getUserDetails();
        userid = String.valueOf(user3.get_user_id());

        List<Tracker> tracker = db.getTrackerDetails(UserId);
        for (Tracker cn : tracker) {
            trackerUserIdList.add(cn.get_user_Id());
            uUserId = cn.get_user_Id();
            uName = cn.get_tracker_Name();
            uEmail = cn.get_tracker_Email();
            Locflag = cn.get_location_flag();
            hisflag = cn.get_history_flag();
            autoflag = cn.get_autocall_flag();
            imgflag = cn.get_imagecapture_flag();
            liveTrackingFlag = cn.get_livetracking_flag();
            appStatusFlag = cn.get_tracker_App_Status();
        }
        //code for getting vallues from database
        List<User> user = db.getAllUsers();
        int count = 0;
        for (User cn : user) {
            count++;
        }
        if (!(count > 0)) {
            Intent intent1 = new Intent(PrivacySettingActivity.this, SignUpActivity.class);
            startActivity(intent1);
        } else {
            User user1 = db.getUserDetails();
            name = user1.get_name();
        }

        locationSetting = db.getAlllocSettings();
        String alertTime = "";
        Boolean cbFlag = false;
        for (LocationSetting cn : locationSetting) {
            int t = cn.get_trackerId();
            if (uUserId.equals(String.valueOf(cn.get_trackerId()))) {
                cbFlag = true;
                alertTime = cn.get_locSettingTime();
            }
        }
        if (cbFlag) {
            cbAddLimit.setChecked(true);
        } else {
            llvalidTillLayout.setVisibility(View.GONE);
            cbAddLimit.setChecked(false);
            rbChooseTime.setChecked(false);
            rbFixTime.setChecked(false);
            txtTime.setText("1.00");
            // LocSwitch.setChecked(false);
        }

        uName = StringUtils.capitalize(uName);
        //Code for action bar with backbutton
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle(uName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme)));
        // status bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.Teal));
        }
        btndecr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtTime.getText().toString();
                Double currentVal = Double.parseDouble(txtTime.getText().toString());

                if (currentVal > 1) {
                    currentVal--;
                } else if (currentVal <= 1 && currentVal > 0.10) {
                    if (currentVal == 1) {
                        currentVal = currentVal - 0.50;
                    } else if (currentVal <= 0.60) {
                        currentVal = currentVal - 0.10;
                    }
                }
                txtTime.setText(String.format("%.2f", currentVal));
            }
        });
        btnIncr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double currentVal = Double.parseDouble(txtTime.getText().toString());

                if (currentVal >= 1 && currentVal <= 11) {
                    currentVal++;
                } else if (currentVal <= 1 && currentVal >= 0.01) {
                    if (currentVal > 0.50) {
                        currentVal = 1.00;
                    } else if (currentVal > 0.01 && currentVal <= 0.50) {
                        currentVal = currentVal + 0.10;
                    }
                }
                txtTime.setText(String.format("%.2f", currentVal));
            }
        });

        rgLocSetting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (rbChooseTime.isChecked()) {
                    btnIncr.setEnabled(true);
                    btndecr.setEnabled(true);
                } else if (rbFixTime.isChecked()) {
                    btnIncr.setEnabled(false);
                    btndecr.setEnabled(false);
                }
            }
        });

        //set switch true bydefault(checked)
        if (Locflag.equals("ON")) {
            LocSwitch.setChecked(true);
            llVilidTillLabel.setVisibility((View.VISIBLE));
        } else {
            LocSwitch.setChecked(false);
            llVilidTillLabel.setVisibility((View.GONE));
        }
        if (hisflag.equals("ON")) {
            HisSwitch.setChecked(true);
        } else {
            HisSwitch.setChecked(false);
        }
        if (autoflag.equals("ON")) {
            AutoSwitch.setChecked(true);
        } else {
            AutoSwitch.setChecked(false);
        }
        if (imgflag != null) {
            ImageSwitch.setChecked(true);
            if (imgflag.equals("ON")) {
                ImageSwitch.setChecked(true);
            } else {
                ImageSwitch.setChecked(false);
            }
        }

        cbAddLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbAddLimit.isChecked()) {
                    llvalidTillLayout.setVisibility(View.VISIBLE);
                    txtSetLimit.setText("Remove Limit");
                    rbFixTime.setChecked(true);
                } else {
                    rbFixTime.setChecked(false);
                    rbChooseTime.setChecked(false);
                    llvalidTillLayout.setVisibility(View.GONE);
                    db.deleteLocSetting(Integer.parseInt(uUserId));
                    settingkeyword = "locoff";
                    updateTracker();
                    txtSetLimit.setText("Set Time Limit");
                }
                txtTime.setText("1.00");
            }
        });
        LocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        Locflag = "ON";
                        settingkeyword = "locon";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        LocSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        LocSwitch.setChecked(false);
                    }
                    llVilidTillLabel.setVisibility((View.VISIBLE));
                } else {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        Locflag = "OFF";
                        settingkeyword = "locoff";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        LocSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();

                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        LocSwitch.setChecked(true);
                    }
                    rbFixTime.setChecked(false);
                    rbChooseTime.setChecked(false);
                    cbAddLimit.setChecked(false);
                    llvalidTillLayout.setVisibility(View.GONE);
                    llVilidTillLabel.setVisibility((View.GONE));
                    db.deleteLocSetting(Integer.parseInt(uUserId));

                }
            }
        });
        HisSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        hisflag = "ON";
                        settingkeyword = "hison";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        HisSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        HisSwitch.setChecked(false);
                    }

                } else {
                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        hisflag = "OFF";
                        settingkeyword = "hisoff";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        HisSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        HisSwitch.setChecked(true);
                    }

                }

            }
        });
        AutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        autoflag = "ON";
                        settingkeyword = "autoon";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        AutoSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        AutoSwitch.setChecked(false);
                    }
                } else {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        autoflag = "OFF";
                        settingkeyword = "autooff";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        AutoSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        AutoSwitch.setChecked(true);
                    }
                }
            }
        });
        ImageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        imgflag = "ON";
                        settingkeyword = "imgon";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        ImageSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        ImageSwitch.setChecked(false);
                    }
                } else {
                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        imgflag = "OFF";
                        settingkeyword = "imgoff";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        ImageSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        ImageSwitch.setChecked(true);
                    }

                }
            }
        });


        if (android.os.Build.VERSION.SDK_INT > 9) {
            policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        locationSetting = db.getAlllocSettings();
        String alertTime = "";
        Boolean cbFlag = false;
        for (LocationSetting cn : locationSetting) {
            int t = cn.get_trackerId();
            if (uUserId.equals(String.valueOf(cn.get_trackerId()))) {
                cbFlag = true;
                alertTime = cn.get_locSettingTime();
            }
        }
        if (cbFlag) {
            cbAddLimit.setChecked(true);
        } else {
            llvalidTillLayout.setVisibility(View.GONE);

            cbAddLimit.setChecked(false);
            rbChooseTime.setChecked(false);
            rbFixTime.setChecked(false);
            txtTime.setText("1.00");
        }


//        calander = Calendar.getInstance();
//        simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
//
//        time = simpleDateFormat.format(calander.getTime());
//12:19:27 PM


        //currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa").format(new Date());
        currentDateTimeString = sdf.getDateTimeInstance().format(new Date());
        Character s = currentDateTimeString.charAt(0);
        if (currentDateTimeString.contains("-")) {
            sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        }

        final Date date, alertDt;
        if (!alertTime.equals("")) {
            try {
                if (currentDateTimeString.contains("-")) {
                    sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                } else if (Character.isLetter(currentDateTimeString.charAt(0))) {
                    sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
                } else {
                    sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                }

                date = sdf.parse(currentDateTimeString);

                sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
                final Calendar calendar = Calendar.getInstance();

                calendar.setTime(date);

                alertDt = sdf.parse(alertTime);
                final Calendar calendar1 = Calendar.getInstance();

                calendar1.setTime(alertDt);

                sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa", Locale.ENGLISH);

                currentDateTimeString = sdf.format(calendar.getTime());
                alertTime = sdf.format(calendar1.getTime());

            } catch (ParseException e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date1 = df.format(Calendar.getInstance().getTime());
                //e.printStackTrace();
                e.printStackTrace();
                appendLog(PrivacySettingActivity.this, "1 PrivacySettingActivity " + e.toString() + date1);
            }
        }
        if (cbAddLimit.isChecked()) {
            txtSetLimit.setText("Remove Limit");
            llvalidTillLayout.setVisibility(View.VISIBLE);
            // Double currentVal = Double.parseDouble(alertTime);
            try {
                Date date1 = sdf.parse(currentDateTimeString);
                Date date2 = sdf.parse(alertTime);

                Long diffInMin = printDifference(date1, date2);

                if (diffInMin >= 720) {
                    rbFixTime.setChecked(true);
                    rbChooseTime.setChecked(false);
                } else if (diffInMin < 720 && diffInMin > 0.01) {
                    rbFixTime.setChecked(false);
                    rbChooseTime.setChecked(true);
                    long hrs = TimeUnit.MINUTES.toHours(diffInMin);
                    diffInMin = diffInMin - hrs * 60;
                    if (hrs >= 1) {
                        // String timeString = String.format("%02d.00", hrs);
                        String timeString = String.format("%02d.%02d", hrs, diffInMin);
                        txtTime.setText(timeString);
                    } else {
                        String timeString = String.format("%02d.%02d", hrs, diffInMin);
                        txtTime.setText(timeString);
                    }
                } else {

                    if (isNetworkAvailable(PrivacySettingActivity.this)) {
                        Locflag = "OFF";
                        settingkeyword = "locoff";
                        updateTracker();
                        progressDialog(progress, "Wait", "Setting...");
                        LocSwitch.setClickable(false);
                        new SendNotificationofSetting().execute();
                    } else {
                        showAlertDialog(PrivacySettingActivity.this, "Network Error!", "Please check internet connection");
                        LocSwitch.setChecked(true);
                    }
                    rbFixTime.setChecked(false);
                    rbChooseTime.setChecked(false);
                    cbAddLimit.setChecked(false);
                    llvalidTillLayout.setVisibility(View.GONE);
                    db.deleteLocSetting(Integer.parseInt(uUserId));
                }
            } catch (ParseException e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date1 = df.format(Calendar.getInstance().getTime());
                //   e.printStackTrace();
                e.printStackTrace();
                appendLog(PrivacySettingActivity.this, "2 PrivacySettingActivity " + e.toString() + date1);
            }
        } else {
            txtSetLimit.setText("Set Time Limit");
            llvalidTillLayout.setVisibility(View.GONE);
        }
    }

    public void updateTracker() {

        db.updateTracker(new Tracker(uUserId, uName, uEmail, Locflag, hisflag, autoflag, imgflag, AddTrackerActivity.liveTrackingFlag, appStatusFlag));
    }

    //Code for Action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (cbAddLimit.isChecked()) {
            if (rgLocSetting.getCheckedRadioButtonId() == -1) {
                // Toast.makeText(getApplicationContext(), "nothing is selected", Toast.LENGTH_SHORT).show();
            } else {
                int selectedId = rgLocSetting.getCheckedRadioButtonId();
                double currentVal = 0.00, valInHour = 0;
                int valInMin = 0;
                if (rbChooseTime.isChecked()) {
                    currentVal = Double.parseDouble(txtTime.getText().toString());

                    if (currentVal >= 1 && currentVal < 12) {
                        valInHour = currentVal;
                    } else if (currentVal < 1 && currentVal > 0.01) {
                        int whole = (int) currentVal;
                        valInMin = (int) ((currentVal - whole) * 100);

                    }
                    Toast.makeText(getApplicationContext(), "Location will visible till " + rbChooseTime.getText().toString() + " Hrs", Toast.LENGTH_SHORT).show();
                }
                if (rbFixTime.isChecked()) {
                    valInHour = 12;
                    Toast.makeText(getApplicationContext(),
                            "Location will visible till " + rbFixTime.getText().toString(), Toast.LENGTH_SHORT).show();
                }

                String finalDateTime = "";
                try {

                    currentDateTimeString = sdf.getDateTimeInstance().format(new Date());
                    Character s = currentDateTimeString.charAt(0);
                    if (currentDateTimeString.contains("-")) {
                        sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                    } else if (Character.isLetter(currentDateTimeString.charAt(0))) {
                        sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
                    } else {
                        sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                    }


                    // currentDateTimeString=sdf.format(sdf.format(currentDateTimeString));
                    final Date date = sdf.parse(currentDateTimeString);
                    sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
                    final Calendar calendar = Calendar.getInstance();

                    calendar.setTime(date);
                    if (valInHour >= 1) {
                        calendar.add(Calendar.HOUR, (int) valInHour);
                        int hrs = (int) valInHour;
                        int mins = (int) ((valInHour - hrs) * 100);
                        calendar.add(Calendar.MINUTE, mins);
                    } else if (valInMin > 0.01 && valInMin <= 60) {
                        calendar.add(Calendar.MINUTE, valInMin);
                    }
                    sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
                    finalDateTime = sdf.format(calendar.getTime());


                    System.out.println("Time here " + sdf.format(calendar.getTime()));
                } catch (ParseException e) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date1 = df.format(Calendar.getInstance().getTime());
                    //e.printStackTrace();
                    e.printStackTrace();
                    appendLog(PrivacySettingActivity.this, "3 PrivacySettingActivity " + e.toString() + date1);
                }

                try {
                    for (LocationSetting cn : locationSetting) {
                        int t = cn.get_trackerId();
                        if (uUserId.equals(String.valueOf(cn.get_trackerId()))) {
                            locSettingFlag = true;
                        }
                    }
                    if (locSettingFlag) {
                        int i = db.updateLocSetting(new LocationSetting(Integer.parseInt(uUserId), finalDateTime));
                        Log.i("result", String.valueOf(i));
                    } else {
                        db.addLocSetting(Integer.parseInt(uUserId), finalDateTime);
                    }
                } catch (Exception e) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date1 = df.format(Calendar.getInstance().getTime());
                    //e.printStackTrace();
                    e.printStackTrace();
                    appendLog(PrivacySettingActivity.this, "4 PrivacySettingActivity " + e.toString() + date1);
                }
                startAlert(5);

            }
        }
        Intent i = new Intent(PrivacySettingActivity.this, DrawerActivity.class);
        i.putExtra("Go_To_Fragment", "B");
        startActivity(i);
    }

    public void startAlert(int time) {
        // some time when u want to run
        Date when = new Date(System.currentTimeMillis());
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
        // Toast.makeText(this, "Alarm after " + time + " seconds",Toast.LENGTH_LONG).show();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 50000,
                pendingIntent);
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
                StrictMode.ThreadPolicy policy;
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
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
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date1 = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(PrivacySettingActivity.this, "5 PrivacySettingActivity " + e.toString() + date1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (responceStatus != null) {
                if (responceStatus.equals("success")) {
                    if ((progress != null) && progress.isShowing())
                        progress.dismiss();
                    LocSwitch.setClickable(true);
                    HisSwitch.setClickable(true);
                    AutoSwitch.setClickable(true);
                    ImageSwitch.setClickable(true);
                } else if (responceStatus.equals("fail")) {
                    if ((progress != null) && progress.isShowing())
                        progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                } else if (responceStatus.equals("useruninstalledapp")) {
                    if ((progress != null) && progress.isShowing())
                        progress.dismiss();
                    Toast.makeText(getApplicationContext(), "User uninstalled iCare Tracker App.", Toast.LENGTH_LONG).show();

                    db.updateTracker(new Tracker(uUserId, uName, uEmail, Locflag, hisflag, autoflag, imgflag, liveTrackingFlag, "false"));
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
}


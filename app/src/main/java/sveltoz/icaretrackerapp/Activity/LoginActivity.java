package sveltoz.icaretrackerapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.progressDialog;

public class LoginActivity extends ActionBarActivity {
    ProgressDialog progress;
    EditText edtTextPin;

    TextView tvForgotLogin, tvSwitchUser;
    DatabaseHandler db = new DatabaseHandler(this);
    String name, email, trackerpin, trackeepin, type, status;
    int userid;
    private android.content.Context context;
    String finalmail;
    StringBuilder sbEmail;

    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    Random random = new Random();
    public static String otp;
    String trackerId, TrackerMail, trackername, result = null, responceStatus;

    public static String onActivity;
    int count;
    InputStream inputStream = null;
    CircularProgressView circularProgressView = null;
    List<User> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        onActivity = "loginActivity";
        progress = new ProgressDialog(LoginActivity.this);
        setContentView(R.layout.activity_login);


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALENDAR,Manifest.permission.READ_PHONE_STATE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        edtTextPin = (EditText) findViewById(R.id.editTextPin);
        tvForgotLogin = (TextView) findViewById(R.id.textviewForgotpass);
        //tvSwitchUser = (TextView) findViewById(R.id.textviewSwitchUser);
        circularProgressView = (CircularProgressView) findViewById(R.id.progress_view);
        circularProgressView.setVisibility(View.INVISIBLE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme)));
        getSupportActionBar().hide();
        // status bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.Teal));
        }
//        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
//            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//            intent.putExtra("extra_pkgname", "sveltoz.icaretrackerapp");
//        }
//        String manufacturer1 = "Lenovo";
//        if(manufacturer1.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
//            //this will open auto start screen where user can enable permission for your app
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//            startActivity(intent);
//        }
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(0);
        Intent i = getIntent();
        trackerId = i.getStringExtra("trackerid");
        trackername = i.getStringExtra("name");
        TrackerMail = i.getStringExtra("TrackerMail");
        user = db.getAllUsers();
        count = 0;

        for (User cn : user) {
            count++;
        }

        if (!(count > 0)) {
            Intent mainIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(mainIntent);
            //   ************* redirect to sign up if not registered**************
//            Intent intent1 = new Intent(LoginActivity.this, SplashActivity.class);
//            startActivity(intent1);
        } else {
            User user1 = db.getUserDetails();
            userid = user1.get_user_id();
            name = user1.get_name();
            email = user1.get_email();
            trackerpin = user1.get_tracker_pin();
            trackeepin = user1.get_trackee_pin();
            type = user1.get_user_type();
            status = user1.get_email_verify();
        }

        if (edtTextPin.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        edtTextPin.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (edtTextPin.getText().length() == 4) {
                    circularProgressView.setVisibility(View.VISIBLE);
                    if (trackerpin.equals(edtTextPin.getText().toString())) {
                        Intent intent1 = new Intent(LoginActivity.this, DrawerActivity.class);
                        intent1.putExtra("Go_To_Fragment", "A");
                        startActivity(intent1);
                    } else {
                        circularProgressView.setVisibility(View.INVISIBLE);
                        edtTextPin.setError("Incorrect PIN");
                    }
                }
            }
        });

        tvForgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(LoginActivity.this)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                    String[] parts = email.split("\\@");
                    String SortEmail = parts[0];
                    String SortEmail1 = parts[1];
                    int k = SortEmail.length();
                    char firstChar = SortEmail.charAt(0);
                    char lastChar = SortEmail.charAt(k - 1);
                    k = k - 2;
                    finalmail = String.valueOf(firstChar);

                    sbEmail = new StringBuilder(finalmail);
                    for (int i = 0; i < k; i++) {
                        sbEmail.append("*");
                    }
                    sbEmail.append(lastChar + "@" + SortEmail1);

                    alertDialogBuilder.setMessage("Are you sure you want to send PIN to " + sbEmail+"?");
                    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            progressDialog(progress, "", "Sending OTP...");
                            otp = random();
                            new SendOtpDetails().execute();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    Toast.makeText(getApplicationContext(), "No network Available!", Toast.LENGTH_LONG).show();
                }
            }
        });
//        tvSwitchUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent welIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
//                startActivity(welIntent);
//            }
//        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        edtTextPin.setText("");
        circularProgressView.setVisibility(View.INVISIBLE);
        user = db.getAllUsers();
        for (User cn : user) {
            count++;
        }

        if (!(count > 0)) {
            Intent mainIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(mainIntent);
            //   ************* redirect to sign up if not registered**************
//            Intent intent1 = new Intent(LoginActivity.this, SplashActivity.class);
//            startActivity(intent1);
        } else {
            User user1 = db.getUserDetails();
            userid = user1.get_user_id();
            name = user1.get_name();
            email = user1.get_email();
            trackerpin = user1.get_tracker_pin();
            trackeepin = user1.get_trackee_pin();
            type = user1.get_user_type();
            status = user1.get_email_verify();
        }
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (count > 0) {
//            Intent logIntent = new Intent(LoginActivity.this, LoginActivity.class);
//            startActivity(logIntent);
//        }
//    }
//
//    @Override
//    protected void () {
//        super.onPostResume();
//        if (count >0){
//            Intent logIntent = new Intent(LoginActivity.this, LoginActivity.class);
//                startActivity(logIntent);
//        }
//
//    }

    // Send OTP to service
    public class SendOtpDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                String url = baseUrl1 + "SendEmailVerificationCode/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("emailToAddress", email);
                jsonObject.accumulate("verificationCode","Verification code to change your iCare Tracker PIN is : "+otp);
                jsonObject.accumulate("Key", "forgotpassword");

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
                appendLog(LoginActivity.this,"1 LoginActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(LoginActivity.this, SubmitOTPActivity.class);
            i.putExtra("OTP", otp);
            i.putExtra("FROM", "LoginActivity");
            startActivity(i);
            progress.dismiss();
        }
    }

    // generate random OTP
    public String random() {
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            int number = getRandomNumber();
            char ch = _CHAR.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private int getRandomNumber() {
        int randomInt = 0;
        randomInt = random.nextInt(_CHAR.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

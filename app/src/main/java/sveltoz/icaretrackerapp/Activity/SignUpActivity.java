package sveltoz.icaretrackerapp.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Pattern;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Trackee;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;
import sveltoz.icaretrackerapp.Service.MyFirebaseInstanceIDService;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.progressDialog;

public class SignUpActivity extends AppCompatActivity {

    ScrollView viewRegister;
    EditText edtTextName, edtTextPin, edtTextConfirmPin;
    Button btnRegister;
    public static TextView tvVerifymail;
    AutoCompleteTextView edtTextEmail;
    String name, emailId, pin, type, responseUserId, responceStatus, FCMtoken, UserMail, lastEmail, trackeeLocFlag = "ON", trackeeHisFlag = "ON", trackeeAutoflag = "ON", trackeeImageCaptureflag = "ON", flag = "ON", result = null;
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public static String otp;
    ArrayList<String> possibleEmails = new ArrayList<String>();
    Boolean isUpdating = false;
    InputStream inputStream = null;
    Random random = new Random();
    public static String from;
    DatabaseHandler db = new DatabaseHandler(this);
    ProgressDialog progress;
    String defaultFlag = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String str = android.os.Build.MANUFACTURER;
        //LENOVO
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }

        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app

            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", "sveltoz.icaretrackerapp");

        }
        viewRegister = (ScrollView) findViewById(R.id.View_register);
        type = "Tracker";

        progress = new ProgressDialog(SignUpActivity.this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme)));
        // status bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.Teal));
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        edtTextName = (EditText) findViewById(R.id.editTextName);
        edtTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
        edtTextPin = (EditText) findViewById(R.id.editTextPin);
        edtTextConfirmPin = (EditText) findViewById(R.id.editTextConfirmPin);
        btnRegister = (Button) findViewById(R.id.buttonRegister);
        tvVerifymail = (TextView) findViewById(R.id.errorText);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmails.add(account.name);
                lastEmail = account.name;
            }
        }

        edtTextEmail.setText(lastEmail);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, possibleEmails);
        //Find TextView control
        //Set the number of characters the user must type before the drop down list is shown
        edtTextEmail.setThreshold(1);
        //Set the adapter
        edtTextEmail.setAdapter(adapter);

        emailId = lastEmail;

        // text Change validations
        edtTextName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.length() > 0) {
                    String tname = edtTextName.getText().toString();
                    if (!tname.matches(".*[a-zA-Z]+.*")) {
                        edtTextName.setError("Special characters are not allowed");
                        name = null;
                    } else {
                        name = edtTextName.getText().toString();
                    }

                } else {
                    edtTextName.setError("Enter Name");
                    name = null;
                }
            }
        });
        edtTextEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidEmail(edtTextEmail.getText().toString()) && s.length() > 0) {
                    edtTextEmail.setError(null);
                    tvVerifymail.setVisibility(View.INVISIBLE);
                    emailId = edtTextEmail.getText().toString();
                    SubmitOTPActivity.verifyflag = "false";
                } else {
                    edtTextEmail.setError("Enter valid email");
                    SubmitOTPActivity.verifyflag = "false";
                    emailId = null;
                }
            }
        });
        edtTextPin.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.length() == 4) {
                    edtTextPin.setError(null);
                    if (edtTextConfirmPin.getText().toString().equals(edtTextPin.getText().toString())) {
                        edtTextConfirmPin.setError(null);
                        pin = edtTextConfirmPin.getText().toString();
                    } else {
                        edtTextConfirmPin.setError("PIN mismatch");
                        pin = null;
                    }
                } else {
                    edtTextPin.setError("Enter 4 digit PIN");
                    pin = null;
                }
            }
        });
        edtTextConfirmPin.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.length() == 4) {
                    if (edtTextConfirmPin.getText().toString().equals(edtTextPin.getText().toString())) {
                        edtTextConfirmPin.setError(null);
                        pin = edtTextConfirmPin.getText().toString();
                    } else {
                        edtTextConfirmPin.setError("PIN mismatch");
                        pin = null;
                    }
                } else {
                    edtTextConfirmPin.setError("Enter 4 digit PIN");
                    pin = null;
                }
            }
        });

        if (SubmitOTPActivity.verifyflag.equals("true")) {
            tvVerifymail.setVisibility(View.INVISIBLE);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(SignUpActivity.this)) {
                    if (name == null || emailId == null || pin == null) { //|| phoneNo == null
                        Toast.makeText(getApplicationContext(), "Enter all valid fields", Toast.LENGTH_LONG).show();
                    } else if (!name.matches("[a-zA-Z.? ]*")) {
                        Toast.makeText(getApplicationContext(), "Special Characters/Numbers are not allowed", Toast.LENGTH_LONG).show();
                    } else {
                        String flag;
                        UserMail = edtTextEmail.getText().toString();
                        flag = SubmitOTPActivity.verifyflag;
                        // flag = "true";// temp code, don't forget to remove
                        if (flag.equals("true") || UserMail.equals(lastEmail)) {
                            tvVerifymail.setVisibility(View.INVISIBLE);
                            tvVerifymail.setTextColor(Color.parseColor("#6200EA"));

                            InsertIfValid();
                        } else {
                            Toast.makeText(getApplicationContext(), "Need To Verify Email", Toast.LENGTH_LONG).show();
                            tvVerifymail.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No network Available!", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvVerifymail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   VerifyEmail
                otp = random();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);


                alertDialogBuilder.setMessage("Are you sure you want to send OTP to " + UserMail + "?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        otp = random();

                        new SendOtpDetails().execute();
                        progressDialog(progress, "", "Sending OTP...");
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
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (SubmitOTPActivity.verifyflag.equals("true")) {
            tvVerifymail.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SubmitOTPActivity.verifyflag.equals("true")) {
            tvVerifymail.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean InsertIfValid() {

        if (name != null && emailId != null && pin != null) {//&& phoneNo != null
            btnRegister.setError(null);
            // Send details to server
            progressDialog(progress, "Loading", "Please wait while loading...");
            FCMtoken = MyFirebaseInstanceIDService.token;
            if (FCMtoken == null) {
                FCMtoken = db.getToken();
            }
            if (FCMtoken != null) {
                new SendRegistrationDetails().execute();
            } else {
                Toast.makeText(SignUpActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
            return true;
        } else {
            Toast.makeText(this, "Enter all valid details", Toast.LENGTH_SHORT).show();
            // edtTextConfirmPin.setError("Enter all valid details");
            return false;
        }
    }

    public boolean UpdateIfValid() {

        if (name != null && emailId != null && pin != null) {//&& phoneNo != null
            btnRegister.setError(null);
            // Send details to server
            new SendUpdateDetails().execute();
            return true;
        } else {
            edtTextConfirmPin.setError("Enter all valid details");
            return false;
        }
    }

    public class SendRegistrationDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl1 + "insertregistrationdetails";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Name", name);
                jsonObject.accumulate("EmailId", emailId);
                jsonObject.accumulate("UserType", type);
                jsonObject.accumulate("EmailVerifyStatus", "yes");
                jsonObject.accumulate("DeviceId", FCMtoken);

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
                    /// String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    responseUserId = mainObject.getString("UserId");
                } else
                    result = "Did not work!";


            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(SignUpActivity.this,"1 SignUpActivity "+e.toString()+date);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (responceStatus.equals("success")) {
                ////////Code for store loc in database
                db.clearAppData();
                db.addUser(new User(Integer.parseInt(responseUserId), name, emailId, pin, null, type, "yes"));
                Intent i = new Intent(SignUpActivity.this, DrawerActivity.class);
                i.putExtra("Go_To_Fragment", "A");
                startActivity(i);
                progress.dismiss();
            } else if (responceStatus.equals("fail")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("User already registered!")
                        .setMessage("Are you sure you want to update?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                progressDialog(progress, "Loading", "Please wait while loading...");
                                isUpdating = true;

                                UpdateIfValid();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                progress.dismiss();
                                Log.d("Insert: ", "Inserting ..");
                                // delete trackers and trackees relations from server

                                new deleteUserDetails().execute();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
              //  Toast.makeText(getApplicationContext(), "User already exist", Toast.LENGTH_LONG).show();
            }
        }
    }


    // Code for sending user update details to service and update user information
    public class SendUpdateDetails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrl1 + "UpdateRegistrationDetails/";
                FCMtoken = MyFirebaseInstanceIDService.token;
                if (FCMtoken == null) {
                    FCMtoken = db.getToken();
                }
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
//{"UserId":"1303","Name":"ana","EmailId":"anagha.rasane@gmail.com","DeviceId":"eqeTOpvoan8:APA91bFnaILJqMcsBqk53wAf_RChYF6PLZrjUZgJxN2SQwzQAIDjZUvhukTBabpQ4gju82WnABF70w63OphJHeXZgmuuvdWoJNgZRQsjjVXhB6taXumB5ItO5YuhML6N5HsO2UZ61S7U"}
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", responseUserId);
                jsonObject.accumulate("Name", name);
                jsonObject.accumulate("EmailId", emailId);
                jsonObject.accumulate("DeviceId", FCMtoken);

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
                Log.i("RegiDetails", e.toString());
                e.printStackTrace();
                appendLog(SignUpActivity.this,"2 SignUpActivity "+e.toString()+date);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                //result = "1";
                db.clearAppData();
                db.addUser(new User(Integer.parseInt(responseUserId), name, emailId, pin, null, type, "yes"));
                new getUsersHistory().execute();
            } else if (responceStatus.equals("emailid and phoneno already exists")) {
                Toast.makeText(getApplicationContext(), "Email already used", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "network issue", Toast.LENGTH_LONG).show();
            }
        }
    }

    // delete users trackees and trackers from server and update information
    public class deleteUserDetails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                String url = baseUrl1 + "UpdateRegistrationDetailsWithDeleteTrackerTrackeeDetails/";
                FCMtoken = MyFirebaseInstanceIDService.token;
                if (FCMtoken == null) {
                    FCMtoken = db.getToken();
                }
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("UserId", responseUserId);
                jsonObject.accumulate("Name", name);
                jsonObject.accumulate("EmailId", emailId);
                jsonObject.accumulate("DeviceId", FCMtoken);

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
                appendLog(SignUpActivity.this,"3 SignUpActivity "+e.toString()+date);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (responceStatus.equals("success")) {
                db.clearAppData();
                db.addUser(new User(Integer.parseInt(responseUserId), name, emailId, pin, null, type, "yes"));
                Intent i = new Intent(SignUpActivity.this, DrawerActivity.class);
                i.putExtra("Go_To_Fragment", "A");
                startActivity(i);
            }
        }

    }
    // if user is already registered then get user history(all trackers&trackees)

    public class getUsersHistory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                // code to consume wcf service which sends the details to the server
                String url = baseUrl1 + "getAllTrackeesTrackers/";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", responseUserId);

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

                HttpResponse httpResponse = httpclient.execute(httpPost);
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    JSONArray jarrdata = new JSONArray(result);
                    JSONObject jObject = jarrdata.getJSONObject(0);// JSONObject("data");
                    //   JSONObject statusObj=new JSONObject()
                    JSONArray jArray = jObject.getJSONArray("data");

                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            String type, uid, name, email;
                            type = oneObject.getString("UserType");
                            uid = oneObject.getString("UserId");
                            name = oneObject.getString("Name");
                            email = oneObject.getString("EmailId");
                            if (type.equals("Tracker")) {
                                db.addTracker(new Tracker(uid, name, email, flag, flag, flag, flag, AddTrackerActivity.liveTrackingFlag, defaultFlag));
                            }
                            if (type.equals("Trackee")) {
                                db.addTrackee(new Trackee(uid, name, email, trackeeLocFlag, trackeeHisFlag, trackeeAutoflag, trackeeImageCaptureflag, defaultFlag));
                            }
                        } catch (JSONException e) {
                            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                            String date = df.format(Calendar.getInstance().getTime());
                            e.printStackTrace();
                            appendLog(SignUpActivity.this,"4 SignUpActivity "+e.toString()+date);
                            // error
                        }
                    }

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                //e.printStackTrace();
                e.printStackTrace();
                appendLog(SignUpActivity.this,"5 SignUpActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(SignUpActivity.this, DrawerActivity.class);
            i.putExtra("Go_To_Fragment", "A");
            startActivity(i);
        }
    }

    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Intent welcomeintent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        welcomeintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(welcomeintent);
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
                jsonObject.accumulate("emailToAddress", UserMail);
                jsonObject.accumulate("verificationCode", otp + " is verification code to verify email-id of iCare Tracker.");
                jsonObject.accumulate("Key", "emailverify");

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
                appendLog(SignUpActivity.this,"6 SignUpActivity "+e.toString()+date);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(SignUpActivity.this, SubmitOTPActivity.class);
            i.putExtra("OTP", otp);
            i.putExtra("FROM", "SignUpActivity");
            startActivity(i);
            progress.dismiss();
        }
    }

    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}

package sveltoz.icaretrackerapp.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class LoginWithEmailActivity extends AppCompatActivity {

    ScrollView viewRegister;
    EditText edtTextPin, edtTextConfirmPin;
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
        setContentView(R.layout.activity_login_with_email);
        viewRegister = (ScrollView) findViewById(R.id.View_login_with_register);
        type = "Tracker";
        progress = new ProgressDialog(LoginWithEmailActivity.this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // edtTextName = (EditText) findViewById(R.id.editTextName);
        edtTextEmail = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextEmail);
        edtTextPin = (EditText) findViewById(R.id.edtPin);
        edtTextConfirmPin = (EditText) findViewById(R.id.edtConfirmPin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvVerifymail = (TextView) findViewById(R.id.tvVerify);

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
                    edtTextEmail.setError("Enter valid Email");
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
                    edtTextPin.setError("Enter 4 Digit Pin");
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
                        edtTextConfirmPin.setError("Password mismatch");
                        pin = null;
                    }
                } else {
                    edtTextConfirmPin.setError("Enter 4 Digit Pin");
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

                if (isNetworkAvailable(LoginWithEmailActivity.this)) {
                    if (emailId == null || pin == null) { //|| phoneNo == null
                        Toast.makeText(getApplicationContext(), "Enter all valid fields", Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginWithEmailActivity.this);


                alertDialogBuilder.setMessage("Are you sure you want to send OTP on " + UserMail);

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        otp = random();

                        new SendOtpDetails().execute();
                        progressDialog(progress, "", "Sending OTP...");
                        // Toast.makeText(SignUpActivity.this, "Wait...!", Toast.LENGTH_LONG).show();
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

        if (emailId != null && pin != null) {//&& phoneNo != null
            btnRegister.setError(null);
            // Send details to server
            progressDialog(progress, "Loading", "Please wait while loading...");
            new getNameAndUserId().execute();
            return true;
        } else {
            Toast.makeText(this, "Enter all valid details", Toast.LENGTH_SHORT).show();
            // edtTextConfirmPin.setError("Enter all valid details");
            return false;
        }
    }

    public boolean UpdateIfValid() {

        if (emailId != null && pin != null) {//&& phoneNo != null
            btnRegister.setError(null);
            // Send details to server
            new SendUpdateDetails().execute();
            return true;
        } else {
            edtTextConfirmPin.setError("Enter all valid details");
            return false;
        }
    }

    public class getNameAndUserId extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                String url = baseUrl1 + "getUserIdNameFromEmailId";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                FCMtoken = MyFirebaseInstanceIDService.token;
                if (FCMtoken == null) {
                    FCMtoken = db.getToken();
                }
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("EmailId", emailId);

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
                    // result = result.replace("", "");
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);
                    /// String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    responseUserId = mainObject.getString("UserId");
                    name = mainObject.getString("Name");
                    //String message = mainObject.getString("Status");
                    //[{"$id":"1","Status":"fail","UserId":"591"}]
                    //[{"$id":"1","Status":"fail","UserId":"63"}]
                    //"[{\"Status\":\"fail\",\"UserId\":\"63\"}]"
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(LoginWithEmailActivity.this,"1 LoginWithEmailActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (responceStatus.equals("success")) {
                ////////Code for store loc in database
                progress.dismiss();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                progressDialog(progress, "Loading", "Please wait while loading...");
                                new SendUpdateDetails().execute();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginWithEmailActivity.this);
                builder.setMessage("You can use only one device to login, on another device application will stop working.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            } else if (responceStatus.equals("fail")) {
                progress.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginWithEmailActivity.this);
                builder.setTitle("Not Registered User!")
                        .setMessage("Need to register..")
                        .setCancelable(true)
                        .setPositiveButton("Register Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent registerIntent = new Intent(LoginWithEmailActivity.this, SignUpActivity.class);
                                startActivity(registerIntent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                // Toast.makeText(getApplicationContext(), "Not Registered User", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Code for sending user update details to service and update user information
    public class SendUpdateDetails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                // String tname = name.replaceAll(" ", "%20");
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
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", responseUserId);
                jsonObject.accumulate("Name", name);
                jsonObject.accumulate("EmailId", emailId);
                jsonObject.accumulate("DeviceId", FCMtoken);
//                jsonObject.accumulate("Key", key);

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
                    // result = result.replace("", "");
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);
                    //  String areaID = mainObject.getString("$id");
                    responceStatus = mainObject.getString("Status");
                    //[{"$id":"1","Status":"fail","UserId":"591"}]
                    //[{"$id":"1","Status":"fail","UserId":"63"}]
                    //"[{\"Status\":\"fail\",\"UserId\":\"63\"}]"
                } else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
              //  e.printStackTrace();
                e.printStackTrace();
                appendLog(LoginWithEmailActivity.this,"2 LoginWithEmailActivity "+e.toString()+date);
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

                // String tname = name.replaceAll(" ", "%20");
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
                    // result = result.replace("", "");
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
                appendLog(LoginWithEmailActivity.this,"3 LoginWithEmailActivity "+e.toString()+date);
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
                db.addUser(new User(Integer.parseInt(responseUserId), name, emailId, pin, null, type, "yes"));
                Intent i = new Intent(LoginWithEmailActivity.this, DrawerActivity.class);
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
                            // error
                            e.printStackTrace();
                            appendLog(LoginWithEmailActivity.this,"4 LoginWithEmailActivity "+e.toString()+date);
                        }
                    }

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
             //    e.printStackTrace();
                e.printStackTrace();
                appendLog(LoginWithEmailActivity.this,"5 LoginWithEmailActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(LoginWithEmailActivity.this, DrawerActivity.class);
            i.putExtra("Go_To_Fragment", "A");
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent welcomeintent = new Intent(LoginWithEmailActivity.this, WelcomeActivity.class);
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
                jsonObject.accumulate("verificationCode", otp);
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
                appendLog(LoginWithEmailActivity.this,"6 LoginWithEmailActivity "+e.toString()+date);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent i = new Intent(LoginWithEmailActivity.this, SubmitOTPActivity.class);
            i.putExtra("OTP", otp);
            i.putExtra("FROM", "LoginWithEmailActivity");
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

package sveltoz.icaretrackerapp.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Trackee;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.showAlertDialog;


public class AddTrackerActivity extends ActionBarActivity {
    EditText edtEmail;
    Button btnAddTracker;
    DatabaseHandler db = new DatabaseHandler(this);
    String dbemailId, userid, emailId, responseID, responseName, responceStatus, result = null, from;
    InputStream inputStream = null;
    String flag = "ON";
    public static String liveTrackingFlag = "OFF";
    public ProgressDialog progress;
    String defaultFlag = "true";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tracker);
        LoginActivity.onActivity = "addTrackerActivity";
        progress = new ProgressDialog(AddTrackerActivity.this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Set action bar with back button
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
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
        edtEmail = (EditText) findViewById(R.id.editTextTrackerEmail);
        btnAddTracker = (Button) findViewById(R.id.buttonAddTracker);
        User user1 = db.getUserDetails();
        userid = String.valueOf(user1.get_user_id());
        dbemailId = String.valueOf(user1.get_email());
        Bundle extra = getIntent().getExtras();
        from = extra.getString("from");

        if (from.equals("trackeeFragment")) {
            getSupportActionBar().setTitle("Add Trackee");
        } else {
            getSupportActionBar().setTitle("Add Tracker");
        }
        btnAddTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddTracker.setEnabled(false);
                emailId = edtEmail.getText().toString();
                Boolean trackerFlag = true;
                if (emailId.equals("")) {
                    edtEmail.setError("Enter Email Id");
                    btnAddTracker.setEnabled(true);
                } else if (dbemailId.equals(emailId)) {
                    edtEmail.setError("Can not add yourself");
                    btnAddTracker.setEnabled(true);
                } else {
                    if (from.equals("trackeeFragment")) {
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        List<Trackee> trackee = db.getAllTrackees();

                        for (Trackee cn : trackee) {
                            String t = cn.get_trackee_Email();
                            if (emailId.equals(cn.get_trackee_Email())) {
                                trackerFlag = false;
                                showAlertDialog(AddTrackerActivity.this, "Already Exist", "This Trackee is already added");
                                btnAddTracker.setEnabled(true);
                            } else {
                            }
                        }
                        if (trackerFlag) {
                            if (emailId.matches(emailPattern)) {
                                if (isNetworkAvailable(AddTrackerActivity.this)) {
                                    progressDialog();
                                    new SendTrackeeDetails().execute();
                                } else {
                                    showAlertDialog(AddTrackerActivity.this, "Oops!", "No network available!");
                                    btnAddTracker.setEnabled(true);
                                }
                            } else {
                                showAlertDialog(AddTrackerActivity.this, "Error", "enter valid email id");
                                btnAddTracker.setEnabled(true);
                            }
                        } else {
                        }
                    } else {
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        List<Tracker> tracker = db.getAllTrackers();

                        for (Tracker cn : tracker) {
                            //  mylist.add(cn.get_tracker_Name());
                            String t = cn.get_tracker_Email();
                            if (emailId.equals(cn.get_tracker_Email())) {
                                trackerFlag = false;
                                showAlertDialog(AddTrackerActivity.this, "Already Exist", "This Tracker is already added");
                                btnAddTracker.setEnabled(true);
                            } else {

                            }
                        }
                        if (trackerFlag) {

                            if (emailId.matches(emailPattern)) {
                                if (isNetworkAvailable(AddTrackerActivity.this)) {
                                    progressDialog();
                                    new SendTrackerDetails().execute();
                                } else {
                                    showAlertDialog(AddTrackerActivity.this, "Oops!", "No network available!");
                                    btnAddTracker.setEnabled(true);
                                }

                            } else {
                                showAlertDialog(AddTrackerActivity.this, "Error", "Enter valid email id");
                                btnAddTracker.setEnabled(true);
                            }

                        } else {
                            //Toast.makeText(getApplicationContext(), "This tracker already exist in your list", Toast.LENGTH_LONG).show();
                            btnAddTracker.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    public class SendTrackeeDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {

                String url = baseUrl1 + "sendEmailIdOfTrackee/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", userid);
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
                appendLog(AddTrackerActivity.this,"1 AddTrackerActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (responceStatus.equals("success")) {
                showAlertDialog1("Request Sent", "Trackee needs to accept your request.");
                btnAddTracker.setEnabled(true);
            } else {
                showAlertDialog(AddTrackerActivity.this, "Oops!", "The Trackee is not registered to iCare.");
                btnAddTracker.setEnabled(true);
            }
        }
    }

    public class SendTrackerDetails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            try {

                String url = baseUrl1 + "sendEmailIdOfTracker/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackeeId", userid);
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
                    result = result.replace("[", "");
                    result = result.replace("]", "");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");
                    responseID = mainObject.getString("UserId");
                    responseName = mainObject.getString("Name");

                } else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(AddTrackerActivity.this,"2 AddTrackerActivity "+e.toString()+date);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (responceStatus.equals("success")) {
                db.addTracker(new Tracker(responseID, responseName, emailId, flag, flag, flag, flag, liveTrackingFlag, defaultFlag));
                btnAddTracker.setEnabled(true);
                onBackPressed();
            } else {
                showAlertDialog(AddTrackerActivity.this, "Oops!", "The Tracker is not registered to iCare.");
                btnAddTracker.setEnabled(true);
            }
        }
    }

      /* Create the actionbar options menu */

    /*************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                break;

            default:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String drawerFrom = "A";
        if (from.equals("trackeeFragment")) {
            drawerFrom = "A";
        } else {
            drawerFrom = "B";
        }
        Intent i = new Intent(AddTrackerActivity.this, DrawerActivity.class);
        i.putExtra("Go_To_Fragment", drawerFrom);
        startActivity(i);
    }

    //Alert dialog code
    public void showAlertDialog1(String title, String message) {
        new AlertDialog.Builder(AddTrackerActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void progressDialog() {
        progress.setTitle("Wait");
        progress.setMessage("Please wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.show();
    }
}

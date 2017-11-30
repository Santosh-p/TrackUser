package sveltoz.icaretrackerapp.Activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;

public class EditProfileActivity extends AppCompatActivity {
    EditText edtUsername;
    Button btnOk, cancel;
    int userid;
    InputStream inputStream = null;
    String result = null;
    String name, email, trackerpin, trackeepin, type, status, responceStatus;
    String tempusername;
    DatabaseHandler db = new DatabaseHandler(this);
    ArrayList<String> possibleEmails = new ArrayList<String>();
    String lastEmail;
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    Random random = new Random();
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(EditProfileActivity.this);
        setContentView(R.layout.activity_edit_profile);
        //edtEmail = (AutoCompleteTextView) findViewById(R.id.edtemail);
        edtUsername = (EditText) findViewById(R.id.edtusername);
      //  VerifyEmail = (TextView) findViewById(R.id.tvVerifyEmail);
        btnOk = (Button) findViewById(R.id.btnok);
        cancel = (Button) findViewById(R.id.btncancel);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle("Edit Profile");
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
        //code for getting vallues from database
        List<User> user = db.getAllUsers();
        int count = 0;
        for (User cn : user) {
            count++;
        }
        if (!(count > 0)) {
            Intent intent1 = new Intent(EditProfileActivity.this, SignUpActivity.class);
            startActivity(intent1);
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

//Code for setting on focus
        edtUsername.setFocusableInTouchMode(true);
        edtUsername.requestFocus();
        if (edtUsername.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            edtUsername.setSelectAllOnFocus(true);
        }
        edtUsername.setFocusableInTouchMode(true);
        edtUsername.setSelectAllOnFocus(true);
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmails.add(account.name);
                lastEmail = account.name;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, possibleEmails);

        edtUsername.setText(name.toString());

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tempusername = edtUsername.getText().toString();
                if (!tempusername.matches("[a-zA-Z.? ]*")) {
                    edtUsername.setError("Special characters are not allowed");

                } else if (tempusername == null || tempusername.equals(""))
                {
                    edtUsername.setError("Enter name");
                }else {

                        if (name.equals(tempusername)) {
                            onBackPressed();

                        } else {
                            if (isNetworkAvailable(EditProfileActivity.this)) {
                                progressDialog("Updating...");
                                new SendUpdateDetails().execute();
                            }else {
                                Toast.makeText(EditProfileActivity.this, "Please check internet connection!", Toast.LENGTH_SHORT).show();
                            }

                        }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        int length=edtUsername.getText().length();
        edtUsername.setSelection(length);
    }

    // Code for sending user update details to service and update user information
    public class SendUpdateDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String tname = tempusername.replaceAll(" ", "%20");

            try {

                String url = baseUrl1+ "UpdateRegistrationDetails/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", userid);
                jsonObject.accumulate("Name", tempusername);
                jsonObject.accumulate("EmailId", email);
                jsonObject.accumulate("DeviceId", db.getToken());
                jsonObject.accumulate("Key", "profileUpdate");

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
                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    result = result.replace("[","");
                    result = result.replace("]","");
                    JSONObject mainObject = new JSONObject(result);

                    responceStatus = mainObject.getString("Status");
                }else
                    result = "Did not work!";

            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                Log.d("InputStream", e.getLocalizedMessage());

                e.printStackTrace();
                appendLog(EditProfileActivity.this,"1 EditProfileActivity "+e.toString()+date);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if ((progress != null) && progress.isShowing())
            progress.dismiss();
            if (responceStatus.equals("success")) {
                //result = "1";
                Log.d("Insert: ", "Inserting ..");
                int t = db.updateUser1(new User(userid, tempusername, email));
                if (t == 1) {
                    if ((progress != null) && progress.isShowing())
                    progress.dismiss();
                    Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(i);
                }
            } else if (responceStatus.equals("emailid already exists")) {
                Toast.makeText(getApplicationContext(), "user already exist", Toast.LENGTH_LONG).show();
            } else if (responceStatus.equals("emailid and phoneno already " +
                    "")) {
                Toast.makeText(getApplicationContext(), "Mobile number and Email already used", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "network issue", Toast.LENGTH_LONG).show();
            }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        //  inflater.inflate(R.menu.profile_activity_menu, menu);
        return true;
    }

    // Action bar related
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // button backpress
    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void progressDialog(String message) {
        progress.setMessage(message);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //  showAlertDialog("Oops!", "Network connection is poor!");
            }
        });
    }
}


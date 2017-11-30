package sveltoz.icaretrackerapp.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edtNewPass,edtConfirmPass;
    Button btnSubmit;
    String name,email,phone, pin,trackeepin,type,status;
    int userid;
    DatabaseHandler db = new DatabaseHandler(this);
    //String storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //Code for action bar with backbutton
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle("Change PIN");
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

        edtNewPass = (EditText)findViewById(R.id.editTextNewPassword);
        edtConfirmPass = (EditText)findViewById(R.id.editTextConfirmPassword);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        edtNewPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (s.length() == 4) {
                    edtNewPass.setError(null);
                    if (edtConfirmPass.getText().toString().equals(edtNewPass.getText().toString())) {
                        edtConfirmPass.setError(null);
                        pin = edtConfirmPass.getText().toString();
                    } else {
                        edtConfirmPass.setError("PIN mismatch");
                        pin=null;
                    }
                    // trackerpin = edtTextPin.getText().toString();
                } else {
                    edtNewPass.setError("Enter 4 Digit PIN");
                    pin=null;
                }
            }
        });
        edtConfirmPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
                if (edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                    edtConfirmPass.setError(null);
                    pin = edtNewPass.getText().toString();
                } else {
                    edtConfirmPass.setError("PIN mismatch");
                    pin=null;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtNewPass.length()<=3)
                {
                    Toast.makeText(getApplicationContext(), "Enter 4 digit no", Toast.LENGTH_LONG).show();

                }
                else if(!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString()))
                {
                    edtConfirmPass.setError("PIN mismatch");
                    edtConfirmPass.setFocusable(true);

                }
                else{
                if (pin != null){
                    User user1=db.getUserDetails();
                    userid = user1.get_user_id();
                    name = user1.get_name();
                    email =user1.get_email();
                    trackeepin = user1.get_trackee_pin();
                    type = user1.get_user_type();
                    status = user1.get_email_verify();
                    //storage = StorageActivity.storageFlag;
                    int t= db.updateUser(new User(userid,name, email, pin, pin,type,"yes"));
                    if(t == 1){
                        Intent i=new Intent(ChangePasswordActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                }}
            }
        });

        if (edtNewPass.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

    // button backpress
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
    }
}

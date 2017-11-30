package sveltoz.icaretrackerapp.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Activity.SignUpActivity.from;

public class SubmitOTPActivity extends AppCompatActivity {

    Button SubmitOTP;
    EditText edtotp;
    String OTP, FROM;
    String EdtOTP;
    public static String verifyflag = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_otp);

        //Code for action bar with backbutton
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle("Enter OTP");
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

        edtotp = (EditText) findViewById(R.id.edtOTP);
        SubmitOTP = (Button) findViewById(R.id.btnSubmitOTP);
        Bundle extra = getIntent().getExtras();
        OTP = extra.getString("OTP");
        FROM = extra.getString("FROM");
        SubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EdtOTP = edtotp.getText().toString();

                if (OTP.equals(EdtOTP)) {
                    if (FROM.equals("SignUpActivity")) {
                        Toast.makeText(SubmitOTPActivity.this, "Verified Successfully", Toast.LENGTH_SHORT).show();
                        verifyflag = "true";
                        onBackPressed();

                    } else if (FROM.equals("LoginWithEmailActivity")) {
                        Toast.makeText(SubmitOTPActivity.this, "Verified Successfully", Toast.LENGTH_SHORT).show();
                        verifyflag = "true";
                        onBackPressed();

                    } else if (FROM.equals("EditProfileActivity")) {
                        verifyflag = "true";
                        onBackPressed();
                        Toast.makeText(getApplicationContext(), "Verified Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Intent i = new Intent(SubmitOTPActivity.this, ChangePasswordActivity.class);
                        startActivity(i);
                    }
                } else {
                    edtotp.setError("Enter Correct OTP");
                }
            }
        });
    }

    //Code for Action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (FROM.equals("LoginActivity")) {
                    Intent i = new Intent(SubmitOTPActivity.this, LoginActivity.class);
                    startActivity(i);
                }else if (FROM.equals("LoginWithEmailActivity")) {
                    onBackPressed();
                }else if (FROM.equals("SignUpActivity")) {

                    onBackPressed();
                } else if (FROM.equals("EditProfileActivity")) {
                    Intent i = new Intent(SubmitOTPActivity.this, EditProfileActivity.class);
                    startActivity(i);
                }
            default:
                if (FROM.equals("LoginActivity")) {
                    Intent i = new Intent(SubmitOTPActivity.this, LoginActivity.class);
                    startActivity(i);
                }else if (FROM.equals("LoginWithEmailActivity")) {
                    onBackPressed();
                }else if (FROM.equals("SignUpActivity")) {
                    onBackPressed();
                } else if (FROM.equals("EditProfileActivity")) {
                    Intent i = new Intent(SubmitOTPActivity.this, EditProfileActivity.class);
                    startActivity(i);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // button backpress
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        if (FROM.equals("LoginActivity")) {
            Intent i = new Intent(SubmitOTPActivity.this, LoginActivity.class);
            startActivity(i);
        } else if (FROM.equals("EditProfileActivity")) {
            super.onBackPressed();
            from = "SubmitOTPActivity";
        } else if (FROM.equals("LoginWithEmailActivity")) {
            super.onBackPressed();
            from = "SubmitOTPActivity";
        }else if (FROM.equals("SignUpActivity")) {
            super.onBackPressed();
            from = "SubmitOTPActivity";
        }
    }
}

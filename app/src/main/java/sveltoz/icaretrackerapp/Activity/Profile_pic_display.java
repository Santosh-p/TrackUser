package sveltoz.icaretrackerapp.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sveltoz.icaretrackerapp.Adapter.TrackerTrackeeAdapter;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.OtherClasses.Utils;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Activity.LoginActivity.onActivity;
import static sveltoz.icaretrackerapp.Activity.ProfileActivity.name;
import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;

public class Profile_pic_display extends AppCompatActivity {
    ImageView ivPreview;
    DatabaseHandler dbHelper;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_display);
        dbHelper = new DatabaseHandler(this);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle(name);
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


        ivPreview = (ImageView) findViewById(R.id.iv_preview_image);
        if (onActivity.equals("profileActivity")) {
            try {
                dbHelper.open();
                byte[] bytes = dbHelper.retreiveImageFromDB();
                dbHelper.close();
                // Show Image from DB in ImageView
                ivPreview.setImageBitmap(Utils.getImage(bytes));
                dbHelper.close();
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                e.printStackTrace();
                appendLog(Profile_pic_display.this,"1 Profile_pic_display "+e.toString()+date);
            }
        } else {
            dbHelper = new DatabaseHandler(getApplicationContext());

            if (loadImageFromDB()) {
                //**** error when click on trackee name

                ivPreview.setImageBitmap(Utils.getImage(bytes));
                Toast.makeText(getApplicationContext(), "Image Loaded from Database...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Image size is too large!", Toast.LENGTH_LONG).show();
            }
        }
    }

    Boolean loadImageFromDB() {
        try {
            dbHelper.open();
            bytes = dbHelper.getStoredImage(Integer.parseInt(TrackerTrackeeAdapter.selectedtrackertrackeeid));
            dbHelper.close();
            return true;
        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            dbHelper.close();
            e.printStackTrace();
            appendLog(Profile_pic_display.this,"2 Profile_pic_display "+e.toString()+date);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (onActivity.equals("profileActivity")) {
            Intent i = new Intent(Profile_pic_display.this, ProfileActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        } else {
            Intent i = new Intent(Profile_pic_display.this, DrawerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.putExtra("Go_To_Fragment", "A");
            startActivity(i);
        }
    }
}

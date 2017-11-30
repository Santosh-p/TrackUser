package sveltoz.icaretrackerapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sveltoz.icaretrackerapp.Adapter.CropingOptionAdapter;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.OtherClasses.CropingOption;
import sveltoz.icaretrackerapp.OtherClasses.Utils;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.progressDialog;

public class ProfileActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    ImageButton ChangePass, btnEdit;
    int userid;
    Bitmap bitmap;
    Bitmap photo;
    DatabaseHandler dbHelper;
    private Uri mImageCaptureUri;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private File outPutFile = null;
    String email, trackerpin, trackeepin, type, status;
    public static String name;
    DatabaseHandler db = new DatabaseHandler(this);
    TextView Username, Emailid, deletAc;
    InputStream inputStream = null;
    String result = null, responceStatus;
    ImageView circularImageView, circularImageViewDisplay;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        LoginActivity.onActivity = "profileActivity";
        progress = new ProgressDialog(ProfileActivity.this);
        //Code for action bar with backbutton
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_keyboard_backspace_white_24dp);
        getSupportActionBar().setTitle("Profile");
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
            Intent intent1 = new Intent(ProfileActivity.this, SignUpActivity.class);
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

        circularImageView = (ImageView) findViewById(R.id.iv_camera);
        circularImageViewDisplay = (ImageView) findViewById(R.id.circleView);


        Username = (TextView) findViewById(R.id.tvUsername);
        Emailid = (TextView) findViewById(R.id.tvEmailid);
        ChangePass = (ImageButton) findViewById(R.id.btnChangePass);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);
        deletAc = (TextView) findViewById(R.id.tvDeleteAc);


        String name1 = StringUtils.capitalize(name.toString());
        Username.setText(name1);
        Emailid.setText(email.toString());

        circularImageViewDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = null;
                try {
                    dbHelper.open();
                    bytes = dbHelper.retreiveImageFromDB();
                    dbHelper.close();
                } catch (Exception e) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    dbHelper.close();
                    e.printStackTrace();
                    appendLog(ProfileActivity.this,"1 ProfileActivity "+e.toString()+date);
                }
                if (bytes != null) {
                    Intent i = new Intent(ProfileActivity.this, Profile_pic_display.class);
                    startActivity(i);
                } else {

                }
            }
        });


        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageOption();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
            }
        });


        dbHelper = new DatabaseHandler(this);

        if (loadImageFromDB()) {

        } else {
        }
    }

    public void changePasswordClick(View v) {
        Intent i = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
        i.putExtra("FROM", "ChangePass");
        startActivity(i);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectImageOption() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Remove"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Remove")) {
                    dialog.dismiss();
                    circularImageViewDisplay.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    db.RemoveImage();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((progress != null) && progress.isShowing())
            progress.dismiss();
        progress = null;
    }

    // button backpress
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProfileActivity.this, DrawerActivity.class);
        i.putExtra("Go_To_Fragment", "A");
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = null;

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();

            System.out.println("Gallery Image URI : " + mImageCaptureUri);
            CropingIMG();


        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            System.out.println("Camera Image URI : " + mImageCaptureUri);
            CropingIMG();
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    String filepath = outPutFile.toString();
                    photo = decodeFile(filepath);
                    mImageCaptureUri = Uri.fromFile(outPutFile);
                    if (null != mImageCaptureUri) {
                        // Saving to Database...
                        if (saveImageInDB(mImageCaptureUri)) {
                            if (mImageCaptureUri != null) {
                                try {
                                    // OI FILE Manager
                                    String filemanagerstring = mImageCaptureUri.getPath();
                                    // MEDIA GALLERY
                                    String selectedImagePath = getPath(mImageCaptureUri);

                                    if (selectedImagePath != null) {
                                        filepath = selectedImagePath;
                                    } else if (filemanagerstring != null) {
                                        filepath = filemanagerstring;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Unknown path",
                                                Toast.LENGTH_LONG).show();
                                        Log.e("Bitmap", "Unknown path");
                                    }
                                    if (filePath != null) {
                                        decodeFile(filepath);
                                    } else {
                                        bitmap = null;
                                    }
                                } catch (Exception e) {
                                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                    String date = df.format(Calendar.getInstance().getTime());
                                    Toast.makeText(getApplicationContext(), "Internal error",
                                            Toast.LENGTH_LONG).show();
                                    Log.e(e.getClass().getName(), e.getMessage(), e);
                                    e.printStackTrace();
                                    appendLog(ProfileActivity.this,"2 ProfileActivity "+e.toString()+date);
                                }
                            }
                        }
                    }
                    circularImageViewDisplay.setImageBitmap(photo);
                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(Calendar.getInstance().getTime());
                e.printStackTrace();
                appendLog(ProfileActivity.this,"3 ProfileActivity "+e.toString()+date);
            }
        }
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private Bitmap decodeFile(String f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) { DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            e.printStackTrace();
            appendLog(ProfileActivity.this,"4 ProfileActivity "+e.toString()+date);
        }
        return null;
    }


    // Save the
    Boolean saveImageInDB(Uri selectedImageUri) {
        try {
            dbHelper.open();
            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] inputData = Utils.getBytes(iStream);
            dbHelper.insertImage(inputData);
            dbHelper.close();
            return true;
        } catch (IOException e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            dbHelper.close();
            e.printStackTrace();
            appendLog(ProfileActivity.this,"5 ProfileActivity "+e.toString()+date);
            return false;
        }
    }

    Boolean loadImageFromDB() {
        try {
            dbHelper.open();
            byte[] bytes = dbHelper.retreiveImageFromDB();
            dbHelper.close();
            if (bytes!=null) {
                // Show Image from DB in ImageView
                circularImageViewDisplay.setImageBitmap(Utils.getImage(bytes));
            }
            return true;


        } catch (Exception e) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            dbHelper.close();
            e.printStackTrace();
            appendLog(ProfileActivity.this,"6 ProfileActivity "+e.toString()+date);
            return false;
        }
    }

    public void deleteAccount(View v) {

        if (isNetworkAvailable(ProfileActivity.this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new DeleteAccountDetails().execute();
                            progressDialog(progress, "", "Deleting..");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(ProfileActivity.this, "Please check internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();

        }
        return super.onKeyDown(keyCode, event);
    }

    public class DeleteAccountDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String url = baseUrl1 + "deleteuser/";
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserId", userid);

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

                appendLog(ProfileActivity.this,"7 ProfileActivity "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (responceStatus.equals("success")) {
                db.clearAppData();

                Toast.makeText(getApplicationContext(), "Account deleted...", Toast.LENGTH_LONG).show();
                if ((progress != null) && progress.isShowing())
                    progress.dismiss();
                Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);
            } else if (responceStatus.equals("fail")) {

            }
        }
    }

}

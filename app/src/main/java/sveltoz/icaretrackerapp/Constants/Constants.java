package sveltoz.icaretrackerapp.Constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by apple on 2/13/17.
 */

public class Constants {

    // public static ProgressDialog progress;

    //http://192.168.10.22/ICareTrackerWCF/FileService.svc/
    public static String baseUrl = "http://202.88.154.118/ICareTrackerWCF/FileService.svc/";
   // static File logFile = new File("sdcard/log.file");
    static File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICareTracker/log.file");

    public static String baseUrl1 = "http://icare.sveltoz.com/api/Home/";
     public static String baseUrlUploadFile="http://icare.sveltoz.com/api/Upload/user/UploadFile";
    public static String baseUrlDownloadFile="http://icare.sveltoz.com/api/Home/DownloadFile?fileName=";
//    public static String baseUrl1 = "http://202.88.154.118/UpdatedICareWebAPI/api/Home/";
//    public static String baseUrlUploadFile="http://202.88.154.118/UpdatedICareWebAPI/api/Upload/user/UploadFile";
//    public static String baseUrlDownloadFile="http://202.88.154.118/UpdatedICareWebAPI/api/Home/DownloadFile?fileName=";


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static boolean checkGPSService(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled || !network_enabled) {
            return false;
        } else
            return true;
    }

    public static void progressDialog(ProgressDialog progress,String title,String message) {
        progress.setTitle(title);
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

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
    public static void showAlertDialog(Context context,String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showAlertDialogforException(final Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton("Send Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        sendEmail(context);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //Generate log file and print exception in it.
    public static void appendLog(Context context,String text)
    {
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        showAlertDialogforException(context,"Oops","Something went wrong.");
    }

    public static void sendEmail(Context context) {

        String pathname= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ICareTracker/";
        String filename="log.file";
        File file=new File(pathname, filename);

        Log.i("Send email", "");
        String[] TO = {"pramodp@sveltoz.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "iCare Tracker Log File");
      //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
          //  finish();
          //  Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }



//Generate log file and print exception in it.
    public static void appendLogforService(Context context,String text)
    {
        // File logFile = new File("sdcard/log.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    //    showAlertDialogforException(context,"Oops","Something went wrong.");
    }


}

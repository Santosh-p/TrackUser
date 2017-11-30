package sveltoz.icaretrackerapp.Activity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sveltoz.icaretrackerapp.Adapter.ListViewAdapter;
import sveltoz.icaretrackerapp.Adapter.NotificationListAdapter;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Request;
import sveltoz.icaretrackerapp.R;

public class PendingNotificationsActivity extends AppCompatActivity {

    private ListView listView;

    ArrayList<Request> reqList = new ArrayList<>();
    ArrayList<Request> sequencedNotifications = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);
    public NotificationListAdapter itemAdapter;
    public ListViewAdapter listViewAdapter;
    int Count,NotificationCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_notifications);

        PendingNotificationsActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        listView = (ListView) findViewById(R.id.listView);
    //code to get notifications count from database
        List<Request> requests = db.getStoredRequests();
        LoginActivity.onActivity = "PendingNotificationsActivity";
        for (Request cn : requests) {
            Count = requests.size();
        }

        NotificationCount = Count;
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
        reqList.addAll(db.getStoredRequests());
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(0);

        for (int j = reqList.size() - 1; j >= 0; j--) {
            Request r=reqList.get(j);
            sequencedNotifications.add(r);
        }

        itemAdapter = new NotificationListAdapter(PendingNotificationsActivity.this,
                R.layout.notification_list_row, sequencedNotifications);

        listView.setAdapter(itemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clearall:
                if (Count == 0) {
                    Toast.makeText(getApplication(), "No notifications!", Toast.LENGTH_LONG).show();

                } else {
                    AlertToDeleteAllRequests();
                }
                break;
            default:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("Go_To_Fragment", "A");
        startActivity(i);
    }
    public void AlertToDeleteAllRequests(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PendingNotificationsActivity.this);
        builder.setTitle("")
                .setMessage("Delete all requests?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteAllRequest();
                        // deleteAllRequest
                        Toast.makeText(getApplication(), "Deleted Successfully..", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("Go_To_Fragment", "A");
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

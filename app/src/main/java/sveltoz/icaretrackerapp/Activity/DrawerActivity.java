package sveltoz.icaretrackerapp.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import sveltoz.icaretrackerapp.BadgeDrawable;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Request;
import sveltoz.icaretrackerapp.Fragments.TabFragment;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Fragments.TabFragment.viewPager;

public class DrawerActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Intent intent;
    static boolean flag;
    private LayerDrawable mCartMenuIcon;
    private int NotificationCount;
    int Count;
    Toolbar toolbar;
    DatabaseHandler db = new DatabaseHandler(this);

 public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.apptheme)));

        // status bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.Teal));
        }
        class SlideMenuClickListener implements
                ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // display view for selected nav drawer item
            }
        }
//code to get notifications count from database
        List<Request> requests = db.getStoredRequests();
        for (Request cn : requests) {
            Count = requests.size();
        }
        NotificationCount = Count;

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    intent = new Intent(DrawerActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.AboutUs) {
                    intent = new Intent(DrawerActivity.this, AboutUsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.RateUs) {
                    Uri uri = Uri.parse("market://details?id=" + DrawerActivity.this.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + DrawerActivity.this.getPackageName())));

                        e.printStackTrace();
                        appendLog(DrawerActivity.this,"1 DrawerActivity "+e.toString()+date);
                    }
                }
                if (menuItem.getItemId() == R.id.Logout) {
                    intent = new Intent(DrawerActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                return false;
            }

        });

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar1, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    // Double back press exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (flag) {
            Intent i = new Intent(DrawerActivity.this, DrawerActivity.class);
            i.putExtra("Go_To_Fragment", "A");
            startActivity(i);
            flag = false;
        }
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extra = getIntent().getExtras();
        String from = extra.getString("Go_To_Fragment");
        LoginActivity.onActivity = "drawerActivity";
        if (from.equals("A")) {
            TabFragment.viewPager.setCurrentItem(0, true);
            // TabFragment.tabLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        } else {
            TabFragment.viewPager.setCurrentItem(1, true);
            //  TabFragment.tabLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.orange));
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoginActivity.onActivity = "drawerActivity";
    }

    public static void navigateFragment(int position) {
        viewPager.setCurrentItem(position, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                intent = new Intent(DrawerActivity.this, PendingNotificationsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        mCartMenuIcon = (LayerDrawable) menu.findItem(R.id.action_notification).getIcon();
        setBadgeCount(this, mCartMenuIcon, String.valueOf(NotificationCount));
        return super.onCreateOptionsMenu(menu);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
}
package sveltoz.icaretrackerapp.Fragments;

/**
 * Created by Ratan on 7/29/2015.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sveltoz.icaretrackerapp.Activity.AddTrackerActivity;
import sveltoz.icaretrackerapp.Activity.DrawerActivity;
import sveltoz.icaretrackerapp.Activity.PrivacySettingActivity;
import sveltoz.icaretrackerapp.Adapter.TrackerTrackeeAdapter;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.TrackerTrackeeDetails;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static android.R.id.list;
import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;

public class TrackerFragment extends ListFragment implements AdapterView.OnItemClickListener {


    ListView listView1;
    ArrayList<String> mylist = new ArrayList<String>();
    ArrayList<String> userIdList = new ArrayList<String>();
    Context mContext;
    DatabaseHandler db = new DatabaseHandler(getContext());
    String uID, UserID;
    String trackerId;
    InputStream inputStream = null;
    String result = null;
    String responceStatus;
    ArrayList<TrackerTrackeeDetails> trackerList;
    public TrackerTrackeeAdapter itemAdapter;
    byte[] bytes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //listView1 = (ListView) view.findViewById(R.id.list);
        DatabaseHandler db = new DatabaseHandler(getContext());
        List<Tracker> tracker = db.getAllTrackers();
        int count = 0;
        trackerList = new ArrayList<TrackerTrackeeDetails>();
        for (Tracker cn : tracker) {
            count++;
            mylist.add(cn.get_tracker_Name());
            userIdList.add(cn.get_user_Id());
            String str = cn.get_tracker_App_Status();
            try {
                db.open();
                bytes = db.getStoredImage(Integer.parseInt(cn.get_user_Id()));
                //bytes = db.retrivetrackertrackeeimage(Integer.parseInt(cn.get_user_Id()));
                db.close();
            } catch (Exception e) {
                db.close();
                e.printStackTrace();
                appendLog(getContext(), "1 TrackerFragment " + e.toString());
            }

            TrackerTrackeeDetails item1 = new TrackerTrackeeDetails(cn.get_user_Id(), cn.get_tracker_Name(), cn.get_tracker_App_Status(), bytes);
            trackerList.add(item1);
        }
        User user1 = db.getUserDetails();
        trackerId = String.valueOf(user1.get_user_id());
    }

    @Override
    public void onStart() {
        super.onStart();
        // TabFragment.tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trackee_fragment, container, false);
        FloatingActionButton AddTracker = (FloatingActionButton) view.findViewById(R.id.btnAddTracker);

        listView1 = (ListView) view.findViewById(list);

        listView1 = (ListView) view.findViewById(list);
//        ColorDrawable myColor = new ColorDrawable(
//                this.getResources().getColor(R.color.white)
//        );

//        listView1.setDivider(myColor);
        // listView1.setBackgroundColor(getResources().getColor(R.color.White_Grey));
        //  listView1.setDividerHeight(3);

        if (trackerList != null && !trackerList.isEmpty()) {
            itemAdapter = new TrackerTrackeeAdapter(getContext(), R.layout.tracker_trackee_list_row, trackerList);

            listView1.setAdapter(itemAdapter);
        }

//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mylist);
//        listView1.setAdapter(adapter);

        // Register the ListView  for Context menu
        registerForContextMenu(listView1);

//        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                uID = userIdList.get(position);
//               String name = mylist.get(position);
//                //  Toast.makeText(getContext(), "On long click " + position, Toast.LENGTH_LONG).show();
//                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                alertDialog.setTitle("Do you want to delete tracker? ");
//                alertDialog.setMessage("" + name);
//                alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//
//                        if (isNetworkAvailable()) {
//                            new SendTrackerDelete().execute();
//                        } else {
//                            showAlertDialog("Oops!", "No network Available!");
//                            // Toast.makeText(getContext(), "No network Available!", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
//                alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//
//                alertDialog.show();
//                return true;
//
//
//            }
//        });
        AddTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isNetworkAvailable()) {
                    // if (isInternetAvailable()) {
                    Intent i = new Intent(getContext(), AddTrackerActivity.class);
                    i.putExtra("from", "trackerFragment");
                    startActivity(i);
                } else {
                    showAlertDialog("Oops!", "No network available!");
                    //  Toast.makeText(getContext(), "check network!", Toast.LENGTH_LONG).show();
                }
                //   } else {
                //        Toast.makeText(getContext(), "No network Available!", Toast.LENGTH_LONG).show();
                //   }
            }
        });

        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserID = userIdList.get(position);
                //  Toast.makeText(getContext(), "On long click " + position, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getContext(), PrivacySettingActivity.class);
                i.putExtra("UserID", UserID);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.trackee_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_tracker) {
            Intent i = new Intent(getContext(), AddTrackerActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_changePass) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {


            return true;
        } else {
            return false;
        }
    }

    public boolean isInternetAvailable() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);

        } catch (Exception e) {
            e.printStackTrace();
            appendLog(getContext(), "2 TrackerFragment " + e.toString());
            return false;

        }
    }

    public class SendTrackerDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {

                String url = baseUrl1 + "DeleteTracker/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", uID);
                jsonObject.accumulate("TrackeeId", trackerId);
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
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(getContext(), "3 TrackerFragment " + e.toString());
            }

//
//            try {
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                URI uri = new URI(baseUrl+"DeleteTracker/" + uID + "/" + trackerId);
//                HttpGet httpget = new HttpGet(uri);
//                httpget.setHeader("Accept", "application/json");
//                httpget.setHeader("Content-type", "application/json; charset=utf-8");
//
//                HttpResponse response = httpClient.execute(httpget);
//                HttpEntity responseEntity = response.getEntity();
//
//                inputStream = responseEntity.getContent();
//                // json is UTF-8 by default
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                StringBuilder sb = new StringBuilder();
//
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                result = sb.toString();
//                JSONObject jObject = new JSONObject(result);
//                JSONArray jArray = jObject.getJSONArray("DeleteTrackerResult");
//                for (int i = 0; i < jArray.length(); i++) {
//                    try {
//                        JSONObject oneObject = jArray.getJSONObject(i);
//                        // Pulling items from the array
//                        responceStatus = oneObject.getString("Status");
//                    } catch (JSONException e) {
//                        // Oops
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            DatabaseHandler db = new DatabaseHandler(getContext());
            if (responceStatus.equals("success")) {

                db.deleteTracker(uID);
                db.deleteLocSetting(Integer.parseInt(uID));
                Intent i = new Intent(getContext(), DrawerActivity.class);
                i.putExtra("Go_To_Fragment", "B");
                startActivity(i);
                Toast.makeText(getContext(), "Tracker deleted successfully..", Toast.LENGTH_LONG).show();
            } else if (responceStatus.equals("fail")) {
                db.deleteTracker(uID);
                db.deleteLocSetting(Integer.parseInt(uID));
                Toast.makeText(getContext(), "Access Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // get user history temporery code
    public void showAlertDialog(String title, String message) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("go to settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        TrackeeFragment.SelectedFragment = "TrackerFragment";
        //  menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (TrackeeFragment.SelectedFragment.equals("TrackerFragment")) {
            if (item.getTitle() == "Delete") {
                int pos = info.position;

                TrackerTrackeeDetails itemname = trackerList.get(pos);
                String filename = itemname.get_name();
                uID = String.valueOf(itemname.get_user_id());
                if (isNetworkAvailable()) {
                    new SendTrackerDelete().execute();
                } else {
                    showAlertDialog("Oops!", "No network available!");
                    // Toast.makeText(getContext(), "No network Available!", Toast.LENGTH_LONG).show();
                }

            }
        }
        return super.onContextItemSelected(item);
    }
}


package sveltoz.icaretrackerapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import sveltoz.icaretrackerapp.Activity.MapsActivity;
import sveltoz.icaretrackerapp.Adapter.TrackerTrackeeAdapter;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Trackee;
import sveltoz.icaretrackerapp.DBClasses.TrackerTrackeeDetails;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static android.content.Context.MODE_PRIVATE;
import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;

/**
 * Created by Ratan on 7/29/2015.
 */


public class TrackeeFragment extends ListFragment implements AdapterView.OnItemClickListener {
    ListView listView1;

    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> userIdList = new ArrayList<String>();
    Context mContext;
    String result = null;
    String responceStatus;
    String uID, name;
    String trackerId, trackeeId;
    public String Keyword, liveTrackingFlag;
    public ProgressDialog progress;
    InputStream inputStream = null;
    public TrackerTrackeeAdapter itemAdapter;
    ArrayList<TrackerTrackeeDetails> trackeeList;
    public static String SelectedFragment;
    byte[] bytes;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler db = new DatabaseHandler(getContext());
        List<Trackee> trackee = db.getAllTrackees();
        int count = 0;
        trackeeList = new ArrayList<TrackerTrackeeDetails>();
        for (Trackee cn : trackee) {
            count++;
            nameList.add(cn.get_trackee_Name());
            userIdList.add(cn.get_user_Id());

            try {
                db.open();
                bytes = db.getStoredImage(Integer.parseInt(cn.get_user_Id()));
                db.close();
            } catch (Exception e) {
                db.close();
                e.printStackTrace();
                appendLog(getContext(), "4 TrackeeFragment " + e.toString());
            }
            TrackerTrackeeDetails item1 = new TrackerTrackeeDetails(cn.get_user_Id(),cn.get_trackee_Name(), cn.get_trackee_App_Status(),bytes);
            trackeeList.add(item1);
        }
        User user1 = db.getUserDetails();
        trackerId = String.valueOf(user1.get_user_id());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracker_fragment, container, false);

        // Set the adapter
        listView1 = (ListView) view.findViewById(android.R.id.list);
        registerForContextMenu(listView1);
//        ColorDrawable myColor = new ColorDrawable(
//                this.getResources().getColor(R.color.white)
//        );
//        listView1.setDivider(myColor);
        //listView1.setBackgroundColor(getResources().getColor(R.color.White_Grey));
        //listView1.setDividerHeight(3);


        if (trackeeList != null && !trackeeList.isEmpty()) {
            itemAdapter = new TrackerTrackeeAdapter(getContext(), R.layout.tracker_trackee_list_row, trackeeList);

            listView1.setAdapter(itemAdapter);
        }

        FloatingActionButton btnAddTrackee = (FloatingActionButton) view.findViewById(R.id.btnAddTrackee);
        btnAddTrackee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isNetworkAvailable()) {
                    Intent i = new Intent(getContext(), AddTrackerActivity.class);
                    i.putExtra("from", "trackeeFragment");
                    startActivity(i);
                } else {
                    showAlertDialog("Oops!", "No network available!");
                    //   Toast.makeText(getContext(), "No network Available!", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (trackeeList != null && !trackeeList.isEmpty()) {
            itemAdapter = new TrackerTrackeeAdapter(getContext(), R.layout.tracker_trackee_list_row, trackeeList);

            listView1.setAdapter(itemAdapter);
        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, nameList);
//        listView1.setAdapter(adapter);
//        registerForContextMenu(listView1);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        uID = userIdList.get(position);
        name = nameList.get(position);

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("selectedUserId", uID);
        editor.putString("name", name);
        editor.commit();

        Keyword = "batreq";
        if (isNetworkAvailable()) {
            // progressDialog();
            liveTrackingFlag = "OFF";
            new SendNotificationForRequest().execute();
        } else {
            showAlertDialog("Network Error!", "Please check internet connection");
        }

        Intent intent1 = new Intent(getActivity(), MapsActivity.class);
        intent1.putExtra("from", "trackermain");
        startActivity(intent1);
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
            appendLog(getContext(), "1 TrackeeFragment " + e.toString());
            return false;
        }
    }

    // code for send delete trackee request to service
    public class SendTrackeeDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {

                String url = baseUrl1+ "DeleteTrackee/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", trackerId);
                jsonObject.accumulate("TrackeeId", uID);
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
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(getContext(), "2 TrackeeFragment " + e.toString());
            }



//            try {
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                URI uri = new URI(baseUrl+"DeleteTrackee/" + trackerId + "/" + uID);
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
//                JSONArray jArray = jObject.getJSONArray("DeleteTrackeeResult");
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

                db.deleteTrackee(uID);

                Intent i = new Intent(getContext(), DrawerActivity.class);
                i.putExtra("Go_To_Fragment", "A");
                startActivity(i);
                Toast.makeText(getContext(), "Trackee deleted successfully..", Toast.LENGTH_LONG).show();
            } else if (responceStatus.equals("fail")) {
                db.deleteTrackee(uID);
                Toast.makeText(getContext(), "Access Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showAlertDialog(String title, String message) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("go to settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        // startActivity(
                        //         new Intent(Settings.ACTION_SETTINGS));

                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class SendNotificationForRequest extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            try {

                String url = baseUrl1+ "notification/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Keyword", Keyword);
                jsonObject.accumulate("TrackeeUserId", uID);
                jsonObject.accumulate("TrackerUserId", trackerId);
                jsonObject.accumulate("LiveTrackingsts", liveTrackingFlag);

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
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
                appendLog(getContext(), "3 TrackeeFragment " + e.toString());
            }
//            try {
//                String link = "http://202.88.154.118/ICareTrackerWCF/FileService.svc/notification/" + Keyword + "/" + uID + "/" + trackerId + "/" + liveTrackingFlag;
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet();
//                request.setURI(new URI(link));
//                HttpResponse response = client.execute(request);
//                Log.i("responce=", response.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return null;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        SelectedFragment = "TrackeeFragment";
        //  menu.setHeaderTitle("Select the action");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (SelectedFragment.equals("TrackeeFragment")) {
            if (item.getTitle() == "Delete") {
                int pos = info.position;
                TrackerTrackeeDetails itemname = trackeeList.get(pos);
                uID = String.valueOf(itemname.get_user_id());
                if (isNetworkAvailable()) {
                    new SendTrackeeDelete().execute();
                } else {
                    showAlertDialog("Oops!", "No network available!");
                }
            }
        }
        return super.onContextItemSelected(item);
    }

}



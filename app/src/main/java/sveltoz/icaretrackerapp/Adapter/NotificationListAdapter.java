package sveltoz.icaretrackerapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
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

import sveltoz.icaretrackerapp.Activity.AddTrackerActivity;
import sveltoz.icaretrackerapp.Activity.DrawerActivity;
import sveltoz.icaretrackerapp.Activity.PendingNotificationsActivity;
import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.Request;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;
import sveltoz.icaretrackerapp.R;

import static sveltoz.icaretrackerapp.Constants.Constants.appendLog;
import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;
import static sveltoz.icaretrackerapp.Constants.Constants.progressDialog;
import static sveltoz.icaretrackerapp.Constants.Constants.showAlertDialog;

/**
 * Created by pramod on 11/29/16..
 */
public class NotificationListAdapter extends BaseAdapter implements View.OnClickListener {
    ProgressDialog progress;
    private LayoutInflater inflater;
    private Activity myContext;
    private ArrayList<Request> datas;
    private Context context;
    List<Request> reqList1;
    private LayoutInflater v;
    Fragment fragment = null;
    private static final int AD_INDEX = 3;
    FragmentManager mFragmentManager;
    private NotificationListAdapter adapter;

    Boolean trackerFlag = true;
    String Flag = "ON", result = null, responceStatus;
    int requestID;
    String trackerId, trackerMail, trackername, accept, notificationType;
    InputStream inputStream = null;

    String defaultFlag = "true";

    public NotificationListAdapter(Context context, int textViewResourceId,
                                   ArrayList<Request> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        //  alertDialog=new SpotsDialog(myContext,R.style.Custom_Progress_Dialog);
        this.datas = objects;
        inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // imageLoader.clearCache();
        v = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        progress = new ProgressDialog(myContext);
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        holder = new ViewHolder();
        final Request post = (Request) datas.get(position);


        Request ei = (Request) post;
        vi = v.inflate(R.layout.notification_list_row, null);
        final TextView postNameView = (TextView) vi.findViewById(R.id.textViewName);
        final TextView postDescView = (TextView) vi.findViewById(R.id.textViewDesc);
        final Button postAcceptButton = (Button) vi.findViewById(R.id.btnAccept);
        final ImageButton postDeleteButton = (ImageButton) vi.findViewById(R.id.btnDelete);
        //postAcceptButton.setTypeface(font);

        notificationType = post.get_req_user_type();
        String name = post.get_req_user_name();
        name = StringUtils.capitalize(name);
        if (notificationType.equals("newtracker")) {
            postAcceptButton.setVisibility(View.VISIBLE);
            postNameView.setText("Request from " + name);
            postDescView.setText("Add as a Tracker");
        } else if (notificationType.equals("newtrackee")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Added as trackee");
        } else if (notificationType.equals("addtrackeeres")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Accepted your request");
        } else if (notificationType.equals("locon")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Allowed you to track location");
        } else if (notificationType.equals("locoff")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Denied you to track location");
        } else if (notificationType.equals("hison")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Allowed you to access call history");
        } else if (notificationType.equals("hisoff")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Denied you to access call history");
        } else if (notificationType.equals("autoon")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Allowed you to record voice");
        } else if (notificationType.equals("autooff")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Denied you to record voice");
        } else if (notificationType.equals("imgon")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Allowed you to capture image");
        } else if (notificationType.equals("imgoff")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Denied you to capture image");
        } else if (notificationType.equals("trackerdeletedyou")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Removed you");
        } else if (notificationType.equals("trackeedeletedyou")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Removed you");
        } else if (notificationType.equals("userupdatedprofile")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Updated profile.");
        } else if (notificationType.equals("DeletedAccount")) {
            postAcceptButton.setVisibility(View.GONE);
            postNameView.setText(name);
            postDescView.setText("Deleted iCare Tracker Account ");
        }
        postAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(myContext)) {
                    trackerId = post.get_req_user_id();
                    trackername = post.get_req_user_name();
                    trackerMail = post.get_req_user_emailid();
                    requestID = post.get_request_id();
                    accept = "yes";
                    progressDialog(progress, "", "Loading...");
                    new SendNotificationResponseOnceUserAccept().execute();
                }else {

                    showAlertDialog(myContext,"Network Error!", "Please check internet connection");
                }
            }
        });
        postDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                trackerId = post.get_req_user_id();
                db.deleteRequest(post.get_request_id());
                db.deleteRequest(requestID);
                reqList1 = db.getStoredRequests();
                if (reqList1.isEmpty()) {
                    Intent i = new Intent(context, DrawerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("Go_To_Fragment", "A");
                    context.startActivity(i);
                }else {
                    Intent intent = new Intent(context, PendingNotificationsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
        });

        return vi;
    }

    //code for send notification to add or not as a tracker
    class SendNotificationResponseOnceUserAccept extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            try {
                DatabaseHandler db = new DatabaseHandler(context);
                User user1 = db.getUserDetails();
                int userid = user1.get_user_id();
                String url = baseUrl1 + "SendAddTrackeeNotificationToTrackerAsResponse/";

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("TrackerId", trackerId);
                jsonObject.accumulate("TrackeeId", userid);
                jsonObject.accumulate("yesnoreply", accept);

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
                appendLog(context,"1 NotificationListAdapter "+e.toString()+date);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            DatabaseHandler db = new DatabaseHandler(context);
            List<Tracker> tracker = db.getAllTrackers();
            if (tracker.size() == 0) {
                trackerFlag = true;
            } else {
                for (Tracker cn : tracker) {
                    //  mylist.add(cn.get_tracker_Name());
                    if (trackerMail.equals(cn.get_tracker_Email())) {
                        trackerFlag = false;
                    } else {
                        // Toast.makeText(getApplicationContext(), "This tracker already exist", Toast.LENGTH_LONG).show();
                    }
                }
            }
            if (trackerFlag) {
                db.addTracker(new Tracker(trackerId, trackername, trackerMail, Flag, Flag, Flag, Flag, AddTrackerActivity.liveTrackingFlag, defaultFlag));
            }


            db.deleteRequest(requestID);

            reqList1 = db.getStoredRequests();
            for(int i=0;i<reqList1.size();i++){
                Request r=reqList1.get(i);
                String reqType=r.get_req_user_type();
                if(reqType.equals("newtracker")){
                    String email=r.get_req_user_emailid();
                    if(email.equals(trackerMail)){
                        requestID = r.get_request_id();
                        db.deleteRequest(requestID);
                    }
                }
            }
            reqList1 = db.getStoredRequests();
            if (reqList1.isEmpty()) {
                Intent i = new Intent(context, DrawerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("Go_To_Fragment", "B");
                context.startActivity(i);
            } else {
                Intent intent = new Intent(context, PendingNotificationsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
    }
}



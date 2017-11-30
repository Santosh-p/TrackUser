package sveltoz.icaretrackerapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sveltoz.icaretrackerapp.DBClasses.DatabaseHandler;
import sveltoz.icaretrackerapp.DBClasses.LocationSetting;
import sveltoz.icaretrackerapp.DBClasses.Tracker;
import sveltoz.icaretrackerapp.DBClasses.User;

import static sveltoz.icaretrackerapp.Constants.Constants.baseUrl1;
import static sveltoz.icaretrackerapp.Constants.Constants.convertInputStreamToString;
import static sveltoz.icaretrackerapp.Constants.Constants.isNetworkAvailable;

public class MyBroadcastReceiver extends BroadcastReceiver {
	MediaPlayer mp;
	List<LocationSetting> locationSetting;
	DatabaseHandler db;
	String uUserId1,uName1,uEmail1,autoflag1, hisflag1, Locflag1, imgflag1, responceStatus1, appStatusFlag1, liveTrackingFlag1,userid,settingkeyword,result,responceStatus;
	ArrayList<String> trackerUserIdList = new ArrayList<String>();
	InputStream inputStream = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
	@Override
	public void onReceive(Context context, Intent intent) {


		db = new DatabaseHandler(context);
		locationSetting = db.getAlllocSettings();
		String alertTime = "";
		String currentDateTimeString;
		for (LocationSetting cn : locationSetting) {
			alertTime = cn.get_locSettingTime();
			if(alertTime != "" || alertTime != null) {
				currentDateTimeString = sdf.getDateTimeInstance().format(new Date());
				try {
					User user1 = db.getUserDetails();
					userid = String.valueOf(user1.get_user_id());
					if(currentDateTimeString.contains("-")){
						sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
					}else if(Character.isLetter(currentDateTimeString.charAt(0))) {
						sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
					}else {
						sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
					}

					final Date date = sdf.parse(currentDateTimeString);

					sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
					final Calendar calendar = Calendar.getInstance();

					calendar.setTime(date);

					final Date alertDt = sdf.parse(alertTime);
					final Calendar calendar1 = Calendar.getInstance();

					calendar1.setTime(alertDt);

					sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa", Locale.ENGLISH);
					currentDateTimeString = sdf.format(calendar.getTime());
					alertTime =sdf.format(calendar1.getTime());


					Date date1 = sdf.parse(currentDateTimeString);
					Date date2 = sdf.parse(alertTime);
					Long diffInMin = printDifference(date1, date2);
					if (diffInMin < 1) {
						int tID = cn.get_trackerId();
						List<Tracker> tracker = db.getTrackerDetails(Integer.toString(tID));
						for (Tracker cn1 : tracker) {
							trackerUserIdList.add(cn1.get_user_Id());
							uUserId1 = cn1.get_user_Id();
							uName1 = cn1.get_tracker_Name();
							uEmail1 = cn1.get_tracker_Email();
							Locflag1 = cn1.get_location_flag();
							hisflag1 = cn1.get_history_flag();
							autoflag1 = cn1.get_autocall_flag();
							imgflag1 = cn1.get_imagecapture_flag();
							liveTrackingFlag1 = cn1.get_livetracking_flag();
							appStatusFlag1 = cn1.get_tracker_App_Status();
						}

						if (isNetworkAvailable(context)) {
							Locflag1 = "OFF";
							settingkeyword = "locoff";
							new SendNotificationofSetting().execute();
						}
						db.deleteLocSetting(Integer.parseInt(uUserId1));
						updateTracker();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {

			}
		}
		//Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
	}
	public long printDifference(Date startDate, Date endDate) {
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		long min = TimeUnit.MILLISECONDS.toMinutes(different);

		return min;
	}
	class SendNotificationofSetting extends AsyncTask<String, Void, String> {

		private Exception exception;

		@Override
		protected String doInBackground(String... params) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			try {
				String url = baseUrl1 + "NotificationForSettings/";

				// 1. create HttpClient
				HttpClient httpclient = new DefaultHttpClient();
				// 2. make POST request to the given URL
				HttpPost httpPost = new HttpPost(url);
				String json = "";

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("OnOffKeyword", settingkeyword);
				jsonObject.accumulate("TrackerId", uUserId1);
				jsonObject.accumulate("TrackeeName", userid);

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
					// result = result.replaceAll("\","");
					JSONObject mainObject = new JSONObject(result);

					responceStatus = mainObject.getString("Status");
				} else
					result = "Did not work!";
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			if (responceStatus != null) {
				if (responceStatus.equals("success")) {

				} else if (responceStatus.equals("fail")) {

				} else if (responceStatus.equals("useruninstalledapp")) {

				}
			}
		}
	}
	public void updateTracker() {

		db.updateTracker(new Tracker(uUserId1, uName1, uEmail1, Locflag1, hisflag1, autoflag1, imgflag1, liveTrackingFlag1, appStatusFlag1));
	}
}

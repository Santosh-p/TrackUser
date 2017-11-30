package sveltoz.icaretrackerapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import sveltoz.icaretrackerapp.DBClasses.TrackerTrackeeDetails;
import sveltoz.icaretrackerapp.R;

/**
 * Created by apple on 1/23/17.
 */

public class TrackerTrackeeAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater;
    private Activity myContext;
    private ArrayList<TrackerTrackeeDetails> datas;
    private Context context;
    private LayoutInflater v;
    Fragment fragment = null;
    private static final int AD_INDEX = 3;
    FragmentManager mFragmentManager;
    public  static  boolean profilefrom = true;
    public  static String selectedtrackertrackeeid,selectedtrackertrackeename;

    public TrackerTrackeeAdapter(Context context, int textViewResourceId,
                                 ArrayList<TrackerTrackeeDetails> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        //  alertDialog=new SpotsDialog(myContext,R.style.Custom_Progress_Dialog);
        this.datas = objects;
        inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // imageLoader.clearCache();
        v = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
        final HistoryAdapter.ViewHolder holder;
        holder = new HistoryAdapter.ViewHolder();
        TrackerTrackeeDetails post = (TrackerTrackeeDetails) datas.get(position);

        TrackerTrackeeDetails ei = (TrackerTrackeeDetails) post;
        vi = v.inflate(R.layout.tracker_trackee_list_row, null);
        final TextView postNameView = (TextView) vi.findViewById(R.id.txtName);
        final TextView postStatusView = (TextView) vi.findViewById(R.id.txtStatus);

        selectedtrackertrackeeid = post.get_user_id();
        selectedtrackertrackeename = post.get_name();
        String name=post.get_name();
        name=StringUtils.capitalize(name);
        postNameView.setText(name);
        if(post.get_status() != null) {

            if (post.get_status().equals("true")) {
                postStatusView.setText("Available");
                postNameView.setTextColor(ContextCompat.getColor(context, R.color.white));
                postStatusView.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                postStatusView.setText("Not Available");
                postNameView.setTextColor(ContextCompat.getColor(context, R.color.Light_Grey));
                postStatusView.setTextColor(ContextCompat.getColor(context, R.color.Light_Grey));
            }
        }


        return vi;
    }

}

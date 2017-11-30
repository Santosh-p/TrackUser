package sveltoz.icaretrackerapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import sveltoz.icaretrackerapp.DBClasses.historyItems;
import sveltoz.icaretrackerapp.R;

/**
 * Created by pramod on 11/17/16..
 */
public class HistoryAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater;
    private Activity myContext;
    private ArrayList<historyItems> datas;
    private Context context;
    private LayoutInflater v;
    Fragment fragment = null;
    private static final int AD_INDEX = 3;
    FragmentManager mFragmentManager;

    public HistoryAdapter(Context context, int textViewResourceId,
                          ArrayList<historyItems> objects) {
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
        final ViewHolder holder;
        holder = new ViewHolder();
        historyItems post = (historyItems) datas.get(position);

        historyItems ei = (historyItems) post;
            vi = v.inflate(R.layout.his_list_row, null);
            final TextView postNumView = (TextView) vi.findViewById(R.id.txtNumber);
            final TextView postCallTimeView = (TextView) vi.findViewById(R.id.txtCallTime);
            final TextView postDurationView = (TextView) vi.findViewById(R.id.txtDuration);
       // final TextView postTypeView = (TextView) vi.findViewById(R.id.txtType);
        final ImageView postCallTypeImageView = (ImageView) vi.findViewById(R.id.imgType);
        String number=post.get_number();
        number = number.replaceAll("[^\\d.]", "");
        int lenth =number.length();
        int removedigit =lenth-10;

        if(number.length()>10){
            number=number.substring(removedigit);
        }
         postNumView.setText(number);
        postDurationView.setText(post.get_call_Duration());
        //postTypeView.setText(post.get_call_Type());
        postCallTimeView.setText(post.get_call_Time());
        if(post.get_call_Type().equals("Outgoing")) {
            postCallTypeImageView.setImageResource(R.mipmap.ic_call_made_white_24dp);
        }
        if(post.get_call_Type().equals("Incoming")) {
            postCallTypeImageView.setImageResource(R.mipmap.ic_call_received_white_24dp);
        }
        if(post.get_call_Type().equals("Missed")) {
            postCallTypeImageView.setImageResource(R.mipmap.ic_call_missed_white_24dp);
        }
        if(post.get_call_Type().equals("Other")) {
            postCallTypeImageView.setImageResource(R.mipmap.ic_call_missed_white_24dp);
        }
        return vi;
    }

}

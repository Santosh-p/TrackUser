package sveltoz.icaretrackerapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import sveltoz.icaretrackerapp.DBClasses.AudioItems;
import sveltoz.icaretrackerapp.R;


/**
 * Created by pramod on 11/17/16..
 */
public class AutoRecordAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater;
    private Activity myContext;
    private ArrayList<AudioItems> datas;
    private Context context;
    private LayoutInflater v;
    Fragment fragment = null;
    private static final int AD_INDEX = 3;
    FragmentManager mFragmentManager;
    AudioItems post;

    public AutoRecordAdapter(Context context, int textViewResourceId,
                          ArrayList<AudioItems> objects) {
        // TODO Auto-generated constructor stub
        myContext = (Activity) context;
        this.datas = objects;
        inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder {
        TextView postTitleView;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        holder = new ViewHolder();
        post = (AudioItems) datas.get(position);

        AudioItems ei = (AudioItems) post;
        vi = v.inflate(
                R.layout.auto_list_row, null);
        final TextView postAudioView = (TextView) vi.findViewById(R.id.txtAudio);
        String filename=post.get_audio_name();
        filename = filename.replaceAll("x", "");
        postAudioView.setText(filename);

        postAudioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = (AudioItems) datas.get(position);
                showAlertDialogForPlayAudio("Play audio","Do you want to play?");
            }
        });

        return vi;

    }
    public void showAlertDialogForPlayAudio(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ICareTracker/AudioFiles/"+post.get_audio_name());
                        intent.setDataAndType(Uri.fromFile(file), "audio/*");
                        context.startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

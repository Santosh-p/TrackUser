package sveltoz.icaretrackerapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import sveltoz.icaretrackerapp.DBClasses.AudioItems;
import sveltoz.icaretrackerapp.R;


/**
 * Created by pramod on 11/17/16..
 */
public class CaptureImageAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater;
    private Activity myContext;
    private ArrayList<AudioItems> datas;
    private Context context;
    private LayoutInflater v;
    Fragment fragment = null;
    private static final int AD_INDEX = 3;
    FragmentManager mFragmentManager;
    AudioItems post;

    public CaptureImageAdapter(Context context, int textViewResourceId,
                               ArrayList<AudioItems> objects) {
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
                R.layout.auto_list_row1, null);
        final GridView postImageView = (GridView) vi.findViewById(R.id.imageview);
        File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICareTracker/ImageFiles/" + post.get_audio_name());
       // /storage/emulated/0/ICareTracker/ImageFiles/40 2017-01-24 18.52.14.jpeg
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int pxWidth = displayMetrics.widthPixels;
                float dpWidth = pxWidth / displayMetrics.density;
                int pxHeight = displayMetrics.heightPixels;
                float dpHeight = pxHeight / displayMetrics.density;
                switch (displayMetrics.densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        imageView.setLayoutParams(new GridView.LayoutParams(85, 85));

                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));

                        break;
                    case DisplayMetrics.DENSITY_HIGH:
                        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));

                        break;
                    case DisplayMetrics.DENSITY_XHIGH:
                        imageView.setLayoutParams(new GridView.LayoutParams(200, 200));

                        break;
                    case DisplayMetrics.DENSITY_XXHIGH:
                        imageView.setLayoutParams(new GridView.LayoutParams(350, 350));

                        break;
                    case DisplayMetrics.DENSITY_XXXHIGH:
                        imageView.setLayoutParams(new GridView.LayoutParams(480, 480));

                        break;
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post = (AudioItems) datas.get(position);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICareTracker/ImageFiles/" + post.get_audio_name());
                        intent.setDataAndType(Uri.fromFile(file), "image/*");
                        context.startActivity(intent);
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                       // showAlertDialogForOpenImage("Delete Image", "Do you want to delete?", position);
                        return false;

                    }
                });
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(myBitmap);
            return imageView;
        }
        return vi;
    }

}
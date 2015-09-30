package git.lawpavilionprime.contentUpdate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 7/9/2015.
 */
public class ContentUpdateAdapter extends BaseAdapter {

    ArrayList<String>  mTitle, mDate,mStatus, mSize;
    ArrayList<Integer> mID;
    ArrayList<Boolean> mShowButton, mCompleted;
    Context mContext;

    public ContentUpdateAdapter(Context context, ArrayList<String> title, ArrayList<String> date, ArrayList<String> status,
                                ArrayList<String> size, ArrayList<Boolean> show, ArrayList<Boolean> completed, ArrayList<Integer> id){

        this.mContext = context;
        this.mTitle = title;
        this.mDate = date;
        this.mStatus = status;
        this.mSize = size;
        this.mShowButton= show;
        this.mCompleted = completed;
        this.mID = id;
    }

    @Override
    public int getCount() {
        return mTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
        TextView status;
        TextView title;
        TextView date;
        TextView size;
        ProgressBar progressBar;
        Button downloadBtn;

        ViewHolder( View v){

            status  =    (TextView) v.findViewById(R.id.status);
            title   =     (TextView) v.findViewById(R.id.title);
            size    =      (TextView) v.findViewById(R.id.size);
            downloadBtn = (Button) v.findViewById(R.id.startDownload);
            date   = (TextView) v.findViewById(R.id.syncDate);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.sync_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.status.setText(mStatus.get(position));
        holder.date.setText(mDate.get(position));
        holder.size.setText(mSize.get(position));
        holder.title.setText(mTitle.get(position));

        holder.progressBar.setId(position);

        if(mShowButton.get(position)){
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }
        else {
            holder.downloadBtn.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        if(mCompleted.get(position)){
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText("Downloaded");
            holder.status.setTextColor(Color.parseColor("#009900"));
            holder.progressBar.setVisibility(View.GONE);
        }
        else{
            holder.status.setText("Pending");
            holder.status.setTextColor(Color.parseColor("#EE0000"));

        }

        return convertView;
    }
}

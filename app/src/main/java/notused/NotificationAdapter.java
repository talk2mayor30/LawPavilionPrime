package notused;

import android.app.Activity;
import android.content.Context;
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
 * Created by don_mayor on 6/24/2015.
 */
public class NotificationAdapter extends BaseAdapter {

    ArrayList<ModuleItem> item;
    Context context;


    int successColor;
    int failedColor;
    boolean updated = false;


//    falcon.com.falcon.Notification ntn;



    public NotificationAdapter(Context context, ArrayList<ModuleItem> item ){

        this.context = context;
        this.item = item;
        successColor = context.getResources().getColor(R.color.success);
        failedColor = context.getResources().getColor(R.color.failed);
//
//        ntn = new falcon.com.falcon.Notification();
    }
    @Override
    public int getCount() {
        return item.size();
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

        ViewHolder(final View convertView){
            status  =    (TextView) convertView.findViewById(R.id.status);
            title   =     (TextView) convertView.findViewById(R.id.title);
            size    =      (TextView) convertView.findViewById(R.id.size);
            downloadBtn = (Button) convertView.findViewById(R.id.startDownload);
            date   = (TextView) convertView.findViewById(R.id.ntnDate);
            progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.notification_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        ModuleItem ntnItem = item.get(position);
        holder.status.setText(ntnItem.getStatus());
        holder.date.setText(ntnItem.getDate());
        holder.size.setText(ntnItem.getSize()+"mb");
        holder.title.setText(ntnItem.getTitle());
        holder.downloadBtn.setTag("btn" + position);
        holder.progressBar.setTag("pgB"+position);
        holder.status.setTag("sts"+position);

        return convertView;
    }
}

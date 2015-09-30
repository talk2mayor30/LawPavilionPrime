package git.lawpavilionprime.Dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 6/19/2015.
 */
public class DashBoardAdapter extends BaseAdapter {

    Integer[] resources;

    Context context;
    Config config;
    int minThreadHeight;

    public DashBoardAdapter(Context context, Integer[] resources, int minThreadHeight){
        this.resources =  resources;
        this.context = context;
        this.minThreadHeight = minThreadHeight;

        config = new Config(this.context);
    }
    @Override
    public int getCount() {

        return resources.length;
    }

    @Override
    public Integer getItem(int position) {

       return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder{

        ImageView dashboardItem;
        ImageView hanger;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dashboard_item_layout, parent, false);
            holder = new ViewHolder();

            holder.dashboardItem = (ImageView) convertView.findViewById(R.id.dashboard_item);
            holder.hanger = (ImageView) convertView.findViewById(R.id.dashboard_hanger);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        int resource = resources[position];
        LinearLayout.LayoutParams params;
        if(position % 2 == 0) {
           params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, config.dpToPx(minThreadHeight));
            holder.dashboardItem.setImageResource(resource);

        }
        else{

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, config.dpToPx( minThreadHeight * 2));
            holder.dashboardItem.setImageResource(resource);
        }
        holder.hanger.setLayoutParams(params);

        return convertView;

    }

}

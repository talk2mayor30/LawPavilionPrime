package git.lawpavilionprime.Dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 6/19/2015.
 */
public class DashBoardHeadAdapter extends BaseAdapter {

    Integer[] headResources;

    Context context;
    Config config;



    public DashBoardHeadAdapter(Context context, Integer[] headResources){
        this.headResources =  headResources;
        this.context = context;

        config = new Config(this.context);
    }
    @Override
    public int getCount() {

        return headResources.length;
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

        ImageView dashboardHeadItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dashboard_head_item_layout, parent, false);
            holder = new ViewHolder();

            holder.dashboardHeadItem = (ImageView) convertView.findViewById(R.id.dashboard_head_item);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        int resource = headResources[position];

        holder.dashboardHeadItem.setImageResource(resource);

        return convertView;

    }

}

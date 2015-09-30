package git.lawpavilionprime.research;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import git.lawpavilionprime.R;

public class ResearchGridAdapter extends BaseAdapter {

        Context context;
        List<String> rowItems;
        int itemHeight, itemWidth;
        LayoutInflater mInflater;
    public ResearchGridAdapter(Context context, List<String> items, int itemHeight, int itemWidth) {

        this.context = context;
        this.rowItems = items;
        this.itemHeight = itemHeight;
        this.itemWidth = itemWidth;

         mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

    }



    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public String getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }


    private class ViewHolder {

        ImageView drawer;
        TextView textView;
        //ImageView leftStand;
        //ImageView rightStand;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;



        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.research_folder_list_item, null);
            holder = new ViewHolder();

            holder.drawer = (ImageView) convertView.findViewById(R.id.drawer);
            holder.textView = (TextView) convertView.findViewById(R.id.drawer_label);
            //holder.leftStand = (ImageView) convertView.findViewById(R.id.leftStand);
            //holder.rightStand = (ImageView)  convertView.findViewById(R.id.rightStand);


            holder.drawer.setLayoutParams(new LinearLayout.LayoutParams(0, itemHeight, 1.0f));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

            holder.drawer.setLayoutParams(new LinearLayout.LayoutParams(0, itemHeight, 1.0f));
        }

        String text = rowItems.get(position);

            holder.textView.setText(text);
            holder.drawer.setImageResource(R.drawable.research_folder_drawer_1);


        return convertView;

    }



}
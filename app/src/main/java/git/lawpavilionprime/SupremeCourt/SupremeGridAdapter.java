package git.lawpavilionprime.SupremeCourt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import git.lawpavilionprime.R;

public class SupremeGridAdapter extends BaseAdapter {

    Context context;
    List<SupremeGridItem> rowItems;

    SupremeGridAdapter(Context context, List<SupremeGridItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }


    private class ViewHolder {

        TextView title, suitNumber;
        ImageView shelfBG;
        RelativeLayout mBook;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jud_grid_list_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.suitNumber = (TextView) convertView.findViewById(R.id.suitNumber);
            holder.shelfBG = (ImageView) convertView.findViewById(R.id.shelfBG);
            holder.mBook = (RelativeLayout) convertView.findViewById(R.id.bookOne);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SupremeGridItem row_pos = rowItems.get(position);
        holder.title.setText(row_pos.getTitle());
        holder.suitNumber.setText(row_pos.getSuitNumber());

        if (!row_pos.getShow()) {
            holder.mBook.setVisibility(View.GONE);
        } else if (row_pos.getShow()) {
            holder.mBook.setVisibility(View.VISIBLE);
        }

        if (row_pos.getType() == "start") {
            holder.shelfBG.setImageResource(R.drawable.left);
        } else if (row_pos.getType() == "end") {
            holder.shelfBG.setImageResource(R.drawable.right);
        }
        else if(row_pos.getType() == "middle"){
            holder.shelfBG.setImageResource(R.drawable.middle);
        }
        else if(row_pos.getType() == "bottom_start"){
            holder.shelfBG.setImageResource(R.drawable.bottom_left);
        }
        else if(row_pos.getType() == "bottom_end"){
            holder.shelfBG.setImageResource(R.drawable.bottom_right);
        }
        else if(row_pos.getType() == "bottom_middle"){
            holder.shelfBG.setImageResource(R.drawable.bottom_middle);
        }


        else if(row_pos.getType() == "head"){
            holder.shelfBG.setImageResource(R.drawable.head);
        }


        return convertView;
    }

}
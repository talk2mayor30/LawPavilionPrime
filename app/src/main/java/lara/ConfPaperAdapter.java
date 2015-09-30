package lara;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import git.lawpavilionprime.R;
import notused.ModuleItem;

/**
 * Created by don_mayor on 8/21/2015.
 */
public class ConfPaperAdapter extends BaseAdapter {

    Context context;
    ArrayList<ConfPaperItem> confPapers;

    public ConfPaperAdapter(Context context, ArrayList<ConfPaperItem> confPapers){
        this.confPapers = confPapers;
        this.context = context;

    }

    @Override
    public int getCount() {
        return confPapers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.conference_papers_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        ConfPaperItem cpItem = confPapers.get(position);
        holder.txtSpeakers.setText(cpItem.getSpeaker());
        holder.txtTitle.setText(cpItem.getTitle());

        return convertView;
    }

    private class ViewHolder{

        TextView    txtSpeakers;
        TextView    txtTitle;

        public ViewHolder(View v){

            txtSpeakers = (TextView) v.findViewById(R.id.speaker);
            txtTitle = (TextView) v.findViewById(R.id.title);

        }
    }
}

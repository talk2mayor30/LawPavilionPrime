package git.lawpavilionprime.practiceArea;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;


/**
 * Created by don_mayor on 6/22/2015.
 */
public class PracticeAreaFirstAdapter extends BaseAdapter {

    ArrayList<Integer> resources;
    Context context;
    LayoutInflater inflater;
    int gridItemHeight;
    FrameLayout.LayoutParams params;
    //FrameLayout.LayoutParams params2;
    Config config;


    public PracticeAreaFirstAdapter(Context context, ArrayList<Integer> resources, int gridItemHeight){
        this.resources = resources;
        this.context = context;
        this.gridItemHeight = gridItemHeight;
        config = new Config(this.context);

//        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, config.dpToPx(context,gridItemHeight));
        inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{

        ImageView imageShelf;
        ImageView label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if(convertView == null){
            convertView = inflater.inflate(R.layout.practice_area_first_grid_item, null);
            holder = new ViewHolder();

            holder.imageShelf = (ImageView) convertView.findViewById(R.id.practiceShelf);
            holder.label = (ImageView) convertView.findViewById(R.id.practice_label);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
            holder.label.setImageResource(resources.get(position));


        //200 is the max Height of the GridItem

        if(gridItemHeight < 200){
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, config.dpToPx(gridItemHeight));
            //20 dp for the minimum padding between the shelf and the label
           // params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, config.dpToPx(context, (gridItemHeight-20)));

            holder.imageShelf.setLayoutParams(params);
            //holder.label.setLayoutParams(params2);
        }
        else{
            Log.d("check", "is greater");
        }

        return convertView;
    }
}

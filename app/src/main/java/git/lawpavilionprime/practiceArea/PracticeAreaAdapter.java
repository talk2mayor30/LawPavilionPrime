package git.lawpavilionprime.practiceArea;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 6/22/2015.
 */
public class PracticeAreaAdapter extends BaseAdapter {

    ArrayList<String> itemPosition;
    Context context;
    LayoutInflater inflater;
    int gridItemHeight;
    LinearLayout.LayoutParams params;
    Config config;


    public PracticeAreaAdapter(Context context, ArrayList<String> position, int gridItemHeight){
        this.itemPosition = position;
        this.context = context;
        this.gridItemHeight = gridItemHeight;
        config = new Config(this.context);

//        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, config.dpToPx(context,gridItemHeight));
        inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemPosition.size();
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Log.d("check", (convertView == null)?"Yes":"No");

        if(convertView == null){
            convertView = inflater.inflate(R.layout.practice_area_item, null);
            holder = new ViewHolder();

            holder.imageShelf = (ImageView) convertView.findViewById(R.id.practiceShelf);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(itemPosition.get(position) == "start"){
            holder.imageShelf.setImageResource(R.drawable.left);
        }
        else if(itemPosition.get(position) == "end"){
            holder.imageShelf.setImageResource(R.drawable.right);
        }
        else{
            holder.imageShelf.setImageResource(R.drawable.middle);
        }

        //200 is the max Height of the GridItem
        if(gridItemHeight < 200){
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, config.dpToPx(gridItemHeight));

            holder.imageShelf.setLayoutParams(params);
        }
        else{
            Log.d("check", "is greater");
        }

        return convertView;
    }
}

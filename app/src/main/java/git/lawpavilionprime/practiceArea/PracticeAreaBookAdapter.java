package git.lawpavilionprime.practiceArea;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;


/**
 * Created by don_mayor on 6/22/2015.
 */
public class PracticeAreaBookAdapter extends BaseAdapter{

    int size;
    LayoutInflater inflater;
    int bookHeight;
    Config config;
    FrameLayout.LayoutParams params1, params2;
    Context context;
    public PracticeAreaBookAdapter(Context context,int size, int bookHeight){
        this.size = size;
        this.bookHeight = bookHeight;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        config = new Config(this.context);
    }

    @Override
    public int getCount() {
        return this.size;
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

        ImageView book;
        ImageView dummyView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.practice_area_book_item, null);

            holder.book = (ImageView) convertView.findViewById(R.id.bookItem);
            holder.dummyView = (ImageView) convertView.findViewById(R.id.dummy);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();

        }
            holder.book.setImageResource(R.drawable.temp_book);

        //200 is the max Height of the GridItem
        if(bookHeight < 200){

            Log.d("check ", "is lesser");
            params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, config.dpToPx(bookHeight));
            params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, config.dpToPx((bookHeight -22) ));
            params2.setMargins(0,0,0, config.dpToPx( (-5)));
            params2.gravity = Gravity.CENTER;
            holder.book.setLayoutParams(params2);
            holder.dummyView.setLayoutParams(params1);
        }
        else{
            Log.d("check", "is greater");
        }



        return convertView;
    }
}

package git.lawpavilionprime;

import android.content.Context;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by don_mayor on 7/20/2015.
 */
public class Adapter extends BaseAdapter {

    ArrayList<String> dummy;
    Context mContext;
    int multiLineHeight;

    public Adapter(Context context, ArrayList<String> dummy, int multiLineHeight) {
        this.mContext = context;
        this.dummy = dummy;
        this.multiLineHeight = multiLineHeight;

    }
    @Override
    public int getCount() {
        return dummy.size();
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout.LayoutParams params = null;
        mHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.curl_search_list_item, parent, false);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, multiLineHeight);

            holder = new mHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (mHolder) convertView.getTag();
        }
        holder.dummy.setLayoutParams(params);
        holder.dummy.setText(dummy.get(position));
        return convertView;
    }
    class mHolder {
        TextView dummy;

        public mHolder(View v) {
            dummy = (TextView) v.findViewById(R.id.text1);

        }
    }

}

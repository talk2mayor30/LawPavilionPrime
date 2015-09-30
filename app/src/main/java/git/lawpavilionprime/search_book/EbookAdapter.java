package git.lawpavilionprime.search_book;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

/**
 * Created by tdscientist on 18-May-15.
 */
public class EbookAdapter extends BaseAdapter {

    ArrayList<String> mTitle, mDescription, mSubject_matter, mIssues, mYear;
    Context mContext;
    String mQuery;
    String direction;
    Config config;

    public EbookAdapter(Context context, ArrayList<String> title, ArrayList<String> subject_matter, ArrayList<String> issues, ArrayList<String> year, ArrayList<String> description, String query, String direction) {
        this.mContext = context;
        this.mTitle = title;
        this.mDescription = description;
        this.mQuery = query;
        this.direction = direction;

        this.mSubject_matter = subject_matter;
        this.mYear = year;
        this.mIssues = issues;

        config = new Config(this.mContext);
    }

    @Override
    public int getCount() {
        return mTitle.size();
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
        mHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
            holder = new mHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (mHolder) convertView.getTag();
        }
        holder.title.setText(mTitle.get(position));
        holder.year.setText(mYear.get(position));
        holder.subject_matter.setText(mSubject_matter.get(position)+ " --- " + mIssues.get(position));
        String str = mDescription.get(position).toString();
        Spannable span = config.searchFor(mQuery.split(" "), str);

        holder.body.setText(span);

        Animation animation;
        if(direction == "left")
           animation  = AnimationUtils.loadAnimation(mContext, R.anim.slide_left);
        else
           animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_right);
        convertView.startAnimation(animation);

        return convertView;
    }
    class mHolder {
        TextView subject_matter, title, year,  body;

        public mHolder(View v) {
            title = (TextView) v.findViewById(R.id.case_title);
            body = (TextView) v.findViewById(R.id.body);
            subject_matter = (TextView) v.findViewById(R.id.subject_matter);
            year = (TextView) v.findViewById(R.id.year);

        }
    }

    public void clearAdapter(){
        mTitle.clear();
        mDescription.clear();
        mIssues.clear();
        mSubject_matter.clear();
        mYear.clear();
    }
}

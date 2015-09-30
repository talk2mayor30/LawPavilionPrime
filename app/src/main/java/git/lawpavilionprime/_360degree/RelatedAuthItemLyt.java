package git.lawpavilionprime._360degree;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import git.lawpavilionprime.R;
import notused.ModuleItem;

/**
 * Created by don_mayor on 9/9/2015.
 */
public class RelatedAuthItemLyt {

    Context context;
    ArrayList<AuthorityItem> authorities;
    LinearLayout parentLayout;

    //To create authority item
    public RelatedAuthItemLyt(Context ctx, ArrayList<AuthorityItem> authorities, LinearLayout parentLayout){

        this.authorities = authorities;
        this.parentLayout = parentLayout;
        context = ctx;
    }

    private class ViewHolder{

        TextView txtAuthority, txtLA_LC, relyingCase;
        LinearLayout authItemLyt;
        View divider;

        ViewHolder( View v ){
            txtAuthority = (TextView) v.findViewById(R.id.authority);
            txtLA_LC = (TextView) v.findViewById(R.id.LA_LC);
            authItemLyt = (LinearLayout) v.findViewById(R.id.authItemlyt);
            relyingCase = (TextView) v.findViewById(R.id.relyCase);
            divider = (View) v.findViewById(R.id.divider);
        }
    }

    public View getView(int position) {

        View convertView = null;

        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.related_auth_item, null);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);
        holder.txtAuthority.setText(authorities.get(position).getAuthority());
        holder.authItemLyt.setTag(position);

        if(authorities.get(position).isLA()){
            holder.txtLA_LC.setText("Latest Authority");
            holder.relyingCase.setVisibility(View.GONE);
        }
        else if(authorities.get(position).isLC()){
            holder.txtLA_LC.setText("Locus Classicus");
            holder.relyingCase.setVisibility(View.GONE);
        }
        else{
            holder.txtLA_LC.setVisibility(View.GONE);
        }

        if( (position + 1) == authorities.size()){
            holder.divider.setVisibility(View.GONE);
        }

        holder.authItemLyt.setTag(position);
        return convertView;
    }
    public void buildLayout(){
        for(int i= 0; i < authorities.size(); i++){
            parentLayout.addView(getView(i));
        }
    }


}

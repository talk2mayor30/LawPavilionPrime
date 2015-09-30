package git.lawpavilionprime._360degree;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 9/9/2015.
 */
public class _360DegreeFragmentA extends Fragment {

    private static final String TAG = "---DEBUG---";
    private LinearLayout parentLayout;
    private LinearLayout fragmentLyt;
    private LinearLayout firstTempLyt;
    private LinearLayout suitLpelrLyt;

    private LinearLayout btmLyt;
    private LinearLayout LytHolder;

    private ArrayList<AuthorityItem> authorities;
    public int SIZE = 10;

    public Configuration configuration;
    public Config config;

    public String[] test_value = { "ACTION", "COURT", "EVIDENCE", "UPTURN", "POSTPONED"};

    public static Fragment newInstance(){
        Fragment fragment = new _360DegreeFragmentA();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuration = getActivity().getResources().getConfiguration();
        config = new Config(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.degree360_fragment_a, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentLayout = (LinearLayout) view.findViewById(R.id.relatedAuthorities);
        fragmentLyt = (LinearLayout) view.findViewById(R.id.fragLyt);
        firstTempLyt = (LinearLayout) view.findViewById(R.id.firstTempLyt);
        suitLpelrLyt = (LinearLayout) view.findViewById(R.id.suit_lpelrLyt);
        btmLyt = (LinearLayout) view.findViewById(R.id.LytBottom);

        authorities = new ArrayList<>();

//        if(configuration.orientation == configuration.ORIENTATION_LANDSCAPE){
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//            fragmentLyt.setOrientation(LinearLayout.HORIZONTAL);
//            params.setMargins(0, 0, config.dpToPx(10), 0);
//            firstTempLyt.setLayoutParams(params);
//
//            LinearLayout.LayoutParams suitLpLytParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            suitLpLytParam.setMargins(0, config.dpToPx(10), 0, config.dpToPx(10));
//            suitLpelrLyt.setOrientation(LinearLayout.VERTICAL);
//            suitLpelrLyt.setLayoutParams(suitLpLytParam);
//        }
//        else{
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            fragmentLyt.setOrientation(LinearLayout.VERTICAL);
//            firstTempLyt.setLayoutParams(params);
//
//            LinearLayout.LayoutParams suitLpLytParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            suitLpLytParam.setMargins(0, config.dpToPx(10), 0, config.dpToPx(10));
//            suitLpelrLyt.setOrientation(LinearLayout.HORIZONTAL);
//            suitLpelrLyt.setLayoutParams(suitLpLytParam);
//        }

        for(int i =0; i<=SIZE; i++){
            AuthorityItem authItem;
            if(i == 0){
                authItem = new AuthorityItem("Laws of economics are an attempt in modelization of economic behavior. Marxism criticized the belief in eternal 'laws of economics', which it considered a product", true, false);
            }
            else if( i == SIZE ){
                authItem = new AuthorityItem("Laws of economics are an attempt in modelization of economic behavior. Marxism criticized the belief in eternal 'laws of economics', which it considered a product", false, true);
            }
            else{
                authItem = new AuthorityItem("Laws of economics are an attempt in modelization of economic behavior. Marxism criticized the belief in eternal 'laws of economics', which it considered a product", false, false);
            }
            authorities.add(authItem);
        }
        RelatedAuthItemLyt authItemLyt = new RelatedAuthItemLyt(getActivity(), authorities, parentLayout);
        authItemLyt.buildLayout();

        int i = 0;
        while ( i <=5 ){
            btmLyt.addView(new createView(getActivity(), 1,  i, 5).getView());
            i+=3;
        }
    }

    private class createView{

        Context mContext;
        Config config;
        LinearLayout.LayoutParams params;

        LinearLayout linearLyt;

        createView(Context context, int _id,  int offset, int max){

            mContext = context;
            config = new Config(mContext);

            linearLyt = new LinearLayout(mContext);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, config.dpToPx(60));
            param.setMargins(0, config.dpToPx(10), 0, config.dpToPx(10));
            linearLyt.setOrientation(LinearLayout.HORIZONTAL);
            linearLyt.setLayoutParams(param);

            for(int i = offset; i <(offset + 3); i++){

                if(i < max ){

                    String label = test_value[i];

                    TextView txtView = new TextView(mContext);
                    params = new LinearLayout.LayoutParams(0, config.dpToPx(60), 1 );
                    params.setMargins(config.dpToPx(10), 0, config.dpToPx(10), 0);
                    txtView.setLayoutParams(params);
                    txtView.setBackgroundResource(R.color.app_theme_color);
                    txtView.setGravity(Gravity.CENTER);
                    txtView.setMaxLines(1);
                    txtView.setText(label);
                    txtView.setAllCaps(true);
                    txtView.setTextColor(mContext.getResources().getColor(R.color.navDrawerBlack));
                    txtView.setTextSize(config.dpToPx(12));
                    txtView.setTypeface(Typeface.DEFAULT_BOLD);

                    linearLyt.addView(txtView);
                }
                else{
                    return;
                }
            }
        }

        public LinearLayout getView(){

            return linearLyt;
        }
    }
}

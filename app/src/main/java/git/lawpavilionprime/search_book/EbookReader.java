package git.lawpavilionprime.search_book;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import git.lawpavilionprime.R;


public class EbookReader extends FragmentActivity  {

    Configuration config;
    EbookFragmentLand landFragment;
    EbookFragmentPort portFragment;
    static String mQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebook_main);

        config = getResources().getConfiguration();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction   = manager.beginTransaction();

        //mQuery = getIntent().getStringExtra("QUERY");
        if(getIntent().getStringExtra("QUERY")== null) {
            Log.d("EBOOK", "NULL");
            mQuery = "order";
        }
        else {
            Log.d("EBOOK", "IS NOT NULL");
            mQuery = getIntent().getStringExtra("QUERY");
        }

        if(config.orientation == config.ORIENTATION_LANDSCAPE) {
            landFragment = (EbookFragmentLand) manager.findFragmentByTag("LAND");
                if(landFragment == null){
                    landFragment = new EbookFragmentLand();
                }
                landFragment.setArguments(savedInstanceState);
            fragmentTransaction.replace(R.id.frag_container, landFragment, "LAND");
        }
        else {
            portFragment = (EbookFragmentPort) manager.findFragmentByTag("PORT");
            if(portFragment == null)
                portFragment = new EbookFragmentPort();

            portFragment.setArguments(savedInstanceState);
            fragmentTransaction.replace(R.id.frag_container, portFragment, "PORT");
        }

        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(config.orientation == config.ORIENTATION_LANDSCAPE){
            outState.putInt("OFFSET", EbookFragmentPort.truePositionOffset);
            outState.putString("QUERY", mQuery);

            outState.putStringArrayList("T_HEAD", EbookFragmentPort.mTempCaseTitle);
            outState.putStringArrayList("HEAD", EbookFragmentPort.mCaseTitle);
            outState.putStringArrayList("T_BODY", EbookFragmentPort.mTempBody);
            outState.putStringArrayList("BODY", EbookFragmentPort.mBody);
            outState.putStringArrayList("T_TYPE", EbookFragmentPort.mTempType);
            outState.putStringArrayList("TYPE", EbookFragmentPort.mType);
            outState.putStringArrayList("T_ID", EbookFragmentPort.mTempId);
            outState.putStringArrayList("ID", EbookFragmentPort.mId);
            outState.putStringArrayList("T_YEAR", EbookFragmentPort.mTempYear);
            outState.putStringArrayList("YEAR", EbookFragmentPort.mYear);

            outState.putStringArrayList("SUBJECT_MATTER", EbookFragmentPort.mSubjectMatter);
            outState.putStringArrayList("T_SUBJECT_MATTER", EbookFragmentPort.mTempSubjectMatter);

            outState.putStringArrayList("ISSUES", EbookFragmentPort.mIssues);
            outState.putStringArrayList("T_ISSUES", EbookFragmentPort.mTempIssues);

        }
        else{
            outState.putInt("OFFSET",  EbookFragmentLand.leftListTruePositionOffset);
            outState.putString("QUERY", mQuery);

            outState.putStringArrayList("T_HEAD", EbookFragmentLand.mTempCaseTitle);
            outState.putStringArrayList("HEAD", EbookFragmentLand.mCaseTitle);
            outState.putStringArrayList("T_BODY", EbookFragmentLand.mTempBody);
            outState.putStringArrayList("BODY", EbookFragmentLand.mBody);
            outState.putStringArrayList("T_TYPE", EbookFragmentLand.mTempType);
            outState.putStringArrayList("TYPE", EbookFragmentLand.mType);
            outState.putStringArrayList("T_ID", EbookFragmentLand.mTempId);
            outState.putStringArrayList("ID", EbookFragmentLand.mId);
            outState.putStringArrayList("T_YEAR", EbookFragmentLand.mTempYear);
            outState.putStringArrayList("YEAR", EbookFragmentLand.mYear);

            outState.putStringArrayList("SUBJECT_MATTER", EbookFragmentLand.mSubjectMatter);
            outState.putStringArrayList("T_SUBJECT_MATTER", EbookFragmentLand.mTempSubjectMatter);

            outState.putStringArrayList("ISSUES", EbookFragmentLand.mIssues);
            outState.putStringArrayList("T_ISSUES", EbookFragmentLand.mTempIssues);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ebook_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

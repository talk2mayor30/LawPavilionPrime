package git.lawpavilionprime._360degree;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import git.lawpavilionprime.R;

public class _360DegreeMain extends ActionBarActivity {



    private String DEBUG = "---DEBUG---";
    private boolean isSinglePage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.degree360);

        Bundle bundle = getIntent().getBundleExtra("ARGUMENT");
        int position = bundle.getInt("POSITION");


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if((position % 2) == 0){
            Fragment  fragment = _360DegreeFragmentB.newInstance();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
        else{
            Fragment fragment = _360DegreeFragmentA.newInstance();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
            //It is dual view layout
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_360_degree_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void authoritySelected(View v){

        String value = String.valueOf(v.getTag());
        Toast.makeText(this, "Do you want to spend " + value + " year behind the bar?", Toast.LENGTH_SHORT).show();
    }
}

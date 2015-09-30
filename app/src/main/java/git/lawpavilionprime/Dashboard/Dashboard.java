package git.lawpavilionprime.Dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import git.lawpavilionprime.R;
import notused.Login;
import git.lawpavilionprime.auth.UserDBAdapter;

public class Dashboard extends ActionBarActivity {

    GridView dashboard;
    GridView dashboardHead;

    // Level 2 of the gridView portrait
    GridView dashboardLevel_2;
    GridView dashboardHeadLevel_2;

    //Initialization of drawables for portrait
    Integer[] resource_id = {R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement};
    Integer[] head_resource_id = {R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item};

    Integer[] resource_id2 = {R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement};
    Integer[] head_resource_id2 = {R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item };

    //Initialization for landscape
    Integer[] head_resource_id_land = {R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,
                                      R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item,R.drawable.dashboard_head_item};
    Integer[] resource_id_land = {R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement,
                                    R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement,
                                        R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement,
                                            R.drawable.dashboard_latest_judgement, R.drawable.dashboard_latest_judgement};
    //something to test
   // ImageView toclick;

    int remaingingScreenHeight;

    Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();

        //Combo of the screen Height already occupied.

        if(config.orientation == config.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.dashbooard);
            //10dp for safety
            remaingingScreenHeight = config.screenHeightDp - 470;
        }
        else{
            //20dp also occupied by margin + 10dp for safety measure :)
            setContentView(R.layout.dashboard_port);
            remaingingScreenHeight = config.screenHeightDp - 670;
        }

        exitBtn = (Button) findViewById(R.id.forceExit);

        dashboard = (GridView) findViewById(R.id.dashboard);
        dashboardHead = (GridView) findViewById(R.id.dashboard_head);

        dashboardLevel_2 = (GridView) findViewById(R.id.dashboard2);
        dashboardHeadLevel_2 = (GridView) findViewById(R.id.dashboard_head2);

       // toclick = (ImageView) findViewById(R.id.lawPavilionLogo);

//
//        toclick.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Intent i  = new Intent(falcon.com.falcon.Dashboard.this, EbookReader.class);
//
//                startActivity(i);
//
//            }
//        });

        if(config.orientation == config.ORIENTATION_LANDSCAPE){
            drawHeadGrid(head_resource_id_land, dashboardHead);
            drawGrid(resource_id_land, dashboard, remaingingScreenHeight/2);
        }
        else{
            drawHeadGrid(head_resource_id,dashboardHead);
            drawGrid(resource_id, dashboard, remaingingScreenHeight/4);
            drawHeadGrid(head_resource_id2, dashboardHeadLevel_2);
            drawGrid(resource_id2, dashboardLevel_2, remaingingScreenHeight/4);
        }

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDBAdapter adpater = new UserDBAdapter(Dashboard.this);
                adpater.open("falcon");
                adpater.executeQuery("UPDATE user SET "+ adpater.LOG_OUT+" = 1");
                adpater.close();

                Dashboard.this.finish();
                Intent intent = new Intent(Dashboard.this, Login.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
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
    public void drawGrid(Integer[] resources, GridView view, int minThreadHeight){
        DashBoardAdapter adapter = new DashBoardAdapter(this, resources, minThreadHeight);

        view.setAdapter(adapter);
    }

    public void drawHeadGrid(Integer[] resources, GridView view) {

        DashBoardHeadAdapter headAdapter = new DashBoardHeadAdapter(this, resources);

        view.setAdapter(headAdapter);

    }


}

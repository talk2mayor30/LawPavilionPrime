package git.lawpavilionprime.research;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import git.lawpavilionprime.R;


public class Research extends Activity {

    int  screenWidthDp, screenHeightDp, NUM_OF_GridITEM_PER_ROW, NUM_OF_ROW;

    //GridWidth is 140, Height is 180
    static int gridItemWidth;
    static int gridItemHeight;

    int TOTAL_GRID_VIEW;

    List<String> gridViewList;
    ResearchGridAdapter gridAdapter;
    GridView gridView;

    TextView textView;

    String[] alphabet = {"a", "b", "c", "d", "e", "f",
                            "g", "h", "i", "j", "k", "l", "m", "n", "o",
                                "p", "q", "r", "s", "t", "u", "v", "w",
                                    "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.research_folder_main);
        Configuration config = getResources().getConfiguration();

        textView = (TextView) findViewById(R.id.drawerId);

        gridViewList = new ArrayList<String>();

        gridView = (GridView) findViewById(R.id.drawerGrid);

        screenHeightDp = config.screenHeightDp - 50 - 65 -30;

        //40dp is the witdth of the drawer stand
        screenWidthDp  = config.screenWidthDp - 30 - 40;

        if(config.orientation == config.ORIENTATION_LANDSCAPE){
            NUM_OF_GridITEM_PER_ROW = 9;
            NUM_OF_ROW = 4;
            gridView.setNumColumns(9);
            gridItemHeight = screenHeightDp / NUM_OF_ROW;
            gridItemWidth = screenWidthDp / NUM_OF_GridITEM_PER_ROW;


        }
        else{
            NUM_OF_GridITEM_PER_ROW = 4;
            NUM_OF_ROW = 9;
            gridView.setNumColumns(4);
            gridItemHeight = screenHeightDp / NUM_OF_ROW;
            gridItemWidth = screenWidthDp / NUM_OF_GridITEM_PER_ROW;
        }


        // The height of the menuHeader is 40 and margin top is 10, 65 is the total space occupied by the header, 30 for footer






//        NUM_OF_GridITEM_PER_ROW = screenWidthDp / gridItemWidth;
//
//        NUM_OF_ROW = screenHeightDp / gridItemHeight;

        TOTAL_GRID_VIEW = NUM_OF_GridITEM_PER_ROW * NUM_OF_ROW;

        drawGrid();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.research, menu);
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

    public void drawGrid(){

        //This for Statement will only be relevant for Changing the text view details

        gridViewList = new ArrayList<String>();

        for(int i = 0; i < TOTAL_GRID_VIEW; i++){

                //Jurisdiction is the textView text
                gridViewList.add("Jurisdiction");

        }
        gridAdapter = new ResearchGridAdapter(this, gridViewList, dpToPx(gridItemHeight), dpToPx(gridItemWidth));

        //gridView.setColumnWidth(dpToPx(gridItemWidth));

        gridView.setAdapter(gridAdapter);

        //The GridViewlabel will determine this
        textView.setText("Drawer "+alphabet[0]+ " - "+alphabet[25]);

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}

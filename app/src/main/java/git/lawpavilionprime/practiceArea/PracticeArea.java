package git.lawpavilionprime.practiceArea;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;

import git.lawpavilionprime.R;

public class PracticeArea extends Activity {

    int screenWidth, screenHeight;
    int remainingScreenWidth, remainingScreenHeight;
    int NUM_OF_COLUMNS, BOOK_NUM_OF_COLUMNS;
    Boolean isFirstGrid = true;
    //ArrayList<String> position;

    GridView labelGrid;
    GridView bookGrid;

    GridView book;
    int bookGridWidth, bookGridHeight, bookWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_area);
        Configuration config = getResources().getConfiguration();

        labelGrid = (GridView) findViewById(R.id.labelGrid);
        bookGrid =  (GridView) findViewById(R.id.bookGrid);

        book = (GridView) findViewById(R.id.book);

        //position = new ArrayList<String>(); //layout padding left and padding =20px
        screenWidth = config.screenWidthDp - 20;

        screenHeight = config.screenHeightDp;
        bookGridWidth = 180; //in dp
        bookWidth = 150;

        //remaining screen width after removing the grid for the label
        remainingScreenWidth =  3 * screenWidth / 5;
        remainingScreenHeight = screenHeight - 100 -20; //header and footer height is 50dp each 20dp for top and bottom padding

        bookGridHeight = remainingScreenHeight / 3;


        NUM_OF_COLUMNS = remainingScreenWidth / bookGridWidth;
        BOOK_NUM_OF_COLUMNS = (remainingScreenWidth - 20) / bookWidth; // the MarginRight for the Overlayed gridView is 20dp;


         //returns num of columns for book grid

        Log.d("Number of column", "result: "+remainingScreenWidth/bookGridWidth);
        Log.d("Column", "result: "+NUM_OF_COLUMNS);
        Log.d("book_number of cloumn: ", "columns: "+ BOOK_NUM_OF_COLUMNS);

        drawFirstGrid(labelGrid, new ArrayList<Integer>()); // Num of column for label grid is one, 3 rows

        drawGrid(NUM_OF_COLUMNS, bookGrid, new ArrayList<String>());


        //Method to populate books
        int TOTAL_BOOK_ITEM_NUM = BOOK_NUM_OF_COLUMNS * 3; // Number of roles
        PracticeAreaBookAdapter  bookAdapter = new PracticeAreaBookAdapter(this, TOTAL_BOOK_ITEM_NUM, bookGridHeight);
        book.setAdapter(bookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practice_area, menu);
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

    public void drawFirstGrid( GridView view, ArrayList<Integer> resources){

        int TOTAL_ITEM = 3;

        for(int i = 0; i<TOTAL_ITEM; i++){
            if(i == 0){
                resources.add(R.drawable.practice_area_label1);
            }
            else if( i == 1){
                resources.add(R.drawable.practice_area_label2);
            }
            else {
                resources.add(R.drawable.practice_area_label3);
            }
        }
        PracticeAreaFirstAdapter firstAdapter = new PracticeAreaFirstAdapter(this, resources, bookGridHeight);
        view.setAdapter(firstAdapter);
    }

    public void drawGrid( int NUM_OF_COLUMNS, GridView view, ArrayList<String> position){

        int TOTAL_ITEM = NUM_OF_COLUMNS * 3; // 3 is the number of rows

        Log.d("total", "total "+TOTAL_ITEM);

        for(int i= 0; i <TOTAL_ITEM; i++){

            if((i % NUM_OF_COLUMNS) == (NUM_OF_COLUMNS -1)){
                position.add("end");
            }
            else{
                position.add("middle");
            }
        }
        PracticeAreaAdapter practiceAdapter = new PracticeAreaAdapter(this, position, bookGridHeight);

        view.setAdapter(practiceAdapter);
    }
}

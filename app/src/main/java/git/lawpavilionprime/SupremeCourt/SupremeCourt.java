package git.lawpavilionprime.SupremeCourt;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;


/**
 * Created by tdscientist on 11-May-15.
 * Display All Supreme Court Judgments, categorized by alphabets
 */
public class SupremeCourt extends ActionBarActivity {

    GridView gridView;
    ArrayList<String> titles, suitNumbers, ids;

    List<SupremeGridItem> gridItems;
    SupremeGridAdapter gridAdapter;

    Spinner alphaSpinner;
    SharedPreferences sharedPreferences;

    private static int currentPage = 0;
    private static int lastItemPosition = 0;
    private static String JARGON = "sc";

    private Button next;
    private Button prev;
    private View headerView;
    private View footerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jud_main);

 //       btnNext = (Button) findViewById(R.id.btnNext);
 //       btnPrev = (Button) findViewById(R.id.btnPrev);

//        btnNext.setOnClickListener(new navigationListener());

//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
//
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("COURT OF APPEAL JUDGMENTS");

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // If an extra data is added to the btnSearch  query
            Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
            if (appData != null) {
                boolean jargon = appData.getBoolean(SupremeCourt.JARGON);
            }
            //doMySearch(query);
        }
        else {
            gridView = (GridView) findViewById(R.id.gridView);
            //LayoutInflater layoutInflater = LayoutInflater.from(this);

            titles = new ArrayList<String>();
            suitNumbers = new ArrayList<String>();
            gridItems = new ArrayList<SupremeGridItem>();
            ids = new ArrayList<String>();
            alphaSpinner = (Spinner) findViewById(R.id.alphabet);

            final ArrayList<String> mAlphabets = new ArrayList<String>();
            for (char alphabets = 'A'; alphabets <= 'Z'; alphabets++) {
                mAlphabets.add(String.valueOf(alphabets));
            }

            sharedPreferences = getSharedPreferences(Config.scJudgments, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mAlphabets);
            alphaSpinner.setAdapter(spinnerAdapter);

            alphaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    editor.putString("alphabet", mAlphabets.get(position));
                    editor.commit();
                    loadShelf();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            loadShelf();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.supreme_court, menu);

       // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
       // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }


    public int getRemainderRow(int screenHeightDp, int numberOfCurrentFillUp, int numberOfTilesPerRow, int gridItemHeight){
        int numberOfRows = 0;
        if(numberOfCurrentFillUp % numberOfTilesPerRow == 0 ){
            numberOfRows = numberOfCurrentFillUp / numberOfTilesPerRow;
        }
        else{
            numberOfRows = numberOfCurrentFillUp /numberOfTilesPerRow +1;
        }
        int remainderRowHeight = (screenHeightDp - (numberOfRows * gridItemHeight)) / gridItemHeight;

        return remainderRowHeight;
    }
    /**
     * Load the shelf
     */
    void loadShelf() {

        titles.clear();
        suitNumbers.clear();
        gridItems.clear();
        ids.clear();

//        if(gridView.getHeaderViewCount()!=0 && gridView.getFooterViewCount()!=0) {
//            gridView.removeFooterView(footerView);
//            gridView.removeHeaderView(headerView);
//        }
        //int index =0;

        /**
         * Logic for full display of shelf
         * all values are in dp
         */
        Configuration configuration = this.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp - 3*2;
        // 56dp is height of toolbar
        // 56dp is height of spinner
        // 10dp for top margin for spinner
        // 10dp for bottom margin for spinner
        int gridItemHeadHeight = 45; //toolbar height
        // padding top is 2dp;
        //footer is 25dp
        int screenHeightDp = configuration.screenHeightDp - 2 - 45 - 25 ;
        int numberOfRemainderRow =0;

        int gridItemHeight = 185; //height of grid

//        int gridSumHeight = gridItemHeadHeight + gridItemHeight;
//        int remainderHeight = screenHeightDp - gridSumHeight;
        int gridViewColumnWidth = 140; // Value assigned in XML for GridView;

        int numberOfTilePerRow = screenWidthDp / gridViewColumnWidth;

        String mString =  "----GENERAL OF ANAMBRA STATE\nv.\nATTORNEY GENERAL OF THE FEDERATION (REASONS)".toUpperCase();
        //int index = 0; //list iterator
        //cursor iterator.. this does not need lastItemPosition.
        for(int index = 0; index < 20; index++){

            titles.add(index+ mString);
            suitNumbers.add("SC." + index+ 3 + "/2015");

            ids.add("" + index);
                if ((index % numberOfTilePerRow) == 0) {
                    gridItems.add(new SupremeGridItem(titles.get(index), suitNumbers.get(index), "start", true));
                } else if ((index % numberOfTilePerRow) == (numberOfTilePerRow - 1)) {

                    gridItems.add(new SupremeGridItem(titles.get(index), suitNumbers.get(index), "end", true));
                } else {

                    gridItems.add(new SupremeGridItem(titles.get(index), suitNumbers.get(index), "middle", true));
                }

            numberOfRemainderRow = getRemainderRow(screenHeightDp, titles.size(), numberOfTilePerRow, gridItemHeight);
            if(numberOfRemainderRow == 0)
                if((index % numberOfTilePerRow) == (numberOfTilePerRow - 1)) {
                    break;
                }
        }
            //currentPage = currentPage +1;
          //  lastItemPosition = lastItemPosition + index +1;




        int remainderTiles = titles.size() % numberOfTilePerRow;
        if (remainderTiles > 0) {
//            numberOfRows = numberOfRows + 1;
            int fillUp = numberOfTilePerRow - remainderTiles;
            for (int i = 0; i < fillUp; i++) {

                if (i == (fillUp - 1)) {
                    gridItems.add(new SupremeGridItem("", "",
                            "end", false));
                } else {
                    gridItems.add(new SupremeGridItem("", "",
                            "middle", false));
                }

            }
        }

        if(numberOfRemainderRow > 0){
            int fillUp = numberOfTilePerRow * (numberOfRemainderRow);
            for (int i = 0; i < fillUp; i++) {
                if ((i % numberOfTilePerRow) == 0) {
                    gridItems.add(new SupremeGridItem("", "",
                            "start", false));
                } else if ((i % numberOfTilePerRow) == (numberOfTilePerRow - 1)) {
                    gridItems.add(new SupremeGridItem("", "",
                            "end", false));
                } else {
                    gridItems.add(new SupremeGridItem("", "",
                            "middle", false));
                }
        }
        }

//        if ((numberOfRows * gridItemHeight) < screenHeightDp) {
//            int remainderRowHeight = (screenHeightDp - (numberOfRows * gridItemHeight)) / gridItemHeight;

//            if (remainderRowHeight == 0) {
//                //numberOfRows = numberOfRows + 1;
//                for (int i = 0; i < numberOfTilePerRow; i++) {
//                    titles.add("");
//                    suitNumbers.add("");
//                    gridItems.add(new GridItem("", "",
//                            "head", false));
//                    if (i == 0) {
//                        gridItems.add(new GridItem("", "",
//                                "head", false));
//                    } else if (i == (numberOfTilePerRow - 1)) {
//                        gridItems.add(new GridItem("", "",
//                                "head", false));
//                    } else {
//                        gridItems.add(new GridItem("", "",
//                                "head", false));
//                    }
//                }
//            } else if (remainderRowHeight > 0) {
//                //numberOfRows = numberOfRows + remainderRowHeight + 1;
//                int fillUp = numberOfTilePerRow * (remainderRowHeight + 1);
//                for (int i = 0; i < fillUp; i++) {
//                    titles.add("");
//                    suitNumbers.add("");
//                    if ((i % numberOfTilePerRow) == 0) {
//                        gridItems.add(new GridItem("", "",
//                                "start", false));
//                    } else if ((i % numberOfTilePerRow) == (numberOfTilePerRow - 1)) {
//                        gridItems.add(new GridItem("", "",
//                                "end", false));
//                    } else {
//                        gridItems.add(new GridItem("", "",
//                                "", false));
//                    }
//
//                }
//            }

//        }





        //StickyGridHeadersListAdapterWrapper<List> check = new StickyGridHeadersListAdapterWrapper(GridItem);

        gridAdapter = new SupremeGridAdapter(this, gridItems);


        gridView.setAdapter(gridAdapter);

        //gridAdapter.notifyDataSetChanged();

//        if(lastItemPosition >=14){
//            btnNext.setEnabled(false);
//        }
//        else{
//            btnNext.setEnabled(true);
//        }


//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                if (titles.get(position).trim().length() > 2) {
//                    uniqID = ids.get(position);
//                    PopupMenu popupMenu = new PopupMenu(falcon.com.falcon.SupremeCourt.this, view);
//                    popupMenu.getMenuInflater().inflate(R.menu.new_arrivals, popupMenu.getMenu());
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.summary:
////                                   // Intent intentSummary = new Intent(falcon.com.falcon.SupremeCourt.this, Summary.class);
////                                    intentSummary.putExtra("id", uniqID);
////                                    intentSummary.putExtra("type", "sc");
////                                    startActivity(intentSummary);
//                                    break;
//
//                                case R.id.full:
////                                    Intent intentFull = new Intent(falcon.com.falcon.SupremeCourt.this, ReaderJud.class);
////                                    intentFull.putExtra("id", uniqID);
////                                    intentFull.putExtra("type", "sc");
////                                    startActivity(intentFull);
//                                    break;
//
//                            }
//
//                            return true;
//                        }
//                    });
//                    popupMenu.show();
//                }
//
//
//            }
//        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        lastItemPosition = lastItemPosition - titles.size();

        //Tried removing views her not support for adapter;

       // gridView = (GridViewWithHeaderAndFooter) findViewById(R.id.gridView);
        //this means the cursor will be reset back.
        loadShelf();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SupremeCourt.JARGON, true);
        startSearch(null, false, appData, false);
        return true;
    }

    private class navigationListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(v == next ){
                Log.d("btnNext ", "check");
               // loadShelf();
            }
            else if(v == prev){

            }
        }
    }

}

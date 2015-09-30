package git.lawpavilionprime.search_book;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import git.lawpavilionprime.R;
import git.lawpavilionprime._360degree._360DegreeMain;
import in.championswimmer.sfg.lib.SimpleFingerGestures;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 *
 */
public class EbookFragmentLand extends Fragment{



    ListView leftListView;
    ListView rightListView;


    //Menu Declarations
    ImageView image1;
    ImageView image2;
    ImageView image3;

    ImageView toogleMenu;
    ImageView toogleMenu2;
    TextView menuTitle;
    boolean isOpen = false;

    LinearLayout menuLeftOver;

    ListView bookmarkList;
    ListView recentList;
    ListView mainList;

    ArrayList<String> mBookmark = new ArrayList<String>();
    ArrayList<String> mRecent = new ArrayList<String>();
    ArrayList<String> mMain = new ArrayList<String>();

    LinearLayout motherLayout;
    //Menu declaration ends;

    static public ArrayList<String> mCaseTitle, mBody, mId, mType,mYear;

    static ArrayList<String> mTempCaseTitle, mTempBody, mTempId, mTempType, mTempYear;

    public static int TOTAL_LIST_ITEMS;
    public static int NUM_ITEMS_PAGE;

    public static int pageCount;
    private int screenHeight;

    public static int leftListTruePositionOffset;
    public static int rightListTruePositionOffset;

    public static ArrayList<String> mTempSubjectMatter, mTempIssues, mSubjectMatter, mIssues;


    // To remove this
    private EbookAdapter leftListAdapter;
    private EbookAdapter rightListAdapter;

    private static int increment = 0;

    Button prev, next, filter, history, search;
    EditText searchBox;
    private static Boolean[] mCheck;

    TextView pages, query;
    BroadcastReceiver receiver;

    public static String filterBy = "";


    //To create database for history
    public static String DB_NAME = "falcon";
    public static String TABLE_NAME = "History";
    public static SQLiteDatabase sqLiteDatabase;

    public static String CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " ( _ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                            " Query VARCHAR NOT NULL );" ;

    static Boolean isSC = false;
    static Boolean isCA = false;
    static Boolean isLFN = false;
    static Boolean isROC = false;
    static int scYearPosition, caYearPosition;
    static boolean isScAll, isCaAll;
    static String scYear, caYear;

    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ebook_fragment_land, container, false);
        super.onViewCreated(view, savedInstanceState);

        leftListView = (ListView)view.findViewById(R.id.leftList);
        rightListView = (ListView) view.findViewById(R.id.rightList);

        pages = (TextView) view.findViewById(R.id.pages);
        query = (TextView) view.findViewById(R.id.query);


        next = (Button) view.findViewById(R.id.next);
        search = (Button) view.findViewById(R.id.search);

        prev = (Button) view.findViewById(R.id.previous);
        filter = (Button) view.findViewById(R.id.search_filter);
        history = (Button) view.findViewById(R.id.search_history);
        searchBox = (EditText) view.findViewById(R.id.editTxt);

        //Menu Initialization
        image1 = (ImageView) view.findViewById(R.id.firstMenu);
        image2 = (ImageView) view.findViewById(R.id.secMenu);
        image3 = (ImageView) view.findViewById(R.id.thirdMenu);

        bookmarkList = (ListView) view.findViewById(R.id.bookMarkList);
        recentList = (ListView) view.findViewById(R.id.recentList);
        mainList = (ListView) view.findViewById(R.id.mainList);


        motherLayout = (LinearLayout) view.findViewById(R.id.menuMother);
        menuLeftOver = (LinearLayout) view.findViewById(R.id.menuLeftOver);

        toogleMenu = (ImageView) view.findViewById(R.id.toogleMenu);
        toogleMenu2 = (ImageView) view.findViewById(R.id.toogleMenu2);


        mCaseTitle = new ArrayList<String>();
        mBody = new ArrayList<String>();
        mId = new ArrayList<String>();
        mType = new ArrayList<String>();
        mYear = new ArrayList<String>();

        //Use temporary array list
        mTempCaseTitle = new ArrayList<String>();
        mTempBody = new ArrayList<String>();
        mTempId = new ArrayList<String>();
        mTempType = new ArrayList<String>();
        mTempYear = new ArrayList<String>();

        mSubjectMatter = new ArrayList<String>();
        mTempSubjectMatter = new ArrayList<String>();

        mIssues = new ArrayList<String>();
        mTempIssues = new ArrayList<String>();


        Configuration configuration = getResources().getConfiguration();
        screenHeight = (configuration.screenHeightDp) - 120; //combo height
        int mBlockHeight = 130; //130dp is the height of each item

        NUM_ITEMS_PAGE = screenHeight /mBlockHeight;

        TOTAL_LIST_ITEMS = 24; //to get this from cursor

        Bundle bundle = getArguments();
        if(bundle == null) {

            for (int i = 0; i < TOTAL_LIST_ITEMS; i++) {
                mCaseTitle.add("This is Item " + (i));
                mTempCaseTitle.add("This is Item " + (i));
                mBody.add("In trial order to manage memory resource and for speed optimization in the app," +
                        " it's better to stage-load cases when the need arises." +
                        " This method returns the requested content and respective page numbers.\n" +
                        "Input params: String court (sc|ca|fhc), " +
                        "String suitno, Integer pages (if null, request all pages in ascending order," +
                        " else fetch requested pages which are supplied via an array)." +
                        (i));
                mTempBody.add("In trial order to manage memory resource and for speed optimization in the app," +
                        " it's better to stage-load cases when the need arises." +
                        " This method returns the requested content and respective page numbers.\n" +
                        "Input params: String court (sc|ca|fhc), " +
                        "String suitno, Integer pages (if null, request all pages in ascending order," +
                        " else fetch requested pages which are supplied via an array)." +
                        (i));

                mTempSubjectMatter.add("This is the subject o");
                mSubjectMatter.add("This is the title o");

                mYear.add("1995");
                mTempYear.add("1995");

                mIssues.add("Ki lo n je be?");
                mTempIssues.add("Ki lo n je be?");


                mId.add("" + (i));
                mTempId.add("" + (i));
                if (i == (TOTAL_LIST_ITEMS - 1)) {
                    mType.add("LFN");
                    mTempType.add("LFN");
                    mYear.add("200" + i);
                    mTempYear.add("200" + i);
                } else if ((i % 2) == 0) {
                    mType.add("SC");
                    mTempType.add("SC");
                    mYear.add("200" + i);
                    mTempYear.add("200" + i);
                } else {
                    mType.add("CA");
                    mTempType.add("CA");
                    mYear.add("200" + i);
                    mTempYear.add("200" + i);
                }
            }

            // Saving History

            if(!mCaseTitle.isEmpty()){
                sqLiteDatabase  =   getActivity().openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
                sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);

                Cursor cursor = sqLiteDatabase.rawQuery("SELECT Query FROM " + TABLE_NAME  + ";", null);

                cursor.moveToFirst();
                boolean isSearchAvailable = false;

                int i = 0;
                while (cursor.isAfterLast() == false){

                    if(cursor.getString(i).equalsIgnoreCase(EbookReader.mQuery)){
                        isSearchAvailable = true;
                    }

                    cursor.moveToNext();
                }
                if(!isSearchAvailable){
                    sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME + " (Query)"+ " VALUES ('"+ EbookReader.mQuery + "'); ");
                }

                sqLiteDatabase.close();
                cursor.close();

            }

            increment = 0;
            TOTAL_LIST_ITEMS = mTempCaseTitle.size();

            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;

            val = val == 0? 0 : 1;
            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;

            Log.d("----", "Size: "+mTempCaseTitle.size());
        }
        else{
            mCaseTitle = (ArrayList<String>) bundle.get("HEAD");
            mTempCaseTitle = (ArrayList<String>) bundle.get("T_HEAD");

            mBody = (ArrayList<String>) bundle.get("BODY");
            mTempBody = (ArrayList<String>) bundle.get("T_BODY");

            mId = (ArrayList<String>) bundle.get("ID");
            mTempId = (ArrayList<String>) bundle.get("T_ID");

            mType = (ArrayList<String>) bundle.get("TYPE");
            mTempType = (ArrayList<String>) bundle.get("T_TYPE");

            mYear = (ArrayList<String>) bundle.get("YEAR");
            mTempYear = (ArrayList<String>) bundle.get("T_YEAR");

            // TODO: this is added
            mSubjectMatter = (ArrayList<String>) bundle.get("SUBJECT_MATTER");
            mTempSubjectMatter = (ArrayList<String>) bundle.get("T_SUBJECT_MATTER");

            mIssues = (ArrayList<String>) bundle.get("ISSUES");
            mTempIssues = (ArrayList<String>) bundle.get("T_ISSUES");
            //End

            increment = 0;

            TOTAL_LIST_ITEMS = mTempCaseTitle.size();

            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;

            val = val == 0? 0 : 1;
            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;

            Log.d("----", "Size: "+mTempCaseTitle.size());
        }




        for(int i = 0; i < 8; i++){
            mBookmark.add("This is the bookmark menu item");
            mRecent.add("This is the recent menu item");
            mMain.add("This is the main menu item");
        }


        //To handle for on orientation change


        int current = 0, initial;
        if(bundle != null) {
            initial = bundle.getInt("OFFSET", 0);
            int temp = ((initial % NUM_ITEMS_PAGE) == 0)? 0:1;
            current  = (initial / NUM_ITEMS_PAGE) + temp;
            if(current % 2 != 0) {
                current = current - 1;
            }
        }

        mCheck = new Boolean[2];

        increment = current;

        loadLeftList(increment, "left", true, filterBy);
        loadRightList(increment + 1, "left", mCheck[0]);

        ArrayAdapter<String> bkAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, mBookmark);
        ArrayAdapter<String> rcAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, mRecent);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, mMain);

        bookmarkList.setAdapter(bkAdapter);
        recentList.setAdapter(rcAdapter);
        mainList.setAdapter(mAdapter);

        menuLeftOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motherLayout.setVisibility(View.GONE);
                isOpen = !isOpen;
            }
        });

        toogleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen) {
                    motherLayout.setVisibility(View.VISIBLE);
                }else{
                    motherLayout.setVisibility(View.GONE);
                }
                isOpen = !isOpen;
            }
        });
        toogleMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpen) {
                    motherLayout.setVisibility(View.VISIBLE);
                }else{
                    motherLayout.setVisibility(View.GONE);
                }
                isOpen = !isOpen;
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentList.setVisibility(View.GONE);
                bookmarkList.setVisibility(View.GONE);
                mainList.setVisibility(View.VISIBLE);

                image1.setSelected(true);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentList.setVisibility(View.VISIBLE);
                bookmarkList.setVisibility(View.GONE);
                mainList.setVisibility(View.GONE);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentList.setVisibility(View.GONE);
                bookmarkList.setVisibility(View.VISIBLE);
                mainList.setVisibility(View.GONE);
            }
        });


        next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(mCheck[0]) {
                    increment += 2;
                    loadLeftList(increment, "left", mCheck[0], filterBy);
                    loadRightList(increment + 1, "left", mCheck[0]);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d("increment", ""+increment);

                if(mCheck[1]) {
                    increment -= 2;
                    loadLeftList(increment, "right", mCheck[1], filterBy);
                    loadRightList(increment+1, "right", true);
                }


            }
        });

        search.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EbookReader.mQuery = searchBox.getText().toString().trim();

                if(EbookReader.mQuery.isEmpty()){
                    searchBox.setError("Input btnSearch word");
                }
                else {
                    filterBy = "";
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), EbookReader.class);
                    intent.putExtra("QUERY" ,EbookReader.mQuery );
                    getActivity().startActivity(intent);
                    searchBox.setError(null);


                }


                //Search(Query)
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBox.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int truePosition = leftListTruePositionOffset + position;
                Intent intent = new Intent(getActivity(), _360DegreeMain.class);
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", position);

                intent.putExtra("ARGUMENT", bundle);
                startActivity(intent);

                Toast.makeText(getActivity(), ""+ truePosition, Toast.LENGTH_SHORT).show();
            }
        });

        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), _360DegreeMain.class);
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", position);

                intent.putExtra("ARGUMENT", bundle);
                startActivity(intent);

                int truePosition = rightListTruePositionOffset + position;
                Toast.makeText(getActivity(), "" + truePosition, Toast.LENGTH_SHORT).show();
            }
        });

        SimpleFingerGestures myLeftViewSfg = new SimpleFingerGestures();
        SimpleFingerGestures myRightViewSfg = new SimpleFingerGestures();

        myRightViewSfg.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                if(mCheck[0]) {
                    increment += 2;
                    loadLeftList(increment, "left", mCheck[0], filterBy);
                    loadRightList(increment + 1, "left", mCheck[0]);
//                    rightListAdapter.clearAdapter();
//                    rightListAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onPinch(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(int fingers) {
                return false;
            }
        });



        myLeftViewSfg.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                if(mCheck[1]) {
                    increment -= 2;
                    loadLeftList(increment, "right", mCheck[1], filterBy);
                    loadRightList(increment+1, "right", true);
                }
                return true;
            }

            @Override
            public boolean onPinch(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(int fingers) {
                return false;
            }



        });

        rightListView.setOnTouchListener(myRightViewSfg);
        leftListView.setOnTouchListener(myLeftViewSfg);


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog filterDialog = new FilterDialog();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(filterDialog, "filterFrag");
                transaction.commit();
            }
        });


        history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                HistoryDialog historyDialog = new HistoryDialog();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(historyDialog, "historyFrag");
                transaction.commit();
            }
        });
        return  view;
    }
    private void CheckEnable(int mIncrement){
        if(mIncrement + 1 == pageCount){
            next.setEnabled(false);
            mCheck[0] = false;
        }
        else{
            next.setEnabled(true);
            mCheck[0] = true;
        }

        if(mIncrement == 0 || mIncrement == 1){
            prev.setEnabled(false);
            mCheck[1] = false;
        }
        else{
            prev.setEnabled(true);
            mCheck[1] = true;
        }
    }

    public void loadRightList(int number,String direction, boolean check){



        if(!check) {
            if(rightListAdapter!=null) {
                rightListAdapter.clearAdapter();
                rightListAdapter.notifyDataSetChanged();
            }
            return;
        }

        ArrayList<String> sortHeader, sortBody, sortId, sortType, sortSubjectMatter, sortIssues, sortYear;

        sortHeader = new ArrayList<String>();
        sortBody = new ArrayList<String>();
        sortId = new ArrayList<String>();
        sortType = new ArrayList<String>();

        sortSubjectMatter = new ArrayList<String>();
        sortIssues = new ArrayList<String>();
        sortYear = new ArrayList<String>();

        int start  = number * NUM_ITEMS_PAGE;

        rightListTruePositionOffset = start;

        for(int i = start; i <(start) + NUM_ITEMS_PAGE; i++){
            if(i < mTempCaseTitle.size()){
                sortHeader.add(mTempCaseTitle.get(i));
                sortBody.add(mTempBody.get(i));
                sortId.add(mTempId.get(i));
                sortType.add(mTempType.get(i));


                sortSubjectMatter.add((mTempSubjectMatter.get(i)));
                sortIssues.add(mTempIssues.get(i));
                sortYear.add(mTempYear.get(i));
            }
            else{
                break;
            }
        }

        rightListAdapter = new EbookAdapter(getActivity(), sortHeader, sortSubjectMatter, sortIssues, sortYear, sortBody, EbookReader.mQuery, direction);
        rightListView.setAdapter(rightListAdapter);

        CheckEnable(number);
    }

    public void loadLeftList(int number, String direction, boolean check, String filterBy){

        if(pageCount == 0){
            next.setEnabled(false);
            prev.setEnabled(false);
            mCheck[0] = false;
            mCheck[1] = false;
            return;
        }

        if(!check)
            return;

        ArrayList<String> sortHeader, sortBody, sortId, sortType, sortSubjectMatter, sortIssues, sortYear;

        sortHeader  =   new ArrayList<String>();
        sortBody    =   new ArrayList<String>();
        sortId      =   new ArrayList<String>();
        sortType    =   new ArrayList<String>();

        sortSubjectMatter = new ArrayList<String>();
        sortIssues = new ArrayList<String>();
        sortYear = new ArrayList<String>();

        int start = number * NUM_ITEMS_PAGE;

        pages.setText("Page " + (number + 1)  + (((number + 2)<=pageCount)? ("-"+(number + 2)):"") + " of " + pageCount);
        query.setText(Html.fromHtml("<b>" + TOTAL_LIST_ITEMS + " results " + "</b>" + " found for " + "<b>" + EbookReader.mQuery +"</b>"+" "+ filterBy));

        leftListTruePositionOffset = start;

        for(int i = start; i <(start) + NUM_ITEMS_PAGE; i++){

            if(i < mTempCaseTitle.size()){
                sortHeader.add(mTempCaseTitle.get(i));
                sortBody.add(mTempBody.get(i));
                sortId.add(mTempId.get(i));
                sortType.add(mTempType.get(i));

                sortSubjectMatter.add((mTempSubjectMatter.get(i)));
                sortIssues.add(mTempIssues.get(i));
                sortYear.add(mTempYear.get(i));
            }

            else{
                break;
            }
        }

        leftListAdapter = new EbookAdapter(getActivity(), sortHeader, sortSubjectMatter, sortIssues, sortYear,  sortBody, EbookReader.mQuery, direction);
        leftListView.setAdapter(leftListAdapter);

        CheckEnable(number);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public static class HistoryDialog extends DialogFragment {


        ListView listView;
        ArrayList<String> mHistory;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view  = inflater.inflate(R.layout.search_history, container, false );
            final Dialog dialog = getDialog();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("SEARCH HISTORY");

            listView = (ListView) view.findViewById(R.id.list);
            mHistory = new ArrayList<String>();

            sqLiteDatabase = getActivity().openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT Query FROM " + TABLE_NAME + ";", null);

            cursor.moveToFirst();
            int  i = 0;
            while(cursor.isAfterLast() == false){

                mHistory.add(cursor.getString(i));
                cursor.moveToNext();
            }
            cursor.close();
            sqLiteDatabase.close();

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, mHistory);
            Log.d("checkAdapter", "adapter check"+adapter+"");

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
                    filterBy = "";
                    String query = adapter.getItem(position);

                    getActivity().finish();

                    Intent intent = new Intent(getActivity(), EbookReader.class);

                    intent.putExtra("QUERY",query );
                    getActivity().startActivity(intent);
                }
            });

            dialog.show();

            return view;
        }
    }

    public static class FilterDialog extends DialogFragment {

        Button apply, cancel;
        LinearLayout sc, ca, lfn, roc;
        CheckBox scCheck, caCheck, lfnCheck, rocCheck, judgeCheck;
        Spinner scSpinner, caSpinner;
        TextView scText, caText, lfnText, rocText, judgeText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final Dialog dialog = getDialog();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Filter Search Result");
            View view = inflater.inflate(R.layout.search_filter, container, false);
            ArrayList<String> scSpinnerArray, caSpinnerArray;
            ArrayAdapter scAdapter, caAdapter;

            sc = (LinearLayout) view.findViewById(R.id.sc);
            ca = (LinearLayout) view.findViewById(R.id.ca);
            lfn = (LinearLayout) view.findViewById(R.id.lfn);
            roc = (LinearLayout) view.findViewById(R.id.roc);

            scCheck = (CheckBox) view.findViewById(R.id.scCheck);
            caCheck = (CheckBox) view.findViewById(R.id.caCheck);
            lfnCheck = (CheckBox) view.findViewById(R.id.lfnCheck);
            rocCheck = (CheckBox) view.findViewById(R.id.rocCheck);

            scText = (TextView) view.findViewById(R.id.scText);
            caText = (TextView) view.findViewById(R.id.caText);
            lfnText = (TextView) view.findViewById(R.id.lfnText);
            rocText = (TextView) view.findViewById(R.id.rocText);

            scSpinner = (Spinner) view.findViewById(R.id.scSpinner);
            caSpinner = (Spinner) view.findViewById(R.id.caSpinner);

            scSpinnerArray = new ArrayList<String>();
            caSpinnerArray = new ArrayList<String>();

            scSpinnerArray.add("All");
            caSpinnerArray.add("All");

            for (int i = 0; i < 10; i++) {
                scSpinnerArray.add("200" + i);
                caSpinnerArray.add("200" + i);
            }

            scAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, scSpinnerArray);
            caAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, caSpinnerArray);

            scSpinner.setAdapter(scAdapter);
            caSpinner.setAdapter(caAdapter);

            apply = (Button) view.findViewById(R.id.apply);
            cancel = (Button) view.findViewById(R.id.cancel);

            restoreState();

            scCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        scSpinner.setVisibility(View.VISIBLE);
                        isSC = true;
                        caSpinner.getSelectedItem();
                        switchApply();
                    } else {
                        scSpinner.setVisibility(View.GONE);
                        isSC = false;
                        switchApply();
                    }
                }
            });
            scSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        scYear = (String)scSpinner.getSelectedItem();
                        scYearPosition = scSpinner.getSelectedItemPosition();
                        if(scYear == "All")
                            isScAll = true;
                        else
                            isScAll = false;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            caSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    caYear =  (String)caSpinner.getSelectedItem();
                    caYearPosition = caSpinner.getSelectedItemPosition();
                    if(caYear == "All")
                        isCaAll = true;
                    else
                        isCaAll = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            caCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        caSpinner.setVisibility(View.VISIBLE);
                        isCA = true;
                        switchApply();
                    } else {
                        caSpinner.setVisibility(View.GONE);
                        isCA = false;
                        switchApply();
                    }
                }
            });

            lfnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        isLFN = true;
                        switchApply();
                    } else {
                        isLFN = false;
                        switchApply();
                    }
                }
            });

            rocCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        isROC = true;
                        switchApply();
                    } else {
                        isROC = false;
                        switchApply();
                    }
                }
            });

            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    filterBy = "filtered by ";

                    if(isSC){
                        filterBy = filterBy + "<b>Supreme Court,</b> ";
                    }
                    if(isCA){
                        filterBy = filterBy + "<b>Court of Appeal,</b> ";
                    }
                    if(isLFN){
                        filterBy = filterBy + "<b>Laws of the Federation,</b> ";
                    }
                    if(isROC){
                        filterBy = filterBy + "<b>Civil Procedure rules,</b>";
                    }

                    filterQuery();
                    dialog.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();
            return view;
        }

        void restoreState(){
            if (isSC) {
                scCheck.setChecked(true);
                scSpinner.setVisibility(View.VISIBLE);
                scSpinner.setSelection(scYearPosition);
            } else {
                scSpinner.setVisibility(View.GONE);
            }

            if (isCA) {
                caCheck.setChecked(true);
                caSpinner.setVisibility(View.VISIBLE);
                caSpinner.setSelection(caYearPosition);
            } else {
                caSpinner.setVisibility(View.GONE);
            }

            if (isLFN) {
                lfnCheck.setChecked(true);
            }

            if (isROC) {
                rocCheck.setChecked(true);
            }

            switchApply();
            cancel.setEnabled(true);
        }

        void switchApply() {

            if (!isSC && !isCA && !isLFN && !isROC) {
                apply.setEnabled(false);
                cancel.setEnabled(true);
            }
            else {
                apply.setEnabled(true);
                cancel.setEnabled(false);
            }
        }

        public void loadAll(){

            mTempCaseTitle.clear();
            mTempBody.clear();
            mTempId.clear();
            mTempType.clear();
            mTempIssues.clear();
            mTempSubjectMatter.clear();
            mTempYear.clear();

            for(int i = 0; i < mCaseTitle.size(); i++){

                    mTempCaseTitle.add(mCaseTitle.get(i));
                    mTempBody.add(mBody.get(i));
                    mTempId.add(mId.get(i));
                    mTempType.add(mType.get(i));

                    mTempSubjectMatter.add(mSubjectMatter.get(i));
                    mTempIssues.add(mIssues.get(i));
                    mTempYear.add(mYear.get(i));
                }

            increment = 0;
            TOTAL_LIST_ITEMS = mTempCaseTitle.size();

            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;

            val = val == 0? 0 : 1;
            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;
        }

        public void filterQuery(){

            mTempCaseTitle.clear();
            mTempBody.clear();
            mTempId.clear();
            mTempType.clear();

            mTempIssues.clear();
            mTempSubjectMatter.clear();
            mTempYear.clear();

            for(int i = 0; i < mCaseTitle.size(); i++){

                if(isSC && (mType.get(i)== "SC") && ( isScAll || mYear.get(i).equals(scYear))){
                   //This fetches only supreme court
                    mTempCaseTitle.add(mCaseTitle.get(i));
                    mTempBody.add(mBody.get(i));
                    mTempId.add(mId.get(i));
                    mTempType.add(mType.get(i));
                    mTempYear.add(mYear.get(i));

                    mTempSubjectMatter.add(mSubjectMatter.get(i));
                    mTempIssues.add(mIssues.get(i));
                }

                if(isCA && (mType.get(i)== "CA")  && ( isCaAll || mYear.get(i).equals(caYear))){
                    //This fetches only court of appeal
                    mTempCaseTitle.add(mCaseTitle.get(i));
                    mTempBody.add(mBody.get(i));
                    mTempId.add(mId.get(i));
                    mTempType.add(mType.get(i));
                    mTempYear.add(mYear.get(i));

                    mTempSubjectMatter.add(mSubjectMatter.get(i));
                    mTempIssues.add(mIssues.get(i));
                }

                if(isLFN && (mType.get(i) == "LFN") ){
                    mTempCaseTitle.add(mCaseTitle.get(i));
                    mTempBody.add(mBody.get(i));
                    mTempId.add(mId.get(i));
                    mTempType.add(mType.get(i));
                    mTempYear.add(mYear.get(i));

                    mTempSubjectMatter.add(mSubjectMatter.get(i));
                    mTempIssues.add(mIssues.get(i));
                }

                if(isROC && (mType.get(i) == "ROC")){
                    mTempCaseTitle.add(mCaseTitle.get(i));
                    mTempBody.add(mBody.get(i));
                    mTempId.add(mId.get(i));
                    mTempType.add(mType.get(i));
                    mTempYear.add(mYear.get(i));

                    mTempSubjectMatter.add(mSubjectMatter.get(i));
                    mTempIssues.add(mIssues.get(i));
                }
            }
            increment = 0;

            TOTAL_LIST_ITEMS = mTempCaseTitle.size();
            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;


            val = val == 0? 0 : 1;
            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

             // send a broadcast
            Intent intent = new Intent("com.me.you");
            intent.putExtra("FILTER",filterBy);
            intent.putExtra("todo", "btnFilter");
            getActivity().sendBroadcast(intent);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("todo").equalsIgnoreCase("btnFilter")) {
                    String filterBy = intent.getStringExtra("FILTER");

                    if(mTempId.size() < 1){
                        //TODO: set no result found layout to visible n
                        Log.d("--DEBUG--", "---"+pageCount);
                    }

                    loadLeftList(0, "left", true, filterBy);
                    loadRightList(1, "left", mCheck[0]);
                }
            }
        };
        getActivity().registerReceiver(receiver,new IntentFilter("com.me.you"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }
}

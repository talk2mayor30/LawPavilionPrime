package git.lawpavilionprime.bookflip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bf.fc.page.curl.view.CurlRenderer;
import bf.fc.page.curl.view.CurlView;
import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;


public class SearchBookPortCopy extends Fragment {

    private CurlView curlView;
    private PageProvider textPageProvider;
    ArrayList<ListItems> aPageListItems;
    ArrayList<ArrayList<ListItems>> allPagesListItem;

    int margin;
    int padding;
    int borderColor;
    int background;
    int border;

    private ListView listView;
    private LinearLayout bookLayout;
    private LinearLayout.LayoutParams layoutParams;
    private int screenDimensionX;
    private int screenDimensionY;

    private GestureDetector gestureDetector;
    Configuration config;

    private TextView query;
    int multiLineHeight;
    float inches;

    int TOTAL_RESULT = 0;
    int TOTAL_PAGES = 0;
    int REMAINDER_RESULT = 0;




    ArrayList<String> title;
    ArrayList<String> body;
    ArrayList<String> subjectMatter;
    ArrayList<String> year;
    ArrayList<String> issues;
    ArrayList<String> id;
    ArrayList<String> type;

    ArrayList<QueryResult> mQueryResult;
    ArrayList<QueryResult> mTempQueryResult;

    Config falconUtility;
    public int NumOfItemPerPage;
    boolean isScrolled = false;
    int mTOTAL = 365;

    String scrollDirection;
    AlertDialog alertDialog = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        falconUtility = new Config(getActivity());
        View view = inflater.inflate(R.layout.search_ebook_port, container, false);

        mQueryResult = new ArrayList<QueryResult>();
        mTempQueryResult = new ArrayList<QueryResult>();
        config = getResources().getConfiguration();

        curlView = (CurlView) view.findViewById(R.id.curl_view);
        bookLayout = (LinearLayout) view.findViewById(R.id.book_layout);
        query = (TextView) view.findViewById(R.id.query);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        title = new ArrayList<String>();
        body = new ArrayList<String>();
        subjectMatter = new ArrayList<String>();
        issues = new ArrayList<String>();
        year = new ArrayList<String>();
        id = new ArrayList<String>();
        type = new ArrayList<String>();

        margin = 0;
        border = 0;
        borderColor = 0xFFFFFFF;
        background = 0xFFFFFFF;


        inches = metrics.heightPixels / metrics.ydpi;

        int width = config.screenWidthDp;
        int height = config.screenHeightDp;

        if ((inches < 7.0) && (config.orientation == config.ORIENTATION_PORTRAIT)) {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            bookLayout.setPadding(0, falconUtility.dpToPx(13), falconUtility.dpToPx(40), falconUtility.dpToPx(40));

            padding = falconUtility.dpToPx(40);

            screenDimensionX = width - 130 - 60; // 40 + 10dp from the layout calculation + 80 for curlview calculation =130 //50 i dont know
            screenDimensionY = height - 233; // (40 +40 +10 +10 header n bottom + margin )+ (13 +40 layout top n bottom margin + + 40*2 curlview padding of top n bottom)
        } else {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            bookLayout.setPadding(0, falconUtility.dpToPx(19), falconUtility.dpToPx(55), falconUtility.dpToPx(65));
            padding = falconUtility.dpToPx(50);

            if (config.orientation == config.ORIENTATION_LANDSCAPE) {

                screenDimensionX = (width - 160) / 2;
                screenDimensionY = height - 20;

            } else {
                screenDimensionX = width - 110 - 60; //50 + 50 +10dp from the layout calculation // 50 Safety
                screenDimensionY = height - 150; //(40 +40 +10 +10 header n bottom + margin )+ (18 +60 layout top n bottom margin + + 50 *2curlview padding of top n bottom)//278
            }
        }



        textPageProvider = new PageProvider(getActivity(), margin, padding, border, borderColor, background);
        listView = (ListView) view.findViewById(R.id.searchList);
        textPageProvider.setView(query);

        aPageListItems = new ArrayList<ListItems>();
        allPagesListItem = new ArrayList<ArrayList<ListItems>>();

        float density = metrics.density;
        textPageProvider.setScreenDimensionX(screenDimensionX);

        Paint paint = new Paint();
        paint.setTextSize(falconUtility.dpToPx(13));
        Paint.FontMetrics fmText = paint.getFontMetrics();
        String txt = "method";

        Rect rect = new Rect();

        float characterHeight = fmText.descent - fmText.top;
        if (inches < 7.0 && (config.orientation == config.ORIENTATION_PORTRAIT)) {
            multiLineHeight = Math.round(characterHeight * 9.2f);
        } else {
            if (metrics.density >= 1.5) {
                multiLineHeight = Math.round(characterHeight * 9.8f);
            } else {
                multiLineHeight = Math.round(characterHeight * 9.6f);
            }
        }

        NumOfItemPerPage = falconUtility.dpToPx(screenDimensionY) / multiLineHeight;

        gestureDetector = new GestureDetector(getActivity(), new GestureEvent());

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {
                    curlView.onTouch(curlView, event);
                }
                return true;
            }

        });

        new LoadData().execute();
        return view;
    }

//    private void CheckEnable() {
//
//        if (increment + 1 == pageCount) {
//            next.setEnabled(false);
//        } else {
//            next.setEnabled(true);
//        }
//        if (increment == 0) {
//            prev.setEnabled(false);
//        } else {
//            prev.setEnabled(true);
//
//        }
//    }


//    public void loadList(int number, String direction, String filterBy) {
//
//        if (pageCount == 0) {
//            if (listAdapter != null) {
//                listAdapter.clearAdapter();
//                listAdapter.notifyDataSetChanged();
//            }
//            return;
//        }
//
//        ArrayList<String> sortHeader, sortBody, sortId, sortType, sortSubjectMatter, sortIssues, sortYear;
//
//        sortHeader = new ArrayList<>();
//        sortBody = new ArrayList<>();
//        sortId = new ArrayList<>();
//        sortType = new ArrayList<>();
//        sortSubjectMatter = new ArrayList<>();
//        sortIssues = new ArrayList<>();
//        sortYear = new ArrayList<>();
//
//        int start = number * NUM_ITEMS_PAGE;
//
//        pages.setText("Page " + (number + 1) + " of " + pageCount);
//        query.setText(Html.fromHtml("<b>" + TOTAL_LIST_ITEMS + " results" + "</b>" + " found for " + "<b> \"" + SearchBook.mQuery + "\"</b> " + filterBy));
//
//        truePositionOffset = start;
//
//        for (int i = start; i < (start) + NUM_ITEMS_PAGE; i++) {
//            if (i < mTempQueryResult.size()) {
//                sortHeader.add(mTempQueryResult.get(i).getTitle());
//                sortBody.add(mTempQueryResult.get(i).getContent());
//                sortId.add("" + mTempQueryResult.get(i).getId());
//                sortType.add(mTempQueryResult.get(i).getSource());
//                sortSubjectMatter.add(mTempQueryResult.get(i).getSubjectmatter());
//                sortIssues.add(mTempQueryResult.get(i).getIssue());
//                sortYear.add("1978");
//            } else {
//                break;
//            }
//        }
//
//        listAdapter = new SearchBookAdapter(getActivity(), sortHeader, sortSubjectMatter, sortIssues, sortYear, sortBody, SearchBook.mQuery, direction);
//        listview.setAdapter(listAdapter);
//        CheckEnable();
//    }


    public static class HistoryDialog extends DialogFragment {
//
//        ListView listView;
//        ArrayList<String> mHistory;
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//            View view = inflater.inflate(R.layout.search_history, container, false);
//
//            listView = (ListView) view.findViewById(R.id.list);
//            DataBaseAdapter baseAdapter = new DataBaseAdapter(getActivity());
//            baseAdapter.open();
//            mHistory = baseAdapter.searchHistory();
//            baseAdapter.close();
//
//            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.search_simple_list_item, mHistory);
//            listView.setAdapter(adapter);
//
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    //filterBy = "";
//                    String query = adapter.getItem(position);
//                    getActivity().finish();
//                    Intent intent = new Intent(getActivity(), SearchBook.class);
//
//                    intent.putExtra(Config.SEARCH_QUERY, query);
//                    getActivity().startActivity(intent);
//                }
//            });
//
//            getDialog().setTitle("SEARCH HISTORY");
//
//            return view;
//        }
//    }
//
//    public static class FilterDialog extends DialogFragment {
//
//        Button apply, cancel;
//
//        Boolean isSC = false;
//        Boolean isCA = false;
//        Boolean isLFN = false;
//        Boolean isROC = false;
//        LinearLayout sc, ca, lfn, roc;
//        CheckBox scCheck, caCheck, lfnCheck, rocCheck, judgeCheck;
//        Spinner scSpinner, caSpinner;
//        TextView scText, caText, lfnText, rocText, judgeText;
//        String scYear, caYear;
//        boolean isScAll, isCaAll;
//
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//            final Dialog dialog = getDialog();
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setTitle("Filter Search Result");
//            View view = inflater.inflate(R.layout.search_filter, container, false);
//
//
//            ArrayList<String> scSpinnerArray, caSpinnerArray;
//            ArrayAdapter scAdapter, caAdapter;
//
//
//            sc = (LinearLayout) view.findViewById(R.id.sc);
//            ca = (LinearLayout) view.findViewById(R.id.ca);
//            lfn = (LinearLayout) view.findViewById(R.id.lfn);
//            roc = (LinearLayout) view.findViewById(R.id.roc);
//
//            scCheck = (CheckBox) view.findViewById(R.id.scCheck);
//            caCheck = (CheckBox) view.findViewById(R.id.caCheck);
//            lfnCheck = (CheckBox) view.findViewById(R.id.lfnCheck);
//            rocCheck = (CheckBox) view.findViewById(R.id.rocCheck);
//
//            scText = (TextView) view.findViewById(R.id.scText);
//            caText = (TextView) view.findViewById(R.id.caText);
//            lfnText = (TextView) view.findViewById(R.id.lfnText);
//            rocText = (TextView) view.findViewById(R.id.rocText);
//
//            scSpinner = (Spinner) view.findViewById(R.id.scSpinner);
//            caSpinner = (Spinner) view.findViewById(R.id.caSpinner);
//
//            scSpinnerArray = new ArrayList<String>();
//            caSpinnerArray = new ArrayList<String>();
//
//            scSpinnerArray.add("All");
//            caSpinnerArray.add("All");
//
//            for (int i = 0; i < 10; i++) {
//                scSpinnerArray.add("200" + i);
//                caSpinnerArray.add("200" + i);
//            }
//
//            scAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, scSpinnerArray);
//            caAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, caSpinnerArray);
//
//            scSpinner.setAdapter(scAdapter);
//            caSpinner.setAdapter(caAdapter);
//
//            apply = (Button) view.findViewById(R.id.apply);
//            cancel = (Button) view.findViewById(R.id.cancel);
//            apply.setEnabled(false);
//
//
//            scCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked) {
//                        scSpinner.setVisibility(View.VISIBLE);
//                        isSC = true;
//                        caSpinner.getSelectedItem();
//                        switchApply();
//                    } else {
//                        scSpinner.setVisibility(View.GONE);
//                        isSC = false;
//                        switchApply();
//                    }
//                }
//            });
//            scSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    scYear = (String) scSpinner.getSelectedItem();
//                    if (scYear == "All")
//                        isScAll = true;
//                    else
//                        isScAll = false;
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            caSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    caYear = (String) caSpinner.getSelectedItem();
//                    if (caYear == "All")
//                        isCaAll = true;
//                    else
//                        isCaAll = false;
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            caCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked) {
//                        caSpinner.setVisibility(View.VISIBLE);
//                        isCA = true;
//                        switchApply();
//                    } else {
//                        caSpinner.setVisibility(View.GONE);
//                        isCA = false;
//                        switchApply();
//                    }
//                }
//            });
//
//            lfnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked) {
//                        isLFN = true;
//                        switchApply();
//                    } else {
//                        isLFN = false;
//                        switchApply();
//                    }
//                }
//            });
//
//            rocCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked) {
//                        isROC = true;
//                        switchApply();
//                    } else {
//                        isROC = false;
//                        switchApply();
//                    }
//                }
//            });
//
////
////            apply.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    filterBy = "filtered by ";
////
////                    if (isSC) {
////                        filterBy = filterBy + "<b>Supreme Court,</b> ";
////                    }
////                    if (isCA) {
////                        filterBy = filterBy + "<b>Court of Appeal,</b> ";
////                    }
////                    if (isLFN) {
////                        filterBy = filterBy + "<b>Laws of the Federation,</b> ";
////                    }
////                    if (isROC) {
////                        filterBy = filterBy + "<b>Civil Procedure rules,</b>";
////                    }
////
////                    filterSearchResult();
////                    dialog.dismiss();
////                }
////            });
//
////            cancel.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    filterBy = "";
////                    loadAll();
////                    dialog.dismiss();
////                }
////            });
//
//            dialog.show();
//            return view;
//        }
//
//        void switchApply() {
//
//            if (!isSC && !isCA && !isLFN && !isROC) {
//                apply.setEnabled(false);
//                cancel.setEnabled(true);
//            } else {
//                apply.setEnabled(true);
//                cancel.setEnabled(false);
//            }
//        }
////
////        public void loadAll() {
////
////            mTempQueryResult.clear();
////
////
////            for (int i = 0; i < mQueryResult.size(); i++) {
////
////                mTempQueryResult.add(mQueryResult.get(i));
////
////            }
////
////            increment = 0;
////            int TOTAL_LIST_ITEMS = mTempQueryResult.size();
////
////            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
////
////            val = val == 0 ? 0 : 1;
////            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;
////        }
//
////        public void filterSearchResult() {
////
////            mTempQueryResult.clear();
////
////            for (int i = 0; i < mQueryResult.size(); i++) {
////
////
////                if (isSC && (mQueryResult.get(i).getSource() == "Supreme Court")) {  // && ( isScAll || mYear.get(i).equals(scYear))
////                    //This fetches only supreme court
////                    mTempQueryResult.add(mQueryResult.get(i));
////
////                }
////
////                if (isCA && (mQueryResult.get(i).getContent() == "Court of Appeal")) { //&& ( isCaAll || mYear.get(i).equals(caYear))
////                    //This fetches only court of appeal
////                    mTempQueryResult.add(mQueryResult.get(i));
////                }
////
////                if (isLFN && (mQueryResult.get(i).getContent() == "Laws of Federation")) {
////
////                    mTempQueryResult.add(mQueryResult.get(i));
////
////                }
////
////                if (isROC && (mQueryResult.get(i).getContent() == "Court Rules")) {
////
////                    mTempQueryResult.add(mQueryResult.get(i));
////
////                }
////            }
////            increment = 0;
////
////            TOTAL_LIST_ITEMS = mTempQueryResult.size();
////            int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
////            val = val == 0 ? 0 : 1;
////            pageCount = (TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE) + val;
////
////        }
//
//        @Override
//        public void onDestroyView() {
//            super.onDestroyView();
//
//            // send a broadcast
//            Intent intent = new Intent("com.me.you");
//            intent.putExtra("todo", "filter");
//            intent.putExtra("FILTER", "Dummy");
//            getActivity().sendBroadcast(intent);
//
//        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getStringExtra("todo").equalsIgnoreCase("filter")) {
//                    String filterBy = intent.getStringExtra("FILTER");
//                    loadList(0, "left", filterBy);
//                }
//
//            }
//        };
//        getActivity().registerReceiver(receiver, new IntentFilter("com.me.you"));
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        getActivity().unregisterReceiver(receiver);
//    }

    class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Bundle bundle = getArguments();
            if (bundle == null) {

                try {

                 //   mQueryResult = new FalconSearchHelper().searchQuery(SearchBook.mQuery);
                 //   mTempQueryResult.addAll(mQueryResult);

                    for (int i=0; i<mTOTAL; i++){
                        mQueryResult.add(new QueryResult(i));
                    }
                    mTempQueryResult.addAll(mQueryResult);

                } catch (Exception e) {
                    e.printStackTrace();
                }




            } else {

//                mQueryResult = (ArrayList<QueryResult>) bundle.getSerializable(Config.SEARCH_QUERY_RESULT);
//                mTempQueryResult = (ArrayList<QueryResult>) bundle.getSerializable(Config.SEARCH_TEMP_QUERY_RESULT);

//                }


            }
            Log.i("---FALCON---", "" + mTempQueryResult.size());
//            if (!mTempQueryResult.isEmpty()) {
//                baseAdapter.open();
//                baseAdapter.searchResultSaveQuery(SearchBook.mQuery);
//                baseAdapter.close();
//            }
//
//
//            int initial, current;
//            if (bundle != null) {
//                initial = bundle.getInt(Config.SEARCH_OFFSET, 0);
//                int temp = (initial % NUM_ITEMS_PAGE == 0) ? 0 : 1;
//                current = (initial / NUM_ITEMS_PAGE) + temp;
//            } else {
//
//                current = 0;
//            }
//
//            increment = current;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TOTAL_RESULT = mTempQueryResult.size();
//            query.setText(Html.fromHtml("<b>" + TOTAL_RESULT + " results" + "</b>" + " found for "
//                    + "<b> \"" + SearchBook.mQuery + "\"</b> "));


            TOTAL_PAGES = TOTAL_RESULT / NumOfItemPerPage;
            REMAINDER_RESULT = TOTAL_RESULT % NumOfItemPerPage;

            int position = 0;

            for (int k = 0; k < TOTAL_PAGES; k++) {

                int start = k * NumOfItemPerPage;
                Log.d("START", "" + start);
                aPageListItems = new ArrayList<ListItems>();
                for (int i = 0; i < NumOfItemPerPage; i++) {

                    position = start + i;

                    aPageListItems.add(new ListItems(mTempQueryResult.get(position).getTitle().toLowerCase(),
                            mTempQueryResult.get(position).getSubjectMatter(), mTempQueryResult.get(position).getIssue(),
                            mTempQueryResult.get(position).getDescription(), mTempQueryResult.get(position).getYear()));
                }
                allPagesListItem.add(aPageListItems);
            }

            aPageListItems = new ArrayList<ListItems>();
            position = TOTAL_PAGES * NumOfItemPerPage;

            for (int k = 0; k < REMAINDER_RESULT; k++) {

                int mPosition = position + k;

                aPageListItems.add(new ListItems(mTempQueryResult.get(mPosition).getTitle().toLowerCase(),
                        mTempQueryResult.get(mPosition).getSubjectMatter(), mTempQueryResult.get(mPosition).getIssue(),
                        mTempQueryResult.get(mPosition).getDescription(),
                        mTempQueryResult.get(mPosition).getYear()));
            }
            allPagesListItem.add(aPageListItems);

            ArrayList<String> backContents = new ArrayList<String>();
            backContents.add(" ");
            backContents.add(" ");
            backContents.add(" ");
            backContents.add(" ");

            textPageProvider.setListItems(allPagesListItem);
            textPageProvider.loadAllPages();
            textPageProvider.setBackStrings(backContents);
            curlView.setPageProvider(textPageProvider);
            curlView.setCurrentIndex(0);

            ArrayList<String> dummy = new ArrayList<String>();

            for (int i = 0; i < NumOfItemPerPage; i++) {
                dummy.add(" ");
            }

            Adapter adapter = new Adapter(getActivity(), dummy, multiLineHeight);
            listView.setAdapter(adapter);

            curlView.setSizeChangedObserver(new CurlView.SizeChangedObserver() {
                @Override
                public void onSizeChanged(int width, int height) {
                    if (width > height) {
                        curlView.setViewMode(CurlRenderer.SHOW_TWO_PAGES);
                    } else {
                        curlView.setViewMode(CurlRenderer.SHOW_ONE_PAGE);
                    }
                }
            });

//            progressMum.setVisibility(View.GONE);
//            if (mTempQueryResult.isEmpty()) {
//                noResultMum.setVisibility(View.VISIBLE);
//                noResult.setText("No result found for \"" + SearchBook.mQuery + "\"");
//            }
//            loadList(increment, "left", filterBy);
//
//            next.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    increment++;
//                    loadList(increment, "left", filterBy);
//                    //CheckEnable();
//                }
//            });
//
//            prev.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    increment--;
//                    loadList(increment, "right", filterBy);
//                    //CheckEnable();
//                }
//            });
//
//            search.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    String newQuery = searchBox.getText().toString().trim();
//                    SearchBook.mQuery = newQuery;
//                    if (newQuery.isEmpty()) {
//                        searchBox.setError("Input search word");
//                    } else {
//                        filterBy = "";
//                        getActivity().finish();
//                        Intent intent = new Intent(getActivity(), SearchBook.class);
//                        intent.putExtra(Config.SEARCH_QUERY, newQuery);
//                        getActivity().startActivity(intent);
//
//                    }
//                }
//            });

//            searchBox.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                    String newQuery = searchBox.getText().toString().trim();
//                    SearchBook.mQuery = newQuery;
//                    if (event.getAction() == KeyEvent.ACTION_DOWN
//                            &&
//                            (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//                        if (newQuery.isEmpty()) {
//                            searchBox.setError("Input search word");
//                        } else {
//                            filterBy = "";
//                            getActivity().finish();
//                            Intent intent = new Intent(getActivity(), SearchBook.class);
//                            intent.putExtra(Config.SEARCH_QUERY, searchBox.getText().toString().trim());
//                            startActivity(intent);
//                            searchBox.setText("");
//                        }
//
//                    } else if ((event.getAction() == KeyEvent.ACTION_DOWN)
//                            &&
//                            ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_ESCAPE)
//                            )) {
//
//                        if (!newQuery.isEmpty()) {
//                            searchBox.setText("");
//                        } else {
//                            getActivity().finish();
//                        }
//
//
//                    }
//
//                    return true;
//                }
//            });
//
//            searchBox.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    searchBox.setError(null);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
//
//            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    int truePosition = truePositionOffset + position;
//                    Toast.makeText(getActivity(), "" + truePosition, Toast.LENGTH_SHORT).show();
//                }
//            });

//            SimpleFingerGestures mySfg = new SimpleFingerGestures();
//            mySfg.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
//                @Override
//                public boolean onSwipeUp(int i, long l) {
//                    return false;
//                }
//
//                @Override
//                public boolean onSwipeDown(int i, long l) {
//                    return false;
//                }
//
//                @Override
//                public boolean onSwipeLeft(int i, long l) {
//                    if (pageCount == 0)
//                        return true;
//
//
//                    if (increment + 1 == pageCount)
//                        return true;
//
//                    increment++;
//                    loadList(increment, "left", filterBy);
//                    //CheckEnable();
//                    return true;
//                }
//
//                @Override
//                public boolean onSwipeRight(int i, long l) {
//                    if (pageCount == 0)
//                        return true;
//
//                    if (increment == 0)
//                        return true;
//
//                    increment--;
//                    loadList(increment, "right", filterBy);
//                    //CheckEnable();
//                    return true;
//                }
//
//                @Override
//                public boolean onPinch(int i, long l) {
//                    return false;
//                }
//
//                @Override
//                public boolean onUnpinch(int i, long l) {
//                    return false;
//                }
//            });
//            listview.setOnTouchListener(mySfg);
//
//            filter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FilterDialog filterDialog = new FilterDialog();
//                    FragmentManager manager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.add(filterDialog, "filterFrag");
//                    transaction.commit();
//                }
//            });
//
//            history.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    HistoryDialog historyDialog = new HistoryDialog();
//                    FragmentManager manager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.add(historyDialog, "historyFrag");
//                    transaction.commit();
//                }
//            });
//
        }
    }


    public void buildDialog(){
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Info");
        builder.setMessage("Do you want to fetch more results")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog = null;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog = null;
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private class GestureEvent extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, final float distanceX, float distanceY) {
            Log.d("POSITION X _ Y", "" + distanceX + "--" + distanceY);
            //Log.d("POSITION Y", ""+distanceY);
            Log.d("GEST SCROLL", "SCROLL");
            isScrolled = true;

            if (distanceX < 0) {
                scrollDirection = "right";

            } else {
                scrollDirection = "left";

            }

            return true;

        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("GEST DOWN", "DOWN");
            isScrolled = false;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("GEST FLING", "FLING");
            isScrolled = true;

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            onFling(e, e, 40f, 30f);
            Log.d("GEST SHOW", "PRESS");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d("GEST UP", "UP");
            if (isScrolled) {
                Log.d("GEST UP", "UP scroll");
            } else {
                if (inches < 7.0 && (config.orientation == config.ORIENTATION_PORTRAIT)) {

                    int positionY = Math.round(event.getY());

                    int spotHeight = 210;

                    Log.d("GEST UP", "UP scroll no scroll");
                    Log.d("GEST SPOT Y", event.getY() + "");
                    Log.d("GEST postion Y", "" + positionY);
                    Log.d("GEST POSITION LINE", spotHeight + "");

                    int position = positionY / multiLineHeight;
                    Log.d("GEST POSITION", position + "");

                    int offsetedPosition =  textPageProvider.currentIndex * NumOfItemPerPage + position;
                    Toast.makeText(getActivity(), "Position: " + offsetedPosition, Toast.LENGTH_SHORT).show();
                } else {

                    int positionY = Math.round(event.getY());
                    int spotHeight = 210;

                    Log.d("GEST UP", "UP scroll no scroll");
                    Log.d("GEST SPOT Y", event.getY() + "");
                    Log.d("GEST height", multiLineHeight + "");
                    Log.d("GEST POSITION LINE", spotHeight + "");

                    int position = positionY / multiLineHeight;
                    Log.d("GEST POSITION", position + "");

                    int offsetedPosition =  textPageProvider.currentIndex * NumOfItemPerPage + position;
                    Toast.makeText(getActivity(), "Position: " + offsetedPosition, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }
}

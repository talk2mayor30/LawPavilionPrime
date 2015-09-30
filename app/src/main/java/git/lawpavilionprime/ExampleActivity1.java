package git.lawpavilionprime;

import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bf.fc.page.curl.view.CurlRenderer;
import bf.fc.page.curl.view.CurlView;


public class ExampleActivity1 extends ActionBarActivity {
    private CurlView curlView;
    private PageProvider1 textPageProvider;
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
    private GestureDetector gestureUp;
    Configuration config;

    private TextView query;
    int multiLineHeight;
    float inches;

    int TOTAL_RESULT = 19;
    int TOTAL_PAGE = 0;
    int REMAINDER_RESULT = 0;

    ArrayList<String> title;
    ArrayList<String> body;
    ArrayList<String> subjectMatter;
    ArrayList<String> year;
    ArrayList<String> issues;

    int NumOfItemPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.search_ebook_port);
        getSupportActionBar().hide();
        config =  getResources().getConfiguration();

        curlView = (CurlView) findViewById(R.id.curl_view);
        bookLayout = (LinearLayout) findViewById(R.id.book_layout);
        query = (TextView) findViewById(R.id.query);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        title = new ArrayList<String>();
        body = new ArrayList<String>();
        subjectMatter = new ArrayList<String>();
        issues = new ArrayList<String>();
        year = new ArrayList<String>();




        String strTitle = "Omolara Adejuwon v. nigerian Navy filed in supreme court of Nigeria on June 2014 " +
                "Omolara Adejuwon v. nigerian Navy filed in supreme court of Nigeria on June 2014 ";
        String strSubjectMatter = "Civil Matter Bla Bla Bla"+
                "Whether Omolara is fit to join the Nigerian Navy AND ALSO TO SEE WHETHER " +
                "THIS TEXT FITS TO A LINE OR NOT IN ORDER TO IMPROVE ON USER EXPERIENCE";
        String strBody="In trial order to manage memoryesource and for speed optimization in the app,\" +\n" +
                "                        \" it's better to stage-load cases when the need arises.\" +\n" +
                "                        \" This method returns the requested content and respective page numbers.\\n\" +\n" +
                "                        \"Input params: String court (sc|ca|fhc), \" +\n" +
                "                        \"String suitno, Integer pages (if null, request all pages in ascending order,\" +\n" +
                "                        \" else fetch requested pages which are supplied via an array)--THE END.";
        String strYear = "2015";

        String strIssues = "THIS TEXT FITS TO A LINE OR NOT IN ORDER TO IMPROVE ON USER EXPERIENCE";

        for(int i= 0; i<TOTAL_RESULT; i++){
            title.add(i+"  "+strTitle);
            subjectMatter.add(strSubjectMatter);
            body.add(strBody);
            year.add(strYear);
            issues.add(strIssues);
        }

        inches = metrics.heightPixels / metrics.ydpi;

        int width =  config.screenWidthDp;
        int height = config.screenHeightDp;

        if((inches < 7.0) && (config.orientation == config.ORIENTATION_PORTRAIT)) {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            bookLayout.setPadding(0, XdpToPx(13), XdpToPx(40), XdpToPx(40));
            padding = XdpToPx(40);

            screenDimensionX = width - 130 -40; // 40 + 10dp from the layout calculation + 80 for curlview calculation =130 //40 i dont know
            screenDimensionY = height - 233; // (40 +40 +10 +10 header n bottom + margin )+ (13 +40 layout top n bottom margin + + 40*2 curlview padding of top n bottom)
        }
        else{
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            bookLayout.setPadding(0, XdpToPx(19), XdpToPx(55), XdpToPx(65));
            padding = XdpToPx(50);

            if(config.orientation == config.ORIENTATION_LANDSCAPE){

                screenDimensionX = (width - 160) / 2;
                screenDimensionY = height - 20 ;

            }
            else {
                screenDimensionX = width - 110 - 40; //50 + 50 +10dp from the layout calculation // 40 Safety
                screenDimensionY = height - 150; //(40 +40 +10 +10 header n bottom + margin )+ (18 +60 layout top n bottom margin + + 50 *2curlview padding of top n bottom)//278
            }
        }


        margin = 0;

        border = 0;
        borderColor = 0xFFFFFFF;
        background = 0xFFFFFFF;

        textPageProvider = new PageProvider1(this, margin, padding, border, borderColor, background);
        listView = (ListView) findViewById(R.id.searchList);
        textPageProvider.setView(query);
        curlView.setPageProvider(textPageProvider);
        curlView.setCurrentIndex(0);
        //curlView.setBackground(getResources().getDrawable().getColor( android.R.color.transparent));

        aPageListItems = new ArrayList<ListItems>();
        allPagesListItem = new ArrayList<ArrayList<ListItems>>();

        float density = metrics.density;

//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        Log.d("LOG CONFIG SCREEN WIDTH--HEIGHT", width+"---"+height);
        Log.d("LOG DENSITY" , density+"");
        Log.d("LOG DPI CONFIG", config.densityDpi+"");
        Log.d("LOG DPI metric", ""+metrics.densityDpi);
        Log.d("LOG height px metric", ""+metrics.heightPixels+"--"+metrics.widthPixels+"---"+metrics.xdpi+"---"+metrics.ydpi);

        textPageProvider.setScreenDimensionX(screenDimensionX);
        Log.d(" LOG DENSITY", ""+getResources().getDisplayMetrics().density);

        Log.d("LOG DIMENSION X", width + " --" + (screenDimensionX) );

        Log.d("LOG SIZE X` IN DP",  width + "--" );

        Log.d("LOG Height ", height+"---"+screenDimensionY);

        Paint paint = new Paint();
        paint.setTextSize(XdpToPx(13));
        Paint.FontMetrics fmText = paint.getFontMetrics();
        String txt = "method";

        Rect rect = new Rect();



        //paint.getTextBounds(txt, 0, txt.length(), rect);

        Log.d("LOG FONT HEIGHT", XdpToPx(13) + "--" );
        Log.d("LOG BOUND WIDTH", rect.width()+"--");

//        float characterHeight =  fmText.descent - fmText.top + (fmText.bottom) ;
//
//        float  middleHeight = (fmText.bottom * 2) - fmText.top + fmText.descent;
//,
//        float lastLine = fmText.descent - fmText.top + (fmText.bottom * 2);

        float characterHeight = fmText.descent - fmText.top ;

        Log.d("LOG Character property", fmText.bottom +"--" +fmText.top + "--"+fmText.leading+"--"+fmText.descent);
        Log.d("LOG CHAR HEIGHT", ""+characterHeight);


        if(inches < 7.0 && (config.orientation == config.ORIENTATION_PORTRAIT)) {
            multiLineHeight  = Math.round(characterHeight * 9.2f );
        }
        else{
            if(metrics.density >=1.5){
                multiLineHeight  = Math.round(characterHeight * 9.8f );
            }
            else{
                multiLineHeight = Math.round(characterHeight * 9.6f);
            }
        }

        Log.d("LOG INCHES", inches+"");

        Log.d("LOG MultiLine Height", multiLineHeight+"");
        Log.d("LOG Compare, dpTopx", ""+YdpToPx(130));
        Log.d("LOG No of lines", ""+screenDimensionY /130);
        Log.d("LOG No of lines character", ""+ (XdpToPx(screenDimensionY) / multiLineHeight));

        //
        NumOfItemPerPage = XdpToPx(screenDimensionY) / multiLineHeight;
//        int remainder = screenDimensionY % 130;
//
//        Log.d("Remainder", ""+remainder);
//        int round  = ((remainder / 10) >= 8)? 1:0;
//        Log.d("Round", ""+round);

        ArrayList<String> dummy = new ArrayList<String>();

        for(int i =0; i<NumOfItemPerPage; i++){
            dummy.add(" ");
        }


        Adapter adapter = new Adapter(ExampleActivity1.this, dummy, multiLineHeight);
        listView.setAdapter(adapter);


        Log.d("ITEM COUNT in DP", NumOfItemPerPage+"");

        Log.d("FONT HEIGHT", fmText.ascent+"--"+fmText.descent+"--"+fmText.leading );
        Log.d("FONT HEIGHT SUM", ""+fmText.ascent + fmText.descent + fmText.leading);


        TOTAL_PAGE = TOTAL_RESULT / NumOfItemPerPage;


        REMAINDER_RESULT = TOTAL_RESULT % NumOfItemPerPage;
        Log.d("ARR TOTAL PAGE", ""+title.size());
        Log.d("ARR REMAINDER", ""+REMAINDER_RESULT );
        Log.d("ARR EACH PAGE", ""+NumOfItemPerPage);

        int position = 0;

        for(int k = 0; k<TOTAL_PAGE; k++){

            int start = k * NumOfItemPerPage;
            Log.d("START", ""+start);

            aPageListItems = new ArrayList<ListItems>();

            for(int i=0 ; i < NumOfItemPerPage ; i++){

                position = start + i;
                aPageListItems.add(new ListItems(title.get(position).toLowerCase(), subjectMatter.get(position).toLowerCase(), body.get(position).toLowerCase(), year.get(position).toLowerCase()));
            }
            allPagesListItem.add(aPageListItems);
        }

        aPageListItems = new ArrayList<ListItems>();
        position = TOTAL_PAGE * NumOfItemPerPage;

        for(int k = 0; k < REMAINDER_RESULT; k++){

            int mPosition = position + k;
            aPageListItems.add(new ListItems(title.get(mPosition).toLowerCase(), subjectMatter.get(mPosition).toLowerCase(), body.get(mPosition).toLowerCase(), year.get(mPosition).toLowerCase()));
        }
            allPagesListItem.add(aPageListItems);

        ArrayList<String> backContents = new ArrayList<String>();
        backContents.add("第一页背面的内容。。。。。。。");
        backContents.add("第二页背面的内容。。。。。。。");
        backContents.add("第三页背面的内容。。。。。。。");
        backContents.add("第四页背面的内容。。。。。。。");

        textPageProvider.setListItems(allPagesListItem);
        textPageProvider.setBackStrings(backContents);



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

        gestureDetector = new GestureDetector(this, new GestureEvent());

        //gestureUp = new GestureDetector(this, new SingleTapUp());


        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {

                    //Toast.makeText(ExampleActivity1.this, "GESture", Toast.LENGTH_SHORT).show();
                    curlView.onTouch(curlView, event);
                }
//
//                else if (gestureUp.onTouchEvent(event)) {
//                    Toast.makeText(ExampleActivity1.this, "Normal", Toast.LENGTH_SHORT).show();
//                    if(!isScrolled){
//                        Log.d("GEST Tap", "No Scroll");
//                    }
//                    else{
//                        Log.d("GEST Tap", "Scroll");
//                    }
//
//                    return curlView.onTouch(curlView, event);
//                }

                return true;
            }

        });
    }

    public int XdpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;
    }

    public int YdpToPx(int dp){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;
    }
    boolean isScrolled = false;

    private class GestureEvent extends GestureDetector.SimpleOnGestureListener {

        String scrollDirection;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, final float distanceX, float distanceY) {
            Log.d("POSITION X _ Y", ""+distanceX +"--"+ distanceY);
            //Log.d("POSITION Y", ""+distanceY);
            Log.d("GEST SCROLL", "SCROLL");
            isScrolled = true;

            if(distanceX < 0){
                scrollDirection = "right";
            }
            else{
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
            onFling( e, e, 40f, 30f);
            Log.d("GEST SHOW", "PRESS");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d("GEST UP", "UP");
            if(isScrolled){
                Log.d("GEST UP", "UP scroll");
            }
            else{
                if(inches < 7.0 && (config.orientation == config.ORIENTATION_PORTRAIT)) {

                    int positionY = Math.round(event.getY());

                    int spotHeight = 210;

                    Log.d("GEST UP", "UP scroll no scroll");
                    Log.d("GEST SPOT Y", event.getY()+"" );
                    Log.d("GEST postion Y", ""+positionY);
                    Log.d("GEST POSITION LINE", spotHeight+"" );

                    int position = positionY / multiLineHeight;

                    if(position <= NumOfItemPerPage)
                        Toast.makeText(ExampleActivity1.this, "Click " + position, Toast.LENGTH_SHORT).show();
                    Log.d("GEST POSITION", position+"" );
                }
                else{

                    int positionY = Math.round(event.getY());
                    int spotHeight = 210;

                    Log.d("GEST UP", "UP scroll no scroll");
                    Log.d("GEST SPOT Y", event.getY()+"" );
                    Log.d("GEST height", multiLineHeight+"");
                    Log.d("GEST POSITION LINE", spotHeight+"" );

                    int position = positionY / multiLineHeight;
                    if(position <= NumOfItemPerPage)
                        Toast.makeText(ExampleActivity1.this, "Click " + position, Toast.LENGTH_SHORT).show();

                    Log.d("GEST POSITION", position+"" );
                }
            }
            return true;
        }
    }



//    private class SingleTapUp extends  GestureDetector.SimpleOnGestureListener{
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent event) {
//
//            return true;
//        }
//    }




}

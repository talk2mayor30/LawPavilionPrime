package git.lawpavilionprime.bookflip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import bf.fc.page.curl.provider.TextPageProvider;
import bf.fc.page.curl.view.CurlPage;

/**
 * Created by don_mayor on 7/17/2015.
 */
public class PageProvider extends TextPageProvider {

    ArrayList<ArrayList<ListItems>> allPagesListItem;

    ArrayList<String> string;
    Context mContext;
    float screenDimensionX;

    private TextView query;

    public static int currentIndex;

    public PageProvider(Context context, int margin, int padding, int border, int borderColor, int background) {

        this(margin, padding, border, borderColor, background);

        string = new ArrayList<String>();
        mContext = context;


    }

    public void setView(TextView queryView) {
        this.query = queryView;
    }


    public PageProvider(int margin, int padding, int border, int borderColor, int background) {

        this.margin = margin;
        this.padding = padding;
        this.border = border;
        this.borderColor = borderColor;
        this.background = background;
    }

    public void setScreenDimensionX(float screenDimensionX) {
        this.screenDimensionX = screenDimensionX;
    }

    public void setListItems(ArrayList<ArrayList<ListItems>> listItems) {

        this.allPagesListItem = listItems;
    }


    public void loadPerPage(ArrayList<ListItems> listItem) {

        String pageContent = "";

        for (int i = 0; i < listItem.size(); i++) {
            String caseTitle = "<font color='#009900'><b>" + listItem.get(i).getCaseTitle().toUpperCase() + "</b></font>";
            String subjectMatter = "<font color='#ee0000'><b>" + listItem.get(i).getSubjectMatter().toUpperCase() + "</b></font><br>";
            String issues = "<font color='#5677fc'><b>" + listItem.get(i).getIssues() + "</b></font><br>";
            String body = "<b>" + listItem.get(i).getBody() + "</b><br>";
            String year = "<font color='#334561'><b>" + listItem.get(i).getYear() + "</b></font><br>";
            pageContent = pageContent + caseTitle + year + subjectMatter +" - "+ issues + body + "<br><br>";
        }
        Log.d("Page Content", pageContent);
        string.add(pageContent);
        setStrings(string);
    }

    @Override
    public void addBack(String backData) {
        super.addBack(backData);
    }

    @Override
    public boolean isEnaleDrawed(int index, boolean isBack) {
        return super.isEnaleDrawed(index, isBack);
    }

    @Override
    public void setBackStrings(ArrayList<String> backStrings) {
        super.setBackStrings(backStrings);
    }

    public PageProvider(ArrayList<String> data) {
        super(data);
    }

    @Override
    public boolean isShouldRotation(boolean isBack) {
        return super.isShouldRotation(isBack);
    }

    @Override
    public void drawBitmap(Canvas c, Rect r, int index, boolean isBack) {

        currentIndex = index;
        String data = getItem(index, isBack);

        if (data == null) {
            Log.d("DATA", "IS NULL");
            return;
        } else {
            Log.d("DATA", "IS NOT NULL");
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        //float density = mContext.getResources().getDisplayMetrics().density;
        textPaint.setTextSize(dpToPx(13f));
        if (isShouldRotation(isBack)) {
            updateCanvasLRSymmetry(c);
        }

        Spanned spanned = Html.fromHtml(data);
        Log.i("--SPANNED--", "" + spanned.toString());
        Log.i("--SPANNED--", "" + spanned.length());

        StaticLayout staticLayout = new StaticLayout(spanned, 0, spanned.length(), textPaint, r.width(), Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);


        c.translate(r.left, r.top);
        staticLayout.draw(c);

        Log.d("LOG STATIC WIDTH", "" + staticLayout.getLineWidth(0));
        Log.d("LOG STATIC HEIGHT", "" + staticLayout.getHeight());
    }

    @Override
    public void updatePage(CurlPage page, int width, int height, int index) {
        super.updatePage(page, width, height, index);
    }

    @Override
    public int getPageCount() {
        return allPagesListItem == null ? 0 : allPagesListItem.size();
    }

    @Override
    public void setStrings(ArrayList<String> strings) {
        super.setStrings(strings);
    }


    @Override
    public String getItem(int index, boolean isBack) {
        return super.getItem(index, isBack);
    }

    @Override
    public void setRotation(boolean isRotation) {
        super.setRotation(isRotation);
    }

    @Override
    public void add(String data) {
        super.add(data);
    }

    public void loadAllPages() {

        for(int k =0; k < allPagesListItem.size(); k++){

            ArrayList<ListItems> aPageListItem = allPagesListItem.get(k);

            for (int i = 0; i < aPageListItem.size(); i++) {

                ListItems singleListitem = aPageListItem.get(i);

                Paint paint = new Paint();
                paint.setTextSize(dpToPx(13f));
                int charNum = 0;

                String caseTitle = singleListitem.getCaseTitle();
                charNum = paint.breakText(caseTitle, true, dpToPx(screenDimensionX - 70), null);
                String updateCaseTitle = caseTitle.substring(0, charNum);
                singleListitem.setCaseTitle(updateCaseTitle + "... ");
                Log.d("LOG TITLE", caseTitle);
                Log.d("LOG TITLE UPDATED", updateCaseTitle);
                float m = paint.measureText(updateCaseTitle);
                Log.d("LOG MEASURE", "" + m);

                String subjectMatter = singleListitem.getSubjectMatter();
                charNum = paint.breakText(subjectMatter, true, dpToPx(screenDimensionX -50), null);
                String updatedSubjectMatter = subjectMatter.substring(0, charNum);
                singleListitem.setSubjectMatter(updatedSubjectMatter + "...");

                Log.d("LOG MATTER", subjectMatter);
                Log.d("LOG MATTER UPDATED", updatedSubjectMatter);

                String body = singleListitem.getBody();
                charNum = paint.breakText(body, true, (dpToPx(screenDimensionX) * 3), null);
                String updatedBody = body.substring(0, charNum);
                singleListitem.setBody(updatedBody+"...");

                Log.d("LOG BODY", body);
                Log.d("LOG BODY UPDATED", updatedBody);
            }
            loadPerPage(aPageListItem);
        }



/*        String year = listitem.getYear();
        charNum = paint.loadAllPages(year, true, rect.width(), null);
        String updateYear = year.substring(0, charNum);
        listitem.setYear(updatedYear);*/

    }

    public float dpToPx(float dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float px = dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);

        return px;
    }

    public float pxTodp(float px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

        float dp = px * (DisplayMetrics.DENSITY_DEFAULT / displayMetrics.xdpi);

        return dp;
    }


}

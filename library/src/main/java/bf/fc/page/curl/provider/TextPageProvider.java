package bf.fc.page.curl.provider;

import android.app.LauncherActivity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import bf.fc.page.curl.view.CurlPage;
import bf.fc.page.curl.view.CurlRenderer;

/**
 * Created by fc on 14-11-21.
 */
public class TextPageProvider extends BasePageProvider {

    private boolean isRotation = true;
    private ArrayList<String> strings;
    private ArrayList<String> backStrings;

    public TextPageProvider(int margin, int padding, int border, int borderColor, int background, ArrayList<String> strings, ArrayList<String> backStrings) {
        super(margin, padding, border, borderColor, background);
        this.strings = strings;
        this.backStrings = backStrings;
    }

    public TextPageProvider(int margin, int padding, int border, int borderColor, int background) {

        this.margin = margin;
        this.padding = padding;
        this.border = border;
        this.borderColor = borderColor;
        this.background = background;
    }

    public void add(String data) {
        strings.add(data);
    }

    public void addBack(String backData) {
        backStrings.add(backData);
    }

    public void setRotation(boolean isRotation) {
        this.isRotation = isRotation;
    }

    public String getItem(final int index, boolean isBack) {
        if (isBack) {
            return (backStrings != null) && (backStrings.size() > index) ? backStrings.get(index) : null;
        } else {
            return (strings != null) && (strings.size() > index) ? strings.get(index) : null;
        }
    }

    @Override
    public boolean isEnaleDrawed(int index, boolean isBack) {
//        if (isBack) {
//            return (backStrings != null) && (backStrings.size() > index) && (backStrings.get(index) != null);
//        } else {
//            return (strings != null) && (strings.size() > index) && (strings.get(index) != null);
//        }
        return super.isEnaleDrawed(index, isBack);
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }


    public void setBackStrings(ArrayList<String> backStrings) {
        this.backStrings = backStrings;
    }

    public TextPageProvider(ArrayList<String> data) {
        this.strings = data;
    }

    public TextPageProvider() {

    }

    public boolean isShouldRotation(boolean isBack) {
        return isRotation && isBack && (viewMode == CurlRenderer.SHOW_TWO_PAGES);
    }

    public void drawBitmap(final Canvas c, Rect r, final int index, boolean isBack) {
        String data = getItem(index, isBack);
        if (data == null) {
            return ;
        }

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        if (isShouldRotation(isBack)) {
            updateCanvasLRSymmetry(c);
        }

        textPaint.setAlpha(255);
        textPaint.setFakeBoldText(true);


        Spanned spanned= Html.fromHtml(data);
        Log.i("--SPANNED--",""+spanned.toString());
        Log.i("--SPANNED--",""+spanned.length());
        StaticLayout staticLayout = new StaticLayout(spanned, 0, spanned.length(), textPaint, r.width(), Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);

        c.translate(r.left, r.top);
        staticLayout.draw(c);
    }

    @Override
    public void updatePage(CurlPage page, int width, int height, int index) {
        super.updatePage(page, width, height, index);
    }

    @Override
    public int getPageCount() {
        return strings == null ? 0 : strings.size();
    }
}

package lara;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;

import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 8/21/2015.
 */
public class ConferencePapers extends ActionBarActivity {

    ListView ltConfPapers;
    ConfPaperAdapter  mConfPaperAdapter;
    ConfPaperItem mConfPaper;
    ArrayList<ConfPaperItem> mConfPapers;

    String text = "Issues in Nigeria law system: A major set back to the growth of thc country";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conference_papers);
        ltConfPapers = (ListView) findViewById(R.id.confMaterial);

        for(int i =0; i<10; i++){
            mConfPaper = new ConfPaperItem("Mr Ilori Seyi", text, "http:www.lawpavilion.com");
            mConfPapers.add(mConfPaper);
        }

        mConfPaperAdapter = new ConfPaperAdapter(this, mConfPapers);
        ltConfPapers.setAdapter(mConfPaperAdapter);
    }
}

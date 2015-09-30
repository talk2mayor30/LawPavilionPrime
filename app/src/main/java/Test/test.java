package Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import git.lawpavilionprime.R;

public class test extends Activity {

    TextView text;
    int i = 0;

    int saved;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        text = (TextView) findViewById(R.id.test);
        Log.d("---CHECK", "CHECK");
        i = getIntent().getIntExtra("EXTRA", 0);

        saved = i;

        if(savedInstanceState != null){
            text.setText("Hello world: "+savedInstanceState.getInt("SAVED"));
        }
        else{
            text.setText("Hello world: "+i);
        }

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = i + 1;
                Intent intent = new Intent(test.this, test.class);
                intent.putExtra("EXTRA",i);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.d("SAVE", "TO_SAVE");
//        outState.putInt("SAVED", saved);
//    }
}

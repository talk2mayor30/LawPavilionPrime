package notused;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.PersistedTaskRunnable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.DBQuery;
import git.lawpavilionprime.R;
import git.lawpavilionprime.SetUp.ModuleAdapter;
import git.lawpavilionprime.SetUp.ModuleDBAdapter;
import git.lawpavilionprime.SetUp.ModuleDownload;


public class Module extends ActionBarActivity{

    public static  ArrayList<String> mTitle, mDate, mSize ,mStatus, mURL, mName;
    public static  ArrayList<String>  mID;
    public static  ArrayList<Boolean> mShowButton,mCompleted;

    private ProgressBar progressBar;

    private ListView listView;
    private static Config config;

    int TOTAL_ITEM = 30;
    ModuleAdapter moduleAdapter;

    //Json string to test with
    static String jsonString = "{ \"response\": [";

    //Json keys
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String SIZE = "size";
    public static final String DATE = "date";
    public static final String URL = "url";
    public static final String ID = "id";
    public static final String STATUS = "status";

    private static final String MODULE_URL = "";
    private static final String DB_NAME = "falcon";

    private Button btnDownload;
    private TextView txtSize;
    private TextView txtTitle;
    private ProgressDialog dialog;
    private static LinearLayout mProgressBar;
    private static TextView txtProgressMessage;
    private static Context mContext;
    private static AlertDialog alert;
    private ModuleDBAdapter dbAdapter;

    private static ArrayList<String> availableModules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.module);

        config = new Config(this);
        mContext = Module.this;

        mTitle = new ArrayList<String>();
        mDate = new ArrayList<String>();
        mStatus = new ArrayList<String>();
        mShowButton = new ArrayList<Boolean>();
        mURL = new ArrayList<String>();
        mCompleted = new ArrayList<Boolean>();
        mID = new ArrayList<String>();
        mName = new ArrayList<String>();
        mSize = new ArrayList<String>();

        //listView = (ListView) findViewById(R.id.ntfView);
        btnDownload = (Button) findViewById(R.id.startDownload);
        txtSize = (TextView) findViewById(R.id.size);
        txtTitle = (TextView) findViewById(R.id.title);

        mProgressBar = (LinearLayout) findViewById(R.id.progressMum);
        txtProgressMessage = (TextView) findViewById(R.id.progressMessage);

        availableModules = new ArrayList<String>();

        dbAdapter = new ModuleDBAdapter(Module.this);

        dbAdapter.open(DB_NAME);
        dbAdapter.createModuleTable();

        Cursor cursor = dbAdapter.fetch("SELECT " + ModuleDBAdapter.MODULE_ID+ ", "+ModuleDBAdapter.NAME
                                                + " FROM " + ModuleDBAdapter.TABLE_NAME );

        if(cursor != null && cursor.getCount() > 0){
            while(!cursor.isAfterLast()){
                availableModules.add(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.MODULE_ID)));
                cursor.moveToNext();
            }
        }
        availableModules.add("43");

       //TODO: remove this
        for(int i=0; i<TOTAL_ITEM; i++){


            if(i!=(TOTAL_ITEM - 1)){
                jsonString += "{\"title\":\"law pavilion\", \"name\":"+ DBQuery.CUSTOMER +", \"date\":\"2015-01-09\",\"id\":\"43\", \"size\":\"25mb\", " +
                        "\"url\":\"http://crm.lawpavilionplus.com/yinkaTestAndroid/customer.zip\"},";

            }
            else{
                jsonString += "{\"title\":\"law pavilion\", \"name\":"+ DBQuery.CUSTOMER +", \"date\":\"2015-01-09\",\"id\":\"41\", \"size\":\"25mb\", " +
                        "\"url\":\"http://crm.lawpavilionplus.com/yinkaTestAndroid/customer.zip\"}";
            }
        }
        jsonString+="]}";
        //End TODO

        if(config.isConnectingToInternet()) {
            //TODO: replace with URL
            txtProgressMessage.setText("Loading...");
            mProgressBar.setVisibility(View.VISIBLE);
            AsyncManager.runBackgroundTask(new _FetchModulesToDownload());
        }

        else {
            showDialog();
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start process


            }
        });
    }

    public static void showDialog(){
        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Error");
        builder.setMessage("Oops Something went wrong\n\nPlease check your internet connection")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(config.isConnectingToInternet()) {
                    //TODO: replace with URL
                    txtProgressMessage.setText("Loading...");
                    mProgressBar.setVisibility(View.VISIBLE);
                    AsyncManager.runBackgroundTask(new _FetchModulesToDownload());
                }
                else {
                    mProgressBar.setVisibility(View.GONE);
                    showDialog();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

   public void mDownload(View v){

      int mPosition= listView.getPositionForView(v);
       progressBar = (ProgressBar) findViewById(mPosition);

       if(config.isConnectingToInternet()) {

           mShowButton.set(mPosition,false) ;
           moduleAdapter.notifyDataSetChanged();
           ModuleDownload download = new ModuleDownload(Module.this, progressBar);
           download.startDownload(mCompleted, mShowButton, moduleAdapter, mPosition);

       }

       else{
           AlertDialog.Builder  builder = new AlertDialog.Builder(this);
           builder.setTitle("Error");
           builder.setMessage("Please check your internet connection")
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Module.this.finish();
                       }
                   });
           alert = builder.create();
           dialog.show();
       }
   }

    @Override
    public void onBackPressed() {

        AsyncManager.cancelAllTasks();

        if(alert !=null && alert.isShowing()) {
            alert.dismiss();
        }
        Module.this.finish();
    }

    private static class _FetchModulesToDownload extends PersistedTaskRunnable<Void, String, Void>{

        @Override
        public String doLongOperation(Void aVoid) throws InterruptedException {

            String jsonResult = null;
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;
                HttpGet httpGet = new HttpGet("http://validate.jsontest.com/?json=[JSON-code-to-validate]");
                try {
                    httpResponse = httpClient.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpEntity = httpResponse.getEntity();
                jsonResult = EntityUtils.toString(httpEntity);

                Log.d("-------------JSON", jsonResult);

                return jsonResult;

            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        public void callback(String res) {
            super.callback(res);
            mProgressBar.setVisibility(View.GONE);

            if(res == null){
                showDialog();
            }
            else {
                try {
                    //TODO: replace jsonString with res
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    Log.d("----JSON_ARRAY",jsonArray.toString());

                    boolean isGotten ;
                    for (int i=0;i<jsonArray.length();i++) {

                        isGotten = false;
                        for (int j = 0; j < availableModules.size(); j++) {
                            if (availableModules.get(j).equalsIgnoreCase(jsonArray.getJSONObject(i).getString(ID))) {
                                isGotten = true;
                            }
                        }
                        if (!isGotten) {
                            Log.d("AVAILABLE", "NOT AVAILABLE");
                            mTitle.add(String.valueOf(jsonArray.getJSONObject(i).get(TITLE)));
                            mDate.add(String.valueOf(jsonArray.getJSONObject(i).get(DATE)));
                            mStatus.add(STATUS);
                            mSize.add(String.valueOf(jsonArray.getJSONObject(i).get(SIZE)));
                            mShowButton.add(true);
                            mURL.add(String.valueOf(jsonArray.getJSONObject(i).get(URL)));
                            mCompleted.add(false);
                            mID.add(jsonArray.getJSONObject(i).getString(ID));
                            mName.add(String.valueOf(jsonArray.getJSONObject(i).get(NAME)));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

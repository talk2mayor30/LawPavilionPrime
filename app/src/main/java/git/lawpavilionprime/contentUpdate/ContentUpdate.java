package git.lawpavilionprime.contentUpdate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.DBQuery;
import git.lawpavilionprime.R;

/**
 * Created by don_mayor on 7/9/2015.
 */
public class ContentUpdate extends ActionBarActivity {

    private static final String TAG_RESPONSE_TITLE = "title";
    private static final String TAG_RESPONSE_SIZE = "size";
    private static final String TAG_RESPONSE_DATE = "date";
    private static final String TAG_RESPONSE_STATUS = "status";
    private static final String TAG_RESPONSE_NAME = "name";
    private static final String TAG_RESPONSE_URL = "url";

    private ListView listUpdate;

    public  static ArrayList<String>   mTitle, mSize, mDate, mStatus, mName, mUrl;
    public  static ArrayList<Integer>  mID;
    public  static ArrayList<Boolean> mShowButton,mCompleted;

    private String mResponse;

    private static int UPDATE_COUNT;
    private ContentUpdateAdapter syncAdapter;
    private DatabaseAdapter dBAdapter;

    // To populate a sample DB
    private static String extractedFile = "/com.falcon/unzipped/customer1.txt";

    // To assign base Url
    private static String baseUrl = "";


    //Temp Arraylist to hold table name
    ArrayList<String> tableName;

    ProgressBar progressBar;
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentupdate);

        mTitle = new ArrayList<String>();
        mSize = new ArrayList<String>();
        mDate = new ArrayList<String>();
        mStatus = new ArrayList<String>();
        mName = new ArrayList<String>();
        mShowButton = new ArrayList<Boolean>();
        mCompleted = new ArrayList<Boolean>();
        mID = new ArrayList<Integer>();
        mUrl = new ArrayList<String>();

        listUpdate = (ListView) findViewById(R.id.syncView);

        tableName = new ArrayList<String>();
        config = new Config(this);

        UPDATE_COUNT = 20;


        //TODO: this should be removed
        dBAdapter = new DatabaseAdapter(this);
        dBAdapter.open(DatabaseAdapter.DB_NAME);
        ReadnInsertFromFile();
        dBAdapter.close();
        //end

        for(int i =0; i < 3; i++){
            tableName.add("Customer");
        }
    }

    public void getAvailableUpdate(View v){
        if(config.isConnectingToInternet()) {
            new findToUpdate().execute();
        }
        else{
            AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Please check your internet connection")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private class findToUpdate extends AsyncTask<Void, Void, String>{

        JSONObject jsonObject;
        JSONArray jsonArray;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ContentUpdate.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Loading...");
            dialog.show();

            jsonObject = new JSONObject();
            jsonArray = new JSONArray();

        }

        @Override
        protected String doInBackground(Void... params) {

            String token= DatabaseAdapter.TOKEN;

            for(int i =0; i<tableName.size(); i++){

               FetchLastModifeidFromTable(tableName.get(i));
            }
            try {

                jsonObject.put("parameter", jsonArray );

            } catch (JSONException e) {

                e.printStackTrace();
            }
            Log.d("---JsonFRMCursor", jsonObject.toString());

            String jsonParameter = jsonObject.toString();

            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity;
                HttpResponse httpResponse = null;

                String requestUrl = baseUrl + "?token = " + token + "&json=" +jsonParameter;

                // replace with requestUrl
                HttpGet httpGet = new HttpGet("http://validate.jsontest.com/?json=[JSON-code-to-validate]");

                httpResponse = httpClient.execute(httpGet);

                httpEntity = httpResponse.getEntity();
                String jsonResult = EntityUtils.toString(httpEntity);

                Log.d("-------------JSONTEST", jsonResult);

                return jsonResult;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            dialog.dismiss();

            mTitle = new ArrayList<String>();
            mDate = new ArrayList<String>();
            mStatus = new ArrayList<String>();
            mShowButton = new ArrayList<Boolean>();
            mUrl = new ArrayList<String>();
            mCompleted = new ArrayList<Boolean>();
            mID = new ArrayList<Integer>();
            mName = new ArrayList<String>();

            //TODO: remove dummy data
            for (int i = 0; i < 10; i++) {

                mTitle.add("Supreme Court Download");
                mSize.add("10mb");
                mDate.add("2015-01-09");
                mStatus.add("Pending");
                mName.add(DBQuery.CUSTOMER);
                mShowButton.add(true);
                mCompleted.add(false);
                mID.add(i);
                mUrl.add("http://validate.jsontest.com/?json=[JSON-code-to-validate]");
            }

            syncAdapter = new ContentUpdateAdapter( ContentUpdate.this, mTitle, mDate, mStatus, mSize, mShowButton, mCompleted, mID);
            listUpdate.setAdapter(syncAdapter);
            //TODO: ends here

            //TODO: uncomment this
//            syncAdapter = new SyncAdapter( SyncData.this, mTitle, mDate, mStatus, mSize, mShowButton, mCompleted, mID);
//            listUpdate.setAdapter(syncAdapter);
//
//            if(res != null) {
//                try {
//                    JSONObject jsonResponse = new JSONObject(res);
//
//                    JSONArray responseArray = jsonResponse.getJSONArray("result");
//
//                    for (int i = 0; i < responseArray.length(); i++) {
//
//                        mTitle.add(responseArray.getJSONObject(i).getString(SyncData.TAG_RESPONSE_TITLE));
//                        mSize.add(responseArray.getJSONObject(i).getString(SyncData.TAG_RESPONSE_SIZE));
//                        mDate.add(responseArray.getJSONObject(i).getString(SyncData.TAG_RESPONSE_DATE));
//                        mStatus.add(responseArray.getJSONObject(i).getString(SyncData.TAG_RESPONSE_STATUS));
//                        mName.add(responseArray.getJSONObject(i).getString(SyncData.TAG_RESPONSE_NAME));
//                        mShowButton.add(true);
//                        mCompleted.add(false);
//                        mID.add(i);
//                        mUrl.add(SyncData.TAG_RESPONSE_URL);
//                    }
//
//                    syncAdapter = new SyncAdapter( SyncData.this, mTitle, mDate, mStatus, mSize, mShowButton, mCompleted, mID);
//                    listUpdate.setAdapter(syncAdapter);
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            else{
//                AlertDialog.Builder  builder = new AlertDialog.Builder(SyncData.this);
//                builder.setTitle("Error");
//                builder.setMessage("\nConnection failed")
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // What happens here?
//                            }
//                        });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
            //ends
        }
        public void FetchLastModifeidFromTable(String tableName){

            dBAdapter = new DatabaseAdapter(ContentUpdate.this);

            dBAdapter.open(DatabaseAdapter.DB_NAME);

            Cursor cursor =  dBAdapter.fetch("select SupportRepId from " + tableName + " order by SupportRepId DESC limit 1");

            try {
                jsonArray.put(new JSONObject().put(tableName, cursor.getString(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dBAdapter.close();
        }
    }

    public void startDownload(View v){

        int mPosition = listUpdate.getPositionForView(v);
        progressBar = (ProgressBar) findViewById(mPosition);

        if(config.isConnectingToInternet()) {
            mShowButton.set(mPosition,false) ;
            syncAdapter.notifyDataSetChanged();
            ContentDownload download = new ContentDownload(this, ContentUpdate.this, progressBar);
            download.startDownload(mCompleted, mShowButton, syncAdapter,dBAdapter, mPosition);
            Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Please check your internet connection")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Sample method to populate the DB
    public void ReadnInsertFromFile(){

        dBAdapter.createTable(DatabaseAdapter.CREATE_CUSTOMER_TABLE);

        File dbfile = new File(Environment.getExternalStorageDirectory() + extractedFile);

        try {

            InputStream insertsStream = new FileInputStream(dbfile);
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

            while (insertReader.ready()) {
                String insertStmt = insertReader.readLine();

                Log.d("---INSERT", insertStmt);
                dBAdapter.insert(insertStmt);

            }
            insertReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int expectedCursorCount = 59;

        //Or use LPDatabase to do this
        Cursor cursor = dBAdapter.fetch("select " + "SupportRepId" + " , "+ DatabaseAdapter.FIRST_NAME+ " from Customer order by SupportRepId DESC limit 10");

        //convertCursorToJsonArray(cursor);

        if (cursor.getCount() == expectedCursorCount) {
            Log.d("---Count", "works fine");

        } else {

        }
    }



}

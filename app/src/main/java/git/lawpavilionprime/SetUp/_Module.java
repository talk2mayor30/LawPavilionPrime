package git.lawpavilionprime.SetUp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
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

import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;


public class _Module extends ActionBarActivity{

    private static Config config;

    //Json string to test with
    static String jsonString = "{ \"response\": [";

    //Json keys
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String SIZE = "size";
    private static final String DATE = "date";
    private static final String URL = "url";
    private static final String ID = "id";
    private static final String STATUS = "status";
    private static final String MODULE_URL = "";
    public static final String DB_NAME = "falcon";

    private static Button btnStartDownload;
    private static ProgressDialog dialog;
    private static LinearLayout mProgressBar;
    private static TextView txtProgressMessage;
    private static Context mContext;
    private static AlertDialog alert;

    //private static Button btnPauseDownload;
    private ModuleDBAdapter dbAdapter;
    private static ArrayList<ModuleItem> downloadedModules;
    private static ArrayList<ModuleItem> modulesToDownload;
    private static ArrayList<ModuleItem> totalModules;


    private static CircleProgressView perModuleProgressBar;
    private static RoundCornerProgressBar allModulesProgress;

    private static LinearLayout mAlertLayout;
    private TextView mAlertTitle;
    private Button mAlertRetry;
    private TextView mAlertMessage;

    private TextView txtCurrentDownload;
    private TextView txtTotalDownload;
    private TextView txtFileDownloadCount;

    private TextView txtRefreshDownload;
    private  RelativeLayout startDownLoadLayout;
    private  LinearLayout progressDownloadLayout;

    private BroadcastReceiver receiver;
    private NewDownloadManager thinDownloadManager;
    private DownloadManager downloadManager;

    private Button btnDownloadState;


    public static boolean isDownloading = false;
    public static final String  BROADCAST_FILTER = "filter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.module);

        config = new Config(this);
        mContext = _Module.this;

        modulesToDownload = new ArrayList<ModuleItem>();
        downloadedModules = new ArrayList<ModuleItem>();
        totalModules = new ArrayList<ModuleItem>();

        btnStartDownload = (Button) findViewById(R.id.startDownload);

        mProgressBar = (LinearLayout) findViewById(R.id.progressMum);
        txtProgressMessage = (TextView) findViewById(R.id.progressMessage);

        mAlertLayout = (LinearLayout) findViewById(R.id.motherAlert);
        mAlertMessage = (TextView) findViewById(R.id.alertMessage);
        mAlertRetry = (Button) findViewById(R.id.alertRetry);
        mAlertTitle = (TextView) findViewById(R.id.alertTitle);

        txtCurrentDownload = (TextView) findViewById(R.id.currentDownload);
        txtTotalDownload = (TextView) findViewById(R.id.totalDownload);
        txtFileDownloadCount = (TextView) findViewById(R.id.dwnCount);


        startDownLoadLayout = (RelativeLayout) findViewById(R.id.startdownloadLayout);
        progressDownloadLayout = (LinearLayout) findViewById(R.id.downloadProgressLayout);
        txtRefreshDownload = (TextView) findViewById(R.id.refreshDownload);
        btnDownloadState = (Button) findViewById(R.id.btnDwnState);

        perModuleProgressBar = (CircleProgressView) findViewById(R.id.circleView);

        perModuleProgressBar.setMaxValue(100);
        perModuleProgressBar.setRimColor(Color.parseColor("#FFDFE6EB"));
        perModuleProgressBar.setBarColor(Color.parseColor("#FF5EB7DD"));
        perModuleProgressBar.setTextColor(Color.parseColor("#FF6F1600"));
        perModuleProgressBar.setValue(0);

        allModulesProgress = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        allModulesProgress.setMax(100);
        allModulesProgress.setProgress(0);


        dbAdapter = new ModuleDBAdapter(_Module.this);

        dbAdapter.open(DB_NAME);

        //TODO: remove this
        dbAdapter.executeQuery("DROP TABLE IF EXISTS "+ModuleDBAdapter.TABLE_NAME);
        dbAdapter.createModuleTable();
        Cursor cursor = dbAdapter.fetch("SELECT * FROM " + ModuleDBAdapter.TABLE_NAME );

        ModuleItem downloadedModule;
        if(cursor != null && cursor.getCount() > 0){
            while(!cursor.isAfterLast()){
                downloadedModule = new ModuleItem();

                downloadedModule.setID(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.MODULE_ID)));
                downloadedModule.setUrl(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.URL)));
                downloadedModule.setName(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.NAME)));
                downloadedModule.setStatus(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.STATUS)));
                downloadedModule.setSize(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.SIZE)));
                downloadedModule.setDate(cursor.getString(cursor.getColumnIndex(ModuleDBAdapter.DATE)));

                downloadedModules.add(downloadedModule);
                cursor.moveToNext();
            }
        }

        //TODO: remove this
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "toyota" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"},";
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "suv" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"},";
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "benz" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"},";
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "kia" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"},";
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "mazda" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"},";
        jsonString += "{\"title\":\"law pavilion\", \"name\":"+ "ford" +", \"date\":\"2015-01-09\",\"id\":\"40\", \"size\":\"25mb\", " +
                "\"url\":\"https://www.adobe.com/enterprise/accessibility/pdfs/acro6_pg_ue.pdf\"}";

        jsonString+="]}";

        if(config.isConnectingToInternet()) {
            //TODO: replace with URL
            txtProgressMessage.setText("Loading...");
            mProgressBar.setVisibility(View.VISIBLE);
            AsyncManager.runBackgroundTask(new _FetchModulesToDownload());
        }
        else {
            mAlertLayout.setVisibility(View.VISIBLE);
        }

        mAlertLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAlertRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAlertLayout.setVisibility(View.GONE);

                if(config.isConnectingToInternet()) {
                    //TODO: replace with URL
                    txtProgressMessage.setText("Loading...");
                    mProgressBar.setVisibility(View.VISIBLE);
                    AsyncManager.runBackgroundTask(new _FetchModulesToDownload());
                }
                else {
                    mAlertLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        btnStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStart();
            }
        });

        startDownLoadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStart();
            }
        });

        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
//
    @Override
    public void onResume() {
        super.onResume();
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if(intent.getStringExtra(BROADCAST_FILTER).equalsIgnoreCase("finish")) {
//                    thinDownloadManager.isDestroyed = true;
//                    _Module.this.finish();
//                }
//                else if(intent.getStringExtra(BROADCAST_FILTER).equalsIgnoreCase("retry")){
//                    _Module.this.finish();
//                    Intent moduleIntent = new Intent(_Module.this, _Module.class);
//                    startActivity(moduleIntent);
//                }
//            }
//        };
//        Log.d(DownloadManager.TAG, "Resumed");
//        _Module.this.registerReceiver(receiver,new IntentFilter("com.me.you"));

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DownloadManager.TAG, "PAUSE STATE");
//        _Module.this.unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

//        if(isDownloading){
//
//            AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
//            builder.setTitle("Info");
//            builder.setMessage("Do you want to continue with your download?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //TODO: iniliaze progress indicator
//                            Intent intent = new Intent(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_HOME);
//                            startActivity(intent);
//
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                                thinDownloadManager.downloadManager.cancelAll();
//                                Intent intent = new Intent("com.me.you");
//                                intent.putExtra(BROADCAST_FILTER, "finish");
//                                mContext.sendBroadcast(intent);
//
//                        }
//                    });
//            alert  = builder.create();
//            alert.show();
//
//        }
//        else{
//            AsyncManager.cancelAllTasks();
//            mProgressBar.setVisibility(View.GONE);
//            super.onBackPressed();
//        }
        if(isDownloading){
              Intent intent = new Intent(Intent.ACTION_MAIN);
              intent.addCategory(Intent.CATEGORY_HOME);
              startActivity(intent);
        }
        else{
            AsyncManager.cancelAllTasks();
            mProgressBar.setVisibility(View.GONE);
            super.onBackPressed();
        }
    }

    public void onClickStart(){
        //TODO: begin download process
//        thinDownloadManager = new NewDownloadManager(_Module.this, modulesToDownload, downloadedModules, totalModules);
//        thinDownloadManager.attachStatusWidget(perModuleProgressBar, allModulesProgress);
//        thinDownloadManager.attachLayouts(startDownLoadLayout, progressDownloadLayout);
//        thinDownloadManager.attachViews(txtCurrentDownload, txtTotalDownload, txtFileDownloadCount, txtRefreshDownload, btnDownloadState);
//        isDownloading = true;
//        thinDownloadManager.BeginDownload();

        btnDownloadState.setText("PAUSE");

        downloadManager = new DownloadManager(_Module.this, modulesToDownload, downloadedModules, totalModules);
        downloadManager.attachStatusWidget(perModuleProgressBar, allModulesProgress);
        downloadManager.attachLayouts(startDownLoadLayout, progressDownloadLayout);
        downloadManager.attachViews(txtCurrentDownload, txtTotalDownload, txtFileDownloadCount, txtRefreshDownload, btnDownloadState);
        isDownloading = true;
        downloadManager.BeginDownload();

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

                httpResponse = httpClient.execute(httpGet);

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

            modulesToDownload = new ArrayList<ModuleItem>();
            totalModules = new ArrayList<ModuleItem>();

            if(res == null){
                mAlertLayout.setVisibility(View.VISIBLE);
            }
            else {
                try {
                    //TODO: replace jsonString with res
                    btnStartDownload.setEnabled(true);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    Log.d("----JSON_ARRAY",jsonArray.toString());

                    boolean isGotten ;
                    ModuleItem aModule;

                    for(int i = 0; i <jsonArray.length(); i++){
                        aModule = new ModuleItem();
                        aModule.setID(jsonArray.getJSONObject(i).getString(ID));
                        aModule.setTitle(jsonArray.getJSONObject(i).getString(TITLE));
                        aModule.setSize(jsonArray.getJSONObject(i).getString(SIZE));
                        aModule.setName(jsonArray.getJSONObject(i).getString(NAME));
                        aModule.setUrl(jsonArray.getJSONObject(i).getString(URL));

                        totalModules.add(aModule);
                    }
                    ModuleItem moduleToDownload;

                    for (int i=0;i<totalModules.size(); i++) {
                        isGotten = false;
                        for (int j = 0; j < downloadedModules.size(); j++) {

                            //use name for the check instead of id...//To remove ID
                            if (downloadedModules.get(j).getID().equalsIgnoreCase(totalModules.get(i).getID())) {
                                isGotten = true;
                            }
                        }
                        if (!isGotten) {


                            moduleToDownload = new ModuleItem();
                            moduleToDownload.setTitle(totalModules.get(i).getTitle());
                            moduleToDownload.setDate(totalModules.get(i).getDate());
                            moduleToDownload.setName(totalModules.get(i).getName());
                            moduleToDownload.setSize(totalModules.get(i).getSize());
                            moduleToDownload.setStatus(totalModules.get(i).getStatus());
                            moduleToDownload.setUrl(totalModules.get(i).getUrl());
                            moduleToDownload.setID(totalModules.get(i).getID());

                            modulesToDownload.add(moduleToDownload);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


package git.lawpavilionprime.SetUp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.grabner.circleprogress.CircleProgressView;
import git.lawpavilionprime.Config;
import git.lawpavilionprime.DBQuery;
import git.lawpavilionprime.Dashboard.Dashboard;

/**
 * Created by don_mayor on 8/6/2015.
 */
public class NewDownloadManager implements DownloadStatusListener{

    private DBQuery dbQuery;
    private Context mContext;

    private ArrayList<ModuleItem> mModulesToDownload;
    private ArrayList<ModuleItem> mModulesDownloaded;
    private ArrayList<ModuleItem> mTotalModules;

    private Config config;

    private AlertDialog mAlert;
    private CircleProgressView mPerModuleProgressBar;
    private RoundCornerProgressBar mAllModulesProgressBar;

    public static final String TAG = "------DEBUG----";

    private int DOWNLOADED_COUNT;
    private int TOTAL_COUNT;
    private int TO_DOWNLOAD_COUNT;

    private RelativeLayout startLayout;
    private LinearLayout progressLayout;
    private TextView txtRefresh;

    private TextView txtCurrentDownload;
    private TextView txtTotalDownload;
    private TextView txtFileDownloadProgress;
    private Button btnDwnState;

    private int taskIterator;

    private String zippedDIR = "/falcon/zipped";
    private String unzippedDIR = "/falcon/unzipped";
    private String downloadedZipFile;
    private String extractedFile;

    public ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    public boolean isDestroyed = false;

    public int currentDownloadID;

    public NewDownloadManager(Context context, ArrayList<ModuleItem> modulesToDownload,
                              ArrayList<ModuleItem> modulesDownloaded, ArrayList<ModuleItem> totalModules){

        mContext = context;
        dbQuery = new DBQuery();
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        config = new Config(mContext);

        mModulesToDownload = modulesToDownload;
        mTotalModules = totalModules;
        mModulesDownloaded = modulesDownloaded;

        DOWNLOADED_COUNT = mModulesDownloaded.size();
        TOTAL_COUNT = mTotalModules.size();
        TO_DOWNLOAD_COUNT = mModulesToDownload.size();

        createDirectory();
    }

    public void attachStatusWidget( CircleProgressView perModuleProgressBar, RoundCornerProgressBar allModulesprogressBar){

        mPerModuleProgressBar = perModuleProgressBar;
        mAllModulesProgressBar = allModulesprogressBar;
    }

    public void attachLayouts( RelativeLayout startLayout, LinearLayout progressLayout){

        this.startLayout = startLayout;
        this.progressLayout = progressLayout;
    }

    public void attachViews(TextView txtCurrentDownload, TextView txtTotalDownload, TextView txtFileDownloadProgress, TextView txtRefresh, Button btnDwnState){

        this.txtRefresh = txtRefresh;
        this.txtCurrentDownload = txtCurrentDownload;
        this.txtFileDownloadProgress = txtFileDownloadProgress;
        this.txtTotalDownload = txtTotalDownload;
        this.btnDwnState = btnDwnState;
    }


    public void initProgress(){

        float progress = Math.round(getTotalProgress(DOWNLOADED_COUNT, TOTAL_COUNT));

        mAllModulesProgressBar.setProgress(progress);
        txtTotalDownload.setText(String.valueOf(progress)+"%");
        txtFileDownloadProgress.setText(DOWNLOADED_COUNT + " files downloaded out of " + TOTAL_COUNT);

    }


    public void BeginDownload(){

        Log.d(TAG, "BEGIN");
        initProgress();

        if(!config.isConnectingToInternet()) {
            showErrorDialog("Error", "Oops!, Something went wrong\n\nPlease check your internet connection");
        }
        else if(!enoughMemory()){
            showErrorDialog("Error", "Oops!, Something went wrong\n\nPlease free up more than 1G memory space");
        }
        else{
            startLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            txtRefresh.setVisibility(View.VISIBLE);

            startDownload();
        }
    }


    public float getTotalProgress(float downloaded, float total){

        float  progress = (downloaded / total) * 100;

        progress = Math.round(progress);

        return progress;
    }

    public void updateProgressPerModule(double progress){
        float mProgress = new Float(progress);
        mPerModuleProgressBar.setValue(mProgress);
    }

    public void updateTotalProgress(){

        ModuleDBAdapter db = new ModuleDBAdapter(mContext);
        db.open(_Module.DB_NAME);

        Cursor cursor = db.fetch("SELECT * FROM "+ModuleDBAdapter.TABLE_NAME);

        DOWNLOADED_COUNT = cursor.getCount();



        //TODO: replace with DOWNLOADED COUNT and TOTAL COUNT
        float progress = getTotalProgress(DOWNLOADED_COUNT, TOTAL_COUNT);


        mAllModulesProgressBar.setProgress(progress);
        txtTotalDownload.setText(String.valueOf(progress)+"%");
        txtFileDownloadProgress.setText(DOWNLOADED_COUNT + " files downloaded out of " + TOTAL_COUNT);

        if(cursor.getCount() >= TOTAL_COUNT){
            //Set up complete
            //Move to the next activity
            Log.d("COUNT", "CURSOR: "+cursor.getCount());
        }
        else{
            //

            //set up incomplete
        }
        cursor.close();
    }


    public void showErrorDialog(String Title, String Message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
        builder.setTitle(Title);
        builder.setMessage(Message)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: iniliaze progress indicator
                        //Make progress indicator visible
                        BeginDownload();
                    }
                });
        mAlert  = builder.create();
        mAlert.show();
    }

    public void startDownload(){

        if(taskIterator < mModulesToDownload.size()) {
            try {
                Toast.makeText(mContext, "New Task", Toast.LENGTH_SHORT).show();
                ModuleItem moduleItem = mModulesToDownload.get(taskIterator);
                //TODO: change the ext to .zip for zip dir
                downloadedZipFile = "/falcon/zipped/"+moduleItem.getName().toLowerCase()+".pdf";
                extractedFile = "/falcon/unzipped/"+moduleItem.getName().toLowerCase()+".txt";

                RetryPolicy retryPolicy = new DefaultRetryPolicy(10000, 100, 2f);

                Uri downloadUri = Uri.parse(moduleItem.getUrl());
                Uri destinationUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + downloadedZipFile);
                DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setDestinationURI(destinationUri)
                        .setRetryPolicy(retryPolicy)
                        .setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(this);

                currentDownloadID = downloadManager.add(downloadRequest);
                txtCurrentDownload.setVisibility(View.VISIBLE);
                txtCurrentDownload.setText("Downloading " + moduleItem.getName() + "..."+ " please wait");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else{

            _Module.isDownloading = false;
            Toast.makeText(mContext, "Task completed", Toast.LENGTH_SHORT).show();
            //Normal method is to finish the activity, go to login, to load dash board..
            Intent i = new Intent(mContext, Dashboard.class);
            mContext.startActivity(i);
        }
    }

    public void createDirectory() {

        File zippedFolder = new File(Environment.getExternalStorageDirectory().toString() + zippedDIR);
        File unzippedFolder = new File(Environment.getExternalStorageDirectory().toString() + unzippedDIR);
        zippedFolder.mkdirs();
        unzippedFolder.mkdirs();

        if (!new File(Environment.getExternalStorageDirectory().toString() + zippedDIR).isDirectory()) {
            Log.e(TAG, " Zipped Folder was not created");
        }
        if (!new File(Environment.getExternalStorageDirectory().toString() + unzippedDIR).isDirectory()) {
            Log.e(TAG, " Unzipped Folder was not created");
        }
    }


    private void deleteFiles() {

        File aFile = new File(Environment.getExternalStorageDirectory() + downloadedZipFile);
        File bFile = new File(Environment.getExternalStorageDirectory() + extractedFile);

        boolean isA = aFile.delete();
        boolean isB = bFile.delete();

        if (isA) {
            Log.i(TAG, " downloaded ZipFile deleted!");
        }
        if (isB) {
            Log.i(TAG, " extracted File deleted!");
        }

    }

    private boolean enoughMemory() {

        long deviceFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //return (deviceFreeSpace > 1000000000);
        return (deviceFreeSpace > 1000000);

    }

    @Override
    public void onDownloadFailed(int id, int i2, String message) {

        _Module.isDownloading = false;

        if(isDestroyed){

        }
        else {

            mPerModuleProgressBar.setValue(0f);
            txtCurrentDownload.setText(message);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Connection lost");
            builder.setMessage("You have downloaded " + DOWNLOADED_COUNT + " files out of " + TOTAL_COUNT + "\n\n" + "Click retry to continue with download")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadManager.cancelAll();

                            Intent intent = new Intent("com.me.you");
                            intent.putExtra(_Module.BROADCAST_FILTER, "retry");
                            mContext.sendBroadcast(intent);

                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            downloadManager.cancelAll();
                            Intent intent = new Intent("com.me.you");
                            intent.putExtra(_Module.BROADCAST_FILTER, "finish");
                            mContext.sendBroadcast(intent);

                        }
                    });

            if (mAlert == null || !mAlert.isShowing()) {
                mAlert = builder.create();
                mAlert.show();
            }
        }
    }

    @Override
    public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

        updateProgressPerModule(progress);
    }


    @Override
    public void onDownloadComplete(int i) {
        Log.d(TAG, "Completed");
        ModuleItem report = mModulesToDownload.get(taskIterator);

        new AsyncDBExec().execute(report);
    }


    public void unZipnExecute(ModuleItem taskReport){

        String dump, dump1;

        try {
            //String name = taskReport.getString("name");
            String name = "Customer";
//
            dump1 = "/falcon/unzipped/"+name+".txt";
            dump = "/falcon/zipped/"+name+".zip";

            FileInputStream fin = new FileInputStream(Environment.getExternalStorageDirectory() + dump);
            ZipInputStream zin = new ZipInputStream(fin);
            BufferedInputStream in = new BufferedInputStream(zin);
            ZipEntry ze;

            Log.v(TAG, "Unzipping_start " + "Now");

            while ((ze = zin.getNextEntry()) != null) {

                FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory() + dump1);
                BufferedOutputStream out = new BufferedOutputStream(fout);

                int n;
                byte b[] = new byte[1024];
                while((n = in.read(b))!=-1){
                    fout.write(b, 0, n);
                }
                zin.closeEntry();
                fout.close();
            }
            zin.close();
            Log.v(TAG, "Unzipping Completed");

            deleteFiles();
            executeFileSQLStatement(name, dump1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ;
//
    }

    public void executeFileSQLStatement(String tableName, String extractedFilePath){

        ModuleDBAdapter  moduleDBAdapter = new ModuleDBAdapter(mContext);
        moduleDBAdapter.open(_Module.DB_NAME);

        // This assumes there is a hashmap keyed by table name that hold table create statement
        String createTable = DBQuery.hmTableCreate.get(tableName);

        //TODO: remove this
        String dropTable = "DROP TABLE IF EXISTS "+tableName;
        moduleDBAdapter.executeQuery(dropTable);


        moduleDBAdapter.executeQuery(createTable);

        File dbfile = new File(Environment.getExternalStorageDirectory() + extractedFilePath);

        int result = 0;
        try {

            InputStream insertsStream = new FileInputStream(dbfile);
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

            while (insertReader.ready()) {

                String insertStmt = insertReader.readLine();

                moduleDBAdapter.executeQuery(insertStmt);
                result++;

            }
            insertReader.close();

        } catch (Exception e) {
            Log.e(TAG , e.toString());
        }

        Cursor cursor = moduleDBAdapter.fetch("select * from  " + tableName);

        Log.d(TAG, result+"--"+cursor.getCount());
        if (cursor.getCount() == result) {
            Log.i(TAG, " dB content insert was successful");

        } else {
            Log.i(TAG, " dB content insert was not successful");
        }
        moduleDBAdapter.close();
        cursor.close();
    }

    public void updateModuleTable(String moduleName, String moduleSize, String moduleUrl){

        ModuleDBAdapter dbAdapter = new ModuleDBAdapter(mContext);

        dbAdapter.open(_Module.DB_NAME);

        dbAdapter.createModuleTable();
        dbAdapter.insert(123, moduleName, "downloaded", moduleSize, moduleUrl, "2015-02-02");

        Cursor cursor = dbAdapter.fetch("SELECT * FROM "+ModuleDBAdapter.TABLE_NAME);

        while(!cursor.isAfterLast()){

            cursor.moveToNext();
        }
        dbAdapter.close();

    }


    private class AsyncDBExec extends AsyncTask<ModuleItem, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtCurrentDownload.setText("extracting file,  please wait");
        }

        @Override
        protected Void doInBackground(ModuleItem... report) {

            String name = report[0].getName();
            String size = report[0].getSize();
            String url = report[0].getUrl();
            unZipnExecute(report[0]);
            updateModuleTable(name, size, url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateTotalProgress();
            txtCurrentDownload.setText("Done!");
            taskIterator++;
            startDownload();
        }
    }

}

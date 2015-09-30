package git.lawpavilionprime.SetUp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.grabner.circleprogress.CircleProgressView;
import dm.core.DownloadManagerPro;
import dm.report.ReportStructure;
import dm.report.listener.DownloadManagerListener;
import git.lawpavilionprime.Config;
import git.lawpavilionprime.DBQuery;
import git.lawpavilionprime.Dashboard.Dashboard;
import git.lawpavilionprime.auth._Login;

/**
 * Created by don_mayor on 7/31/2015.
 */
public class DownloadManager implements DownloadManagerListener {


    private DBQuery dbQuery;
    DownloadManagerPro downloadManagerPro;
    Context mContext;

    ArrayList<ModuleItem> mModulesToDownload;
    ArrayList<ModuleItem> mModulesDownloaded;
    ArrayList<ModuleItem> mTotalModules;

    ArrayList<Integer> downloadManagerTaskIds;
    Config config;

    AlertDialog mAlert;
    CircleProgressView mPerModuleProgressBar;
    private RoundCornerProgressBar mAllModulesProgressBar;

    public static final String TAG = "------DEBUG----";

    private int DOWNLOADED_COUNT;
    private int TOTAL_COUNT;
    private int TO_DOWNLOAD_COUNT;
    String unzippedDIR = "/falcon/unzipped";

    private RelativeLayout startLayout;
    private LinearLayout progressLayout;
    private TextView txtRefresh;

    private TextView txtCurrentDownload;
    private TextView txtTotalDownload;
    private TextView txtFileDownloadProgress;
    private Button btnDwnState;

    private int taskIterator;
    private boolean userPaused = false;

    public DownloadManager(Context context, ArrayList<ModuleItem> modulesToDownload, ArrayList<ModuleItem> modulesDownloaded, ArrayList<ModuleItem> totalModules){

        mContext = context;
        downloadManagerPro = new DownloadManagerPro(mContext);

        downloadManagerPro.init("falcon/zipped/", 3, this );
        config = new Config(mContext);
        downloadManagerTaskIds = new ArrayList<Integer>();

        mModulesToDownload = modulesToDownload;
        mTotalModules = totalModules;
        mModulesDownloaded = modulesDownloaded;

        DOWNLOADED_COUNT = mModulesDownloaded.size();
        TOTAL_COUNT = mTotalModules.size();
        TO_DOWNLOAD_COUNT = mModulesToDownload.size();

        dbQuery = new DBQuery();
        createDirectory();
        addTask();
    }

    public void createDirectory() {

        File unzippedFolder = new File(Environment.getExternalStorageDirectory().toString() + unzippedDIR);
        unzippedFolder.mkdirs();

        if (!new File(Environment.getExternalStorageDirectory().toString() + unzippedDIR).isDirectory()) {
            Log.e(TAG, " Unzipped Folder was not created");
        }
        else{
            Log.e(TAG, "Unzipped folder was created");
        }
    }

    public void addTask(){

        for(int i = 0; i< mModulesToDownload.size(); i++){
            Log.d("URL",  mModulesToDownload.get(i).getUrl() );
            Log.d("MODULES", ""+i);
            downloadManagerTaskIds.add( downloadManagerPro.addTask(mModulesToDownload.get(i).getName(), mModulesToDownload.get(i).getUrl(), true, true));
        }
    }

    public void updateProgressPerModule(double progress){
        float mProgress = new Float(progress);
        mPerModuleProgressBar.setValue(mProgress);
    }

    public void updateTotalProgress(long taskid){

        ModuleDBAdapter db = new ModuleDBAdapter(mContext);
        db.open(_Module.DB_NAME);

        Cursor cursor = db.fetch("SELECT * FROM "+ModuleDBAdapter.TABLE_NAME);

        DOWNLOADED_COUNT = cursor.getCount();
        Log.d(TAG, "count: "+DOWNLOADED_COUNT);
        Log.d(TAG, "total: "+TOTAL_COUNT);

        //Dummy method
        int total = downloadManagerTaskIds.size() - 1;
        int progrss = taskIterator;

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
            int lastIndex = downloadManagerTaskIds.size() - 1;
            long lastTaskId = downloadManagerTaskIds.get(lastIndex);
            if(taskid == lastTaskId){
                //Re queue
                Log.d(TAG, "EQUAL==="+lastTaskId+"==="+taskid);
            }
            else{
                //Continue
                Log.d(TAG, "EQUAL==="+lastTaskId+"==="+taskid);
            }
            //set up incomplete
        }
        cursor.close();
    }

    public void BeginDownload(){

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
            //txtRefresh.setVisibility(View.VISIBLE);

            startDownload();
        }
    }

    public void initProgress(){

        float progress = Math.round(getTotalProgress(DOWNLOADED_COUNT, TOTAL_COUNT));

        mAllModulesProgressBar.setProgress(progress);
        txtTotalDownload.setText(String.valueOf(progress) + "%");
        txtFileDownloadProgress.setText(DOWNLOADED_COUNT + " files downloaded out of " + TOTAL_COUNT);

        txtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManagerPro.pauseDownload(downloadManagerTaskIds.get(taskIterator));
                downloadManagerPro.dispose();
                Intent intent = new Intent(mContext,_Login.class);
                mContext.startActivity(intent);
            }
        });

        btnDwnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnDwnState.getText().toString().equals("PAUSE")){
                    userPaused = true;
                    downloadManagerPro.pauseDownload(downloadManagerTaskIds.get(taskIterator));
                    btnDwnState.setText("CONTINUE");
                }
                else{
                    userPaused = false;
                    btnDwnState.setText("PAUSE");
                    startDownload();
                }
            }
        });
    }

    public void startDownload(){

        if(taskIterator <downloadManagerTaskIds.size()) {
            try {
                Toast.makeText(mContext, "New Task", Toast.LENGTH_SHORT).show();
                int taskId = downloadManagerTaskIds.get(taskIterator);
                downloadManagerPro.startDownload(taskId);
                txtCurrentDownload.setVisibility(View.VISIBLE);
                txtCurrentDownload.setText("Downloading " + mModulesToDownload.get(taskIterator).getName() + "..." + " please wait");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            downloadManagerPro.dispose();

            _Module.isDownloading = false;
            Toast.makeText(mContext, "Task completed", Toast.LENGTH_SHORT).show();
            //Normal method is to finish the activity, go to login, to load dash board..
            //Update the user column: user_setup_status = completed
            Intent i = new Intent(mContext, Dashboard.class);
            mContext.startActivity(i);

        }
    }

    public float getTotalProgress(float downloaded, float total){

        float  progress = (downloaded / total) * 100;

        progress = Math.round(progress);

        return progress;
    }

    public void Alert(String title, String message){

        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (config.isConnectingToInternet()) {
                            //TODO: replace with URL

                        } else {

                        }
                    }
                });
        mAlert = builder.create();
        mAlert.show();
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

    private boolean enoughMemory() {

        long deviceFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //return (deviceFreeSpace > 1000000000);
        return (deviceFreeSpace > 1000000);
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


    public void startSingleDownload(int task_id){

        try {
            downloadManagerPro.startDownload(task_id);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseSingleDownload(int task_id){

        downloadManagerPro.pauseDownload(task_id);
    }

    public ReportStructure getSingleDownloadStatus(long task_id){

        int taskToken = (int) (long) task_id;

        ReportStructure report = downloadManagerPro.singleDownloadStatus(taskToken);
        return report;
    }
    public void notifyTaskChecked(){

        downloadManagerPro.notifiedTaskChecked();

    }

    public void deleteTask(int task_id, boolean deleteTaskFile){

        downloadManagerPro.delete(task_id, deleteTaskFile);
    }

    /**
     * close db connection
     * if your activity goes to userPaused or stop state
     * you have to call this method to disconnect from db
     */

    public void disconnetFromDB(){


        downloadManagerPro.dispose();
    }


    @Override
    public void OnDownloadStarted(long taskId) {


        Log.d(TAG, "download start");

    }

    @Override
    public void OnDownloadPaused(long taskId) {
        if(userPaused){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Download paused", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            try {
                downloadManagerPro.startDownload((int) (long) taskId);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
        Log.d(TAG,""+percent);
        updateProgressPerModule(percent);

    }

    @Override
    public void OnDownloadFinished(long taskId) {
        Log.d(TAG, "download finished");
        updateProgressPerModule(100);

    }

    @Override
    public void OnDownloadRebuildStart(long taskId) {
        Log.d(TAG, "rebuild started");
    }

    /*  unzip the data
        to fix the progress
        extract
        execute sql query
        confirm from the database
        update the module table
        confirm from module
        update the totalprogress
        delete the file
        check module count == server count
        update the user table
        else repeat process*/

    @Override
    public void OnDownloadRebuildFinished(long taskId) {
        Log.d(TAG, "rebuild finished");

    }

    @Override
    public void OnDownloadCompleted(final long taskId) {
        Log.d(TAG, ""+taskId);

        ReportStructure report = getSingleDownloadStatus(taskId);
        String name="";
        String url="";
        String size= "";

        try {

            JSONObject jsonReport = report.toJsonObject();
            name = jsonReport.getString("name");
            size = jsonReport.getString("fileSize");
            url = jsonReport.getString("url");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    txtCurrentDownload.setText("extracting file.. please wait");
                }
            });

            unZipnExecute(report);
            updateModuleTable(name, size, url);
            deleteTask((int) (long) taskId, true);
            //delete file
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Handler");
                    updateTotalProgress(taskId);
                    taskIterator++;
                    startDownload();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(long taskId) {

        Log.d(TAG, "Connection lost");
    }

    public void unZipnExecute(ReportStructure taskReport){

        String filePath;
        String extractedFilePath;
        JSONObject report = taskReport.toJsonObject();

        Log.d(TAG, report.toString());

        try {
            filePath = report.getString("saveAddress");
            //String name = report.getString("name");
            String name = "Customer";

            extractedFilePath = "/falcon/unzipped/"+name+".txt";

            FileInputStream fin = new FileInputStream(filePath);
            ZipInputStream zin = new ZipInputStream(fin);
            BufferedInputStream in = new BufferedInputStream(zin);
            ZipEntry ze;

            Log.v(TAG, "Unzipping_start " + "Now");

            while ((ze = zin.getNextEntry()) != null) {

                Log.v(TAG, "Unzipping " + ze.getName());

                FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory() + extractedFilePath);
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

            executeFileSQLStatement(name, extractedFilePath);
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

    private void deleteFiles(String extractedFile) {

        File bFile = new File(Environment.getExternalStorageDirectory() + extractedFile);
        boolean isB = bFile.delete();
        if (isB) {
            Log.i(TAG, " extracted File deleted!");
        }
    }





}

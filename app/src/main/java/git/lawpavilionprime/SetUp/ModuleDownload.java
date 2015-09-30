package git.lawpavilionprime.SetUp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import git.lawpavilionprime.DBQuery;
import notused.Module;

/**
 * Created by don_mayor on 7/7/2015.
 */
public class ModuleDownload {


//    ProgressDialog progressDialogOne;
//    ProgressDialog progressDialogTwo;

    String TAG = "LPELR 7.0 ";
    String mUrl = "";
    String zippedDIR = "/com.falcon/zipped";
    String unzippedDIR = "/com.falcon/unzipped";
    String downloadedZipFile="";
    String extractedFile = "";
    String mdB = "LawPavilionPrime";
    String createTable = "";

// This should be removed
    String dropTable = "DROP TABLE IF EXISTS Customer;";
//
//    String createTable = "CREATE TABLE IF NOT EXISTS Customer ( CustomerId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
//            "    FirstName VARCHAR(40)  NOT NULL,\n" +
//            "    LastName VARCHAR(20)  NOT NULL,\n" +
//            "    Company VARCHAR(80),\n" +
//            "    Address VARCHAR(70),\n" +
//            "    City VARCHAR(40),\n" +
//            "    State VARCHAR(40),\n" +
//            "    Country VARCHAR(40),\n" +
//            "    PostalCode VARCHAR(10),\n" +
//            "    Phone VARCHAR(24),\n" +
//            "    Fax VARCHAR(24),\n" +
//            "    Email VARCHAR(60)  NOT NULL,\n" +
//            "    SupportRepId INTEGER);";

    int expectedCursorCount = 59;
    String expectedZipFileSize = "";

    boolean isFinished = false;

    Context mContext;

    ArrayList<Boolean> dwnCompleted;
    ArrayList<Boolean> shwBtn;
    ModuleAdapter ntnAdapter;
    ProgressBar progressBar;
    private int currentPosition;
    DBQuery dbQuery;

    public ModuleDownload(Context context, ProgressBar progressBar){

        this.mContext = context;
        this.progressBar = progressBar;
        dbQuery = new DBQuery();
    }

    public void startDownload(ArrayList<Boolean> dwnCompleted, ArrayList<Boolean> shwBtn, ModuleAdapter ntnAdapter, int currentPosition){

        String tableName = Module.mName.get(currentPosition);

        extractedFile += "/com.falcon/unzipped/"+tableName.toLowerCase()+currentPosition+".txt";
        createTable = DBQuery.hmTableCreate.get(tableName);
        mUrl = Module.mURL.get(currentPosition);
        downloadedZipFile = "/com.falcon/zipped/"+tableName+currentPosition+".zip";

        this.dwnCompleted = dwnCompleted;
        this.shwBtn = shwBtn;
        this.ntnAdapter = ntnAdapter;

        this.currentPosition = currentPosition;
        if (enoughMemory()) {
            new myProcessOne().execute();
        } else {
            AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Error");
            builder.setMessage("Oops insufficiently memory")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private class myProcessOne extends AsyncTask<String, String, Boolean> {

        boolean isDownloaded = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            createDirectory();

            Log.d("-------------", "PRE");
        }

        @Override
        protected Boolean doInBackground(String... params) {

            isDownloaded = false;

            int count;

            try {

                URL url = new URL(mUrl);
                URLConnection urlConnection = url.openConnection();

                urlConnection.connect();

                int lenghtOfFile = urlConnection.getContentLength();
//                DataOutputStream printout = new DataOutputStream (urlConnection.getOutputStream ());
//
//                String content = "USERNAME=" + URLEncoder.encode(user) + "&PASSWORD=" + URLEncoder.encode (pass);
//
//                printout.writeBytes (content);
//                printout.flush ();
//                printout.close();


                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + downloadedZipFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                isDownloaded = true;

                Log.d("------DWN_ZIP", "back");
            } catch (Exception e) {
                Log.e(TAG + " Download Error ", e.toString());

                isDownloaded = false;
            }

            return isDownloaded;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(Integer.parseInt(values[0]));

        }

        @Override
        protected void onPostExecute(Boolean isDownloaded) {
            super.onPostExecute(isDownloaded);
            if(isDownloaded) {
                new myProcessTwo().execute();
            }
            else{
                //Notify Error in download..
                dwnCompleted.set(currentPosition, false);
                shwBtn.set(currentPosition, true);
                ntnAdapter.notifyDataSetChanged();

                Log.d("-------------", "POST");
            }




        }
    }

    private class myProcessTwo extends AsyncTask<String, String, Boolean> {
        boolean status =false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialogTwo = new ProgressDialog(mContext);
//            progressDialogTwo.setMessage("Extracting File...");
//            progressDialogTwo.setIndeterminate(true);
//            progressDialogTwo.setCancelable(false);
//            progressDialogTwo.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            status = false;

            try {
                FileInputStream fin = new FileInputStream(Environment.getExternalStorageDirectory() + downloadedZipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                BufferedInputStream in = new BufferedInputStream(zin);
                ZipEntry ze;

                Log.v(TAG, "Unzipping_start " + "Now");

                while ((ze = zin.getNextEntry()) != null) {

                    Log.v(TAG, "Unzipping " + ze.getName());

                    FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory() + extractedFile);
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
                executeFileSQL();
                status = true;

            } catch (Exception e) {
                //Log.e(TAG + " Unzip Error ", ""+ e.printStackTrace());
                e.printStackTrace();
                status = false;
            }

        return status;

        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
           // progressDialogTwo.dismiss();
            if(status){
                dwnCompleted.set(currentPosition, true);
                shwBtn.set(currentPosition, false);
                Log.d("---EXEC", "AFTER COMP");
            }
            else{
                dwnCompleted.set(currentPosition, false);
                shwBtn.set(currentPosition, true);
                Log.d("---EXEC", "BEFORE COMP");

                //Notify about zipping error..
            }
            ntnAdapter.notifyDataSetChanged();
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
    private void executeFileSQL() {

        Log.d("---EXECQL", "BEFORESQL");

        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase(mdB, mContext.MODE_PRIVATE, null);

        Log.d("---EXECSQL", "SQL");

        //sqLiteDatabase.execSQL(dropTable);
        sqLiteDatabase.execSQL(createTable);

        File dbfile = new File(Environment.getExternalStorageDirectory() + extractedFile);

        int result = 0;
        try {

            InputStream insertsStream = new FileInputStream(dbfile);
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

            while (insertReader.ready()) {
                String insertStmt = insertReader.readLine();

                Log.d("---INSERT", insertStmt);
                sqLiteDatabase.execSQL(insertStmt);
                result++;
            }
            insertReader.close();
        } catch (Exception e) {
            Log.e(TAG + " dB Execution Error ", e.toString());

        }
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Customer ", null);
        if (cursor.getCount() == expectedCursorCount) {
            Log.i(TAG, " dB content insert was successful");

            deleteFiles();

        } else {

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

        if (isA && isB) {
            isFinished = true;
        }
    }

    private boolean enoughMemory() {

        long deviceFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        //return (deviceFreeSpace > 1000000000);
        return (deviceFreeSpace > 1000000);
    }
}

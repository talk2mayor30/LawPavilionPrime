package git.lawpavilionprime.contentUpdate;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import git.lawpavilionprime.DBQuery;

/**
 * Created by don_mayor on 7/14/2015.
 */
public class ContentDownload {

    Context mContext;
    ProgressBar mProggressBar;
    ActionBarActivity mActivity;

    DBQuery dbQuery;
    private static String createTable = "";
    private static ArrayList<Boolean> dwnCompleted;
    private static ArrayList<Boolean> shwBtn;
    private ContentUpdateAdapter syncAdapter;
    private DatabaseAdapter dBAdapter;
    
    private  int currentPosition;
    private String mUrl;

    private int ResponseFromServer;
    private static String jsonParam = "";

    private JSONObject jsonObject;
    private JSONArray jsonArraySC;
    private JSONArray jsonArrayCA;
    private JSONArray jsonArrayUers;
    private JSONArray jsonArrayLFN;
    private JSONArray  jsonArrayCustomer;
    private String mResponse;


    private String SAMPLEJSON = "{ \"response\": { \"Customer\":[{\"CustomerId\": \"1\", \"FirstName\": \"Leonie\", " +
            "\"LastName\": \"Kohler\", \"Company\": \"null\", \"Address\": \"Git Limited\", \"City\": \"Opebi\", \"State\": \"Lagos\", " +
            "\"Country\": \"Nigeria\", \"PostalCode\": \"342\", \"Phone\": \"+23409500322\", \"Fax\": \"null\",  \"Email\": \"me@gmail.com\", " +
            "\"SupportRepId\": \"4\"}], \"Supreme_Court\":[{}] } }";

    private static final String TAG_CUSTOMER = "Customer";

    // Main JSON nodes
    private static final String TAG_RESPONSE_SUPREME_COURT = "supreme_court";
    private static final String TAG_RESPONSE_COURT_OF_APPEAL = "";
    private static final String TAG_RESPONSE_LAW_OF_FED = "law_of_federation";

    private static final String TAG_RESPONSE_USERS = "users";
    private static final String TAG_RESPONSE_DATA = "response";
    private static final String TAG_USER_RESPONSE = "code";

    // Task JSON nodes
    private static final String TAG_SC_ID = "id";
    private static final String TAG_SC_NAME = "name";
    private static final String TAG_SC_DESCRIPTION = "details";
    private static final String TAG_SC_ASSIGNED_TO = "assignee";
    private static final String TAG_SC_DUE_DATE_TIME = "due_time";
    private static final String TAG_SC_STATUS = "status";
    private static final String TAG_SC_CASE = "task_case";
    private static final String TAG_SC_DT_CREATED = "created";
    private static final String TAG_SC_DT_MODIFIED = "modified";

    // Court JSON nodes
    private static final String TAG_CA_ID = "id";
    private static final String TAG_CA_TITLE = "title";
    private static final String TAG_CA_DATE_OF_HEARING = "date_hearing";
    private static final String TAG_CA_DATE_OF_NEXT_HEARING = "next_hearing";
    private static final String TAG_CA_STAGE_OF_NEXT_HEARING = "stage_next";
    private static final String TAG_CA_SUMMARY = "summary";
    private static final String TAG_CA_NEXT_ACTION_PLAN = "next_action";
    private static final String TAG_CA_COUNSEL = "counsel";
    private static final String TAG_CA_COURT_NAME = "court_name";
    private static final String TAG_CA_DT_CREATED = "created";
    private static final String TAG_CA_DT_MODIFIED = "modified";
    private static final String TAG_CA_STATUS = "status";
    private static final String TAG_CA_MODIFIED_BY = "modified_by";

    // Matters JSON nodes
    private static final String TAG_LFN_ID = "id";
    private static final String TAG_LFN_SUIT_NO = "suitno";
    private static final String TAG_LFN_CASE_TITLE = "name";
    private static final String TAG_LFN_COURT_NAME = "court_name";
    private static final String TAG_LFN_CASE_URL = "url";
    private static final String TAG_LFN_CASE_TYPE = "case_type";
    private static final String TAG_LFN_CASE_FACTS = "facts";
    private static final String TAG_LFN_DT_CREATED = "created";
    private static final String TAG_LFN_DT_MODIFIED = "modified";

    // Users JSON nodes
    private static final String TAG_USER_ID = "id";
    private static final String TAG_USER_FIRST_NAME = "firstname";
    private static final String TAG_USER_LAST_NAME = "lastname";
    private static final String TAG_USER_DT_CREATED = "created";
    private static final String TAG_USER_DT_MODIFIED = "modified";
    private static final String TAG_DATA_NAME_NUMBER = "expiry_date";
    private static int UPDATE_COUNT;


    public ContentDownload(ActionBarActivity activity, Context context, ProgressBar progressBar){

        this.mContext = context;

        this.mActivity = activity;
        this.mProggressBar = progressBar;
        this.dbQuery = new DBQuery();


    }

    public void startDownload(ArrayList<Boolean> dwnCompleted, ArrayList<Boolean> shwBtn, ContentUpdateAdapter syncAdapter, DatabaseAdapter dBAdapter, int currentPosition) {

        String tableName = ContentUpdate.mName.get(currentPosition);

        createTable = DBQuery.hmTableCreate.get(tableName);
        this.dwnCompleted = dwnCompleted;
        this.shwBtn = shwBtn;
        this.syncAdapter = syncAdapter;
        this.dBAdapter = dBAdapter;
        this.currentPosition = currentPosition;
        this.mUrl = ContentUpdate.mUrl.get(this.currentPosition);

        Log.d("---URL", mUrl + " --- " + createTable + "----" + tableName);
        try {
            JSONObject js = new JSONObject(SAMPLEJSON);
            Log.d("VALID", "JSON is valid");
        } catch (JSONException e) {
            Log.d("VALID", "JSON is invalid");
            e.printStackTrace();
        }
        new SyncRecordFromServer().execute();
    
    }

    public class SyncRecordFromServer extends AsyncTask<Void, Void, Integer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String dMessage = "Synchronizing data..." + "\n"
                    + "Kindly wait for process to be completed";
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(dMessage);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @SuppressWarnings("static-access")
        @Override
        protected Integer doInBackground(Void... arg0) {

            final String URL_SYNC = mUrl;

            // Create a new HttpClient and Post Header
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL_SYNC);
            HttpResponse httpResponse;

            int responseStatus = 0;

            try {

                httpResponse = httpClient.execute(httpGet);
                mResponse = EntityUtils.toString(httpResponse.getEntity());

                // Get JSON Object Nodes from array
                jsonObject = new JSONObject(mResponse);
                Log.i("JSON Whole response: ", jsonObject.toString());

                //responseStatus = jsonObject.getInt(TAG_USER_RESPONSE);

                //Test with Sample Json here;
                jsonObject = new JSONObject(SAMPLEJSON);

                JSONObject dataJsonObject = jsonObject
                        .getJSONObject(TAG_RESPONSE_DATA);

                Log.i("Json Response", dataJsonObject.toString());
                jsonArrayCustomer = dataJsonObject.getJSONArray(TAG_CUSTOMER);

                for (int i = 0; i < jsonArrayCustomer.length(); i++) {

                    JSONObject object = jsonArrayCustomer.getJSONObject(i);

                    // You need to do "'" string escape ):-
                    final String id = object.getString("CustomerId");
                    final String firstname = object.getString("FirstName");
                    final String lastname = object.getString("LastName");
                    final String company = object.getString("Company");
                    final String address = object.getString("Address");
                    final String city = object.getString("City");
                    final String state = object.getString("State");
                    final String country = object.getString("Country");
                    final String postalcode = object.getString("PostalCode");
                    final String phone = object.getString("Phone");
                    final String fax = object.getString("Fax");
                    final String email = object.getString("Email");
                    final String supportrepid = object.getString("SupportRepId");

                    // delete & replace each record
                    dBAdapter = new DatabaseAdapter(mContext);
                    SQLiteDatabase db  = dBAdapter.open(DatabaseAdapter.DB_NAME);

                    db.execSQL(DatabaseAdapter.CREATE_CUSTOMER_TABLE);

                    Cursor cursor = db.rawQuery("select * from Customer", null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){

                        Log.d("----Cursor RbU", cursor.getPosition()+" --"+cursor.getString(cursor.getColumnIndex("FirstName")) );
                        cursor.moveToNext();
                    }
                    cursor.close();

                    db.execSQL("delete from "+ TAG_CUSTOMER + "where id="+id);
                    db.execSQL("INSERT INTO Customer values( " + id + " ,'"+firstname +"'" + " ,'"+lastname +"'"
                            + " ,'"+company +"'" + " ,'"+address +"'" + " ,'"+city  +"'" + " ,'"+state+"'" + " ,'"+country +"'"
                            + " ,'"+postalcode +"'" + " ,'"+phone +"'" + " ,'"+fax +"'" + " ,'"+email +
                            "'" + " ,"+supportrepid+ ")" );


                    cursor = db.rawQuery("select * from Customer", null);
                    cursor.moveToFirst();
                    Log.d("Cursor count", "count: "+cursor.getCount());

                    while (!cursor.isAfterLast()){
                        Log.d("----Cursor ", cursor.getPosition()+" --"+cursor.getString(cursor.getColumnIndex("FirstName")) );
                        cursor.moveToNext();
                    }
                    db.close();
                    dBAdapter.close();
                    cursor.close();
                }

                // Get Update for each Table, supreme court, court of appeal, user, and more
//                jsonArraySC= dataJsonObject.getJSONArray(TAG_RESPONSE_SUPREME_COURT);
//                jsonArrayCA = dataJsonObject
//                        .getJSONArray(TAG_RESPONSE_COURT_OF_APPEAL);
//                jsonArrayLFN = dataJsonObject
//                        .getJSONArray(TAG_RESPONSE_LAW_OF_FED);
//                jsonArrayUers = dataJsonObject.getJSONArray(TAG_RESPONSE_USERS);
//                //End the the get
//
//                // Loop through Supreme Court; Update database.
//                for (int i = 0; i < jsonArraySC.length(); i++) {
//
//                    JSONObject object = jsonArraySC.getJSONObject(i);
//
//                    // You need to do "'" string escape ):-
//                    final String id = object.getString(TAG_SC_ID).replace(
//                            "'", "''");
//                    final String name = object.getString(TAG_SC_NAME)
//                            .replace("'", "''");
//                    final String task_case = object.getString(TAG_SC_CASE)
//                            .replace("'", "''");
//                    final String description = object.getString(
//                            TAG_SC_DESCRIPTION).replace("'", "''");
//                    final String assigned_to = object.getString(
//                            TAG_SC_ASSIGNED_TO).replace("'", "''");
//                    final String due_time = object.getString(
//                            TAG_SC_DUE_DATE_TIME).replace("'", "''");
//                    final String status = object.getString(TAG_SC_STATUS)
//                            .replace("'", "''");
//                    final String dt_created = object.getString(
//                            TAG_SC_DT_CREATED).replace("'", "''");
//                    final String dt_modified = object.getString(
//                            TAG_SC_DT_MODIFIED).replace("'", "''");
//
//
//                    // delete & replace each record
//                    dBAdapter.open();
//
//                    dBAdapter.delete("delete from table where id = id");
//
//                    dBAdapter.insert("insert into table data where id=id");
//
//                    dBAdapter.close();
//
//                }
//
//                // Loop through Court of Appeal; Update database.
//                for (int i = 0; i < jsonArrayCA.length(); i++) {
//
//                    JSONObject object = jsonArrayCA.getJSONObject(i);
//
//                    final String id = object.getString(TAG_CA_ID)
//                            .replace("'", "''");
//                    final String title = object
//                            .getString(TAG_CA_TITLE)
//                            .replace("'", "''");
//                    final String date_hearing = object.getString(
//                            TAG_CA_DATE_OF_HEARING).replace("'", "''");
//                    final String date_next_hearing = object.getString(
//                            TAG_CA_DATE_OF_NEXT_HEARING).replace("'",
//                            "''");
//                    final String stage_next_hearing = object.getString(
//                            TAG_CA_STAGE_OF_NEXT_HEARING).replace("'",
//                            "''");
//                    final String summary = object.getString(
//                            TAG_CA_SUMMARY).replace("'", "''");
//                    final String next_action_plan = object.getString(
//                            TAG_CA_NEXT_ACTION_PLAN)
//                            .replace("'", "''");
//                    final String counsel = object.getString(
//                            TAG_CA_COUNSEL).replace("'", "''");
//                    final String court_name = object.getString(
//                            TAG_CA_COURT_NAME).replace("'", "''");
//                    final String dt_created = object.getString(
//                            TAG_CA_DT_CREATED).replace("'", "''");
//                    final String dt_modified = object.getString(
//                            TAG_CA_DT_MODIFIED).replace("'", "''");
//                    final String status = object.getString(
//                            TAG_CA_STATUS).replace("'", "''");
//                    final String modified_by = object.getString(
//                            TAG_CA_MODIFIED_BY).replace("'", "''");
//
//                    // delete & replace each record
//                    dBAdapter.open();
//                    dBAdapter.delete("delete from table where id = id");
//                    dBAdapter.insert("insert into table data where id=id");
//                    dBAdapter.close();
//                }
//
//                // Loop through Matters; Update database.
//                for (int i = 0; i < jsonArrayLFN.length(); i++) {
//
//                    JSONObject object = jsonArrayLFN.getJSONObject(i);
//
//                    final String id = object.getString(TAG_LFN_ID).replace(
//                            "'", "''");
//                    final String suit_no = object.getString(TAG_LFN_SUIT_NO)
//                            .replace("'", "''");
//                    final String court_name = object.getString(
//                            TAG_LFN_COURT_NAME).replace("'", "''");
//                    final String case_title = object.getString(
//                            TAG_LFN_CASE_TITLE).replace("'", "''");
//                    final String url = object.getString(TAG_LFN_CASE_URL)
//                            .replace("'", "''");
//                    final String case_type = object.getString(
//                            TAG_LFN_CASE_TYPE).replace("'", "''");
//                    final String case_facts = object.getString(
//                            TAG_LFN_CASE_FACTS).replace("'", "''");
//                    final String dt_created = object.getString(
//                            TAG_LFN_DT_CREATED).replace("'", "''");
//                    final String dt_modified = object.getString(
//                            TAG_LFN_DT_MODIFIED).replace("'", "''");
//
//                    // delete & replace each record
//                    dBAdapter.open();
//                    dBAdapter.delete("delete from table where id = id");
//                    dBAdapter.insert("insert into table data where id=id");
//                    dBAdapter.close();
//
//                }
//
//                // Loop through Users; Update database.
//                for (int i = 0; i < jsonArrayUers.length(); i++) {
//
//                    JSONObject object = jsonArrayUers.getJSONObject(i);
//
//                    final String id = object.getString(TAG_USER_ID).replace(
//                            "'", "''");
//                    final String firstname = object.getString(
//                            TAG_USER_FIRST_NAME).replace("'", "''");
//                    final String lastname = object
//                            .getString(TAG_USER_LAST_NAME).replace("'", "''");
//                    final String dt_created = object.getString(
//                            TAG_USER_DT_CREATED).replace("'", "''");
//                    final String dt_modified = object.getString(
//                            TAG_USER_DT_MODIFIED).replace("'", "''");
//
//                    // Dr lppex Nefario ):-
//
//                    dBAdapter.open();
//                    Cursor cursorMyID = dBAdapter.FetchMyOwnID("query to fetch my own ID");
//                    dBAdapter.close();
//
//                    if (cursorMyID != null && cursorMyID.getCount() > 0) {
//                        if (cursorMyID.moveToFirst()) {
//                            String myID = cursorMyID.getString(cursorMyID
//                                    .getColumnIndex(DatabaseAdapter._ID)); // to replace with DATA_ID
//                            if (myID.equals(id)) {
//                                final String nameNumber = object
//                                        .getString(TAG_DATA_NAME_NUMBER);
//
//                                dBAdapter.open();
//                                //baseAdapter.UpdateNameNumber(nameNumber);
//                                dBAdapter.close();
//
//                            }
//                        }
//                    }
//                    // End ):-
//
//                    // delete & replace each record
//
//                    dBAdapter.open();
//                    dBAdapter.delete("delete from table where id = id");
//                    dBAdapter.insert("insert into table data where id=id");
//                    dBAdapter.close();

 //               }



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ResponseFromServer = responseStatus;
            return responseStatus;

        }

        @SuppressWarnings("static-access")
        @Override
        protected void onPostExecute(Integer outcome) {
            // TODO Auto-generated method stub
            super.onPostExecute(outcome);
            progressDialog.dismiss();

            Log.d("RESPONSE AT END", "Outcome: "+outcome);
//            if (ResponseFromServer == 200) {
//
//                Toast.makeText(mContext, "Data update successful",
//                        Toast.LENGTH_LONG).show();
//                mActivity.finish();
//
//                Intent intent = new Intent(mActivity, Dashboard.class);
//                mContext.startActivity(intent);
//
//            } else if (ResponseFromServer == 401) {
//                // Toast user for license expiry.
//                Toast.makeText(
//                        mContext,
//                        "User license expired.\n"
//                                + "Contact your administrator.",
//                        Toast.LENGTH_LONG).show();
//
//                // Delete token from database
//                dBAdapter.open();
//                dBAdapter.delete("delete from table where id = id");
//                dBAdapter.close();
//
//                // Launch login class
//                Intent intent = new Intent(mActivity, Dashboard.class);
//                mContext.startActivity(intent);
//
//            } else if (ResponseFromServer == 403) {
//                // Invalid token
//                Toast.makeText(mContext,
//                        "Session expired.\n" + "Login again.",
//                        Toast.LENGTH_LONG).show();
//
//                // Delete token from database
//                dBAdapter.open();
//                dBAdapter.delete("delete from table where token");
//                dBAdapter.close();
//
//                // Launch login class
//                Intent intent = new Intent(mActivity, Dashboard.class);
//                mContext.startActivity(intent);
//
//            }
        }
    }
}

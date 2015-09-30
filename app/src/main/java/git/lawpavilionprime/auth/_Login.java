package git.lawpavilionprime.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import git.lawpavilionprime.Config;
import git.lawpavilionprime.Dashboard.Dashboard;
import git.lawpavilionprime.SetUp._Module;
import git.lawpavilionprime.bookstore.BookStore;
import git.lawpavilionprime.gcmutils.RegisterApp;
import notused.Module;
import git.lawpavilionprime.R;

public class _Login extends Activity {

    private TextView    txtEmail;
    private TextView    txtPassword;
    private Button      btnLogin;
    private CheckBox    ckRememberMe;
    private TextView    txtForgotPassword;
    private TextView    txtSignUp;
    private Button      btnSignUp;
    private Validator   validator;

    private static String _email;
    private static String _password;

    //Response node
    private static String max_device_count;
    private static String device_count;
    private static String license_type;
    private static String expiry_date;
    private static String customer_id;
    private static String email;
    private static String image_source;
    private static String first_name;
    private static String last_name;
    private static String request_status;
    private static String user_status;
    private static String message;
    private static String token;
    private static String status;
    private static String Base64_image_resource;
    private static String license_code;
    private static String set_up_status;

    private String testJSON = "{ \"message\":\"success\", \"max_device_count\":\"3\", \"device_count\":\"2\", \"license_type\":\"multiple\"," +
            "\"expiry_date\":\"1000\", \"user_status\":\"active\", \"request_status\":\"success\", \"customer_id\":\"2\", \"email\":\"test@gmail.com\", \"image_source\":\"/SDCARD/lawpavilion/\", " +
            "\"first_name\":\"Mayowa\", \"last_name\":\"Egbewunmi\", \"token\":\"r3f64dsa\"}";

    public static String DB_NAME = "falcon";
    private Config config;
    private static String baseUrl;
    static UserDBAdapter dbAdapter;

    private static String NEW_USER_TYPE = "new_user";
    private static String LOGGED_OUT_USER_TYPE = "logged_out";
    private static String SIGN_IN_TYPE = "new_user";
    static Context mContext;
    static LinearLayout mProgressBar;
    static TextView txtProgressMessage;
    static AlertDialog backPressedDialog;

    static boolean isAsyncTaskRunning = false;

    private Button btnBkStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_);

        mContext = _Login.this;
        txtEmail =   (TextView)  findViewById(R.id.email);
        txtPassword =   (TextView)  findViewById(R.id.password);
        btnLogin    =   (Button)    findViewById(R.id.login);
        ckRememberMe =  (CheckBox)  findViewById(R.id.checkbox);
        txtForgotPassword =   (TextView)    findViewById(R.id.forgotPassword);
        txtSignUp   =   (TextView)  findViewById(R.id.signUp);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        validator   =   new Validator();
        config      =   new Config(_Login.this);
        dbAdapter = new UserDBAdapter(this);

        mProgressBar = (LinearLayout) findViewById(R.id.progressMum);
        txtProgressMessage = (TextView) findViewById(R.id.progressMessage);
        btnBkStore = (Button) findViewById(R.id.btnBkStore);
        baseUrl = "http://lawpavilionstore.com/android/login";


        dbAdapter.open(DB_NAME);

        dbAdapter.createUserTable();

        Cursor cursor = dbAdapter.fetch("SELECT * from " + dbAdapter.TABLE_NAME +" limit 1");
        if(cursor != null){
            //new user
            if(cursor.getCount() == 1){
                String loggedOut = cursor.getString(cursor.getColumnIndex(dbAdapter.LOG_OUT));
                if(loggedOut.equalsIgnoreCase("0")){
                    //User not looged out..continue
                    String token = cursor.getString(cursor.getColumnIndex(dbAdapter.TOKEN));
                    Toast.makeText(_Login.this, "Existing", Toast.LENGTH_SHORT).show();

                    String set_up_status = cursor.getString(cursor.getColumnIndex(dbAdapter.SET_UP_STATUS));

                    if(set_up_status.equalsIgnoreCase("pending")){

                        Intent intent  = new Intent(_Login.this, _Module.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //TODO: remove this
                        //dbAdapter.executeQuery("DROP TABLE " + dbAdapter.TABLE_NAME + ";");
                        _Login.this.finish();
                    }
                    else{
                        Intent intent  = new Intent(_Login.this, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //TODO: remove this
                        //dbAdapter.executeQuery("DROP TABLE " + dbAdapter.TABLE_NAME + ";");
                        _Login.this.finish();
                    }
                }
                else{
                    //user has been forced to login..
                    //check if gcm exist for user..
                    SIGN_IN_TYPE = LOGGED_OUT_USER_TYPE;
                }
            }
            else{
                //try register GCM for new user
                Log.d("---LOG---", "GCM started");
                new RegisterApp(getApplicationContext()).execute();

                //New user account..
                SIGN_IN_TYPE = NEW_USER_TYPE;
            }
            cursor.close();
        }
        else{
            Toast.makeText(_Login.this, "DB error", Toast.LENGTH_SHORT).show();
            _Login.this.finish();
            //Do something here
        }
        dbAdapter.close();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _email = txtEmail.getText().toString().trim();
                _password = txtPassword.getText().toString().trim();
                boolean isFieldSet = true;

                if(!validator.isValidEmail(_email)){
                    txtEmail.setError(Validator.emailErrorMessage);
                    isFieldSet = false;
                }

                if(validator.isEmpty(_password)){
                    txtPassword.setError(Validator.defaultErrorMessage);
                    isFieldSet = false;
                }
               //Toast.makeText(_Login.this, "Ready for Async Task", Toast.LENGTH_SHORT).show();

                if(isFieldSet){
                    connectIfInternetIsAvailable();
                }
                else{
                    return;
                }
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_Login.this, _ForgotPassword.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_Login.this, _SignUp.class);
                startActivity(intent);
            }
        });

//        txtSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(_Login.this, _SignUp.class);
//                startActivity(intent);
//            }
//        });
        btnBkStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(_Login.this, BookStore.class);
                startActivity(intent);
            }
        });
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //no action
            }
        });
    }

    public void connectIfInternetIsAvailable(){
        if(config.isConnectingToInternet()){
            txtProgressMessage.setText("Please wait...");
            mProgressBar.setVisibility(View.VISIBLE);

            AsyncManager.runBackgroundTask(new _AsyncLogin());

        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(_Login.this);
            builder.setTitle("Error");
            builder.setMessage("Oops! Something went wrong!\n\nplease check your internet connection")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mProgressBar.setVisibility(View.GONE);
                        }
                    })
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connectIfInternetIsAvailable();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public void onBackPressed() {

        if(isAsyncTaskRunning){
            AlertDialog.Builder builder = new AlertDialog.Builder(_Login.this);
            builder.setTitle("Info");
            builder.setMessage("You are about to  exit the application\n\nYour Login is almost complete, Do you want to continue?")
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("BACK", "BACK PRESSED");
                            mProgressBar.setVisibility(View.GONE);
                            AsyncManager.cancelAllTasks();
                            _Login.this.finish();
                        }
                    })
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            backPressedDialog = builder.create();
            backPressedDialog.show();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    private static class _AsyncLogin extends PersistedTaskRunnable<Void, String, Void>{

        @Override
        public String doLongOperation(Void aVoid) throws InterruptedException {
            try {

                isAsyncTaskRunning = true;

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity;
                HttpResponse httpResponse = null;

                String parameters = "?email=" + _email + "&password=" + _password ;
                String requestUrl = baseUrl + parameters;
                HttpGet httpGet = new HttpGet(requestUrl);

                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                String jsonResult = EntityUtils.toString(httpEntity);
                Log.d("---------JSONTEST", jsonResult);

                return jsonResult;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void callback(String res) {

            Log.d("CALLED", "CALLED");
            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Error");

                builder.setMessage("Oops! Something went wrong\n\nplease check your internet connection")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtProgressMessage.setText("Please wait...");
                                mProgressBar.setVisibility(View.VISIBLE);
                                AsyncManager.runBackgroundTask(new _AsyncLogin());

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                try {
                    Log.d("response", res);
                    JSONObject jsonSettingsResponse = new JSONObject(res).getJSONObject("settings");
                    String responseCode = jsonSettingsResponse.getString("success");
                    String responseMessage = jsonSettingsResponse.getString("message");
                    Log.d("LOG, success", "exec"+responseCode + responseMessage);
                    mProgressBar.setVisibility(View.GONE);

                    if(responseCode.equalsIgnoreCase("1")){

                        //TODO: u change customer id data type check for error


                        JSONArray jsonArrayData = new JSONObject(res).getJSONArray("data");

                        if(jsonArrayData.length() == 1){

                            JSONObject jsonData = jsonArrayData.getJSONObject(0);

                            max_device_count = jsonData.getString("max_device_count");
                            device_count = jsonData.getString("device_count");
                            //license_type = jsonData.getString("license_type");
                            license_type = "multiple";
                            expiry_date = jsonData.getString("expiry_date");
                            customer_id = jsonData.getString("customer_id");
                            email = jsonData.getString("email");
                            image_source = jsonData.getString("image_source");
                            first_name = jsonData.getString("first_name");
                            last_name = jsonData.getString("last_name");
                            status = jsonData.getString("status");
                            Base64_image_resource = jsonData.getString("Base64_image_resource");
                            token = "rds23a";
                            license_code = jsonData.getString("license_code");
                        }

                        //update the DB here..//open to instantiate
                        dbAdapter.open(DB_NAME);
                        dbAdapter.executeQuery("DELETE FROM "+ dbAdapter.TABLE_NAME);

                        if(SIGN_IN_TYPE == NEW_USER_TYPE){

                            //TODO: change to content-values

                            dbAdapter.insert(1, customer_id, first_name, last_name, email, status, image_source, expiry_date,
                                    license_type, Base64_image_resource, token, 0);

//                            String insertQuery = "INSERT INTO "+ dbAdapter.TABLE_NAME +" VALUES (\"" + 1 + "\", \""  + customer_id +"\",  \""  +
//                                    first_name +"\", \""+ last_name + "\", \""+ email + "\", \""+ status +"\", \""+
//                                    image_source + "\", \""+ expiry_date + "\", \""+ license_type +"\",  \"" +
//                                    Base64_image_resource +"\", \"" + "pending" +"\",  \"" + token +"\", "+ 0 +");";
//
//                            dbAdapter.executeQuery(insertQuery);
                        }
                        else if(SIGN_IN_TYPE == LOGGED_OUT_USER_TYPE){
                            //TODO: change to content-values

                            dbAdapter.insert(1, customer_id, first_name, last_name, email, status, image_source, expiry_date,
                                    license_type, Base64_image_resource, token, 0);

                            Cursor cursor = dbAdapter.fetch("SELECT * FROM "+dbAdapter.TABLE_NAME);


//                            String insertQuery = "INSERT INTO "+ dbAdapter.TABLE_NAME + "( " + dbAdapter._ID + ", "+ dbAdapter.CUSTOMER_ID + ", "+ dbAdapter.FIRST_NAME + ", "+
//                                    dbAdapter.LAST_NAME + ", "+ dbAdapter.EMAIL + ", "+ dbAdapter.USER_STATUS + ", "+ dbAdapter.IMAGE_SOURCE + ", "+ dbAdapter.EXPIRY_DATE + ", "+
//                                    dbAdapter.LICENSE_TYPE + ", "+ dbAdapter.BASE64_IMAGE_SOURCE + ", "+ dbAdapter.TOKEN + ", "+ dbAdapter.LOG_OUT + ", "+")"
//                                    +" VALUES (\"" + 1 + "\", \""  + customer_id +"\",  \""  +
//                                    first_name +"\", \""+ last_name + "\", \""+ email + "\", \""+ status +"\", \""+
//                                    image_source + "\", \""+ expiry_date + "\", \""+ license_type +"\",  \"" +
//                                    Base64_image_resource +"\", \""  + token +"\", "+ 0 +");";
//
//                            dbAdapter.executeQuery(insertQuery);
                        }


                        Cursor cursor = dbAdapter.fetch("SELECT * FROM "+ dbAdapter.TABLE_NAME);
                        cursor.moveToFirst();
                        int  i = 0;
                        while (!cursor.isAfterLast()){
                            String cur = cursor.getString(cursor.getColumnIndex(dbAdapter.LAST_NAME));
                            Log.d("CURSOR RES", ""+cur);
                            cursor.moveToNext();
                        }
                        dbAdapter.close();

                        if(status.equalsIgnoreCase("active")){
                            Integer expDate = Integer.valueOf(expiry_date);

                            if(expDate > 0){
                                LoadSession();
                            }
                            else if(expDate == -1000){
                                //New user without license
                                //
                                LoadSession();
                            }
                            else{
                                Toast.makeText(mContext, "License expired", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            //  Existing user expired
                        }
                    }
                    else if(responseCode.equalsIgnoreCase("0")){
                        //Success
                        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Info");
                        builder.setMessage(responseMessage)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                catch (JSONException e) {
                    //TODO: create an alert error;
                    e.printStackTrace();
                }

            }


        }
    }

    static void LoadSession(){


        //TODO: uncomment this
        if(SIGN_IN_TYPE == LOGGED_OUT_USER_TYPE){
            // Restart the activity
            Intent intent = new Intent(mContext, _Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }
        else if(SIGN_IN_TYPE == NEW_USER_TYPE){
            //Integer maxDeviceCount = Integer.valueOf(max_device_count)
            Integer nDeviceCount = Integer.valueOf(device_count);
            Integer maxDeviceCount = 1;

            if(nDeviceCount < maxDeviceCount){
                txtProgressMessage.setText("Finishing...");
                mProgressBar.setVisibility(View.VISIBLE);

                //Send the Device ID..
                AsyncManager.runBackgroundTask(new _AsyncSendDeviceId());

                SharedPreferences sharedPref = mContext.getSharedPreferences(_Login.class.getSimpleName(), Context.MODE_PRIVATE);
                String  gcmID = sharedPref.getString("registration_id", null);

                if(gcmID == null){
                    //generate n send GCM ID
                    new RegisterApp(mContext).execute();
                    Log.d("--DEBUG--", "not available");
                }
                else{
                    //Send GCM ID
                    Log.d("--DEBUG---CHECK", gcmID);
                    new RegisterApp(mContext).execute();
                }
            }
            else{
                //notify of max usage of device
                //drop table..
                dbAdapter.open(DB_NAME);
                dbAdapter.executeQuery("DROP TABLE "+ dbAdapter.TABLE_NAME);
                dbAdapter.close();
                Toast.makeText(mContext, "Maximum number of device reached", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private static class _AsyncSendDeviceId extends PersistedTaskRunnable<Void, String, Void>{

        @Override
        public String doLongOperation(Void aVoid) throws InterruptedException {
            try {

                isAsyncTaskRunning = true;
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity;
                HttpResponse httpResponse = null;

                String parameters = "?email=" + _email + "&password=" + _password ;
                String requestUrl = baseUrl + parameters;
                HttpGet httpGet = new HttpGet("http://validate.jsontest.com/?json=[JSON-code-to-validate]");

                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                String jsonResult = EntityUtils.toString(httpEntity);
                Log.d("---------JSONTEST", jsonResult);

                return jsonResult;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void callback(String res) {

            Log.d("POST", "CALL");
            mProgressBar.setVisibility(View.GONE);
            isAsyncTaskRunning = false;

            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Info");
                builder.setMessage("Oops! something went wrong, couldn't finish authentication\n\n Try _Login again")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbAdapter.open(DB_NAME);
                                dbAdapter.executeQuery("DROP TABLE "+ dbAdapter.TABLE_NAME +";");
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                if(backPressedDialog != null && backPressedDialog.isShowing())
                    backPressedDialog.dismiss();

                    //TODO: change Module.class to _Login.class
                    Intent intent = new Intent(mContext, _Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    mContext.startActivity(intent);
            }
        }
    }


}

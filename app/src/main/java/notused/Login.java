package notused;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
import git.lawpavilionprime.R;
import git.lawpavilionprime.auth.UserDBAdapter;
import git.lawpavilionprime.auth.Validator;

public class Login extends Activity {

    private TextView    txtEmail;
    private TextView    txtPassword;
    private Button      btnLogin;
    private CheckBox    ckRememberMe;
    private TextView    txtForgotPassword;
    private TextView    txtSignUp;
    private Validator validator;

    private String _email;
    private String _password;

    //Response node
    private String max_device_count;
    private String device_count;
    private String license_type;
    private String expiry_date;
    private String customer_id;
    private String email;
    private String image_source;
    private String first_name;
    private String last_name;
    private String request_status;
    private String user_status;
    private String message;
    private String token;
    private String status;
    private String Base64_image_resource;
    private String license_code;
    private String set_up_status;

    private String testJSON = "{ \"message\":\"success\", \"max_device_count\":\"3\", \"device_count\":\"2\", \"license_type\":\"multiple\"," +
            "\"expiry_date\":\"1000\", \"user_status\":\"active\", \"request_status\":\"success\", \"customer_id\":\"2\", \"email\":\"test@gmail.com\", \"image_source\":\"/SDCARD/lawpavilion/\", " +
            "\"first_name\":\"Mayowa\", \"last_name\":\"Egbewunmi\", \"token\":\"r3f64dsa\"}";

    private static String DB_NAME = "falcon";
    private Config config;
    private String baseUrl;
    UserDBAdapter dbAdapter;

    private String NEW_USER_TYPE = "new_user";
    private String LOGGED_OUT_USER_TYPE = "logged_out";

    private String SIGN_IN_TYPE = "new_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtEmail =   (TextView)  findViewById(R.id.email);
        txtPassword =   (TextView)  findViewById(R.id.password);
        btnLogin    =   (Button)    findViewById(R.id.login);
        ckRememberMe =  (CheckBox)  findViewById(R.id.checkbox);
        txtForgotPassword =   (TextView)    findViewById(R.id.forgotPassword);
        txtSignUp   =   (TextView)  findViewById(R.id.signUp);
        validator   =   new Validator();
        config      =   new Config(Login.this);
        dbAdapter = new UserDBAdapter(this);

        baseUrl = "http://lawpavilionstore.com/android/login";

        dbAdapter.open(DB_NAME);
        //TODO: remove drop table query
        dbAdapter.executeQuery("DROP TABLE IF EXISTS" + dbAdapter.TABLE_NAME + ";");
        //dbAdapter.createUserTable();

        Cursor cursor = dbAdapter.fetch("SELECT * from " + dbAdapter.TABLE_NAME);

        if(cursor != null){
            //new user
            if(cursor.getCount() == 1) {

                String loggedOut = cursor.getString(cursor.getColumnIndex(dbAdapter.LOG_OUT));
                    if(loggedOut.equalsIgnoreCase("0")){
                        //User not looged out..continue
                        String token = cursor.getString(cursor.getColumnIndex(dbAdapter.TOKEN));
                        Toast.makeText(Login.this, "Existing", Toast.LENGTH_SHORT).show();

                        Login.this.finish();
                        String set_up_status = cursor.getString(cursor.getColumnIndex(dbAdapter.SET_UP_STATUS));

                        if(set_up_status.equalsIgnoreCase("pending")){

                            Intent intent  = new Intent(Login.this, Module.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else{
                            Intent intent  = new Intent(Login.this, Dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }
                    else{
                            //user has been forced to login..
                            SIGN_IN_TYPE = LOGGED_OUT_USER_TYPE;
                        }
                }
                else{
                            //New user account..
                            SIGN_IN_TYPE = NEW_USER_TYPE;
                }
        }
        else{
            Toast.makeText(Login.this, "DB error", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(Login.this, "Ready for Async Task", Toast.LENGTH_SHORT).show();

                if(isFieldSet){
                    if(config.isConnectingToInternet()){

                        AsyncLogin asyncLogin = new AsyncLogin();
                        asyncLogin.execute();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        builder.setTitle("Error");
                        builder.setMessage("Oops! Something went wrong!\n\nplease check your internet connection")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AsyncLogin asyncLogin = new AsyncLogin();
                                        asyncLogin.execute();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
                else{
                    return;
                }



            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

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

    private class AsyncLogin extends AsyncTask<Void, Void, String>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {

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
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            progressDialog.dismiss();
            //parse jsonResponse here..

            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Error");

                builder.setMessage("Oops! Something went wrong!\n\nplease check your internet connection")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AsyncLogin asyncLogin = new AsyncLogin();
                                asyncLogin.execute();
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


                    if(responseCode.equalsIgnoreCase("1")){

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

                        //update the DB here..

                       dbAdapter.open(DB_NAME);
                        dbAdapter.executeQuery("DELETE FROM "+ dbAdapter.TABLE_NAME);

                        if(SIGN_IN_TYPE == NEW_USER_TYPE){

                            String insertQuery = "INSERT INTO "+ dbAdapter.TABLE_NAME +" VALUES (\"" + 1 + "\", \""  + customer_id +"\",  \""  +
                                    first_name +"\", \""+ last_name + "\", \""+ email + "\", \""+ status +"\", \""+
                                    image_source + "\", \""+ expiry_date + "\", \""+ license_type +"\",  \"" +
                                    Base64_image_resource +"\", \"" + "pending" +"\",  \"" + token +"\", "+ 0 +");";

                            dbAdapter.executeQuery(insertQuery);
                        }
                        else if(SIGN_IN_TYPE == LOGGED_OUT_USER_TYPE){

                            String insertQuery = "INSERT INTO "+ dbAdapter.TABLE_NAME + "( " + dbAdapter._ID + ", "+ dbAdapter.CUSTOMER_ID + ", "+ dbAdapter.FIRST_NAME + ", "+
                                    dbAdapter.LAST_NAME + ", "+ dbAdapter.EMAIL + ", "+ dbAdapter.USER_STATUS + ", "+ dbAdapter.IMAGE_SOURCE + ", "+ dbAdapter.EXPIRY_DATE + ", "+
                                    dbAdapter.LICENSE_TYPE + ", "+ dbAdapter.BASE64_IMAGE_SOURCE + ", "+ dbAdapter.TOKEN + ", "+ dbAdapter.LOG_OUT + ", "+")"
                                    +" VALUES (\"" + 1 + "\", \""  + customer_id +"\",  \""  +
                                    first_name +"\", \""+ last_name + "\", \""+ email + "\", \""+ status +"\", \""+
                                    image_source + "\", \""+ expiry_date + "\", \""+ license_type +"\",  \"" +
                                    Base64_image_resource +"\", \""  + token +"\", "+ 0 +");";

                            dbAdapter.executeQuery(insertQuery);
                        }


                        Cursor cursor = dbAdapter.fetch("SELECT * FROM "+ dbAdapter.TABLE_NAME);
                        cursor.moveToFirst();
                        int  i = 0;

                        while (!cursor.isAfterLast()){

                            String cur = cursor.getString(i);
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
                                Toast.makeText(Login.this, "License expired", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            // Existing user expired
                        }

                    }
                    else if(responseCode.equalsIgnoreCase("0")){
                        //Success
                        AlertDialog.Builder  builder = new AlertDialog.Builder(Login.this);
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
                    e.printStackTrace();
                }
             }

        }

    public void LoadSession(){

            if(SIGN_IN_TYPE == LOGGED_OUT_USER_TYPE){
                // Restart the activity
                Login.this.finish();
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }
            else if(SIGN_IN_TYPE == NEW_USER_TYPE){
                //Integer maxDeviceCount = Integer.valueOf(max_device_count)
                Integer nDeviceCount = Integer.valueOf(device_count);
                Integer maxDeviceCount = 1;

                if(nDeviceCount < maxDeviceCount){

                    //Send the Device ID..
                    AsyncSendDeviceId asyncDeviceId = new AsyncSendDeviceId();
                    asyncDeviceId.execute();

                }
                else{
                    //notify of max usage of device
                    //drop table..
                    dbAdapter.open(DB_NAME);
                    dbAdapter.executeQuery("DROP TABLE "+ dbAdapter.TABLE_NAME);
                    dbAdapter.close();
                    Toast.makeText(Login.this, "Maximum number of device reached", Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class AsyncSendDeviceId extends AsyncTask<Void, Void, String>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Finishing...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            try {

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
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            progressDialog.dismiss();
            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Info");
                builder.setMessage("Oops! something went wrong, couldn't finishing authentication\n\n Try Login again")
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
                Login.this.finish();
                //Restart activity
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }
        }
    }
}

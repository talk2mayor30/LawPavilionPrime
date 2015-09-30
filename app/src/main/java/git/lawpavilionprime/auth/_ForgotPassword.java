package git.lawpavilionprime.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import org.json.JSONException;
import org.json.JSONObject;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;

public class _ForgotPassword extends Activity {

    private TextView txtEmail;
    private Button btnForgotPassword;
    private Button btnLogin;

    TextView txtLogin;
    Validator validator;
    Config config;

    static String _email;
    static String _password;
    static String baseUrl;

    static LinearLayout mProgressBar;
    static TextView txtProgressMessage;

    private static Context mContext;
    private static boolean isAsyncTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_);

        txtEmail = (TextView) findViewById(R.id.email);
        txtLogin = (TextView) findViewById(R.id.login);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        txtProgressMessage = (TextView) findViewById(R.id.progressMessage);
        mProgressBar = (LinearLayout) findViewById(R.id.progressMum);

        baseUrl = "http://lawpavilionstore.com/android/forgot_password";
        validator = new Validator();
        config = new Config(_ForgotPassword.this);

        mContext = _ForgotPassword.this;

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ForgotPassword.this.finish();
            }
        });

//        txtLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                _ForgotPassword.this.finish();
//            }
//        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _email   = txtEmail.getText().toString().trim();

                if(!validator.isValidEmail(_email)){
                    txtEmail.setError(Validator.emailErrorMessage);
                    return;
                }

                connectIfInternetIsAvailable();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forgot_password, menu);
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

    public void connectIfInternetIsAvailable(){
        if(config.isConnectingToInternet()){
            txtProgressMessage.setText("Please wait...");
            mProgressBar.setVisibility(View.VISIBLE);
            AsyncManager.runBackgroundTask(new _AsyncRetrievePassword());
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(_ForgotPassword.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(_ForgotPassword.this);
            builder.setTitle("Info");
            builder.setMessage("You are about to  exit the application\n\nYour password retrieval is almost complete, Do you want to continue?")
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("BACK", "BACK PRESSED");
                            mProgressBar.setVisibility(View.GONE);
                            AsyncManager.cancelAllTasks();
                            _ForgotPassword.this.finish();
                        }
                    })
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            super.onBackPressed();
        }
    }

    private static class _AsyncRetrievePassword extends PersistedTaskRunnable<Void, String, Void> {

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

            isAsyncTaskRunning = false;
            mProgressBar.setVisibility(View.GONE);
            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
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
                                AsyncManager.runBackgroundTask(new _AsyncRetrievePassword());

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                try {

                    Log.d("response", res);
                    JSONObject jsonSettingsResponse = new JSONObject(res).getJSONObject("settings");
                    String responseCode = jsonSettingsResponse.getString("success");
                    String responseMessage = jsonSettingsResponse.getString("message");

                    Log.d("LOG, success", "exec"+responseCode + responseMessage);
                    Toast.makeText(mContext, responseCode + "---" + responseMessage, Toast.LENGTH_SHORT).show();

                    if(responseCode.equalsIgnoreCase("1")){
                        //Failed
                        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Info");
                        builder.setMessage(responseMessage + "\n\n" + "")
                                .setPositiveButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Load email apps
                                        Intent intent   =   new Intent(mContext, _Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(intent);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
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
                    e.printStackTrace();
                }
            }
        }
    }
}

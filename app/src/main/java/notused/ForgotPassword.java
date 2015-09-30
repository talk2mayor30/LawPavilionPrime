package notused;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import git.lawpavilionprime.Config;
import git.lawpavilionprime.R;
import git.lawpavilionprime.auth.Validator;

public class ForgotPassword extends Activity {

    TextView txtEmail;
    Button btnForgotPassword;

    TextView txtLogin;
    Validator validator;
    Config config;

    String _email;
    String _password;
    String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        txtEmail = (TextView) findViewById(R.id.email);
        txtLogin = (TextView) findViewById(R.id.login);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);

        baseUrl = "http://lawpavilionstore.com/android/forgot_password";
        validator = new Validator();
        config = new Config(ForgotPassword.this);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ForgotPassword.this.finish();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               _email   = txtEmail.getText().toString().trim();

                if(!validator.isValidEmail(_email)){
                    txtEmail.setError(Validator.emailErrorMessage);
                    return;
                }
                if(config.isConnectingToInternet()){
                    AsyncRetrievePassword asyncLogin = new AsyncRetrievePassword();
                    asyncLogin.execute();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
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
                                    AsyncRetrievePassword asyncLogin = new AsyncRetrievePassword();
                                    asyncLogin.execute();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

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


    private class AsyncRetrievePassword extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(ForgotPassword.this);
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
            //parse jsonResponse here..
            progressDialog.dismiss();

            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(ForgotPassword.this);
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
                                AsyncRetrievePassword asyncLogin = new AsyncRetrievePassword();
                                asyncLogin.execute();
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
                    Toast.makeText(ForgotPassword.this, responseCode + "---" + responseMessage, Toast.LENGTH_SHORT).show();

                    if(responseCode.equalsIgnoreCase("1")){
                        //Failed
                        AlertDialog.Builder  builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setTitle("Info");
                        builder.setMessage(responseMessage + "\n\n" + "")
                                .setPositiveButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Load email apps
                                        Intent intent   =   new Intent(ForgotPassword.this, Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(responseCode.equalsIgnoreCase("0")){
                        //Success
                        AlertDialog.Builder  builder = new AlertDialog.Builder(ForgotPassword.this);
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

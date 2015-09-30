package git.lawpavilionprime.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class _SignUp extends Activity {

    private TextView    txtLastName;
    private TextView    txtPassword;
    private TextView    txtConfirmPassword;
    private TextView    txtFirstName;
    private TextView    txtEmail;
    private TextView    txtContact;
    private Button      btnSignUp;
    private TextView    txtLogin;
    private Button      btnLogin;
    private TextView    txtResponseMessage;

    private Spinner     spnCountry;
    private Spinner     spnState;

    private Validator validator;
    private static String baseUrl;

    private static String firstname;
    private static String lastname;
    private static String email;
    private static String password;
    private static String confirmPassword;
    private static String contact;
    private static String country;
    private static String state;

    private String[] _country;
    private String[] _state;

    private Config config;
    private ArrayAdapter countryAdapter;
    private ArrayAdapter stateAdapter;

    private static LinearLayout mProgressBar;
    private static TextView txtProgressMessage;
    private static Context mContext;

    private static boolean isAsyncTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_);

        txtLastName =   (TextView) findViewById(R.id.lastname);
        txtFirstName =   (TextView) findViewById(R.id.firstname);
        txtEmail    =   (TextView) findViewById(R.id.email);
        txtPassword =   (TextView)  findViewById(R.id.password);
        txtConfirmPassword  =   (TextView)  findViewById(R.id.confirmPassword);
        txtContact  =   (TextView)  findViewById(R.id.contact);
        btnSignUp   =   (Button) findViewById(R.id.btnSignUp);
        txtLogin    =   (TextView)  findViewById(R.id.login);
        btnLogin    =   (Button) findViewById(R.id.btnLogin);
        txtResponseMessage  =   (TextView)  findViewById(R.id.responseMessage);
        config  =   new Config(_SignUp.this);
        spnCountry = (Spinner) findViewById(R.id.country);
        spnState = (Spinner) findViewById(R.id.state);

        _country = getResources().getStringArray(R.array.country_array_name);
        countryAdapter = new ArrayAdapter(_SignUp.this, R.layout.simple_list_item, _country);
        spnCountry.setAdapter(countryAdapter);

        _state = getResources().getStringArray(R.array.nigeria_states_array_list);
        stateAdapter = new ArrayAdapter(_SignUp.this, R.layout.simple_list_item, _state);
        spnState.setAdapter(stateAdapter);

        mProgressBar = (LinearLayout) findViewById(R.id.progressMum);
        txtProgressMessage = (TextView) findViewById(R.id.progressMessage);

        mContext = _SignUp.this;

        spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(_country[position].equalsIgnoreCase("Nigerian")){

                    spnState.setClickable(true);
                }
                else{
                    spnState.setSelection(0);
                    spnState.setClickable(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        validator = new Validator();
        baseUrl = "http://lawpavilionstore.com/android/sign_up";

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _SignUp.this.finish();
            }
        });
//
//        txtLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _SignUp.this.finish();
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstname = txtFirstName.getText().toString().trim();
                lastname = txtLastName.getText().toString().trim();
                email = txtEmail.getText().toString().trim();
                password = txtPassword.getText().toString().trim();
                confirmPassword = txtConfirmPassword.getText().toString().trim();
                contact = txtContact.getText().toString().trim();
                state = spnState.getSelectedItem().toString().trim();
                country = spnCountry.getSelectedItem().toString().trim();

                boolean isFieldSet = true;

                if (validator.isEmpty(firstname)) {

                    txtFirstName.setError(Validator.defaultErrorMessage);
                    isFieldSet = false;
                }

                if (validator.isEmpty(lastname)) {

                    txtLastName.setError(Validator.defaultErrorMessage);
                    isFieldSet = false;

                }

                if(!validator.stateSelected(state)){

                    Toast.makeText(_SignUp.this, "State not selected", Toast.LENGTH_LONG).show();
                    isFieldSet = false;
                }

                if (!validator.isValidEmail(email)) {

                    txtEmail.setError(Validator.emailErrorMessage);
                    isFieldSet = false;

                }

                if (validator.isEmpty(password)) {

                    txtPassword.setError("Password cannot be empty");
                    isFieldSet = false;

                }

                if (!validator.passwordConfirmed(password, confirmPassword)) {
                    txtConfirmPassword.setError(Validator.passwordErrorMessage);
                    isFieldSet = false;

                }

                if (validator.isEmpty(contact)) {
                    txtContact.setError("Contact cannot be empty");
                    isFieldSet = false;

                }

                if (isFieldSet) {
                    connectIfInternetIsAvailable();

                } else {
                    return ;
                }
            }
        });
    }

    public void connectIfInternetIsAvailable(){

        if (config.isConnectingToInternet()) {
            txtProgressMessage.setText("Please wait...");
            mProgressBar.setVisibility(View.VISIBLE);
            AsyncManager.runBackgroundTask(new _AsyncRegister());

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(_SignUp.this);
            builder.setTitle("Error");

            builder.setMessage("Oops! Something went wrong!\n\nplease verify your internet connection")
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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

    @Override
    public void onBackPressed() {
        if(isAsyncTaskRunning){
            AlertDialog.Builder builder = new AlertDialog.Builder(_SignUp.this);
            builder.setTitle("Info");
            builder.setMessage("You are about to  exit the application\n\nYour sign up is almost complete, Do you want to continue?")
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("BACK", "BACK PRESSED");
                            mProgressBar.setVisibility(View.GONE);
                            AsyncManager.cancelAllTasks();
                            _SignUp.this.finish();
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

    private static class _AsyncRegister extends PersistedTaskRunnable<Void, String, Void>{

        @Override
        public String doLongOperation(Void aVoid) throws InterruptedException {
            try {

                isAsyncTaskRunning = true;

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity;
                HttpResponse httpResponse = null;

                String parameters = "?email=" + email + "&password=" + password + "&confirm_password=" + password
                        + "&first_name=" + firstname + "&last_name=" + lastname + "&country=" + country
                        + "&state=" + state;

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
            super.callback(res);
            isAsyncTaskRunning = false;
            mProgressBar.setVisibility(View.GONE);

            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Error");
                builder.setMessage("Oops! Something went wrong!\n\nplease verify your internet connection")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtProgressMessage.setText("Please wait");
                                mProgressBar.setVisibility(View.VISIBLE);
                                AsyncManager.runBackgroundTask(new _AsyncRegister());
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
                    Toast.makeText(mContext, responseCode+"---"+responseMessage, Toast.LENGTH_SHORT).show();

                    if(responseCode.equalsIgnoreCase("1")){
                        //Failed
                        AlertDialog.Builder  builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Info");
                        builder.setMessage(responseMessage + "\n\n" + "A confirmation link has been sent to your email, "+
                                "please confirm your account from your email before you log in")
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

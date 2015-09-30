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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class SignUp extends Activity {

    private TextView    txtLastName;
    private TextView    txtPassword;
    private TextView    txtConfirmPassword;
    private TextView    txtFirstName;
    private TextView    txtEmail;
    private TextView    txtContact;
    private Button      btnSignUp;
    private TextView    txtLogin;
    private TextView    txtResponseMessage;

    private Spinner     spnCountry;
    private Spinner     spnState;

    private Validator validator;
    private String baseUrl;

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String confirmPassword;
    private String contact;
    private String country;
    private String state;

    private String[] _country;
    private String[] _state;

    private Config config;
    private ArrayAdapter countryAdapter;
    private ArrayAdapter stateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        txtLastName =   (TextView) findViewById(R.id.lastname);
        txtFirstName =   (TextView) findViewById(R.id.firstname);
        txtEmail    =   (TextView) findViewById(R.id.email);
        txtPassword =   (TextView)  findViewById(R.id.password);
        txtConfirmPassword  =   (TextView)  findViewById(R.id.confirmPassword);
        txtContact  =   (TextView)  findViewById(R.id.contact);
        btnSignUp   =   (Button) findViewById(R.id.sign_up);
        txtLogin    =   (TextView)  findViewById(R.id.login);
        txtResponseMessage  =   (TextView)  findViewById(R.id.responseMessage);
        config  =   new Config(SignUp.this);
        spnCountry = (Spinner) findViewById(R.id.country);
        spnState = (Spinner) findViewById(R.id.state);

        _country = getResources().getStringArray(R.array.country_array_name);
        countryAdapter = new ArrayAdapter(SignUp.this, R.layout.simple_list_item, _country);
        spnCountry.setAdapter(countryAdapter);

        _state = getResources().getStringArray(R.array.nigeria_states_array_list);
        stateAdapter = new ArrayAdapter(SignUp.this, R.layout.simple_list_item, _state);
        spnState.setAdapter(stateAdapter);

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

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignUp.this.finish();
//                Intent intent   =   new Intent(SignUp.this, Login.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

            }
        });

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

                    Toast.makeText(SignUp.this, "State not selected", Toast.LENGTH_LONG).show();
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

                    if (config.isConnectingToInternet()) {
                        AsyncRegister asyncRegister = new AsyncRegister();
                        asyncRegister.execute();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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
                                        AsyncRegister asyncRegister = new AsyncRegister();
                                        asyncRegister.execute();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    return ;
                }
            }
        });

//        txtResponseMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //load email apps
//            }
//        });
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
        super.onBackPressed();
    }

    private class AsyncRegister extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(SignUp.this);
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
        protected void onPostExecute(String res) {
            super.onPostExecute(res);


            if(res == null){
                AlertDialog.Builder  builder = new AlertDialog.Builder(SignUp.this);
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
                                AsyncRegister asyncRegister = new AsyncRegister();
                                asyncRegister.execute();
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
                    Toast.makeText(SignUp.this, responseCode+"---"+responseMessage, Toast.LENGTH_SHORT).show();

                    if(responseCode.equalsIgnoreCase("1")){
                        //Failed
                        AlertDialog.Builder  builder = new AlertDialog.Builder(SignUp.this);
                        builder.setTitle("Info");
                        builder.setMessage(responseMessage + "\n\n" + "A confirmation link has been sent to your email"+
                                "Please confirm your account from your email before you log in")
                                .setPositiveButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Load email apps
                                        Intent intent   =   new Intent(SignUp.this, Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(responseCode.equalsIgnoreCase("0")){
                        //Success
                        AlertDialog.Builder  builder = new AlertDialog.Builder(SignUp.this);
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
           progressDialog.dismiss();
        }
    }


}

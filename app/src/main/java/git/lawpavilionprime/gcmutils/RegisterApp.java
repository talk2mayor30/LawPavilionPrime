package git.lawpavilionprime.gcmutils;

/**
 * Author: LANREWAJU
 * Date Created: Jul 15,2015
 * Time Created: 12:07
 * Project Name: LawPavilion
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import git.lawpavilionprime.auth.UserDBAdapter;
import git.lawpavilionprime.auth._Login;

public class RegisterApp extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GCMRelated";
    static final String SENDER_ID = "918978542580";
    Context ctx;
    GoogleCloudMessaging gcm;
    String GcmID = null;
    private int appVersion;

    public RegisterApp(Context ctx ){
        this.ctx = ctx;
        this.gcm = GoogleCloudMessaging.getInstance(ctx);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("GCMRelated", "Registration ongoing");
    }

    @Override
    protected String doInBackground(Void... arg0) {
        String msg = "";
        Log.d("GCMRelated", "Registration entered dib");
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            GcmID = gcm.register(SENDER_ID);

            msg = "Device registered, registration ID=" + GcmID;
            Log.d("GCMRelated", msg);
            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            storeRegistrationId(ctx, GcmID);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    private void storeRegistrationId(Context ctx, String regid) {

        UserDBAdapter dbAdapter = new UserDBAdapter(ctx);
        dbAdapter.open(_Login.DB_NAME);
        Cursor cursor = dbAdapter.fetch("SELECT * from "+dbAdapter.TABLE_NAME+ " limit 1");
        Log.d("--CURSOR--", "---"+cursor.getCount());
        if(cursor!=null && cursor.getCount() == 1){
            dbAdapter.update("UPDATE " + dbAdapter.TABLE_NAME + " SET " + dbAdapter.GCM_ID + " = '" + regid + "' WHERE " + dbAdapter._ID + " = 1;");
            cursor = dbAdapter.fetch("SELECT "+dbAdapter.GCM_ID +" from "+dbAdapter.TABLE_NAME+" limit 1");
            Log.d("---GCM ID---", "----" + regid + "----" + cursor.getString(cursor.getColumnIndex(dbAdapter.GCM_ID)));
        }
        else{
            final SharedPreferences prefs = ctx.getSharedPreferences(_Login.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("registration_id", regid);
            editor.putInt("appVersion", appVersion);
            editor.commit();
            Log.d("---DEBUG--REG", regid);
        }
        dbAdapter.close();
        cursor.close();
    }

    private void sendRegistrationIdToBackend() {
        URI url = null;
        try {
            url = new URI("http://kassafrica.com/gcmutils/register.php?name=mayowa&email=mayor&regid="+ GcmID);
            Log.d("GCMRelated", "url is " + url);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);
        try {
            httpclient.execute(request);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(ctx, "Registration Completed. Now you can see the notifications", Toast.LENGTH_SHORT).show();
        Log.d("GCMRelated", "Registration completed " + result);
    }
}

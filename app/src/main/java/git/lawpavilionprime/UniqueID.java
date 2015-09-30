package git.lawpavilionprime;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.security.MessageDigest;

import git.lawpavilionprime.R;

public class UniqueID extends Activity {

    TextView txtDeviceID;

    String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_id);

        txtDeviceID = (TextView) findViewById(R.id.unique);

        getCleartextID_HARDCHECK();
        String md5 = MD5(deviceID);

        Log.d("MD5", md5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.unique_id, menu);
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

    public void  getCleartextID_HARDCHECK (){
        String ret = "";

        txtDeviceID.setText("SERIAL : " + Build.SERIAL);
        deviceID = Build.SERIAL;

        Log.d("SERIAL, ", deviceID);

        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if(telMgr != null && hasTelephony(this)){
            //Log.i("DEVICE IMEI", telMgr.getDeviceId() + "");
            txtDeviceID.setText("IMEI : " + telMgr.getDeviceId());

            deviceID += telMgr.getDeviceId();
        }
        else{
            deviceID +=deviceID;
        }

        Log.i("DEVICE SERIAL", Build.SERIAL );
        Log.i("DEVICE IMEI", telMgr.getDeviceId() + "");
        Log.d("UNIQUE ID", deviceID);

    }

    static public boolean hasTelephony(Context mContext)
    {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null)
            return false;

        //devices below are phones only
        if (Build.VERSION.SDK_INT < 5)
            return true;

        PackageManager pm = mContext.getPackageManager();

        if (pm == null)
            return false;

        boolean retval = false;
        try
        {
            Class<?> [] parameters = new Class[1];
            parameters[0] = String.class;
            Method method = pm.getClass().getMethod("hasSystemFeature", parameters);
            Object [] parm = new Object[1];
            parm[0] = "android.hardware.telephony";
            Object retValue = method.invoke(pm, parm);
            if (retValue instanceof Boolean)
                retval = ((Boolean) retValue).booleanValue();
            else
                retval = false;
        }
        catch (Exception e)
        {
            retval = false;
        }

        return retval;
    }

    public String MD5(String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return "";
        }
    }


}

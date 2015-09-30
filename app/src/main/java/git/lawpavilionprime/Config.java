package git.lawpavilionprime;

        import android.bluetooth.BluetoothAdapter;
        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.content.res.Configuration;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Build;
        import android.telephony.TelephonyManager;
        import android.text.Spannable;
        import android.text.SpannableString;
        import android.text.Spanned;
        import android.text.TextUtils;
        import android.text.style.ForegroundColorSpan;
        import android.util.DisplayMetrics;
        import android.util.Log;

        import java.lang.reflect.Method;
        import java.security.MessageDigest;

/**
 * Created by don_mayor on 6/9/2015.
 */
public class Config {

    public static final String scJudgments = "jud.sc";

    public static final String caJudgments = "jud.ca";
    public static final String DEFAULT = "";
    public static final String FIRST_NAME = "mayowa";
    public static String deviceID;

    Context mContext;

    public Config(Context context){

        mContext = context;
        String deviceID = MD5(getDeviceID());
        String deviceName = getDeviceName();

        isMoreTenInches();

        Log.d("ID TO SEND", deviceID);
        Log.d("Device Name", deviceName);

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;
    }

    public Spannable searchFor(String[] text, String prose) {
        Spannable raw = new SpannableString(prose);
        ForegroundColorSpan[] spans = raw.getSpans(0,
                raw.length(),
                ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            raw.removeSpan(span);
        }
        String [] content = prose.split(" ");

        for (String word : text) {

            for(String mContent : content ){
                if(word.equalsIgnoreCase(mContent)){
                    int index = TextUtils.indexOf(raw, mContent);
                    while (index >= 0) {
                        raw.setSpan(new ForegroundColorSpan(0xFF8B008B), index, index
                                + mContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        index = TextUtils.indexOf(raw, mContent, index + mContent.length());
                    }
                }
            }
        }
        return raw;
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    /**
     * MD5 hashing
     *
     * @param toEncrypt
     * @return
     */
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

    public String getDeviceID(){

        deviceID = Build.SERIAL;

        Log.d("SERIAL, ", deviceID);

        TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(telMgr != null && hasTelephony(mContext)){
            deviceID += telMgr.getDeviceId();
        }
        else if(mBluetoothAdapter != null){
            String bluetoothAddress = mBluetoothAdapter.getAddress().replace(":", "");
            deviceID +=bluetoothAddress;
        }
        else{
            deviceID+=deviceID;
        }

        Log.i("DEVICE SERIAL", Build.SERIAL );
        Log.i("DEVICE IMEI", telMgr.getDeviceId() + "");
        Log.d("DEVICE ID TO ENCRYPT", deviceID);
        Log.d("BLEUTOOTH", mBluetoothAdapter.getAddress().replace(":", ""));

        return deviceID;

    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String brand = Build.BRAND;
        String product = Build.PRODUCT;
        Log.d("PRODUCT", product);
        Log.d("BRAND", brand);
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public boolean isMoreTenInches(){
        Configuration config = mContext.getResources().getConfiguration();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float inches;
        if(config.orientation == config.ORIENTATION_PORTRAIT) {
            inches  = displayMetrics.heightPixels / displayMetrics.ydpi;
            Log.d("INCHES PORT", ""+inches);
        }
        else{
            inches  = displayMetrics.widthPixels / displayMetrics.xdpi;
            Log.d("INCHES LAND", ""+inches);
        }

        if(inches > 8.0){
            Log.d("INCHES", " 10 INCHES");
            return true;
        }
        else{
            Log.d("INCHES", " NOT 10INCHES");
        }

        return false;
    }
}


package git.lawpavilionprime.gcmutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import git.lawpavilionprime.Config;


/**
 * Created by LANREWAJU on 23/04/2015.
 */
public class SharedPrefs {
    Context mContext;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    public SharedPrefs(Context context) {
        mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPref.edit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
    }

    public String getString(String key) {
        return sharedPref.getString(key, Config.DEFAULT);
    }

    public String getString(String key, String defaultValue) {
        return sharedPref.getString(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
    }

    public boolean getBoolean(String key) {
        return sharedPref.getBoolean(key, false);
    }

    public int getInt(String key,int defaultValue) {
        return sharedPref.getInt(key, defaultValue);
    }

    public void setToDefault(String key) {
        editor.putString(key, Config.DEFAULT);
    }

    public void commit() {
        editor.apply();
    }
}

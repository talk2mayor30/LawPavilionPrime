package git.lawpavilionprime.auth;

/**
 * Created by don_mayor on 7/9/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDBAdapter {

    public static final String TABLE_NAME = "user";
    public static final String LICENSE_TYPE = "license_type";
    public static final String EXPIRY_DATE = "expiry_date";
    public static final String _ID = "_id";
    public static final String EMAIL = "email";
    public static final String IMAGE_SOURCE = "image_source";
    public static final String BASE64_IMAGE_SOURCE = "Base64_image_resource";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USER_STATUS = "user_status";
    public static final String TOKEN = "token";
    public static final String LOG_OUT = "log_out";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String SET_UP_STATUS = "set_up_status";
    public static final String GCM_ID = "gcm_id";


    public static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            +   "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            CUSTOMER_ID + " INTEGER  NOT NULL,\n" +
            FIRST_NAME + " VARCHAR(40)  NOT NULL,\n" +
            LAST_NAME + " VARCHAR(40)  NOT NULL,\n" +
            EMAIL + " VARCHAR(40),\n" +
            USER_STATUS + " VARCHAR(40),\n" +
            IMAGE_SOURCE + " VARCHAR(10),\n" +
            EXPIRY_DATE + " VARCHAR(24),\n" +
            LICENSE_TYPE + " VARCHAR(24),\n" +
            BASE64_IMAGE_SOURCE + " BLOB NOT NULL,\n" +
            SET_UP_STATUS + " VARCHAR(60) NULL, \n"+
            TOKEN + " VARCHAR(60)  NOT NULL,\n" +
            GCM_ID + " VARCHAR NULL, \n"+
            LOG_OUT + " INTEGER);";

    private Context mContext;

    private SQLiteDatabase database;

    public UserDBAdapter(Context c) {
        mContext = c;
    }

    public SQLiteDatabase open(String DB_NAME) throws SQLException {

        database = mContext.openOrCreateDatabase(DB_NAME , mContext.MODE_PRIVATE, null);

        return database;

    }

    public void executeQuery(String query){
        database.execSQL(query);
    }

    public void close() {
        database.close();
    }

    public void insert(int _id, String customer_id, String first_name,
                       String last_name, String email, String user_status, String image_source,
                       String expiry_date, String license_type, String base64_image_source, String token, int log_out){

        ContentValues values = new ContentValues();
        values.put(_ID, _id);
        values.put(CUSTOMER_ID, customer_id);
        values.put(FIRST_NAME, first_name);
        values.put(LAST_NAME, last_name);
        values.put(EMAIL, email);
        values.put(USER_STATUS, user_status);
        values.put(IMAGE_SOURCE, image_source);
        values.put(EXPIRY_DATE, expiry_date);
        values.put(LICENSE_TYPE, license_type);
        values.put(BASE64_IMAGE_SOURCE, base64_image_source);
        values.put(TOKEN, token);
        values.put(LOG_OUT, log_out);
        values.put(SET_UP_STATUS, "pending");

        database.insert(TABLE_NAME, null, values);

    }


    public void insert(String insertQuery) {

        database.execSQL(insertQuery);
    }

    public void createUserTable(){

        Log.d("LOG", CREATE_USER_TABLE);
        database.execSQL(CREATE_USER_TABLE);
    }

    public Cursor fetch(String query) {

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor FetchMyOwnID(String query){

        Cursor cursor = database.rawQuery(query, null);

        return cursor;
    }

    public void update(String query) {

        database.execSQL(query);
    }

    public void delete(String query) {

        database.execSQL(query);
    }
}
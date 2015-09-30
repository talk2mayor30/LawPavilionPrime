package git.lawpavilionprime.SetUp;

/**
 * Created by don_mayor on 7/29/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ModuleDBAdapter {

    public static final String TABLE_NAME = "modules";
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String SIZE = "size";
    public static final String MODULE_ID = "module_id";
    public static final String URL = "module_url";
    public static final String DATE = "date";

    public static String DROP_MODULE_TABLE = "DROP TABLE  IF EXISTS "+TABLE_NAME ;

    public static String CREATE_MODULE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            +   "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            MODULE_ID + " INTEGER  NOT NULL,\n"+
            NAME + " VARCHAR(40)  NOT NULL,\n" +
            SIZE + " VARCHAR(40)  NOT NULL,\n" +
            URL + " VARCHAR(40) NOT NULL,\n"+
            DATE + " VARCHAR(40) NOT NULL,\n"+
            STATUS + " VARCHAR(40)  NOT NULL);";

    private Context mContext;

    private SQLiteDatabase database;

    public ModuleDBAdapter(Context c) {
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

    public void insert(int module_id, String name, String status, String size, String url, String date){

        ContentValues values = new ContentValues();
        values.put(MODULE_ID, module_id);
        values.put(NAME, name);
        values.put(STATUS, status);
        values.put(SIZE, size);
        values.put(URL, url);
        values.put(DATE, date);

        database.insert(TABLE_NAME, null, values);

    }

    public void createModuleTable(){

        database.execSQL(CREATE_MODULE_TABLE);
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
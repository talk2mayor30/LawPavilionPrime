package git.lawpavilionprime.contentUpdate;

/**
 * Created by don_mayor on 7/9/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {

    public static final String TABLE_NAME = "Customer";
    // Table columns
    public static final String _ID = "CustomerId";

    public static final String FIRST_NAME = "FirstName";
    public static final String TOKEN = "token";

    // Database Information
    static final String DB_NAME = "falcon";

    // database version
    static final int DB_VERSION = 1;

    public static String DROP_TABLE = "DROP TABLE IF EXISTS Customer;";

    public static String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            +   "( CustomerId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    FirstName VARCHAR(40)  NOT NULL,\n" +
            "    LastName VARCHAR(20)  NOT NULL,\n" +
            "    Company VARCHAR(80),\n" +
            "    Address VARCHAR(70),\n" +
            "    City VARCHAR(40),\n" +
            "    State VARCHAR(40),\n" +
            "    Country VARCHAR(40),\n" +
            "    PostalCode VARCHAR(10),\n" +
            "    Phone VARCHAR(24),\n" +
            "    Fax VARCHAR(24),\n" +
            "    Email VARCHAR(60)  NOT NULL,\n" +
            "    SupportRepId INTEGER);";

    private Context mContext;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context c) {
        mContext = c;
    }

    public SQLiteDatabase open(String DB_NAME) throws SQLException {

        database = mContext.openOrCreateDatabase(DB_NAME , mContext.MODE_PRIVATE, null);

        return database;

    }

    public void close() {
        database.close();
    }

    public void insert(String insertQuery) {

        database.execSQL(insertQuery);
    }

    public void createTable(String query){

        database.execSQL(DROP_TABLE); // to test drop
        database.execSQL(query);
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
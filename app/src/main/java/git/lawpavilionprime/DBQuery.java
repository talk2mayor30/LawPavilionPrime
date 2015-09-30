package git.lawpavilionprime;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by don_mayor on 6/29/2015.
 */
public class DBQuery {

    public static final String BOOKS_LIST            =   "books_list";
    public static final String BOOK_CATEGORY_LIST   =   "book_category_list";
    public static final String BOOK_AUTHORS_LIST     =   "book_authors_list";
    public static final String RENTAL_RECORDS       =   "rental_records";
    public static final String RENT_INFORMATION     =   "rent_information";
    public static final String CUSTOMER =    "Customer";

    public static HashMap<String, String> hmTableCreate = new HashMap<String, String>();


    private static final String CREATE_BOOKS_LIST_TABLE = "CREATE TABLE IF NOT EXISTS " + BOOKS_LIST
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, book_title TEXT, book_subject TEXT, description TEXT," +
            "pages INTEGER, price TEXT, published_date TEXT, book_image TEXT, book_type TEXT, available_purchase_type TEXT," +
            "status TEXT, base_64_encoded_image BLOB, book_image_name TEXT,  deleted INTEGER, last_modified String);";

    private static final String CREATE_BOOK_CATEGORY_LIST_TABLE ="CREATE TABLE IF NOT EXISTS " + BOOK_CATEGORY_LIST
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT, description TEXT, status TEXT, deleted INTEGER," +
            "last_modified TEXT);";

    private static final String CREATE_BOOK_AUTHORS_LIST = "CREATE TABLE IF NOT EXISTS" + BOOK_AUTHORS_LIST
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, author TEXT, author_bio TEXT, image_source TEXT, deleted INTEGER, " +
            "last_modified TEXT);";

    private static final String CREATE_RENTAL_RECORDS = "CREATE TABLE IF NOT EXISTS" + RENTAL_RECORDS
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, book_title TEXT, book_subject TEXT, book_image TEXT, book_price TEXT, " +
            "customer_name TEXT, customer_email TEXT, purchase_type TEXT, expiry_date TEXT, current_status TEXT, deleted INTEGER," +
            "last_modified TEXT);";

    private static final String CREATE_RENT_INFORMATION = "CREATE TABLE IF NOT EXISTS" + RENT_INFORMATION
            + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, book_title TEXT, book_subject TEXT, rent_plan_name TEXT, price TEXT, status TEXT" +
            "deleted INTEGER, last_modified TEXT);";

    private static String TEST_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + CUSTOMER
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

    public SQLiteDatabase mDb;

    public DBQuery(SQLiteDatabase mDb){

        this.mDb = mDb;
    }
    public DBQuery(){

        hmTableCreate.put(BOOKS_LIST, CREATE_BOOKS_LIST_TABLE);
        hmTableCreate.put(RENT_INFORMATION, CREATE_RENT_INFORMATION);
        hmTableCreate.put(RENTAL_RECORDS, CREATE_RENTAL_RECORDS);
        hmTableCreate.put(BOOK_AUTHORS_LIST, CREATE_BOOK_AUTHORS_LIST);
        hmTableCreate.put(BOOK_CATEGORY_LIST, CREATE_BOOK_CATEGORY_LIST_TABLE);
        hmTableCreate.put(BOOK_AUTHORS_LIST, CREATE_BOOK_AUTHORS_LIST);
        hmTableCreate.put(CUSTOMER, TEST_CREATE_TABLE);
    }


    public long createBooksList(String name, Integer deleted) {

        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("deleted", deleted);
        return mDb.insert(BOOKS_LIST, null, initialValues);
    }

    public void insertBooksList(String name, Integer deleted) {
        createBooksList(name, deleted);
    }

    public long createBookCategoryList(String name, Integer deleted) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("deleted", deleted);
        return mDb.insert(BOOKS_LIST, null, initialValues);
    }

    public void insertBookCategoryList(String name, Integer deleted) {

        createBookCategoryList(name, deleted);
    }

    public long createBookAuthorsList(String name, Integer deleted) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("deleted", deleted);
        return mDb.insert(BOOKS_LIST, null, initialValues);
    }

    public void insertBookAuthorsList(String name, Integer deleted) {

        createBookAuthorsList(name, deleted);
    }

    public long createRentalRecord(String name, Integer deleted) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("deleted", deleted);
        return mDb.insert(BOOKS_LIST, null, initialValues);
    }

    public void insertRentalRecord(String name, Integer deleted) {

        createRentalRecord(name, deleted);
    }

    public long createRentInformation(String name, Integer deleted) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("name", name);
        initialValues.put("deleted", deleted);
        return mDb.insert(BOOKS_LIST, null, initialValues);
    }

    public void insertRentInformation(String name, Integer deleted) {

        createRentInformation(name, deleted);
    }

}

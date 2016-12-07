package cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import models.Config;
import models.Favourite;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "favouritesManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "favourites";
    private static final String TABLE_CONFIG = "settings";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_ADDR = "address";
    private static final String KEY_PLACEID = "place_id";

    //Config table key fields
    private static final String KEY_DISTANCE_UNIT = "distance_unit";
    private static final String KEY_TEMP_UNIT = "temperature_unit";
    private static final String KEY_ENABLE_NOTIFICATIONS = "enable_notifications";
    private static final String KEY_LAST_OFFER_ID = "last_offer_id";

    private static final String CREATE_CONFIG_TABLE = "CREATE TABLE " + TABLE_CONFIG + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DISTANCE_UNIT + " TEXT," + KEY_TEMP_UNIT + " TEXT" + ", " + KEY_ENABLE_NOTIFICATIONS +" TEXT" + ", " + KEY_LAST_OFFER_ID +" TEXT)";

    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_PH_NO + " TEXT,"
            + KEY_LAT + " TEXT,"
            + KEY_LONG + " TEXT,"
            + KEY_ADDR + " TEXT,"
            + KEY_PLACEID + " TEXT" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONFIG_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIG);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addFavourite(Favourite favourite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, favourite.get_name()); // Contact Name
        values.put(KEY_PH_NO, favourite.get_phone_number()); // Contact Phone
        values.put(KEY_LAT, favourite.get_latitude()); // Contact Phone
        values.put(KEY_LONG, favourite.get_longitude()); // Contact Phone
        values.put(KEY_ADDR, favourite.get_address()); // Contact Phone
        values.put(KEY_PLACEID, favourite.get_placeId());

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Favourite getFavourite(String placeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Favourite favourite;

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,KEY_NAME, KEY_PH_NO, KEY_LAT, KEY_LONG, KEY_ADDR, KEY_PLACEID }, KEY_PLACEID + " = '" + placeId + "'", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            favourite = new Favourite(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

        } else {

            favourite = new Favourite();

        }

        return favourite;
    }

    // Getting All Contacts
    public List<Favourite> getAllFavourites() {
        List<Favourite> favouriteList = new ArrayList<Favourite>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Favourite favourite = new Favourite();
                favourite.set_id(Integer.parseInt(cursor.getString(0)));
                favourite.set_name(cursor.getString(1));
                favourite.set_phone_number(cursor.getString(2));
                favourite.set_latitude(cursor.getString(3));
                favourite.set_longitude(cursor.getString(4));
                favourite.set_address(cursor.getString(5));
                favourite.set_placeId(cursor.getString(6));
                // Adding contact to list
                favouriteList.add(favourite);
            } while (cursor.moveToNext());
        }

        // return contact list
        return favouriteList;
    }

    // Updating single contact
    public int updateFavourite(Favourite favourite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, favourite.get_name());
        values.put(KEY_PH_NO, favourite.get_phone_number());
        values.put(KEY_LAT, favourite.get_latitude());
        values.put(KEY_LONG, favourite.get_longitude());
        values.put(KEY_ADDR, favourite.get_address());
        values.put(KEY_PLACEID, favourite.get_placeId());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(favourite.get_id()) });
    }

    // Deleting single contact
    public void deleteFavourite(String placeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_PLACEID + " = '" + placeId + "'", null);
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    // Adding new config
    public void addConfig(Config config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISTANCE_UNIT, config.get_distance_unit());
        values.put(KEY_TEMP_UNIT, config.get_temperature_unit());

        // Inserting Row
        db.insert(TABLE_CONFIG, null, values);
        db.close(); // Closing database connection
    }

    public int updateConfig(Config config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISTANCE_UNIT, config.get_distance_unit());
        values.put(KEY_TEMP_UNIT, config.get_temperature_unit());

        return db.update(TABLE_CONFIG, values, KEY_ID + " = 1", null);
    }

    public int updateDistanceConfig(String configDistance) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DISTANCE_UNIT, configDistance);

        return db.update(TABLE_CONFIG, values, KEY_ID + " = 1", null);
    }

    public int updateTempConfig(String configTemp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TEMP_UNIT, configTemp);

        return db.update(TABLE_CONFIG, values, KEY_ID + " = 1", null);
    }

    public int updateNotificationConfig(String config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENABLE_NOTIFICATIONS, config);

        return db.update(TABLE_CONFIG, values, KEY_ID + " = 1", null);
    }

    public int setUpdateLastOfferIDConfig(String config) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAST_OFFER_ID, config);

        return db.update(TABLE_CONFIG, values, KEY_ID + " = 1", null);
    }

    // Getting single contact
    public Config getConfig() {
        SQLiteDatabase db = this.getReadableDatabase();
        Config config;

        Cursor cursor = db.query(TABLE_CONFIG, new String[] { KEY_ID, KEY_DISTANCE_UNIT, KEY_TEMP_UNIT, KEY_ENABLE_NOTIFICATIONS, KEY_LAST_OFFER_ID}, KEY_ID + " = 1", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //Log.d("Count config", cursor.getString(3));
            config = new Config(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        } else {

            config = new Config();

        }

        return config;
    }

}
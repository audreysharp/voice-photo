package com.example.audrey.voicephoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.R.attr.name;

/**
 * Created by audrey on 11/19/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private String TAG = this.getClass().getSimpleName();

    private static final String DATABASE_NAME = "PhotosDb";
    private static final int DATABASE_VERSION = 1;

    // TABLE NAMES
    private static final String TABLE_IMAGES = "photos";

    /* Keys for Table String */
    private static final String ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    // add key for general location?

    String CREATE_TABLE_CALL = "CREATE TABLE " + TABLE_IMAGES + "(" + ID + " INTEGER PRIMARY KEY," + KEY_PATH + " TEXT," + KEY_TAGS + " TEXT," + KEY_LAT + " TEXT," + KEY_LON
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    /* Method to add a photo */
    public long addPhoto(String path, String tags, String lat, String lon) {
        long c;

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PATH, path);
        values.put(KEY_TAGS, tags);
        values.put(KEY_LAT, lat);
        values.put(KEY_LON, lon);

        c = database.insert(TABLE_IMAGES, null, values);
        database.close();
        return c;
    }

    /* Get all image paths from DB */
    public ArrayList<String> getAllImages() {
        String query = "SELECT * FROM " + TABLE_IMAGES;
        ArrayList<String> imagePaths = new ArrayList<String>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(KEY_PATH));
                imagePaths.add(path);
            }
        }
        return imagePaths;
    }

    public ArrayList<String> getImageFromPath(String path) {
        String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE path = '" + path + "'";
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            ArrayList<String> result = new ArrayList<>();
            result.add(c.getString(c.getColumnIndex(KEY_TAGS)));
            result.add(c.getString(c.getColumnIndex(KEY_LAT)));
            result.add(c.getString(c.getColumnIndex(KEY_LON)));

            return result;
        } else {
            return null;
        }
    }

    /**
     * NOT DONE
     * Search for records by tag or location
    * */
    public String searchForImage(String search) {
        String query = "SELECT PATH FROM " + TABLE_IMAGES + " WHERE tags = " + search + " OR lat = " + search + " OR lon = " + search;
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            // insert for loop here
            String path = c.getString(c.getColumnIndex(KEY_PATH));
            return path;
        } else {
            return "NORESULTS";
        }
    }
}

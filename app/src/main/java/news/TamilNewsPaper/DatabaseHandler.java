package news.TamilNewsPaper;

/**
 * Created by Elcot on 12/14/2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHandler extends SQLiteOpenHelper  {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "favourateLinkDB";
    private static final String TABLE_FavourateLinks = "favourateLinks";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "linkName";
    private static final String KEY_Url = "linkUrl";
    private static final String ISLite = "ISLite";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FavourateLinks + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_Url + " TEXT unique,"
                + ISLite + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FavourateLinks);

        // Create tables again
        onCreate(db);
    }


    // code to add the new contact
    void addContact(FavourateLinksSqlLiteTable favourateLinksSqlLiteTable) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, favourateLinksSqlLiteTable.getLinkName()); // Contact Name
        values.put(KEY_Url, favourateLinksSqlLiteTable.getLinkUrl()); // Contact Phone
        values.put(ISLite, favourateLinksSqlLiteTable.getIsLite()); // Contact Phone

        // Inserting Row
       db.insert(TABLE_FavourateLinks, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all contacts in a list view
    public List<FavourateLinksSqlLiteTable> getAllFavourateLinks() {
        List<FavourateLinksSqlLiteTable> favourateLinksList = new ArrayList<FavourateLinksSqlLiteTable>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FavourateLinks;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FavourateLinksSqlLiteTable favourateLinksSqlLiteTable = new FavourateLinksSqlLiteTable();
                favourateLinksSqlLiteTable.setId(Integer.parseInt(cursor.getString(0)));
                favourateLinksSqlLiteTable.setLinkName(cursor.getString(1));
                favourateLinksSqlLiteTable.setLinkUrl(cursor.getString(2));
                favourateLinksSqlLiteTable.setIsLite(cursor.getString(3));
                // Adding contact to list
                favourateLinksList.add(favourateLinksSqlLiteTable);
            } while (cursor.moveToNext());
        }

        // return contact list
        return favourateLinksList;
    }

    public List<FavourateLinksSqlLiteTable> getTop3FavourateLinks() {
        List<FavourateLinksSqlLiteTable> favourateLinksList = new ArrayList<FavourateLinksSqlLiteTable>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FavourateLinks + " order by " + KEY_ID + " DESC limit 4";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FavourateLinksSqlLiteTable favourateLinksSqlLiteTable = new FavourateLinksSqlLiteTable();
                favourateLinksSqlLiteTable.setId(Integer.parseInt(cursor.getString(0)));
                favourateLinksSqlLiteTable.setLinkName(cursor.getString(1));
                favourateLinksSqlLiteTable.setLinkUrl(cursor.getString(2));
                favourateLinksSqlLiteTable.setIsLite(cursor.getString(3));

                // Adding contact to list
                favourateLinksList.add(favourateLinksSqlLiteTable);
            } while (cursor.moveToNext());
        }

        // return contact list
        return favourateLinksList;
    }

    public void deleteAllExceptTop3() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FavourateLinks,
                "ROWID NOT IN (SELECT ROWID FROM " + TABLE_FavourateLinks + " ORDER BY " + KEY_ID + " DESC LIMIT 4)",
                null);
    }

}

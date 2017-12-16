package tmm.tracki5.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arun on 19/02/16.
 */
public class TrackAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static TrackAccess instance;

    public static final String USER_TABLE_NAME = "user_track";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_PHONE_NUMBER = "phone_number";
    public static final String USER_SESSION_API = "session_api";
    public static final String USER_GENDER = "gender";
    public static final String USER_DOB = "dob";

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private TrackAccess(Context context) {
        this.openHelper = new TrackHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static TrackAccess getInstance(Context context) {
        if (instance == null) {
            instance = new TrackAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }


    public boolean insertUser  (String session, String phone, String fName, String lName, String gen, String age)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_SESSION_API, session);
        contentValues.put(USER_PHONE_NUMBER, phone);
        contentValues.put(USER_FIRST_NAME, fName);
        contentValues.put(USER_LAST_NAME, lName);
        contentValues.put(USER_GENDER, gen);
        contentValues.put(USER_DOB, age);
        db.insert(USER_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getSessionPH(int id){
        Cursor res =  db.rawQuery( "select * from "+USER_TABLE_NAME +" where "+USER_COLUMN_ID+" ="+id+"", null );
        return res;
    }

   /* public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    } */

    public boolean updateUser (Integer id, String session, String phone, String fName, String lName, String gen, String age)
    {
        if(getSessionPH(id) != null && getSessionPH(id).moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_SESSION_API, session);
            contentValues.put(USER_PHONE_NUMBER, phone);
            contentValues.put(USER_FIRST_NAME, fName);
            contentValues.put(USER_LAST_NAME, lName);
            contentValues.put(USER_GENDER, gen);
            contentValues.put(USER_DOB, age);
            db.update(USER_TABLE_NAME, contentValues, "user_id = ? ", new String[]{Integer.toString(id)});
            return true;
        }else{
            boolean bool = insertUser(session, phone, fName, lName, gen, age);
            return bool;
        }
    }

    public Integer deleteUser
            (Integer id)
    {
        return db.delete(USER_TABLE_NAME,
                "user_id = ? ",
                new String[] { Integer.toString(id) });
    }

   /* public ArrayList<String> getAllCotacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    } */
}

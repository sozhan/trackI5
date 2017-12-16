package tmm.tracki5.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arun on 19/02/16.
 */
public class TrackHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "track_tmm.db";
    private static final int DATABASE_VERSION = 1;

    public TrackHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE user_track (" +
                        "user_id integer PRIMARY KEY AUTOINCREMENT," +
                        "session_api text," +
                        "phone_number text," +
                        "first_name text," +
                        "last_name text," + "email text,"+
                        "gender text," +
                        "dob text" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }
}

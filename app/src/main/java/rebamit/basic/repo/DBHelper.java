package rebamit.basic.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import rebamit.basic.object.Outfit;

/**
 * Created by rebeccatu on 5/25/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "DBHelper";

    // Database Name
    private static final String DATABASE_NAME = "Basic.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create database table");

        //All necessary tables you like to create will create here
        String CREATE_TABLE_OUTFIT = "CREATE TABLE " + Outfit.TABLE + "( "
                + Outfit.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Outfit.KEY_PICTURE + " TEXT, "
                + Outfit.KEY_TAG + " TEXT, "
                + Outfit.KEY_DATE + " TEXT, "
                + Outfit.KEY_GEOTAG + " TEXT )";

        db.execSQL(CREATE_TABLE_OUTFIT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Outfit.TABLE);

        // Create tables again
        onCreate(db);
    }
}


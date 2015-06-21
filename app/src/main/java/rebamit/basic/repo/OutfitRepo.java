package rebamit.basic.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rebamit.basic.object.Outfit;

/**
 * Created by rebeccatu on 5/25/15.
 */
public class OutfitRepo {
    private DBHelper dbHelper;

    /**
     * Constructor
     *
     * @param context context
     */
    public OutfitRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Inserts outfits into database
     *
     * @param o
     * @return int outfitID
     */
    public int insert(Outfit o) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Outfit.KEY_PICTURE, o.picture);
        values.put(Outfit.KEY_TAG, o.tag);
        values.put(Outfit.KEY_DATE, o.date.getTime());
        values.put(Outfit.KEY_GEOTAG, o.geotag);

        // Inserting Row
        long outfitID = db.insert(Outfit.TABLE, null, values);

        db.close();

        return (int) outfitID;
    }

    /**
     * Deletes outfit from database
     *
     * @param outfitID outfitID
     */
    public void delete(int outfitID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Outfit.TABLE, Outfit.KEY_ID + " = ?", new String[]{String.valueOf(outfitID)});
        db.close();
    }

    /**
     * Updates outfits in database
     *
     * @param o outfit
     */
    public void update(Outfit o) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Outfit.KEY_PICTURE, o.picture);
        values.put(Outfit.KEY_TAG, o.tag);
        values.put(Outfit.KEY_ID, o.id);
        values.put(Outfit.KEY_DATE, o.date.getTime());
        values.put(Outfit.KEY_GEOTAG, o.geotag);

        db.update(Outfit.TABLE, values, Outfit.KEY_ID + " = ?", new String[]{String.valueOf(o.id)});

        // Closing database connection
        db.close();
    }

    /**
     * Retrieves all outfits from the database
     *
     * @return ArrayList<outfit>
     */
    public ArrayList<Outfit> getAllOutfit() {

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Outfit.TABLE;

        ArrayList<Outfit> outfitList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Outfit o = new Outfit();
                o.id = cursor.getLong(cursor.getColumnIndex(Outfit.KEY_ID));
                o.picture = cursor.getString(cursor.getColumnIndex(Outfit.KEY_PICTURE));
                o.tag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_TAG));
                o.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Outfit.KEY_DATE))));
                o.geotag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_GEOTAG));
                outfitList.add(o);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return outfitList;
    }

    /**
     * Get a outfit from the database using its message
     *
     * @param outfitTag
     * @return ArrayList<outfit>
     */
    public ArrayList<Outfit> getOutfitByTag(String outfitTag) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Outfit.TABLE +
                " WHERE lower(" + Outfit.KEY_TAG + ") LIKE ?";

        Log.e("sql", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, new String[]{'%' + outfitTag.toLowerCase() + '%'});

        ArrayList<Outfit> outfitList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Outfit o = new Outfit();
                o.id = cursor.getLong(cursor.getColumnIndex(Outfit.KEY_ID));
                o.picture = cursor.getString(cursor.getColumnIndex(Outfit.KEY_PICTURE));
                o.tag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_TAG));
                o.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Outfit.KEY_DATE))));
                o.geotag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_GEOTAG));
                outfitList.add(o);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return outfitList;
    }

    public ArrayList<Outfit> getOutfitByDate(String outfitDate1, String outfitDate2) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Outfit.TABLE +
                " WHERE " + Outfit.KEY_DATE + " BETWEEN ? AND ?";

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(Calendar.YEAR, Integer.parseInt(outfitDate1.substring(0, 4)));
        start.set(Calendar.MONTH, Integer.parseInt(outfitDate1.substring(5, 7)) - 1);
        start.set(Calendar.DATE, Integer.parseInt(outfitDate1.substring(8, 10)));
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end.set(Calendar.YEAR, Integer.parseInt(outfitDate2.substring(0, 4)));
        end.set(Calendar.MONTH, Integer.parseInt(outfitDate2.substring(5, 7)) - 1);
        end.set(Calendar.DATE, Integer.parseInt(outfitDate2.substring(8, 10)));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        String startTime = "" + start.getTime().getTime();
        String endTime = "" + end.getTime().getTime();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{startTime, endTime});

        ArrayList<Outfit> outfitList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Outfit o = new Outfit();
                o.id = cursor.getLong(cursor.getColumnIndex(Outfit.KEY_ID));
                o.tag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_TAG));
                o.picture = cursor.getString(cursor.getColumnIndex(Outfit.KEY_PICTURE));
                o.geotag = cursor.getString(cursor.getColumnIndex(Outfit.KEY_GEOTAG));
                o.date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Outfit.KEY_DATE))));
                outfitList.add(o);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return outfitList;
    }
}

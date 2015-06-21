package rebamit.basic.object;

import java.util.Date;

/**
 * Created by rebeccatu on 5/25/15.
 */
public class Outfit {
    //table name
    public static final String TABLE = "Outfit";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_TAG = "tag";
    public static final String KEY_DATE = "date";
    public static final String KEY_GEOTAG = "geotag";

    // property help us to keep data
    public long id;
    public String picture;
    public String tag;
    public Date date;
    public String geotag;

    public Outfit() {
    }


    public Outfit(long id, String tag, Date date, String picture, String geotag) {
        this.id = id;
        this.date = date;
        this.picture = picture;
        this.tag = tag;
        this.geotag= geotag;
    }
}

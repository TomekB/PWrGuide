package pl.pwr.guide.exterior;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PoiDatabaseHelper extends SQLiteOpenHelper{

	private static final String TAG = "PoiProvider";
	private static final String TABLE = "poi";

	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LON = "longitude";
	public static final String KEY_SHORT = "shortDescription";
	public static final String KEY_DESC = "description";
	public static final String KEY_LINK = "link";
	public static final String KEY_CATEGORY = "category";

	private static final String DATABASE_CREATE = "create table " + TABLE
			+ " (" + KEY_ID + " integer primary key autoincrement, " + KEY_NAME + " TEXT, " + KEY_LAT
			+ " LONG, " + KEY_LON + " LONG, " + KEY_SHORT + " TEXT, " + KEY_DESC + " TEXT, "
			+ KEY_LINK + " TEXT, " + KEY_CATEGORY + " INTEGER);";

	public PoiDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}

}

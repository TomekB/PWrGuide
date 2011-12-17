package pl.pwr.guide.exterior.providers;

import pl.pwr.guide.exterior.helpers.PoiDatabaseHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TripProvider extends ContentProvider{

	public static String AUTHORITY = "pl.pwr.guide.exterior.provider.guide.trips";
	
	public static final Uri CONTENT_URI = Uri
			.parse("content://"+AUTHORITY+"/trip");
	
	private static final int ITEM = 1;
	private static final int ITEM_ID = 2;

	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_POIS = "pois";
	
	private SQLiteDatabase database;

	private static final String ITEM_TABLE = "trips";
	private static final String _NAME = "database.db";
	private static final int _VERSION = 1;
		
	public static final int ID_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int POIS_COLUMN = 2;


	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "item", ITEM);
		uriMatcher.addURI(AUTHORITY, "item/#", ITEM_ID);
	}
	
	@Override
	public boolean onCreate() {
		Context context = getContext();

		PoiDatabaseHelper dbHelper;
		dbHelper = new PoiDatabaseHelper(context, _NAME, null,
				_VERSION);
		database = dbHelper.getWritableDatabase();
		return (database == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ITEM:
			return "vnd.android.cursor.dir/vnd.pwr.guide.trips";
		case ITEM_ID:
			return "vnd.android.cursor.item/vnd.pwr.guide.trips";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ITEM_TABLE);

		switch (uriMatcher.match(uri)) {
		case ITEM_ID:
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}

		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_NAME;
		} else {
			orderBy = sort;
		}

		Cursor c = qb.query(database, projection, selection, selectionArgs,
				null, null, orderBy + " DESC");

		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {

		long rowID = database.insert(ITEM_TABLE, "trips", _initialValues);

		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			count = database.delete(ITEM_TABLE, where, whereArgs);
			break;

		case ITEM_ID:
			String segment = uri.getPathSegments().get(1);
			count = database.delete(ITEM_TABLE,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case ITEM:
			count = database.update(ITEM_TABLE, values, where, whereArgs);
			break;

		case ITEM_ID:
			String segment = uri.getPathSegments().get(1);
			count = database.update(ITEM_TABLE, values,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}

package org.bwgz.quotation.content.provider;

import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class QuotationContentProvider extends ContentProvider {
	static private String TAG = QuotationContentProvider.class.getSimpleName();

	static public String METHOD_GETQUOTATION = "GETQUOTATION";
	static public String METHOD_GETAUTHOR = "GETAUTHOR";
	
	private QuotationSQLiteHelper quotationSQLiteHelper;

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		quotationSQLiteHelper = new QuotationSQLiteHelper(getContext(), Quotation.TABLE, null, 1);

        return true;
	}

	private boolean startsWith(Uri uri, Uri with) {
		return uri.getPath().startsWith(with.getPath());
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, String.format("query - uri: %s  projection: %s  selection: %s  selectionArgs: %s  String sortOrder: %s", uri, projection, selection, selectionArgs, sortOrder));
		Cursor cursor = null;
		
		if (startsWith(uri, Quotation.CONTENT_URI)) {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		    queryBuilder.setTables(Quotation.TABLE);

			if (!uri.equals(Quotation.CONTENT_URI)) {
		    	String _id = uri.getPath().substring(Quotation.CONTENT_URI.getPath().length(), uri.getPath().length());
				Log.d(TAG, String.format("_id: %s", _id));
		    	queryBuilder.appendWhere(Quotation._ID + "= '" + _id + "'");
		    }
		    
			SQLiteDatabase database = quotationSQLiteHelper.getWritableDatabase();
		    cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
		    cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}

	    return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, String.format("insert - uri: %s  values: %s", uri, values));
		
		if (startsWith(uri, Quotation.CONTENT_URI)) {
			SQLiteDatabase database = quotationSQLiteHelper.getWritableDatabase();
			long id = quotationSQLiteHelper.insert(database, Quotation.TABLE, values);
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, String.format("update - uri: %s  values: %s  selection: %s  selectionArgs: %s", uri, values, selection, selectionArgs));
		int id = -1;
		
		if (startsWith(uri, Quotation.CONTENT_URI)) {
			SQLiteDatabase database = quotationSQLiteHelper.getWritableDatabase();
			id = (int) quotationSQLiteHelper.update(database, Quotation.TABLE, values, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return id;
	}
}

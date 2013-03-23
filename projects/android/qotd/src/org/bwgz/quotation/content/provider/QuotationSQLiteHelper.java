package org.bwgz.quotation.content.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bwgz.qotd.R;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuotationSQLiteHelper extends SQLiteOpenHelper {
	static private String TAG = QuotationSQLiteHelper.class.getSimpleName();

	private Context context;
	
	private static final String QUOTATION_CREATE = 
			"create table quotation ("					+
			"_id string primary key,"					+
			"state integer not null,"					+
			"modified long not null,"					+
			"quotation text,"							+
			"author_name text,"							+
			"author_image text"							+
			");";	

	public QuotationSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version); 	 
		
		this.context = context;
	}

	private void initialize(SQLiteDatabase db) {
		InputStream ins = context.getResources().openRawResource(R.raw.quotations);

		CSVReader listReader = new CSVReader(new InputStreamReader(ins));

		String[] list;
		try {
			while((list = listReader.readNext()) != null ) {
			    db.execSQL(String.format("insert into quotation (_id, state, modified) values ('%s', %d, %d);", list[0], Quotation.State.UNINITIALIZED.getValue(), System.currentTimeMillis()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, String.format("onCreate - db: %s", db));
	    db.execSQL(QUOTATION_CREATE);
	    initialize(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, String.format("onUpgrade - db: %s  oldVersion: %d  newVersion: %s", db, oldVersion, newVersion));
	    db.execSQL("DROP TABLE IF EXISTS quotation");
	    onCreate(db);
	}
	
	public long insert(SQLiteDatabase db, String table, ContentValues values) {
		values.put(Quotation.STATE, Quotation.State.UNINITIALIZED.getValue());
		values.put(Quotation.MODIFIED, System.currentTimeMillis());
		return db.insert(table, null, values);
	}
	
	public long update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
		values.put(Quotation.STATE, Quotation.State.INITIALIZED.getValue());
		values.put(Quotation.MODIFIED, System.currentTimeMillis());
		return db.update(table, values, whereClause, whereArgs);
	}
}

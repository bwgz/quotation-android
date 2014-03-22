/*
 * Copyright (C) 2014 bwgz.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bwgz.quotation.content.provider;

import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Source;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuotationSQLiteHelper extends SQLiteOpenHelper {
	static private String TAG = QuotationSQLiteHelper.class.getSimpleName();
	
	private static final String QUOTATION_CREATE = 
			"CREATE TABLE " + Quotation.TABLE + " ("		+
			"modified LONG NOT NULL,"						+
			"evictable BOOLEAN NOT NULL DEFAULT 0,"			+
			"_id STRING PRIMARY KEY,"						+
			"quotation TEXT NOT NULL,"						+
			"language TEXT NOT NULL,"						+
			"spoken_by_character TEXT,"						+
			"source_id STRING REFERENCES source (_id),"		+
			"author_count LONG"								+
			");";	

	private static final String QUOTATION_PERSON_CREATE = 
			"CREATE TABLE " + QuotationPerson.TABLE + " ("		+
			"modified LONG NOT NULL,"							+
			"quotation_id STRING REFERENCES quotation (_id),"	+
			"person_id STRING REFERENCES person (_id),"			+
			"PRIMARY KEY (quotation_id, person_id)"				+
			");";	
	
	private static final String QUOTATION_SUBJECT_CREATE = 
			"CREATE TABLE " + QuotationSubject.TABLE + " ("		+
			"modified LONG NOT NULL,"							+
			"quotation_id STRING REFERENCES quotation (_id),"	+
			"subject_id STRING REFERENCES subject (_id),"		+
			"PRIMARY KEY (quotation_id, subject_id)"			+
			");";	
	
	private static final String BOOKMARK_QUOTATION = 
			"CREATE TABLE " + BookmarkQuotation.TABLE + " ("			+
			BookmarkQuotation.MODIFIED + " LONG NOT NULL,"				+
			BookmarkQuotation.BOOKMARK_ID + " STRING PRIMARY KEY"		+
			");";	

	private static final String PERSON_CREATE = 
			"CREATE TABLE " + Person.TABLE + " ("		+
			Person.MODIFIED + " LONG NOT NULL,"					+
			Person.EVICTABLE + " BOOLEAN NOT NULL DEFAULT 0,"	+
			Person._ID + " STRING PRIMARY KEY,"					+
			Person.NAME + " TEXT,"								+
			Person.DESCRIPTION + " TEXT,"						+
			Person.NOTABLE_FOR + " TEXT,"						+
			Person.LANGUAGE + " TEXT,"							+
			Person.CITATION_PROVIDER + " TEXT,"					+
			Person.CITATION_STATEMENT + " TEXT,"				+
			Person.CITATION_URI + " TEXT,"						+
			Person.IMAGE_ID + " TEXT,"							+
			Person.QUOTATION_COUNT + " LONG"					+
			");";	

	private static final String BOOKMARK_PERSON = 
			"CREATE TABLE " + BookmarkPerson.TABLE + " ("			+
			BookmarkPerson.MODIFIED + " LONG NOT NULL,"				+
			BookmarkPerson.BOOKMARK_ID + " STRING PRIMARY KEY"		+
			");";	

	private static final String SUBJECT_CREATE = 
			"CREATE TABLE " + Subject.TABLE + " ("				+
			Subject.MODIFIED + " LONG NOT NULL,"				+
			Subject.EVICTABLE + " BOOLEAN NOT NULL DEFAULT 0,"	+
			Subject._ID + " STRING PRIMARY KEY,"				+
			Subject.NAME + " TEXT,"								+
			Subject.DESCRIPTION + " TEXT,"						+
			Subject.LANGUAGE + " TEXT,"							+
			Subject.IMAGE_ID + " TEXT,"							+
			Subject.QUOTATION_COUNT + " LONG"					+
			");";	
	
	private static final String BOOKMARK_SUBJECT = 
			"CREATE TABLE " + BookmarkSubject.TABLE + " ("			+
			BookmarkSubject.MODIFIED + " LONG NOT NULL,"			+
			BookmarkSubject.BOOKMARK_ID + " STRING PRIMARY KEY"		+
			");";	

	private static final String SOURCE_CREATE = 
			"CREATE TABLE " + Source.TABLE + " ("		+
			"modified LONG NOT NULL,"					+
			"evictable BOOLEAN NOT NULL DEFAULT 0,"		+
			"_id STRING PRIMARY KEY,"					+
			"name TEXT NOT NULL,"						+
			"type TEXT NOT NULL"						+
			");";	

	private static final String PICK_QUOTATION_CREATE = 
			"CREATE TABLE " + PickQuotation.TABLE + " ("			+
			"modified LONG NOT NULL,"								+
			"pick_id STRING PRIMARY KEY REFERENCES quotation (_id)"	+
			");";	
	
	private static final String PICK_PERSON_CREATE = 
			"CREATE TABLE " + PickPerson.TABLE + " ("				+
			"modified LONG NOT NULL,"								+
			"pick_id STRING PRIMARY KEY REFERENCES person (_id)"	+
			");";	

	private static final String PICK_SUBJECT_CREATE = 
			"CREATE TABLE " + PickSubject.TABLE + " ("				+
			"modified LONG NOT NULL,"								+
			"pick_id STRING PRIMARY KEY REFERENCES subject (_id)"	+
			");";	

    static public final int DATABASE_VERSION = 3;

    public QuotationSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version); 	 
		Log.d(TAG, String.format("QuotationSQLiteHelper - context: %s  name: %s  factory: %s  version: %s", context, name, factory, version));
	}
	
	private final static String[] tables = {
		Quotation.TABLE,
		BookmarkQuotation.TABLE,
		Person.TABLE,
		BookmarkPerson.TABLE,
		QuotationPerson.TABLE,
		Subject.TABLE,
		BookmarkSubject.TABLE,
		QuotationSubject.TABLE,
		Source.TABLE,
		PickQuotation.TABLE,
		PickPerson.TABLE,
		PickSubject.TABLE
	};
	
	private final static String[] sqls = {
		QUOTATION_CREATE,
		BOOKMARK_QUOTATION,
		PERSON_CREATE,
		BOOKMARK_PERSON,
		QUOTATION_PERSON_CREATE,
		SUBJECT_CREATE,
		BOOKMARK_SUBJECT,
		QUOTATION_SUBJECT_CREATE,
		SOURCE_CREATE,
		PICK_QUOTATION_CREATE,
		PICK_PERSON_CREATE,
		PICK_SUBJECT_CREATE
	};

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, String.format("onCreate - db: %s", db));
		for (String sql : sqls) {
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, String.format("onUpgrade - db: %s  oldVersion: %d  newVersion: %s", db, oldVersion, newVersion));
		for (String table : tables) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		
	    onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.d(TAG, String.format("onOpen - db: %s", db));
		db.execSQL("PRAGMA foreign_keys=ON");
	}

	public long insert(SQLiteDatabase db, String table, ContentValues values) {
		values.put(QuotationContract.StatusColumns.MODIFIED, System.currentTimeMillis());
		return db.replace(table, null, values);
	}
	
	public long insert(SQLiteDatabase db, String table, ContentValues values, boolean evictable) {
		values.put(QuotationContract.CacheColumns.EVICTABLE, evictable);
		return insert(db, table, values);
	}
	
	public int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
		values.put(QuotationContract.StatusColumns.MODIFIED, System.currentTimeMillis());
		return db.update(table, values, whereClause, whereArgs);
	}
	
	public int delete(SQLiteDatabase db, String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}
}

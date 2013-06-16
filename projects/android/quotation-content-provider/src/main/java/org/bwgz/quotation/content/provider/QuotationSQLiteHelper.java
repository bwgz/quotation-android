/*
 * Copyright (C) 2013 bwgz.org
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bwgz.google.api.services.freebase.util.TopicUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Base64;
import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class QuotationSQLiteHelper extends SQLiteOpenHelper {
	static private String TAG = QuotationSQLiteHelper.class.getSimpleName();
	
	private static final String QUOTATION_CREATE = 
			"CREATE TABLE quotation ("					+
			"modified LONG NOT NULL,"					+
			"evictable BOOLEAN NOT NULL DEFAULT 0,"		+
			"_id STRING PRIMARY KEY,"					+
			"quotation TEXT NOT NULL,"					+
			"language TEXT NOT NULL"					+
			");";	

	private static final String PERSON_CREATE = 
			"CREATE TABLE person ("						+
			"modified LONG NOT NULL,"					+
			"evictable BOOLEAN NOT NULL DEFAULT 0,"		+
			"_id STRING PRIMARY KEY,"					+
			"name TEXT,"								+
			"description TEXT,"							+
			"language TEXT NOT NULL,"					+
			"citation_provider TEXT,"					+
			"citation_statement TEXT,"					+
			"citation_uri TEXT,"						+
			"image BLOB"								+
			");";	

	private static final String QUOTATION_PERSON_CREATE = 
			"CREATE TABLE quotation_person ("										+
			"modified LONG NOT NULL,"												+
			"quotation_id STRING REFERENCES quotation (_id) ON DELETE CASCADE,"		+
			"person_id STRING REFERENCES person (_id) ON DELETE CASCADE,"			+
			"PRIMARY KEY (quotation_id, person_id)"									+
			");";	
	
	public QuotationSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version); 	 
		Log.d(TAG, String.format("QuotationSQLiteHelper - context: %s  name: %s  factory: %s  version: %s", context, name, factory, version));
	}
	
	class InitializeDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Void> {
		private static final String COMMON_TYPE_DESCRIPTION		= "/common/topic/description";
		private static final String PEOPLE_PERSON_QUOTATIONS	= "/people/person/quotations";
		private static final String TYPE_OBJECT_NAME			= "/type/object/name";
	
		private void initialize(SQLiteDatabase db) {
			Log.d(TAG, String.format("initialize - db: %s", db));
		    JsonFactory jsonFactory = new JacksonFactory();
			ArrayMap<String, String> images = null;
			
			try {
				InputStream in = InitializeDatabaseTask.class.getResourceAsStream("/authors-image.json");
				JsonParser parser = jsonFactory.createJsonParser(in);
				images = parser.parse(ArrayMap.class, null);
				parser.close();
				in.close();
			} catch (FileNotFoundException e) {
	        	Log.e(TAG, "author images initialization file not found", e);
			} catch (IOException e) {
	        	Log.e(TAG, "author images initialization file read failed", e);
			}
	
			try {
				InputStream in = InitializeDatabaseTask.class.getResourceAsStream("/authors.json");
				JsonParser parser = jsonFactory.createJsonParser(in);
				TopicLookup[] persons = parser.parse(TopicLookup[].class, null);
				parser.close();
				in.close();
				
				for (TopicLookup person : persons) {
					SQLiteStatement statement;
					long rowId;
					
					String personId = person.getId();
					String name = TopicUtil.getFirstPropertyValue(person, TYPE_OBJECT_NAME).toString();
					String description = TopicUtil.getFirstPropertyValue(person, COMMON_TYPE_DESCRIPTION).toString();
					Citation citation = (Citation) TopicUtil.getPropertyValue(person, COMMON_TYPE_DESCRIPTION, 0, "citation");
					String language = (String) TopicUtil.getPropertyValue(person, COMMON_TYPE_DESCRIPTION, 0, "lang");
	
					byte[] image = null;
					if (images != null) {
						String string = images.get(personId);
						if (string != null) {
							image = Base64.decodeBase64(string);
						}
					}
	
					statement = db.compileStatement("insert into person (modified, _id, name, description, language, image, citation_provider, citation_statement, citation_uri) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
					statement.bindLong(1, System.currentTimeMillis());
					statement.bindString(2, personId);
					statement.bindString(3, name);
					statement.bindString(4, description);
					statement.bindString(5, language);
					statement.bindBlob(6, image);
					statement.bindString(7, citation != null ? citation.getProvider() : new String());
					statement.bindString(8, citation != null ? citation.getStatement() : new String());
					statement.bindString(9, citation != null ? citation.getUri() : new String());
					rowId = statement.executeInsert();
					statement.close();
					Log.d(TAG, String.format("rowId: %d  id: %s  name: %s", rowId, personId, name));
				
					List<TopicValue> quotaions = TopicUtil.getPropertyValues(person, PEOPLE_PERSON_QUOTATIONS);
					for (TopicValue quotation : quotaions) {
						String quotationId = TopicUtil.getPropertyValue(quotation, "id").toString();
						String text = TopicUtil.getPropertyValue(quotation, "text").toString();
						language = TopicUtil.getPropertyValue(quotation, "lang").toString();
						
						statement = db.compileStatement("insert into quotation (modified, _id, quotation, language) values (?, ?, ?, ?)");
						statement.bindLong(1, System.currentTimeMillis());
						statement.bindString(2, quotationId);
						statement.bindString(3, text);
						statement.bindString(4, language);
						rowId = statement.executeInsert();
						statement.close();
						Log.d(TAG, String.format("rowId: %d  id: %s  quotation: %s", rowId, quotationId, text));
						
						statement= db.compileStatement("insert into quotation_person (modified, quotation_id, person_id) values (?, ?, ?)");
						statement.bindLong(1, System.currentTimeMillis());
						statement.bindString(2, quotationId);
						statement.bindString(3, personId);
						rowId = statement.executeInsert();
						statement.close();
						Log.d(TAG, String.format("rowId: %d  quotation_id: %s  person_id: %s", rowId, quotationId, personId));
					}
				}
			} catch (FileNotFoundException e) {
	        	Log.e(TAG, "author initialization file not found", e);
			} catch (IOException e) {
	        	Log.e(TAG, "authors initialization file read failed", e);
			}
		}

		@Override
		protected Void doInBackground(SQLiteDatabase... params) {
			initialize(params[0]);
			return null;
		}
	}
	
	private final static String[] tables = {
		QuotationContract.Quotation.TABLE,
		QuotationContract.Person.TABLE,
		QuotationContract.QuotationPerson.TABLE,
	};
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, String.format("onCreate - db: %s", db));
	    db.execSQL(QUOTATION_CREATE);
	    db.execSQL(PERSON_CREATE);
	    db.execSQL(QUOTATION_PERSON_CREATE);

	    new InitializeDatabaseTask().execute(db);
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

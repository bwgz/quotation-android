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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PersonQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickColumns;
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
import org.bwgz.quotation.content.provider.QuotationContract.SubjectQuotation;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class QuotationContentProvider extends ContentProvider {
	static private String TAG = QuotationContentProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private QuotationSQLiteHelper quotationSQLiteHelper;
    
    public static final int QUOTATION				=  1;
    public static final int QUOTATION_ID			=  2;
    public static final int QUOTATION_PERSON		=  3;
    public static final int QUOTATION_PERSON_ID		=  4;
    public static final int QUOTATION_SUBJECT		=  5;
    public static final int QUOTATION_SUBJECT_ID	=  6;
    public static final int BOOKMARK_QUOTATION		=  7;
    public static final int BOOKMARK_QUOTATION_ID	=  8;
    
    public static final int PERSON					=  9;
    public static final int PERSON_ID				= 10;
    public static final int PERSON_QUOTATION		= 11;
    public static final int PERSON_QUOTATION_ID		= 12;
    public static final int BOOKMARK_PERSON			= 13;
    public static final int BOOKMARK_PERSON_ID		= 14;
   
    public static final int SUBJECT					= 15;
    public static final int SUBJECT_ID				= 16;
    public static final int SUBJECT_QUOTATION		= 17;
    public static final int SUBJECT_QUOTATION_ID	= 18;
    public static final int BOOKMARK_SUBJECT		= 19;
    public static final int BOOKMARK_SUBJECT_ID		= 20;

    public static final int SOURCE					= 21;
    public static final int SOURCE_ID				= 22;

    public static final int PICK_QUOTATION			= 23;
    public static final int PICK_QUOTATION_ID		= 24;
    public static final int PICK_PERSON				= 25;
    public static final int PICK_PERSON_ID			= 26;
    public static final int PICK_SUBJECT			= 27;
    public static final int PICK_SUBJECT_ID			= 28;
    
    static {
        uriMatcher.addURI(QuotationContract.AUTHORITY, Quotation.SEGMENT, QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, Quotation.SEGMENT + "/m/*", QUOTATION_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, QuotationPerson.SEGMENT, QUOTATION_PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, QuotationPerson.SEGMENT + "/m/*", QUOTATION_PERSON_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, QuotationSubject.SEGMENT, QUOTATION_SUBJECT);
        uriMatcher.addURI(QuotationContract.AUTHORITY, QuotationSubject.SEGMENT + "/m/*", QUOTATION_SUBJECT_ID);
        
        uriMatcher.addURI(QuotationContract.AUTHORITY, Person.SEGMENT, PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, Person.SEGMENT + "/m/*", PERSON_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PersonQuotation.SEGMENT, PERSON_QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PersonQuotation.SEGMENT + "/m/*", PERSON_QUOTATION_ID);
       
        uriMatcher.addURI(QuotationContract.AUTHORITY, Subject.SEGMENT, SUBJECT);
        uriMatcher.addURI(QuotationContract.AUTHORITY, Subject.SEGMENT + "/m/*", SUBJECT_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, SubjectQuotation.SEGMENT, SUBJECT_QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, SubjectQuotation.SEGMENT + "/m/*", SUBJECT_QUOTATION_ID);
         
        uriMatcher.addURI(QuotationContract.AUTHORITY, Source.SEGMENT, SOURCE);
        uriMatcher.addURI(QuotationContract.AUTHORITY, Source.SEGMENT + "/m/*", SOURCE_ID);
       
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkQuotation.SEGMENT, BOOKMARK_QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkQuotation.SEGMENT + "/m/*", BOOKMARK_QUOTATION_ID);
        
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkPerson.SEGMENT, BOOKMARK_PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkPerson.SEGMENT + "/m/*", BOOKMARK_PERSON_ID);
        
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkSubject.SEGMENT, BOOKMARK_SUBJECT);
        uriMatcher.addURI(QuotationContract.AUTHORITY, BookmarkSubject.SEGMENT + "/m/*", BOOKMARK_SUBJECT_ID);

        uriMatcher.addURI(QuotationContract.AUTHORITY, PickQuotation.SEGMENT, PICK_QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PickQuotation.SEGMENT + "/m/*", PICK_QUOTATION_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PickPerson.SEGMENT, PICK_PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PickPerson.SEGMENT + "/m/*", PICK_PERSON_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PickSubject.SEGMENT, PICK_SUBJECT);
        uriMatcher.addURI(QuotationContract.AUTHORITY, PickSubject.SEGMENT + "/m/*", PICK_SUBJECT_ID);
    }

    public static UriMatcher getUriMatcher() {
    	return uriMatcher;
    }
    
    @Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		quotationSQLiteHelper = new QuotationSQLiteHelper(getContext(), QuotationContract.DATABASE, null, QuotationSQLiteHelper.DATABASE_VERSION);
		
        return true;
	}
	
	@Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri))
        {
            case QUOTATION:
                return Quotation.CONTENT_TYPE;
            case QUOTATION_ID:
                return Quotation.CONTENT_ITEM_TYPE;

            default:
                return null;
        }
	}
	
	private String getTables(Uri uri) {
		String tables = null;
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION:
            case QUOTATION_ID:
            	tables = Quotation.TABLE;
            	break;
            case QUOTATION_PERSON:
            case QUOTATION_PERSON_ID:
            	tables = QuotationPerson.TABLE;
            	break;
            case QUOTATION_SUBJECT:
            case QUOTATION_SUBJECT_ID:
            	tables = QuotationSubject.TABLE;
            	break;
            case BOOKMARK_QUOTATION:
            case BOOKMARK_QUOTATION_ID:
            	tables = BookmarkQuotation.TABLE;
            	break;
            	
            case PERSON:
            case PERSON_ID:
            	tables = Person.TABLE;
            	break;
            case PERSON_QUOTATION:
            case PERSON_QUOTATION_ID:
             	tables = PersonQuotation.TABLE;
            	break;
            case BOOKMARK_PERSON:
            case BOOKMARK_PERSON_ID:
            	tables = BookmarkPerson.TABLE;
            	break;
           	
            case SUBJECT:
            case SUBJECT_ID:
            	tables = Subject.TABLE;
            	break;
            case SUBJECT_QUOTATION:
            case SUBJECT_QUOTATION_ID:
            	tables = SubjectQuotation.TABLE;
            	break;
            case BOOKMARK_SUBJECT:
            case BOOKMARK_SUBJECT_ID:
            	tables = BookmarkSubject.TABLE;
            	break;
            	
            case SOURCE:
            case SOURCE_ID:
            	tables = Source.TABLE;
            	break;
            	
            case PICK_QUOTATION:
            case PICK_QUOTATION_ID:
            	tables = PickQuotation.TABLE;
            	break;
            case PICK_PERSON:
            case PICK_PERSON_ID:
            	tables = PickPerson.TABLE;
            	break;
            case PICK_SUBJECT:
            case PICK_SUBJECT_ID:
            	tables = PickSubject.TABLE;
            	break;
       }

        return tables;
	}
	
	private String getSelection(Uri uri) {
		String selection = null;
		String column = null;
		String value = null;
		
        switch (uriMatcher.match(uri)) {
            case QUOTATION_ID:
            	column = Quotation.FULL_ID;
            	value = Quotation.getId(uri);
            	break;
            case QUOTATION_PERSON_ID:
            	column = QuotationPerson.QUOTATION_ID;
            	value = QuotationPerson.getId(uri);
            	break;
            case QUOTATION_SUBJECT_ID:
            	column = QuotationSubject.QUOTATION_ID;
            	value = QuotationSubject.getId(uri);
            	break;
            case BOOKMARK_QUOTATION_ID:
            	column = BookmarkQuotation.BOOKMARK_ID;
            	value = BookmarkQuotation.getId(uri);
            	break;
            case PERSON_ID:
            	column = Person._ID;
            	value = Person.getId(uri);
            	break;
            case PERSON_QUOTATION_ID:
            	column = PersonQuotation.PERSON_ID;
            	value = PersonQuotation.getId(uri);
            	break;
            case BOOKMARK_PERSON_ID:
            	column = BookmarkPerson.BOOKMARK_ID;
            	value = BookmarkPerson.getId(uri);
            	break;
            case SUBJECT_ID:
            	column = Subject._ID;
            	value = Subject.getId(uri);
            	break;
            case SUBJECT_QUOTATION_ID:
            	column = SubjectQuotation.SUBJECT_ID;
            	value = SubjectQuotation.getId(uri);
        	    break;
            case BOOKMARK_SUBJECT_ID:
            	column = BookmarkSubject.BOOKMARK_ID;
            	value = BookmarkSubject.getId(uri);
            	break;
            case SOURCE_ID:
            	column = Source._ID;
            	value = Source.getId(uri);
            	break;
            case PICK_QUOTATION_ID:
            	column = PickQuotation.PICK_ID;
            	value = PickQuotation.getId(uri);
            	break;
            case PICK_PERSON_ID:
            	column = PickPerson.PICK_ID;
            	value = PickPerson.getId(uri);
            	break;
            case PICK_SUBJECT_ID:
            	column = PickSubject.PICK_ID;
            	value = PickSubject.getId(uri);
            	break;
        }
        
        if (column != null && value != null) {
        	selection = String.format("%s = '%s'", column, value);
        }
		
        return selection;
	}
	
	private boolean doSync(Uri uri) {
		boolean result = false;
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
            case QUOTATION_PERSON_ID:
            case QUOTATION_SUBJECT_ID:
            case PERSON_ID:
            case PERSON_QUOTATION_ID:
            case SUBJECT_ID:
            case SUBJECT_QUOTATION_ID:
            case SOURCE_ID:
            case PICK_QUOTATION_ID:
            case PICK_PERSON_ID:
            case PICK_SUBJECT_ID:
            	result = true;
            	break;
        }

        return result;
	}
	
	private boolean isPick(Uri uri, String id) {
		boolean result;
		
		String selection = String.format("%s = ?", PickColumns.PICK_ID);
		Cursor cursor = query(uri, new String[] { PickColumns.PICK_ID }, selection, new String[] { id }, null);
		result = cursor.moveToFirst();
		cursor.close();
		
		return result;
	}
	
	private List<String> getManyToMany(Uri content, String projection, String selection, String selectionId) {
		List<String> list = new ArrayList<String>();
		
		Cursor cursor = query(content, new String[] { projection }, String.format("%s = ?", selection), new String[] { selectionId }, null);
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.moveToPosition(i)) {
				list.add(cursor.getString(cursor.getColumnIndex(projection)));
			}
		}
		cursor.close();
		
		return list;
	}
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		ContentProviderResult[] result = super.applyBatch(operations);
		
		for (ContentProviderOperation operation : operations) {
    		getContext().getContentResolver().notifyChange(operation.getUri(), null);
		}
		
		return result;
	}
    
    @Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, String.format("query - uri: %s  projection: %s  selection: %s  selectionArgs: %s  String sortOrder: %s", uri, projection, selection, selectionArgs, sortOrder));
		Cursor cursor = null;
		
		if (selection == null) {
			selection = getSelection(uri);
		}
        
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		SQLiteDatabase database = quotationSQLiteHelper.getReadableDatabase();
        
		switch (uriMatcher.match(uri)) {
        	case QUOTATION_ID:
			    builder.setTables(Quotation.TABLE +
			    					" LEFT JOIN " + Source.TABLE + " ON " + Source.FULL_ID + " = " + Quotation.SOURCE_ID +
			    					" LEFT JOIN " + BookmarkQuotation.TABLE + " ON " + BookmarkQuotation.BOOKMARK_ID + " = " + Quotation.FULL_ID);
		        break;
        	case QUOTATION_PERSON_ID:
			    builder.setTables(QuotationPerson.TABLE +
			    					" LEFT JOIN " + Person.TABLE + " ON " + Person.FULL_ID + " = " + QuotationPerson.PERSON_ID +
			    					" LEFT JOIN " + BookmarkPerson.TABLE + " ON " + BookmarkPerson.BOOKMARK_ID + " = " + Person.FULL_ID);
		        break;
        	case PERSON_ID:
			    builder.setTables(Person.TABLE +
			    					" LEFT JOIN " + BookmarkPerson.TABLE + " ON " + BookmarkPerson.BOOKMARK_ID + " = " + Person.FULL_ID);
		        break;
        	case PERSON_QUOTATION_ID:
			    builder.setTables(PersonQuotation.TABLE +
			    					" LEFT JOIN " + Quotation.TABLE + " ON " + Quotation.FULL_ID + " = " + PersonQuotation.QUOTATION_ID +
			    					" LEFT JOIN " + Source.TABLE + " ON " + Source.FULL_ID + " = " + Quotation.SOURCE_ID +
			    					" LEFT JOIN " + BookmarkQuotation.TABLE + " ON " + BookmarkQuotation.BOOKMARK_ID + " = " + Quotation.FULL_ID);
		        break;
        	case SUBJECT_ID:
			    builder.setTables(Subject.TABLE +
			    					" LEFT JOIN " + BookmarkSubject.TABLE + " ON " + BookmarkSubject.BOOKMARK_ID + " = " + Subject.FULL_ID);
		        break;
        	case SUBJECT_QUOTATION_ID:
			    builder.setTables(SubjectQuotation.TABLE +
			    					" LEFT JOIN " + Quotation.TABLE + " ON " + Quotation.FULL_ID + " = " + SubjectQuotation.QUOTATION_ID +
			    					" LEFT JOIN " + Source.TABLE + " ON " + Source.FULL_ID + " = " + Quotation.SOURCE_ID +
			    					" LEFT JOIN " + BookmarkQuotation.TABLE + " ON " + BookmarkQuotation.BOOKMARK_ID + " = " + Quotation.FULL_ID);
		        break;
        	case BOOKMARK_QUOTATION:
        	case BOOKMARK_QUOTATION_ID:
			    builder.setTables(BookmarkQuotation.TABLE +
			    					" LEFT JOIN " + Quotation.TABLE + " ON " + BookmarkQuotation.BOOKMARK_ID + " = " + Quotation.FULL_ID  + 
						    		" LEFT JOIN " + Source.TABLE + " ON " + Source.FULL_ID + " = " + Quotation.SOURCE_ID);
		        break;
        	case BOOKMARK_PERSON:
        	case BOOKMARK_PERSON_ID:
			    builder.setTables(BookmarkPerson.TABLE +
			    					" LEFT JOIN " + Person.TABLE + " ON " + BookmarkPerson.BOOKMARK_ID + " = " + Person.FULL_ID);
		        break;
        	case BOOKMARK_SUBJECT:
        	case BOOKMARK_SUBJECT_ID:
			    builder.setTables(BookmarkSubject.TABLE +
			    					" LEFT JOIN " + Subject.TABLE + " ON " + BookmarkSubject.BOOKMARK_ID + " = " + Subject.FULL_ID);
		        break;
        	case PICK_QUOTATION:
        	case PICK_QUOTATION_ID:
			    builder.setTables(PickQuotation.TABLE +
			    					" LEFT JOIN " + Quotation.TABLE + " ON " + PickQuotation.PICK_ID + " = " + Quotation.FULL_ID  + 
						    		" LEFT JOIN " + Source.TABLE + " ON " + Source.FULL_ID + " = " + Quotation.SOURCE_ID +
						    		" LEFT JOIN " + BookmarkQuotation.TABLE + " ON " + BookmarkQuotation.BOOKMARK_ID + " = " + Quotation.FULL_ID);
		        break;
        	case PICK_PERSON:
        	case PICK_PERSON_ID:
			    builder.setTables(PickPerson.TABLE +
			    					" LEFT JOIN " + Person.TABLE + " ON " + PickPerson.PICK_ID + " = " + Person.FULL_ID +
			    					" LEFT JOIN " + BookmarkPerson.TABLE + " ON " + BookmarkPerson.BOOKMARK_ID + " = " + Person.FULL_ID);
		        break;
        	case PICK_SUBJECT:
        	case PICK_SUBJECT_ID:
			    builder.setTables(PickSubject.TABLE +
			    					" LEFT JOIN " + Subject.TABLE + " ON " + PickSubject.PICK_ID + " = " + Subject.FULL_ID +
			    					" LEFT JOIN " + BookmarkSubject.TABLE + " ON " + BookmarkSubject.BOOKMARK_ID + " = " + Subject.FULL_ID);
		        break;
      	default:
			    builder.setTables(getTables(uri));
		        break;
        }
		
		cursor = builder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		if (cursor.getCount() == 0 && doSync(uri)) {
			Log.d(TAG, String.format("query - requesting sync uri: %s", uri));
			Bundle extras = new Bundle();
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
	        extras.putString(QuotationSyncAdapter.SYNC_EXTRA_URI, uri.toString());
	        
			ContentResolver.requestSync(new Account(QuotationAccount.NAME, QuotationAccount.TYPE), QuotationContract.AUTHORITY, extras);
		}
		
	    return cursor;
	}
    
    private List<Uri> getBookmarkQuotationNotifies(String id) {
		List<Uri> notifies = new ArrayList<Uri>();
		
		notifies.add(BookmarkQuotation.CONTENT_URI);
		notifies.add(Quotation.withAppendedId(id));
		if (isPick(PickQuotation.CONTENT_URI, id)) {
    		notifies.add(PickQuotation.CONTENT_URI);
    		notifies.add(PickQuotation.withAppendedId(id));
		}
		List<String> list = getManyToMany(PersonQuotation.CONTENT_URI, PersonQuotation.PERSON_ID, PersonQuotation.QUOTATION_ID, id);
		for (String item : list) {
    		notifies.add(PersonQuotation.withAppendedId(item));
		}
		list = getManyToMany(SubjectQuotation.CONTENT_URI, SubjectQuotation.SUBJECT_ID, SubjectQuotation.QUOTATION_ID, id);
		for (String item : list) {
    		notifies.add(SubjectQuotation.withAppendedId(item));
		}

		return notifies;
    }
 
    private List<Uri> getBookmarkPersonNotifies(String id) {
		List<Uri> notifies = new ArrayList<Uri>();
		
		notifies.add(BookmarkPerson.CONTENT_URI);
		notifies.add(Person.withAppendedId(id));
		if (isPick(PickPerson.CONTENT_URI, id)) {
    		notifies.add(PickPerson.CONTENT_URI);
    		notifies.add(PickPerson.withAppendedId(id));
		}
		List<String> list = getManyToMany(PersonQuotation.CONTENT_URI, PersonQuotation.PERSON_ID, PersonQuotation.QUOTATION_ID, id);
		for (String item : list) {
    		notifies.add(PersonQuotation.withAppendedId(item));
		}
    	
		return notifies;
    }
 
    private List<Uri> getBookmarkSubjectNotifies(String id) {
		List<Uri> notifies = new ArrayList<Uri>();
		
		notifies.add(BookmarkSubject.CONTENT_URI);
		notifies.add(Subject.withAppendedId(id));
		if (isPick(PickSubject.CONTENT_URI, id)) {
    		notifies.add(PickSubject.CONTENT_URI);
    		notifies.add(PickSubject.withAppendedId(id));
		}
		List<String> list = getManyToMany(SubjectQuotation.CONTENT_URI, SubjectQuotation.SUBJECT_ID, SubjectQuotation.QUOTATION_ID, id);
		for (String item : list) {
    		notifies.add(SubjectQuotation.withAppendedId(item));
		}
    	
		return notifies;
    }
 
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, String.format("insert - uri: %s  values: %s", uri, values));
		
    	long row;
    	
    	switch (uriMatcher.match(uri)) {
            case QUOTATION_ID:
            case PERSON_ID:
            case SUBJECT_ID:
            case SOURCE_ID:
            	row = quotationSQLiteHelper.insert(quotationSQLiteHelper.getWritableDatabase(), getTables(uri), values, true);
            	break;
            default:
            	row = quotationSQLiteHelper.insert(quotationSQLiteHelper.getWritableDatabase(), getTables(uri), values);
            	break;
        }
    	
		if (row == -1) {
			uri = null;
		}
		else {
			List<Uri> notifies = new ArrayList<Uri>();
			
			notifies.add(uri);
			
	    	switch (uriMatcher.match(uri)) {
            case PICK_QUOTATION_ID:
		    	notifies.add(PickQuotation.CONTENT_URI);
            	break;
            case PICK_PERSON_ID:
		    	notifies.add(PickPerson.CONTENT_URI);
            	break;
            case PICK_SUBJECT_ID:
		    	notifies.add(PickSubject.CONTENT_URI);
            	break;
	    	case BOOKMARK_QUOTATION_ID:
	    		notifies = getBookmarkQuotationNotifies(BookmarkQuotation.getId(uri));
	    		break;
	    	case BOOKMARK_PERSON_ID:
	    		notifies = getBookmarkPersonNotifies(BookmarkPerson.getId(uri));
	    		break;
	    	case BOOKMARK_SUBJECT_ID:
	    		notifies = getBookmarkSubjectNotifies(BookmarkSubject.getId(uri));
	    		break;
	        }
		
	    	for (Uri notify : notifies) {
	    		getContext().getContentResolver().notifyChange(notify, null);
	    	}
		}
        
		Log.d(TAG, String.format("insert - database size: %d  max: %d", new File(quotationSQLiteHelper.getReadableDatabase().getPath()).length(), quotationSQLiteHelper.getReadableDatabase().getMaximumSize()));

		return uri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, String.format("update - uri: %s  values: %s  selection: %s  selectionArgs: %s", uri, values, selection, selectionArgs));
		
		if (selection == null) {
			selection = getSelection(uri);
		}

		int rows = quotationSQLiteHelper.update(quotationSQLiteHelper.getWritableDatabase(), getTables(uri), values, selection, selectionArgs);
		if (rows > 0) {
			List<Uri> notifies = new ArrayList<Uri>();
			
			notifies.add(uri);
			
	    	switch (uriMatcher.match(uri)) {
            case PICK_QUOTATION_ID:
		    	notifies.add(PickQuotation.CONTENT_URI);
            	break;
            case PICK_PERSON_ID:
		    	notifies.add(PickPerson.CONTENT_URI);
            	break;
            case PICK_SUBJECT_ID:
		    	notifies.add(PickSubject.CONTENT_URI);
            	break;
	    	case BOOKMARK_QUOTATION_ID:
	    		notifies = getBookmarkQuotationNotifies(BookmarkQuotation.getId(uri));
	    		break;
	    	case BOOKMARK_PERSON_ID:
	    		notifies = getBookmarkPersonNotifies(BookmarkPerson.getId(uri));
	    		break;
	    	case BOOKMARK_SUBJECT_ID:
	    		notifies = getBookmarkSubjectNotifies(BookmarkSubject.getId(uri));
	    		break;
	        }
		
	    	for (Uri notify : notifies) {
	    		getContext().getContentResolver().notifyChange(notify, null);
	    	}
		}
        
        return rows;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, String.format("delete - uri: %s  selection: %s  selectionArgs: %s", uri, selection, selectionArgs));
		
		if (selection == null) {
			selection = getSelection(uri);
		}

 		int rows = quotationSQLiteHelper.delete(quotationSQLiteHelper.getWritableDatabase(), getTables(uri), selection, selectionArgs);
		if (rows > 0) {
			List<Uri> notifies = new ArrayList<Uri>();
			
			notifies.add(uri);
			
	    	switch (uriMatcher.match(uri)) {
            case PICK_QUOTATION_ID:
		    	notifies.add(PickQuotation.CONTENT_URI);
            	break;
            case PICK_PERSON_ID:
		    	notifies.add(PickPerson.CONTENT_URI);
            	break;
            case PICK_SUBJECT_ID:
		    	notifies.add(PickSubject.CONTENT_URI);
            	break;
	    	case BOOKMARK_QUOTATION_ID:
	    		notifies = getBookmarkQuotationNotifies(BookmarkQuotation.getId(uri));
	    		break;
	    	case BOOKMARK_PERSON_ID:
	    		notifies = getBookmarkPersonNotifies(BookmarkPerson.getId(uri));
	    		break;
	    	case BOOKMARK_SUBJECT_ID:
	    		notifies = getBookmarkSubjectNotifies(BookmarkSubject.getId(uri));
	    		break;
	        }
		
	    	for (Uri notify : notifies) {
	    		getContext().getContentResolver().notifyChange(notify, null);
	    	}
		}
        
        return rows;
	}
}

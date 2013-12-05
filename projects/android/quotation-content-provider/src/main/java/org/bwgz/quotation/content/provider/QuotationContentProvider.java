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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class QuotationContentProvider extends ContentProvider {
	static private String TAG = QuotationContentProvider.class.getSimpleName();

    static private final int QUEUE_CAPACITY = 3;

    private ArrayBlockingQueue<Uri> queue = new ArrayBlockingQueue<Uri>(QUEUE_CAPACITY);
	private RandomUriProducerTask randomUriProducerTask;
	private EvictTask evictTask;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int QUOTATION			= 1;
    private static final int QUOTATION_ID		= 2;
    private static final int QUOTATION_RANDOM	= 3;
    private static final int PERSON				= 4;
    private static final int PERSON_ID			= 5;
    private static final int QUOTATION_PERSON	= 6;
    private static final int PERSON_QUOTATION	= 7;

    private static final int MAX_EVICTABLE_QUOTATIONS = 1000;
    
    static
    {
        uriMatcher.addURI(QuotationContract.AUTHORITY, "quotation", QUOTATION);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "quotation/m/*", QUOTATION_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "quotation/random", QUOTATION_RANDOM);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "person", PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "person/m/*", PERSON_ID);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "quotation/person", QUOTATION_PERSON);
        uriMatcher.addURI(QuotationContract.AUTHORITY, "person/quotation", PERSON_QUOTATION);
    }

	private QuotationSQLiteHelper quotationSQLiteHelper;
	private FreebaseHelper freebaseHelper;

	private class EvictTask extends TimerTask {
		private final String evictableQuotationsQuery = String.format("(%s = 1)", Person.EVICTABLE);
		private final String lowerQuotationsQuery = String.format("%s IN (SELECT %s FROM %s WHERE (%s = 1) ORDER BY %s ASC LIMIT ?)", Quotation._ID, Quotation._ID, Quotation.TABLE, Quotation.EVICTABLE, Quotation.MODIFIED);
		private final String emptyAuthorsQuery = String.format("((%s = 1) AND (%s NOT IN (SELECT %s FROM %s)))", Person.EVICTABLE, Person._ID, QuotationPerson.PERSON_ID, QuotationPerson.TABLE);
		
		private int getEvictableQuotations() {
			int rows = 0;
			
			Cursor cursor = query(Quotation.CONTENT_URI, null, evictableQuotationsQuery, null, null);
			rows = cursor.getCount();
			cursor.close();
			
			return rows;
		}
		@Override
		public void run() {
			
			int count = getEvictableQuotations();
			Log.i(TAG, String.format("first pass evictable quotations: %d", count));
			if (count > MAX_EVICTABLE_QUOTATIONS) {
				int rows;
				rows = delete(Quotation.CONTENT_URI, lowerQuotationsQuery, new String[] { Long.toString(count - MAX_EVICTABLE_QUOTATIONS) });
				Log.i(TAG, String.format("deleted %d old quotations", rows));
	
				if (rows != 0) {
					rows = delete(Person.CONTENT_URI, emptyAuthorsQuery, null);
					Log.i(TAG, String.format("deleted %d persons with no quotations", rows));
				}
			}
		}
	}

    private void runEvictTask() {
    	evictTask = new EvictTask();
		new Timer().schedule(evictTask, TimeUnit.MINUTES.toMillis(5), TimeUnit.HOURS.toMillis(12));
    }

	private void runRandomUriProducerTask() {
		Log.d(TAG, String.format("runRandomUriProducerTask[before test] - task: %s (%s)", randomUriProducerTask, randomUriProducerTask != null ? randomUriProducerTask.getStatus() : "null"));
    	if (randomUriProducerTask != null) {
    		if (randomUriProducerTask.getStatus() == AsyncTask.Status.FINISHED) {
    			randomUriProducerTask = null;
    		}
    	}
		Log.d(TAG, String.format("runRandomUriProducerTask[after test] - task: %s (%s)", randomUriProducerTask, randomUriProducerTask != null ? randomUriProducerTask.getStatus() : "null"));
    	
    	if (randomUriProducerTask == null) {
			randomUriProducerTask = new RandomUriProducerTask(getContext(), freebaseHelper);
			Log.d(TAG, String.format("runRandomUriProducerTask[before execute] - task: %s (%s)", randomUriProducerTask, randomUriProducerTask.getStatus()));
			randomUriProducerTask.execute(queue);
			Log.d(TAG, String.format("runRandomUriProducerTask[after execute] - task: %s (%s)", randomUriProducerTask, randomUriProducerTask.getStatus()));
    	}
    }

    private Uri getRandomQuotationUri() {
		Uri uri = null;
		
		runRandomUriProducerTask();
		try {
			Log.d(TAG, String.format("take uri from queue"));
			uri = queue.take();
			Log.d(TAG, String.format("uri: %s", uri));
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}
    	
		return uri;
    }

    @Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		quotationSQLiteHelper = new QuotationSQLiteHelper(getContext(), QuotationContract.Quotation.TABLE, null, QuotationSQLiteHelper.DATABASE_VERSION);
		
		String name = getContext().getPackageName();
		String keys[] = null;
		
        try {
        	ProviderInfo info = getContext().getPackageManager().getProviderInfo(new ComponentName(getContext().getPackageName(), getClass().getName()), PackageManager.GET_META_DATA);
			Log.d(TAG, String.format("onCreate - info: %s", info));
	    	
			if (info != null && info.metaData != null) {
				Log.d(TAG, String.format("onCreate - info.metaData: %s", info.metaData));
				
				int id = info.metaData.getInt("freebase.api.keys");
				Log.d(TAG, String.format("onCreate - freebase.api.keys: %d", id));
				if (id != 0) {
					keys = getContext().getResources().getStringArray(id);
					Log.d(TAG, String.format("onCreate - freebase.api.keys: %s", keys));
				}
				
				if (keys == null) {
					String key = info.metaData.getString("freebase.api.key");
					Log.d(TAG, String.format("onCreate - freebase.api.key: %s", key));
					
					if (key != null) {
						keys = new String[] { key };
					}
				}
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
        
        if (keys == null) {
        	Log.w(TAG, "working without Freebase API key");
        }
		
        freebaseHelper = new FreebaseHelper(name, keys);
        
        runEvictTask();
		runRandomUriProducerTask();
		
        return true;
	}
	
	@Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri))
        {
            case QUOTATION:
                return QuotationContract.Quotation.CONTENT_TYPE;
            case QUOTATION_ID:
                return QuotationContract.Quotation.CONTENT_ITEM_TYPE;
            case QUOTATION_RANDOM:
                return QuotationContract.Quotation.CONTENT_ITEM_TYPE;
            case PERSON:
                return QuotationContract.Person.CONTENT_TYPE;
            case PERSON_ID:
                return QuotationContract.Person.CONTENT_ITEM_TYPE;
            case QUOTATION_PERSON:
                return QuotationContract.QuotationPerson.CONTENT_TYPE;
            default:
                return null;
        }
	}
	
	private String getTable(Uri uri) {
		String table = null;
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
            case QUOTATION:
            	table = QuotationContract.Quotation.TABLE;
            	break;
            case PERSON_ID:
            case PERSON:
            	table = QuotationContract.Person.TABLE;
            	break;
            case QUOTATION_PERSON:
            case PERSON_QUOTATION:
            	table = QuotationContract.QuotationPerson.TABLE;
            	break;
        }

        return table;
	}
	
	private boolean doSync(Uri uri) {
		boolean result = false;
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
            case PERSON_ID:
            	result = true;
            	break;
        }

        return result;
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
		
		String table = getTable(uri);
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Quotation.getId(uri) + "'";
            	break;
            case PERSON_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Person.getId(uri) + "'";
            	break;
        }
		
		switch (uriMatcher.match(uri))
        {
	    	case QUOTATION_RANDOM:
	    		uri = getRandomQuotationUri();
	    		if (uri != null) {
		    		Log.d(TAG, String.format("random quotation query - _uri: %s", uri));		
		    		MatrixCursor _cursor = new MatrixCursor(new String[] { Quotation._ID });
		    		_cursor.addRow(new String[] { Quotation.getId(uri) });
		    		cursor = _cursor;
	    		}
    			break;
        	default:
		        if (table != null) {
			        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			        builder.setTables(getTable(uri));
					cursor = builder.query(quotationSQLiteHelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					
					if (cursor.getCount() == 0 && doSync(uri)) {
						Log.d(TAG, String.format("query - requesting sync uri: %s", uri));
						Bundle extras = new Bundle();
				        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
				        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
				        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				        extras.putString(QuotationSyncAdapter.SYNC_EXTRAS_URI, uri.toString());
				        
						ContentResolver.requestSync(new QuotationAccount(), QuotationContract.AUTHORITY, extras);
					}
		        }
		        break;
        }
        
	    return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, String.format("insert - uri: %s  values: %s", uri, values));
		
		String table = getTable(uri);
		
        if (table != null) {
        	long row;
        	
        	switch (uriMatcher.match(uri))
            {
                case QUOTATION_ID:
                case PERSON_ID:
                	row = quotationSQLiteHelper.insert(quotationSQLiteHelper.getWritableDatabase(), table, values, true);
                	break;
                default:
                	row = quotationSQLiteHelper.insert(quotationSQLiteHelper.getWritableDatabase(), table, values);
                	break;
            }

			if (row == -1) {
				uri = null;
			}
        }
        
		return uri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, String.format("update - uri: %s  values: %s  selection: %s  selectionArgs: %s", uri, values, selection, selectionArgs));
		int rows = 0;
		
		String table = getTable(uri);
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Quotation.getId(uri) + "'";
            	break;
            case PERSON_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Person.getId(uri) + "'";
            	break;
       }

        if (table != null) {
        	rows = quotationSQLiteHelper.update(quotationSQLiteHelper.getWritableDatabase(), table, values, selection, selectionArgs);
			if (rows > 0) {
        		getContext().getContentResolver().notifyChange(uri, null);
			}
        }
        
        return rows;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, String.format("delete - uri: %s  selection: %s  selectionArgs: %s", uri, selection, selectionArgs));
		int rows = 0;
		
		String table = getTable(uri);
		
        switch (uriMatcher.match(uri))
        {
            case QUOTATION_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Quotation.getId(uri) + "'";
            	break;
            case PERSON_ID:
                selection = (selection != null ? selection : new String()) + "_ID = " + "'" + QuotationContract.Person.getId(uri) + "'";
            	break;
       }

        if (table != null) {
        	rows = quotationSQLiteHelper.delete(quotationSQLiteHelper.getWritableDatabase(), table, selection, selectionArgs);
			if (rows > 0) {
        		getContext().getContentResolver().notifyChange(uri, null);
			}
        }
        
        return rows;
	}
}

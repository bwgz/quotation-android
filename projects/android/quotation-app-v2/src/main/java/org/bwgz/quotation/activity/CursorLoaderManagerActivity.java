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
package org.bwgz.quotation.activity;

import java.util.HashMap;
import java.util.Map;

import org.bwgz.quotation.core.CursorLoaderManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class CursorLoaderManagerActivity extends VolleyActivity implements CursorLoaderManager {
	static public final String TAG = CursorLoaderManagerActivity.class.getSimpleName();
	
    private int loaderId;
    private Map<Integer, CursorLoaderListener> loaderIdMap;
	
    protected synchronized int getNextLoaderId() {
		return loaderId++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loaderIdMap = new HashMap<Integer, CursorLoaderListener>();
    }
    
	@Override
	public int initLoader(CursorLoaderListener listener, Bundle bundle) {
		int loaderId = getNextLoaderId();
		
    	getSupportLoaderManager().initLoader(loaderId, bundle, this);
    	loaderIdMap.put(loaderId, listener);
    	
		return loaderId;
	}

	@Override
	public void destroyLoader(int id) {
		Log.d(TAG, String.format("destroyLoader - id: %d", id));
		getSupportLoaderManager().destroyLoader(id);
		loaderIdMap.remove(id);
	}
	
	@Override
    public int initLoader(CursorLoaderListener listener, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		//Log.d(TAG, String.format("onPickLoad - view: %s  uri: %s  projection: %s  selection: %s  selectionArgs: %s  String sortOrder: %s", view, uri, projection, selection, selectionArgs, sortOrder));
    	
        Bundle bundle = new Bundle();
        bundle.putParcelable(LOADER_BUNDLE_URI, uri);
        bundle.putStringArray(LOADER_BUNDLE_PROJECTION, projection);
        bundle.putString(LOADER_BUNDLE_SELECTION, selection);
        bundle.putStringArray(LOADER_BUNDLE_SELECTION_ARGS, selectionArgs);
        bundle.putString(LOADER_BUNDLE_SORT_ORDER, sortOrder);
        
        return initLoader(listener, bundle);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.d(TAG, String.format("onCreateLoader - id: %d  bundle: %s", id, bundle));
		
		Uri uri = bundle.getParcelable(LOADER_BUNDLE_URI);
		String[] projection = bundle.getStringArray(LOADER_BUNDLE_PROJECTION);
		String selection = bundle.getString(LOADER_BUNDLE_SELECTION);
		String[] selectionArgs = bundle.getStringArray(LOADER_BUNDLE_SELECTION_ARGS);
		String sortOrder = bundle.getString(LOADER_BUNDLE_SORT_ORDER);
		
		Log.d(TAG, String.format("onCreateLoader - uri: %s", uri));
		
        return new CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
		
		CursorLoaderListener listener = loaderIdMap.get(loader.getId());
		if (listener != null) {
			listener.onCursorLoadFinished(loader, cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, String.format("onLoaderReset - loader: %s", loader));
	}
}

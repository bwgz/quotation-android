package org.bwgz.quotation.fragment;

import java.util.HashMap;
import java.util.Map;

import org.bwgz.quotation.core.CursorLoaderManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseArray;

public class CursorLoaderManagerFragment extends VolleyFragment implements CursorLoaderManager {
	static public final String TAG = CursorLoaderManagerFragment.class.getSimpleName();

    private int loaderId;
    private SparseArray<CursorLoaderListener> loaderIdMap;
	
    protected synchronized int getNextLoaderId() {
		return loaderId++;
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
		
        loaderIdMap = new SparseArray<CursorLoaderListener>();
    }
    
	@Override
	public int initLoader(CursorLoaderListener listener, Bundle bundle) {
		int loaderId = getNextLoaderId();
		
    	getLoaderManager().initLoader(loaderId, bundle, this);
    	loaderIdMap.put(loaderId, listener);
    	
		return loaderId;
	}

	@Override
	public void destroyLoader(int id) {
		Log.d(TAG, String.format("destroyLoader - id: %d", id));
		getLoaderManager().destroyLoader(id);
		loaderIdMap.remove(id);
	}
   
	@Override
    public int initLoader(CursorLoaderListener listener, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, String.format("initLoader - listener: %s  uri: %s  projection: %s  selection: %s  selectionArgs: %s  String sortOrder: %s", listener, uri, projection, selection, selectionArgs, sortOrder));
    	
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
		
		return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
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

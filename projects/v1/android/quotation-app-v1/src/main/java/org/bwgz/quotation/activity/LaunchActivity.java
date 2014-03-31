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
package org.bwgz.quotation.activity;

import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class LaunchActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	static public final String TAG = LaunchActivity.class.getSimpleName();
	
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", bundle));

		setContentView(R.layout.launch_activity);
        getSupportLoaderManager().initLoader(0 , null, this);
    }
    
    
	@Override
	public void onStart() {
		super.onStart();
    	Log.d(TAG, String.format("onResume"));
	}
	  
	@Override
    protected void onResume() {
    	super.onResume();
    	Log.d(TAG, String.format("onStart"));
 
    	ImageView view = (ImageView) findViewById(R.id.launch);
    	view.setBackgroundResource(R.drawable.launch);
    	AnimationDrawable animation = (AnimationDrawable) view.getBackground();
    	animation.start();
    	
	    Toast toast = Toast.makeText(this, R.string.initialization_message, (int) TimeUnit.SECONDS.toMillis(3));
		toast.show();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d(TAG, String.format("onPause"));
    }


    @Override
    protected void onStop() {
    	super.onStop();
    	Log.d(TAG, String.format("onStop"));
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
		Log.d(TAG, String.format("onDestroy"));
    }


	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d(TAG, String.format("onCreateLoader - loaderID: %d  bundle: %s", loaderID, bundle));
        CursorLoader loader = new CursorLoader(
            this,		
            Quotation.withAppendedId("/random"),        
            new String[] { Quotation.QUOTATION },
            null,      
            null,      
            null       
    		);
        
        return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (count: %d)", loader, cursor, cursor != null ? cursor.getCount() : 0));
		
     	Intent intent;
     	
		if (cursor.moveToFirst()) {
			Uri uri = Quotation.withAppendedId(cursor.getString(cursor.getColumnIndex(Quotation._ID)));
	    	Log.d(TAG, String.format("onLoadFinished - uri: %s", uri));
	
	     	intent = new Intent(Intent.ACTION_VIEW, uri);
		}
		else {
	        intent = new Intent().setClass(this, QuotationActivity.class);
		}
		
	    startActivity(intent);
		finish();
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
	}
}

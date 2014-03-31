package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.google.analytics.tracking.android.MapBuilder;

public class BaseActivity extends AdMobActivity {
	static public final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
    	switch (item.getItemId()) {
    	case R.id.settings:
	        Intent intent = new Intent().setClass(this, SettingsActivity.class);
    	    startActivity(intent);
 
    	    getTracker().send(MapBuilder.createEvent("ui.action", "button.press", "menu.settings", null).build());
	    	return true;
    	}
		
        return super.onOptionsItemSelected(item);
    }
}

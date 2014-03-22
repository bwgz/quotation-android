package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;

import android.app.SearchManager;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

public abstract class PicksActivity extends CursorLoaderManagerActivity {
	static public final String TAG = PicksActivity.class.getSimpleName();

	abstract protected CharSequence getQueryHint();
	abstract protected int getSearchType();

    protected int getMenuResId() {
    	return R.menu.options_menu;
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
		
        getMenuInflater().inflate(getMenuResId(), menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getQueryHint());
     
        return true;
    }

}

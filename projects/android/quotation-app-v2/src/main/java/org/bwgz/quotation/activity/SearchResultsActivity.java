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

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.adapter.SearchResultsAdapter;
import org.bwgz.quotation.model.picks.Pick;
import org.bwgz.quotation.search.FreebaseSearch;
import org.bwgz.quotation.search.SearchResultsTask;

import com.google.analytics.tracking.android.MapBuilder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchResultsActivity extends CursorLoaderManagerActivity {
	static private final String TAG = SearchResultsActivity.class.getSimpleName();
	
	static public final String EXTRA_SEARCH_TYPE = "extra.search.type";
	static public final int DEFAULT_LIMIT = 10;
	
	private SearchResultsTask loadSearchResultsTask;
	private SearchResultsAdapter searchResultsAdapter;

	@Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - bundle: %s", bundle));
		
		setContentView(R.layout.search_results_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
		Log.d(TAG, String.format("onNewIntent - intent: %s", intent));
		
        handleIntent(intent);
    }

    private String getSearchType(int type) {
		String string;
		
		switch (type) {
		case FreebaseSearch.SEARCH_TYPE_KEYWORD:
		default:
			string = "keyword";
			break;
		case FreebaseSearch.SEARCH_TYPE_AUTHOR:
			string = "author";
			break;
		case FreebaseSearch.SEARCH_TYPE_SUBJECT:
			string = "subject";
			break;
		}
		
		return string;
    }

    private void handleIntent(final Intent intent) {
		Log.d(TAG, String.format("handleIntent - intent: %s", intent));
		
        String query = intent.getStringExtra(SearchManager.QUERY);
        int type = intent.getIntExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_KEYWORD);

	    getTracker().send(MapBuilder.createEvent("ui.search", getSearchType(type), query, null).build());

		searchResultsAdapter = new SearchResultsAdapter(this, R.layout.quotation_pick_view, new ArrayList<Pick>(), this, getImageLoader());
    	loadSearchResultsTask = (SearchResultsTask) new SearchResultsTask(this, searchResultsAdapter, 0, DEFAULT_LIMIT).execute(intent);
        
        ListView listView = (ListView) findViewById(R.id.result_list);
        listView.setAdapter(searchResultsAdapter);
		
		listView.setOnScrollListener(new OnScrollListener() {
		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.d(TAG, String.format("onScroll - view %s  firstVisibleItem: %d  visibleItemCount: %d  totalItemCount: %d", view, firstVisibleItem, visibleItemCount, totalItemCount));
		        if ((loadSearchResultsTask.getStatus() == AsyncTask.Status.FINISHED) && (totalItemCount < searchResultsAdapter.getHits()) && (firstVisibleItem + visibleItemCount == totalItemCount)) {
		            Log.d(TAG, "Load Next Page!");
		            
		    		loadSearchResultsTask = (SearchResultsTask) new SearchResultsTask(SearchResultsActivity.this, searchResultsAdapter, totalItemCount, Math.max(visibleItemCount, DEFAULT_LIMIT)).execute(intent);
		        }
		    }

		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	SearchResultsAdapter adapter = (SearchResultsAdapter) parent.getAdapter();
            	
            	List<String> mids = new ArrayList<String>();
            	for (int i = 0; i < adapter.getCount(); i++) {
            		Pick pick = (Pick) adapter.getItem(i);
            		mids.add(pick.getId());
            	}
            	
            	Intent intent = new Intent(getApplicationContext(), QuotationActivity.class);
            	intent.putExtra(QuotationActivity.EXTRA_IDS, mids.toArray(new String[mids.size()]));
            	intent.putExtra(QuotationActivity.EXTRA_POSITION, position);
            	startActivity(intent);
            }
		});

   }

    @Override
    public void startActivity(Intent intent) {      
		Log.d(TAG, String.format("startActivity - intent: %s", intent));
		
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            int type = getIntent().getIntExtra(EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_KEYWORD);

            intent.putExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, type);
        }

        super.startActivity(intent);
    }
    
    private void setSearchHint(Menu menu, int type) {
		int hintId = 0;
		
		switch (type) {
		case FreebaseSearch.SEARCH_TYPE_KEYWORD:
			hintId = R.string.search_keyword;
			break;
		case FreebaseSearch.SEARCH_TYPE_AUTHOR:
			hintId = R.string.search_author;
			break;
		case FreebaseSearch.SEARCH_TYPE_SUBJECT:
			hintId = R.string.search_subject;
			break;
		}
		
		if (hintId != 0) {
			String hint = getString(hintId);
	
			if (hint != null) {
		        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		        searchView.setQueryHint(hint);
			}
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
       
        Intent intent = getIntent();
        if (intent != null) {
        	int type = intent.getIntExtra(EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_KEYWORD);
    		Log.d(TAG, String.format("onCreateOptionsMenu - type: %s", type));
        	setSearchHint(menu, type);
        	
        	String query = intent.getStringExtra(SearchManager.QUERY);
    		Log.d(TAG, String.format("onCreateOptionsMenu - query: %s", query));
            if (query != null) {
	        	searchView.setIconified(true);
	        	searchView.setQuery(query, false);
	            searchView.clearFocus();
            }
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
        	NavUtils.navigateUpFromSameTask(this);
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
}

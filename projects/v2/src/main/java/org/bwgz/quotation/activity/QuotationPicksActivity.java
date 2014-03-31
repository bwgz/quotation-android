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

import org.bwgz.quotation.R;
import org.bwgz.quotation.adapter.ActivityAdapter;
import org.bwgz.quotation.adapter.QuotationPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationQuery;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.search.FreebaseSearch;
import org.bwgz.quotation.widget.picks.PicksView;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

public class QuotationPicksActivity extends PicksActivity implements CursorLoaderListener {
	static public final String TAG = QuotationPicksActivity.class.getSimpleName();

	private PicksView picksView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.standard_grid_view);
        setTitle(R.string.quotations_title);
  
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		picksView = (PicksView) inflater.inflate(R.layout.quotation_picks_expanded_view, null);
		picksView.setActivityClass(QuotationActivity.class);
		picksView.setExpand(true);
		initLoader(this, PickQuotation.CONTENT_URI, QuotationQuery.PROJECTION, null, null, null);
		
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));
    }
    
	@Override
	protected CharSequence getQueryHint() {
		return getString(R.string.search_keyword);
	}
	
	@Override
	protected int getSearchType() {
		return FreebaseSearch.SEARCH_TYPE_KEYWORD;
	}

	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		QuotationPicksCursorAdapter adapter = picksView.getAdapter();
		if (adapter == null) {
			adapter = new QuotationPicksCursorAdapter(this, cursor, R.layout.quotation_pick_view, getImageLoader());
			picksView.setAdapter(adapter);
		}
		else {
			adapter.swapCursor(cursor);
		}
	}
}

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
package org.bwgz.quotation.adapter;

import java.util.List;

import org.bwgz.quotation.core.CursorLoaderManager;
import org.bwgz.quotation.model.picks.Pick;

import com.android.volley.toolbox.ImageLoader;

import android.content.Context;
import android.util.Log;

public class SearchResultsAdapter extends QuotationPicksArrayAdapter {
	static public final String TAG = SearchResultsAdapter.class.getSimpleName();
	
	private int hits;
	
	public SearchResultsAdapter(Context context, int resId, List<Pick> picks, CursorLoaderManager cursorLoaderManager, ImageLoader imageLoader) {
		super(context, resId, picks, cursorLoaderManager, imageLoader);
		Log.d(TAG, String.format("SearchResultsAdapter - context: %s  resId: %d  picks: %s (%d)  cursorLoaderManager: %s  imageLoader: %s", context, resId, picks, picks.size(), cursorLoaderManager, imageLoader));
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}
}


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
import org.bwgz.quotation.adapter.SubjectPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.search.FreebaseSearch;
import org.bwgz.quotation.widget.picks.PicksView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

public class SubjectPicksActivity extends PicksActivity implements CursorLoaderListener {
	static public final String TAG = SubjectPicksActivity.class.getSimpleName();

	private PicksView picksView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.standard_grid_view);
        setTitle(R.string.subjects_title);
  
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		picksView = (PicksView) inflater.inflate(R.layout.subject_picks_expanded_view, null);
		picksView.setActivityClass(SubjectActivity.class);
		picksView.setExpand(true);
        initLoader(this, PickSubject.CONTENT_URI, new String[] { Subject.FULL_ID, Subject.NAME, Subject.DESCRIPTION, Subject.IMAGE_ID, Subject.QUOTATION_COUNT, BookmarkSubject.BOOKMARK_ID }, null, null, null);
	    
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));
    }

	@Override
	protected CharSequence getQueryHint() {
		return getString(R.string.search_subject);
	}
	
	@Override
	protected int getSearchType() {
		return FreebaseSearch.SEARCH_TYPE_SUBJECT;
	}

	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		SubjectPicksCursorAdapter adapter = picksView.getAdapter();
		if (adapter == null) {
			adapter = new SubjectPicksCursorAdapter(this, cursor, R.layout.subject_pick_view, getImageLoader());
			picksView.setAdapter(adapter);
		}
		else {
			adapter.swapCursor(cursor);
		}
	}
}

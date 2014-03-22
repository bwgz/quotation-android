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
package org.bwgz.quotation.fragment;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.SubjectActivity;
import org.bwgz.quotation.adapter.ActivityAdapter;
import org.bwgz.quotation.adapter.SubjectPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.widget.picks.PicksView;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class HomeSubjectsFragment extends CursorLoaderManagerFragment implements CursorLoaderListener {
	static public final String TAG = HomeSubjectsFragment.class.getSimpleName();
	
	private PicksView picksView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - this: %s  inflater: %s  container: %s  savedInstanceState: %s", this, inflater, container, savedInstanceState));
		
		GridView gridView = (GridView) inflater.inflate(R.layout.standard_grid_view, container, false);
		
		picksView = (PicksView) inflater.inflate(R.layout.subject_picks_unexpanded_view, null);
		picksView.setActivityClass(SubjectActivity.class);
        initLoader(this, PickSubject.CONTENT_URI, new String[] { Subject.FULL_ID, Subject.NAME, Subject.DESCRIPTION, Subject.IMAGE_ID, Subject.QUOTATION_COUNT, BookmarkSubject.BOOKMARK_ID }, null, null, null);

        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));

        return gridView;
    }
    
	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		SubjectPicksCursorAdapter adapter = new SubjectPicksCursorAdapter(getActivity(), cursor, R.layout.subject_pick_view, getImageLoader());
		picksView.setAdapter(adapter);
	}
}

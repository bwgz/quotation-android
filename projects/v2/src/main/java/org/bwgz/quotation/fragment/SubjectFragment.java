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

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.PickViewPagerActivity;
import org.bwgz.quotation.activity.QuotationActivity;
import org.bwgz.quotation.adapter.LoadingAdapter;
import org.bwgz.quotation.adapter.QuotationPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationQuery;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.content.provider.QuotationContract.SubjectQuotation;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.analytics.tracking.android.MapBuilder;

public class SubjectFragment extends PickFragment implements CursorLoaderListener {
	static public final String TAG = SubjectFragment.class.getSimpleName();

	private int subjectLoaderId = -1;
	private int quotationLoaderId = -1;
	private ViewHolder viewHolder;
	
	private class ViewHolder {
		public NetworkImageView subject_image;
		public TextView subject_name;
		public TextView subject_description_short;
		public TextView subject_description_full;
		public LinearLayout subject_description_layout;
		public RelativeLayout subject_description_layout_short;
		public RelativeLayout subject_description_layout_full;
		public GridView quotation_grid;
	}
	
	private class GridViewOnItemClickListener implements OnItemClickListener {
		private GridView gridView;
		
		public GridViewOnItemClickListener(GridView gridView) {
			this.gridView = gridView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, String.format("onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
			if (gridView.getAdapter() instanceof QuotationPicksCursorAdapter) {
				QuotationPicksCursorAdapter adapter = (QuotationPicksCursorAdapter) gridView.getAdapter();
				Cursor cursor = (Cursor) adapter.getItem(position);
				Log.d(TAG, String.format("onItemClick - quotationId: %s", cursor.getString(cursor.getColumnIndex(Quotation._ID))));
				
				List<String> ids = new ArrayList<String>();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					ids.add(cursor.getString(cursor.getColumnIndex(Quotation._ID)));
				}
				
				Intent intent = new Intent(view.getContext(), QuotationActivity.class);
				intent.putExtra(PickViewPagerActivity.EXTRA_IDS, ids.toArray(new String[ids.size()]));
				intent.putExtra(PickViewPagerActivity.EXTRA_POSITION, position);
				
				view.getContext().startActivity(intent);
			}
		}
	}
	
	private OnClickListener toggleDescription = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Log.d(TAG, String.format("onClick - view: %s", view));
			RelativeLayout relativeLayout = viewHolder.subject_description_layout_short;
			relativeLayout.setVisibility(relativeLayout.isShown() ? View.GONE : View.VISIBLE );
			
			relativeLayout = viewHolder.subject_description_layout_full;
			relativeLayout.setVisibility(relativeLayout.isShown() ? View.GONE : View.VISIBLE );
		}
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - savedInstanceState: %s", savedInstanceState));

		View view = inflater.inflate(R.layout.subject_fragment, container, false);

		viewHolder = new ViewHolder();
		viewHolder.subject_name = (TextView) view.findViewById(R.id.subject_name);
		viewHolder.subject_image = (NetworkImageView) view.findViewById(R.id.subject_image);
		viewHolder.subject_description_short = (TextView) view.findViewById(R.id.subject_description_short);
		viewHolder.subject_description_full = (TextView) view.findViewById(R.id.subject_description_full);
		viewHolder.subject_description_layout = (LinearLayout) view.findViewById(R.id.subject_description_layout);
		viewHolder.subject_description_layout_short = (RelativeLayout) view.findViewById(R.id.subject_description_layout_short);
		viewHolder.subject_description_layout_full = (RelativeLayout) view.findViewById(R.id.subject_description_layout_full);
		viewHolder.subject_description_short.setOnClickListener(toggleDescription);
		viewHolder.subject_description_full.setOnClickListener(toggleDescription);
		viewHolder.quotation_grid = (GridView) view.findViewById(R.id.quotations);

		GridView gridView = viewHolder.quotation_grid;
		gridView.setOnItemClickListener(new GridViewOnItemClickListener(gridView));
		gridView.setAdapter(new LoadingAdapter());

		Bundle bundle = new Bundle();
        bundle.putParcelable(LOADER_BUNDLE_URI, Subject.withAppendedId(getPick().getId()));
        bundle.putStringArray(LOADER_BUNDLE_PROJECTION, new String[] { Subject._ID, Subject.NAME, Subject.DESCRIPTION, Subject.IMAGE_ID, BookmarkSubject.BOOKMARK_ID });
        subjectLoaderId = initLoader(this, bundle);
        
        quotationLoaderId = initLoader(this, SubjectQuotation.withAppendedId(getPick().getId()), QuotationQuery.PROJECTION, null, null, null);
        
        return view;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
	    switch (item.getItemId()) {
	    case R.id.bookmark:
			if (isBookmarked()) {
				getView().getContext().getContentResolver().delete(BookmarkSubject.withAppendedId(getPick().getId()), null, null);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.delete", getPick().getId(), null).build());
			}
			else {
			    ContentValues values = new ContentValues();
			    values.put(BookmarkSubject.BOOKMARK_ID, getPick().getId());
				getView().getContext().getContentResolver().insert(BookmarkSubject.withAppendedId(getPick().getId()), values);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.add", getPick().getId(), null).build());
			}
	        break;
	    }

	    return false;
	}

	private void updateSubject(Cursor cursor) {
		Log.d(TAG, String.format("updateSubject - cursor: %s (%d)", cursor, cursor.getCount()));
		if (cursor.moveToFirst()) {
			setSubjectName(cursor.getString(cursor.getColumnIndex(Subject.NAME)));
			setSubjectImage(cursor.getString(cursor.getColumnIndex(Subject.IMAGE_ID)));
			setSubjectDescription(cursor.getString(cursor.getColumnIndex(Subject.DESCRIPTION)));
			setBookmarked(cursor.getString(cursor.getColumnIndex(BookmarkSubject.BOOKMARK_ID)) != null);
		}
	}

	private void updateQuotations(Cursor cursor) {
		Log.d(TAG, String.format("updateQuotations - cursor: %s (%d)", cursor, cursor.getCount()));
		
		if (cursor.getCount() != 0) {
			GridView gridView = viewHolder.quotation_grid;
			if (gridView.getAdapter() instanceof QuotationPicksCursorAdapter) {
				QuotationPicksCursorAdapter adapter = (QuotationPicksCursorAdapter) gridView.getAdapter();
				adapter.swapCursor(cursor);
			}
			else {
				QuotationPicksCursorAdapter adapter = new QuotationPicksCursorAdapter(getView().getContext(), cursor, R.layout.quotation_pick_view, getImageLoader());
				gridView.setAdapter(adapter);
			}
		}
	}

	private void setSubjectName(String string) {
		setTextView(viewHolder.subject_name, string);
	}

	private void setSubjectDescription(Spanned text) {
		if (text != null) {
			setTextView(viewHolder.subject_description_short, text);
			setTextView(viewHolder.subject_description_full, text);
			View view = viewHolder.subject_description_layout;
			view.setVisibility(View.VISIBLE);
		}
	}

	private void setSubjectDescription(String text) {
		if (text != null) {
			text = text.replace("\n", "<p>").trim();
			setSubjectDescription(Html.fromHtml(text));
		}
	}

	private void setSubjectImage(String value) {
		Log.d(TAG, String.format("setAuthorImage - value: %s", value));
		
		NetworkImageView view = viewHolder.subject_image;
		if (view != null && value != null) {
			setNetworkImageView(view, value);
			view.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s", loader, cursor));
		
		if (loader.getId() == subjectLoaderId) {
			updateSubject(cursor);
		}
		else if (loader.getId() == quotationLoaderId) {
			updateQuotations(cursor);
		}
	}

	@Override
	protected String getTrackerCategory() {
		return "ui.subject";
	}
}

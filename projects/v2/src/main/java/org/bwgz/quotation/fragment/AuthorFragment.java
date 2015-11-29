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
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.PersonQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationQuery;
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

public class AuthorFragment extends PickFragment implements CursorLoaderListener {
	static public final String TAG = AuthorFragment.class.getSimpleName();

	private int authorLoaderId = -1;
	private int quotationLoaderId = -1;
	private ViewHolder viewHolder;
	
	private class ViewHolder {
		public NetworkImageView author_image;
		public TextView author_name;
		public TextView author_description_short;
		public TextView author_description_full;
		public TextView author_notable_for;
		public LinearLayout author_description_layout;
		public RelativeLayout author_description_layout_short;
		public RelativeLayout author_description_layout_full;
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
			RelativeLayout relativeLayout = viewHolder.author_description_layout_short;
			relativeLayout.setVisibility(relativeLayout.isShown() ? View.GONE : View.VISIBLE );
			
			relativeLayout = viewHolder.author_description_layout_full;
			relativeLayout.setVisibility(relativeLayout.isShown() ? View.GONE : View.VISIBLE );
		}
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - savedInstanceState: %s", savedInstanceState));

		View view = inflater.inflate(R.layout.author_fragment, container, false);

		viewHolder = new ViewHolder();
		viewHolder.author_name = (TextView) view.findViewById(R.id.author_name);
		viewHolder.author_image = (NetworkImageView) view.findViewById(R.id.author_image);
		viewHolder.author_description_short = (TextView) view.findViewById(R.id.author_description_short);
		viewHolder.author_description_full = (TextView) view.findViewById(R.id.author_description_full);
		viewHolder.author_notable_for = (TextView) view.findViewById(R.id.author_notable_for);
		viewHolder.author_description_layout = (LinearLayout) view.findViewById(R.id.author_description_layout);
		viewHolder.author_description_layout_short = (RelativeLayout) view.findViewById(R.id.author_description_layout_short);
		viewHolder.author_description_layout_full = (RelativeLayout) view.findViewById(R.id.author_description_layout_full);
		viewHolder.quotation_grid = (GridView) view.findViewById(R.id.quotations);

		viewHolder.author_description_short.setOnClickListener(toggleDescription);
		viewHolder.author_description_full.setOnClickListener(toggleDescription);
        
		GridView gridView = viewHolder.quotation_grid;
		gridView.setOnItemClickListener(new GridViewOnItemClickListener(gridView));
		gridView.setAdapter(new LoadingAdapter());

		Bundle bundle = new Bundle();
        bundle.putParcelable(LOADER_BUNDLE_URI, Person.withAppendedId(getPick().getId()));
        bundle.putStringArray(LOADER_BUNDLE_PROJECTION, new String[] { Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.CITATION_PROVIDER, Person.CITATION_STATEMENT, Person.CITATION_URI, BookmarkPerson.BOOKMARK_ID });
        authorLoaderId = initLoader(this, bundle);
        quotationLoaderId = initLoader(this, PersonQuotation.withAppendedId(getPick().getId()), QuotationQuery.PROJECTION, null, null, null);
        
        return view;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
	    switch (item.getItemId()) {
	    case R.id.bookmark:
			if (isBookmarked()) {
				getView().getContext().getContentResolver().delete(BookmarkPerson.withAppendedId(getPick().getId()), null, null);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.delete", getPick().getId(), null).build());
			}
			else {
			    ContentValues values = new ContentValues();
			    values.put(BookmarkPerson.BOOKMARK_ID, getPick().getId());
				getView().getContext().getContentResolver().insert(BookmarkPerson.withAppendedId(getPick().getId()), values);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.add", getPick().getId(), null).build());
			}
	        break;
	    }

	    return false;
	}

	private void updateAuthor(Cursor cursor) {
		Log.d(TAG, String.format("updateAuthor - cursor: %s (%d)", cursor, cursor.getCount()));
		if (cursor.moveToFirst()) {
			setAuthorName(cursor.getString(cursor.getColumnIndex(Person.NAME)));
			setAuthorNotableFor(cursor.getString(cursor.getColumnIndex(Person.NOTABLE_FOR)));
			setAuthorImage(cursor.getString(cursor.getColumnIndex(Person.IMAGE_ID)));
			setBookmarked(cursor.getString(cursor.getColumnIndex(BookmarkPerson.BOOKMARK_ID)) != null);
	        
			String description = cursor.getString(cursor.getColumnIndex(Person.DESCRIPTION));
			if (description != null) {
				StringBuilder descriptionBuffer = new StringBuilder();
	    		descriptionBuffer.append(description);
	    		
	    		String citation_provider = cursor.getString(cursor.getColumnIndex(Person.CITATION_PROVIDER));
	    		if (citation_provider != null) {
	        		String citation_statement = cursor.getString(cursor.getColumnIndex(Person.CITATION_STATEMENT));
	        		String citation_uri = cursor.getString(cursor.getColumnIndex(Person.CITATION_URI));
	    			descriptionBuffer.append(" ");
	    			descriptionBuffer.append(generateCitation(citation_provider, citation_statement, citation_uri));
	    		}
	    		
	    		setAuthorDescription(descriptionBuffer.toString());
			}
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
				QuotationPicksCursorAdapter adapter = new QuotationPicksCursorAdapter(getView().getContext(), cursor, R.layout.author_quotation_pick_view, getImageLoader());
				gridView.setAdapter(adapter);
			}
		}
	}

	private void setAuthorName(String string) {
		setTextView(viewHolder.author_name, string);
	}

	private void setAuthorDescription(Spanned text) {
		if (text != null) {
			setTextView(viewHolder.author_description_short, text);
			setTextView(viewHolder.author_description_full, text);
			View view = viewHolder.author_description_layout;
			view.setVisibility(View.VISIBLE);
		}
	}

	private void setAuthorDescription(String text) {
		if (text != null) {
			text = text.replace("\n", "<p>").trim();
			setAuthorDescription(Html.fromHtml(text));
		}
	}

	private void setAuthorNotableFor(String string) {
		setTextView(viewHolder.author_notable_for, string);
	}

	private void setAuthorImage(String value) {
		Log.d(TAG, String.format("setAuthorImage - value: %s", value));
		
		NetworkImageView view = viewHolder.author_image;
		if (view != null && value != null) {
			setNetworkImageView(view, value);
			view.setVisibility(View.VISIBLE);
		}
	}

    private String generateCitation(String provider, String statement, String uri) {
    	String citation;
    	
    	if (uri != null) {
    		citation = String.format("<a href=\"%s\" target=\"_new\" title=\"%s\">%s</a>", uri, statement != null ? statement : provider, provider);
    	}
    	else {
    		citation = String.format("[%s]", provider);
    	}
    	
    	return citation;
    }
    
	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s", loader, cursor));
		
		if (loader.getId() == authorLoaderId) {
			updateAuthor(cursor);
		}
		else if (loader.getId() == quotationLoaderId) {
			updateQuotations(cursor);
		}
	}

	@Override
	protected String getTrackerCategory() {
		return "ui.view.author";
	}
}

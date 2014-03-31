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
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Source;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.analytics.tracking.android.MapBuilder;

public class QuotationFragment extends PickFragment implements CursorLoaderListener {
	static public final String TAG = QuotationFragment.class.getSimpleName();

	private int quotationLoaderId;
	private int authorLoaderId;
	private ViewHolder viewHolder;
	
	private class ViewHolder {
		public TextView quotation_text;
		public TextView spoken_by_character;
		public TextView source;
		public NetworkImageView author_image;
		public TextView author_name;
		public TextView author_description_citation_full;
		public TextView author_description_short;
		public TextView author_description_full;
		public TextView author_notable_for;
		public LinearLayout author_description_layout;
		public RelativeLayout author_description_layout_short;
		public RelativeLayout author_description_layout_full;
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
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - savedInstanceState: %s", savedInstanceState));

		View view = inflater.inflate(R.layout.quotation_fragment, container, false);
		
		viewHolder = new ViewHolder();
		viewHolder.quotation_text = (TextView) view.findViewById(R.id.quotation_text);
		viewHolder.spoken_by_character = (TextView) view.findViewById(R.id.spoken_by_character);
		viewHolder.source = (TextView) view.findViewById(R.id.source);
		viewHolder.author_name = (TextView) view.findViewById(R.id.author_name);
		viewHolder.author_image = (NetworkImageView) view.findViewById(R.id.author_image);
		viewHolder.author_description_citation_full = (TextView) view.findViewById(R.id.author_description_citation_full);
		viewHolder.author_description_short = (TextView) view.findViewById(R.id.author_description_short);
		viewHolder.author_description_full = (TextView) view.findViewById(R.id.author_description_full);
		viewHolder.author_notable_for = (TextView) view.findViewById(R.id.author_notable_for);
		viewHolder.author_description_layout = (LinearLayout) view.findViewById(R.id.author_description_layout);
		viewHolder.author_description_layout_short = (RelativeLayout) view.findViewById(R.id.author_description_layout_short);
		viewHolder.author_description_layout_full = (RelativeLayout) view.findViewById(R.id.author_description_layout_full);
		
		viewHolder.author_description_layout.setOnClickListener(toggleDescription);
		
		if (viewHolder.author_image != null) {
			viewHolder.author_image.setDefaultImageResId(R.drawable.pick_image_holder);
		}

        Bundle bundle = new Bundle();
        bundle.putParcelable(LOADER_BUNDLE_URI, Quotation.withAppendedId(getPick().getId()));
        bundle.putStringArray(LOADER_BUNDLE_PROJECTION, new String[] { Quotation.QUOTATION, Quotation.SPOKEN_BY_CHARACTER, Source.NAME, Source.TYPE, BookmarkQuotation.BOOKMARK_ID });
        quotationLoaderId = initLoader(this, bundle);

        bundle = new Bundle();
        bundle.putParcelable(LOADER_BUNDLE_URI, QuotationPerson.withAppendedId(getPick().getId()));
        bundle.putStringArray(LOADER_BUNDLE_PROJECTION, new String[] { Person._ID, Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.CITATION_PROVIDER, Person.CITATION_STATEMENT, Person.CITATION_URI });
        authorLoaderId = initLoader(this, bundle);
        
        return view;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
	    switch (item.getItemId()) {
	    case R.id.bookmark:
			if (isBookmarked()) {
				getView().getContext().getContentResolver().delete(BookmarkQuotation.withAppendedId(getPick().getId()), null, null);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.delete", getPick().getId(), null).build());
			}
			else {
			    ContentValues values = new ContentValues();
			    values.put(BookmarkQuotation.BOOKMARK_ID, getPick().getId());
				getView().getContext().getContentResolver().insert(BookmarkQuotation.withAppendedId(getPick().getId()), values);
	    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "bookmark.add", getPick().getId(), null).build());
			}
	        break;
    	case R.id.share:
			String quotation = (String) viewHolder.quotation_text.getText();
			String author = (String) viewHolder.author_name.getText();
			if (author != null && author.length() != 0) {
				quotation += " " + author;
			}

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, quotation);
			startActivity(Intent.createChooser(intent, "Quotation"));
    	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), "share", getPick().getId(), null).build());
    	    break;
	    }

	    return false;
	}

	private void setQuotation(String string) {
		setTextView(viewHolder.quotation_text, string);
	}
	
	private void setSource(String name, String type) {
		Log.d(TAG, String.format("setSource - name: %s  type: %s", name, type));
		if (name != null) {
			String string;
			
			if (type == null) {
				string = type;
			}
			else {
				string = String.format("%s (%s)", name, type);
			}
		
			setTextView(viewHolder.source, string);
			
			View view = getView().findViewById(R.id.spoken_by_character_source_layout);
			view.setVisibility(View.VISIBLE);
			view = getView().findViewById(R.id.source_layout);
			view.setVisibility(View.VISIBLE);
		}
	}
	
	private void setSpokenByCharacter(String string) {
		Log.d(TAG, String.format("setSpokenByCharacter - string: %s", string));
		if (string != null) {
			setTextView(viewHolder.spoken_by_character, string);
			View view = getView().findViewById(R.id.spoken_by_character_source_layout);
			view.setVisibility(View.VISIBLE);
			view = getView().findViewById(R.id.spoken_by_character_layout);
			view.setVisibility(View.VISIBLE);
		}
	}

	private void updateQuotation(Cursor cursor) {
		Log.d(TAG, String.format("updateQuotation - cursor: %s (%d)", cursor, cursor.getCount()));
		if (cursor.moveToFirst()) {
			setQuotation(cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION)));
			setSpokenByCharacter(cursor.getString(cursor.getColumnIndex(Quotation.SPOKEN_BY_CHARACTER)));
			setSource(cursor.getString(cursor.getColumnIndex(Source.NAME)), cursor.getString(cursor.getColumnIndex(Source.TYPE)));
			setBookmarked(cursor.getString(cursor.getColumnIndex(BookmarkQuotation.BOOKMARK_ID)) != null);
		}
	}

	private void setAuthorName(String string) {
		setTextView(viewHolder.author_name, string);
		View view = getView().findViewById(R.id.author_name_layout);
		view.setVisibility(View.VISIBLE);
	}

	private void setAuthorDescriptionCitation(String text) {
		if (text != null) {
			setTextView(viewHolder.author_description_citation_full, Html.fromHtml(text));
			View view = getView().findViewById(R.id.author_description_citation_full);
			view.setVisibility(View.VISIBLE);
		}
	}

	private void setAuthorDescription(String text) {
		if (text != null) {
			text = text.trim();
			setTextView(viewHolder.author_description_short, text);
			setTextView(viewHolder.author_description_full, text);
			View view = viewHolder.author_description_layout;
			view.setVisibility(View.VISIBLE);
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

	private void updateAuthor(Cursor cursor) {
		Log.d(TAG, String.format("updateAuthor - cursor: %s (%d)", cursor, cursor.getCount()));
		if (cursor.moveToFirst()) {
			setAuthorName(cursor.getString(cursor.getColumnIndex(Person.NAME)));
			setAuthorNotableFor(cursor.getString(cursor.getColumnIndex(Person.NOTABLE_FOR)));
			setAuthorImage(cursor.getString(cursor.getColumnIndex(Person.IMAGE_ID)));
			setAuthorDescription(cursor.getString(cursor.getColumnIndex(Person.DESCRIPTION)));
			
	        StringBuilder buffer = new StringBuilder();
    		String citation_provider = cursor.getString(cursor.getColumnIndex(Person.CITATION_PROVIDER));
    		
    		if (citation_provider != null) {
        		String citation_statement = cursor.getString(cursor.getColumnIndex(Person.CITATION_STATEMENT));
        		String citation_uri = cursor.getString(cursor.getColumnIndex(Person.CITATION_URI));
    			buffer.append(" ");
    			buffer.append(generateCitation(citation_provider, citation_statement, citation_uri));
    		}
			
    		setAuthorDescriptionCitation(buffer.toString().trim().replace("\n", "<p>"));
		}
	}

	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s", loader, cursor));
		
		if (quotationLoaderId == loader.getId()) {
			updateQuotation(cursor);
		}
		else if (authorLoaderId == loader.getId()) {
			updateAuthor(cursor);
		}
	}

	@Override
	protected String getTrackerCategory() {
		return "ui.view.quotation";
	}
}

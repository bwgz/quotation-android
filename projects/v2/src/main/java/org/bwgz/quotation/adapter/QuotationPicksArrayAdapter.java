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

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Source;
import org.bwgz.quotation.core.CursorLoaderManager;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.model.picks.Pick;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class QuotationPicksArrayAdapter extends PicksArrayAdapter {
	static public final String TAG = QuotationPicksArrayAdapter.class.getSimpleName();
	
	static private class ViewHolder {
		public TextView quotation_text;
		public TextView author_name;
		public NetworkImageView author_image;
		public CheckBox bookmark;
		
		public List<Integer> loaderIds;
	}

	private class QuotationCursorLoaderListener implements CursorLoaderListener {
		private ViewHolder viewHolder;
		
		public QuotationCursorLoaderListener(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		@Override
		public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			Log.d(TAG, String.format("onLoadCursorFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			
			if (cursor.moveToFirst()) {
				setTextView(viewHolder.quotation_text, cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION)));
				setCheckBox(viewHolder.bookmark, cursor.getString(cursor.getColumnIndex(BookmarkQuotation.BOOKMARK_ID)) != null);
			}
		}
	}

	private class AuthorCursorLoaderListener implements CursorLoaderListener {
		private ViewHolder viewHolder;
		
		public AuthorCursorLoaderListener(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		@Override
		public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			Log.d(TAG, String.format("onLoadCursorFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			
			StringBuilder builder = new StringBuilder();
			String image_id = null;
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				
				if (i == 0) {
					image_id = cursor.getString(cursor.getColumnIndex(Person.IMAGE_ID));
				}
				if (i != 0) {
					builder.append(", ");
				}
				
				builder.append(cursor.getString(cursor.getColumnIndex(Person.NAME)));
			}
			
			setTextView(viewHolder.author_name, builder.toString());
			setNetworkImageView(viewHolder.author_image, image_id);
		}
	}
	
	public QuotationPicksArrayAdapter(Context context, int resId, List<Pick> picks, CursorLoaderManager cursorLoaderManager, ImageLoader imageLoader) {
		super(context, resId, picks, cursorLoaderManager, imageLoader);
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		Log.d(TAG, String.format("getView - position: %d  convertView: %s  parent: %s", position, convertView, parent));
		View view = null;
		String id = getItem(position).getId();
		ViewHolder viewHolder;
		
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();

            setTextView(viewHolder.quotation_text, R.string.loading_quotation);
			//setTextView(viewHolder.quotation_text, new String());
			setTextView(viewHolder.author_name, new String());
			setCheckBox(viewHolder.bookmark, false);		
			
			List<Integer> loaderIds = (List<Integer>) viewHolder.loaderIds;
			if (loaderIds != null) {
				for (int loaderId : loaderIds) {
					getLoaderManager().destroyLoader(loaderId);
				}
			}
			view = convertView;
		}
		else {
	    	LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(getResId(), parent, false);
			
			viewHolder = new ViewHolder();
			
			viewHolder.quotation_text = (TextView) view.findViewById(R.id.quotation_text);
			viewHolder.author_name = (TextView) view.findViewById(R.id.author_name);
			viewHolder.author_image = (NetworkImageView) view.findViewById(R.id.author_image);
			viewHolder.bookmark = (CheckBox) view.findViewById(R.id.bookmark);
		}

		List<Integer> loaderIds = new ArrayList<Integer>();
        int loaderId = getLoaderManager().initLoader(new QuotationCursorLoaderListener(viewHolder), Quotation.withAppendedId(id), new String[] { BookmarkQuotation.BOOKMARK_ID, Quotation.FULL_ID, Quotation.QUOTATION, Source.NAME, Source.TYPE }, null, null, null);
        loaderIds.add(loaderId);
        loaderId = getLoaderManager().initLoader(new AuthorCursorLoaderListener(viewHolder), QuotationPerson.withAppendedId(id), new String[] { Person.FULL_ID, Person.NAME, Person.IMAGE_ID }, null, null, null);
        loaderIds.add(loaderId);
        viewHolder.loaderIds = loaderIds;
        
		view.setTag(viewHolder);
		
		if (viewHolder.author_image != null) {
			viewHolder.author_image.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.pick_image_holder));
		}

		if (viewHolder.bookmark != null) {
			viewHolder.bookmark.setOnClickListener(new BookmarkOnClickListener(view.getContext(), BookmarkQuotation.withAppendedId(id), BookmarkQuotation.BOOKMARK_ID, id));
		}
		
        return view;
	}
}

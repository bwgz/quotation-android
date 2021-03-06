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
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
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

public class SubjectPicksArrayAdapter extends PicksArrayAdapter {
	static public final String TAG = SubjectPicksArrayAdapter.class.getSimpleName();

	static private class ViewHolder {
		public TextView subject_name;
		public TextView subject_description;
		public NetworkImageView subject_image;
		public TextView quotation_count;
		public CheckBox bookmark;
		
		public List<Integer> loaderIds;
	}

	private class AuthorCursorLoaderListener implements CursorLoaderListener {
		private ViewHolder viewHolder;
		
		public AuthorCursorLoaderListener(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}
		
		@Override
		public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			Log.d(TAG, String.format("onLoadCursorFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			
			if (cursor.moveToFirst()) {
				setTextView(viewHolder.subject_name, cursor.getString(cursor.getColumnIndex(Subject.NAME)));
				setTextView(viewHolder.subject_description, cursor.getString(cursor.getColumnIndex(Subject.DESCRIPTION)));
				setNetworkImageView(viewHolder.subject_image, cursor.getString(cursor.getColumnIndex(Subject.IMAGE_ID)));
				setTextView(viewHolder.quotation_count, String.valueOf(cursor.getLong(cursor.getColumnIndex(Subject.QUOTATION_COUNT))));
				setCheckBox(viewHolder.bookmark, cursor.getString(cursor.getColumnIndex(BookmarkSubject.BOOKMARK_ID)) != null);
			}
		}
	}
	
	public SubjectPicksArrayAdapter(Context context, int resId, List<Pick> picks, CursorLoaderManager cursorLoaderManager, ImageLoader immageLoader) {
		super(context, resId, picks, cursorLoaderManager, immageLoader);
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		Log.d(TAG, String.format("getView - position: %d  convertView: %s  parent: %s", position, convertView, parent));
		View view = null;
		String id = getItem(position).getId();
		ViewHolder viewHolder;
		
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
			
			setTextView(viewHolder.subject_name, new String());
			setTextView(viewHolder.subject_description, new String());
			setImageViewBitmap(viewHolder.subject_image, null);
			setTextView(viewHolder.quotation_count, new String());
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
			
			viewHolder = new ViewHolder();
			viewHolder.subject_name = (TextView) view.findViewById(R.id.subject_name);
			viewHolder.subject_description = (TextView) view.findViewById(R.id.subject_description);
			viewHolder.subject_image = (NetworkImageView) view.findViewById(R.id.subject_image);
			viewHolder.quotation_count = (TextView) view.findViewById(R.id.quotation_count);
			viewHolder.bookmark = (CheckBox) view.findViewById(R.id.bookmark);
		}

		List<Integer> loaderIds = new ArrayList<Integer>();
        int loaderId = getLoaderManager().initLoader(new AuthorCursorLoaderListener(viewHolder), Subject.withAppendedId(id), new String[] { Subject.FULL_ID, Subject.NAME, Subject.DESCRIPTION, Subject.IMAGE_ID, Subject.QUOTATION_COUNT, BookmarkSubject.BOOKMARK_ID }, null, null, null);
        loaderIds.add(loaderId);
        viewHolder.loaderIds = loaderIds;
        
		view.setTag(viewHolder);
		
		if (viewHolder.subject_image != null) {
			viewHolder.subject_image.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.pick_image_holder));
		}

		if (viewHolder.bookmark != null) {
			viewHolder.bookmark.setOnClickListener(new BookmarkOnClickListener(view.getContext(), BookmarkSubject.withAppendedId(id), BookmarkQuotation.BOOKMARK_ID, id));
		}
		
        return view;
	}

}

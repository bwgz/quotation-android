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

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class SubjectPicksCursorAdapter extends PicksCursorAdapter {
	static public final String TAG = SubjectPicksCursorAdapter.class.getSimpleName();

	static private class ViewHolder {
		public TextView subject_name;
		public TextView subject_description;
		public NetworkImageView subject_image;
		public TextView quotation_count;
		public CheckBox bookmark;
	}

	public SubjectPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader immageLoader) {
		super(context, cursor, resId, immageLoader);
	}

	public SubjectPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader, int maxCount) {
		super(context, cursor, resId, imageLoader, maxCount);
		Log.d(TAG, String.format("SubjectPicksCursorAdapter - context: %s  cursor: %s (%d)  resId: %d  imageLoader: %s  maxCount: %d", context, cursor, cursor.getCount(), resId, imageLoader, maxCount));
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
		Log.d(TAG, String.format("newView - context: %s  cursor: %s (%d)   parent: %s", context, cursor, cursor.getCount(), parent));
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(getResId(), parent, false);

		ViewHolder viewHolder = new ViewHolder();
		viewHolder.subject_name = (TextView) view.findViewById(R.id.subject_name);
		viewHolder.subject_description = (TextView) view.findViewById(R.id.subject_description);
		viewHolder.subject_image = (NetworkImageView) view.findViewById(R.id.subject_image);
		viewHolder.quotation_count = (TextView) view.findViewById(R.id.quotation_count);
		viewHolder.bookmark = (CheckBox) view.findViewById(R.id.bookmark);
		view.setTag(viewHolder);

		String id = cursor.getString(cursor.getColumnIndex(Subject._ID));
		Log.d(TAG, String.format("newView - view: %s  id: %s", view, id));

		if (viewHolder.bookmark != null) {
			viewHolder.bookmark.setOnClickListener(new BookmarkOnClickListener(context, BookmarkSubject.withAppendedId(id), BookmarkQuotation.BOOKMARK_ID, id));
		}

		if (viewHolder.subject_image != null) {
			viewHolder.subject_image.setImageResource(R.drawable.pick_image_holder);
			viewHolder.subject_image.setDefaultImageResId(R.drawable.pick_image_holder);
		}

		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d(TAG, String.format("bindView - view: %s  context: %s  cursor: %s (%d)", view, context, cursor, cursor.getCount()));
		
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		setTextView(viewHolder.subject_name, cursor.getString(cursor.getColumnIndex(Subject.NAME)));
		setTextView(viewHolder.subject_description, cursor.getString(cursor.getColumnIndex(Subject.DESCRIPTION)));
		setNetworkImageView(viewHolder.subject_image, cursor.getString(cursor.getColumnIndex(Subject.IMAGE_ID)));
		setTextView(viewHolder.quotation_count, cursor.getString(cursor.getColumnIndex(Subject.QUOTATION_COUNT)));
		setCheckBox(viewHolder.bookmark, cursor.getString(cursor.getColumnIndex(BookmarkSubject.BOOKMARK_ID)) != null);
	}
}

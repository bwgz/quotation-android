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
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

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

public class QuotationPicksCursorAdapter extends PicksCursorAdapter {
	static public final String TAG = QuotationPicksCursorAdapter.class.getSimpleName();
	
	static private class ViewHolder {
		public TextView quotation_text;
		public TextView author_name;
		public NetworkImageView author_image;
		public CheckBox bookmark;
	}

	public QuotationPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader) {
		super(context, cursor, resId, imageLoader);
	}

	public QuotationPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader, int maxCount) {
		super(context, cursor, resId, imageLoader, maxCount);
		Log.d(TAG, String.format("QuotationPicksCursorAdapter - context: %s  cursor: %s (%d)  resId: %d  imageLoader: %s  maxCount: %d", context, cursor, cursor.getCount(), resId, imageLoader, maxCount));
	}

	private void setAuthorName(TextView view, String value) {
		Log.d(TAG, String.format("setAuthor - value: %s", value));
		
		if (value != null) {
			value = value.replace(",", ", ");
		}
		
		setTextView(view, value);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.d(TAG, String.format("newView - context: %s  cursor: %s (%d)   parent: %s", context, cursor, cursor.getCount(), parent));
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(getResId(), parent, false);
		
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.quotation_text = (TextView) view.findViewById(R.id.quotation_text);
		viewHolder.author_name = (TextView) view.findViewById(R.id.author_name);
		viewHolder.author_image = (NetworkImageView) view.findViewById(R.id.author_image);
		viewHolder.bookmark = (CheckBox) view.findViewById(R.id.bookmark);
		view.setTag(viewHolder);
		
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d(TAG, String.format("bindView - view: %s  context: %s  cursor: %s (%d)", view, context, cursor, cursor.getCount()));
		Log.d(TAG, String.format("bindView - id: %s  checkBox: %s  text: %s", cursor.getString(cursor.getColumnIndex(Quotation._ID)), cursor.getString(cursor.getColumnIndex(BookmarkQuotation.BOOKMARK_ID)), cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION))));
		
		ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder.bookmark != null) {
            String id = cursor.getString(cursor.getColumnIndex(Quotation._ID));
            viewHolder.bookmark.setOnClickListener(new BookmarkOnClickListener(context, BookmarkQuotation.withAppendedId(id), BookmarkQuotation.BOOKMARK_ID, id));
        }

        if (viewHolder.author_image != null) {
            viewHolder.author_image.setImageResource(R.drawable.pick_image_holder);
            viewHolder.author_image.setDefaultImageResId(R.drawable.pick_image_holder);
        }

        setTextView(viewHolder.quotation_text, cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION)));
		setCheckBox(viewHolder.bookmark, cursor.getString(cursor.getColumnIndex(BookmarkQuotation.BOOKMARK_ID)) != null);
		setAuthorName(viewHolder.author_name, cursor.getString(cursor.getColumnIndex(Quotation.AUTHOR_NAMES)));
		setNetworkImageView(viewHolder.author_image, cursor.getString(cursor.getColumnIndex(Quotation.AUTHOR_IMAGE_IDS)));
	}
}

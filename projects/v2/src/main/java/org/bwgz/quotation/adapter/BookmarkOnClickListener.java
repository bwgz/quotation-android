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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class BookmarkOnClickListener implements OnClickListener {
	static public final String TAG = BookmarkOnClickListener.class.getSimpleName();
	private Context context;
	private Uri uri;
	private String key;
	private String id;

	public BookmarkOnClickListener(Context context, Uri uri, String key, String id) {
		this.context = context;
		this.uri = uri;
		this.key = key;
		this.id = id;
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, String.format("onClick - view: %s ", view));
		CheckBox checkBox = (CheckBox) view;
		Log.d(TAG, String.format("onClick - uri: %s  isChecked: %s ", uri, checkBox.isChecked()));
		
		if (checkBox.isChecked()) {
		    ContentValues values = new ContentValues();
		    values.put(key, id);
			context.getContentResolver().insert(uri, values);
		}
		else {
			context.getContentResolver().delete(uri, null, null);
		}
	}
}


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

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;
import org.bwgz.quotation.core.CursorLoaderManager;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public abstract class PicksCursorAdapter extends CursorAdapter {
	static public final String TAG = PicksCursorAdapter.class.getSimpleName();

	private int resId;
	private CursorLoaderManager cursorLoaderManager;
	private ImageLoader imageLoader;
	private int maxCount;
	private FreebaseHelper freebaseHelper;

	public PicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader, int maxCount) {
		super(context, cursor, 0);
		Log.d(TAG, String.format("PicksCursorAdapter - context: %s  cursor: %s (%d)  resId: %d  imageLoader: %s  maxCount: %d", context, cursor, cursor.getCount(), resId, imageLoader, maxCount));
		
		this.resId = resId;
		this.imageLoader = imageLoader;
		this.maxCount = maxCount;
		this.freebaseHelper = FreebaseHelper.makeInstance(context);
	}

	public PicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader) {
		this(context, cursor, resId, imageLoader, Integer.MAX_VALUE);
		Log.d(TAG, String.format("PicksCursorAdapter - context: %s  cursor: %s (%d)  resId: %d  imageLoader: %s", context, cursor, cursor.getCount(), resId, imageLoader));
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	    if (observer != null) {
	        super.unregisterDataSetObserver(observer);
	    }
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public CursorLoaderManager getCursorLoaderManager() {
		return cursorLoaderManager;
	}

	public void setCursorLoaderManager(CursorLoaderManager loaderManager) {
		this.cursorLoaderManager = loaderManager;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	protected void setTextView(TextView view, String value) {
        if (view != null) {
        	view.setText(value != null ? value : new String());
        }
	}

	protected void setCheckBox(CheckBox view, boolean value) {
		if (view != null) {
			view.setChecked(value);
		}
	}
	
	protected void setTextView(View view, int resId, String text) {
        TextView textView = (TextView) view.findViewById(resId);
        if (textView != null) {
        	textView.setText(text != null ? text : new String());
        }
	}

	protected void setCheckBox(View view, int resId, boolean checked) {
		CheckBox checkBox = (CheckBox) view.findViewById(resId);
		if (checkBox != null) {
			checkBox.setChecked(checked);
		}
	}
	
	protected void setNetworkImageView(NetworkImageView view, String value) {
		Log.d(TAG, String.format("setNetworkImageView - view: %s  value: %s", view, value));
		
		if (view != null) {
			if (value != null) {
				String url = freebaseHelper.getImageUrl(value, view.getWidth(), view.getHeight());
				Log.d(TAG, String.format("setNetworkImageView - url: %s", url));
				view.setImageUrl(url, getImageLoader());
			}
			else {
				view.setImageResource(R.drawable.pick_image_holder);
			}
		}
	}

	protected void setImageViewBitmap(View view, int resId, Bitmap bitmap) {
        ImageView imageView = (ImageView) view.findViewById(resId);
        if (imageView != null) {
        	imageView.setImageBitmap(bitmap);
        }
	}
	
	@Override
	public int getCount() {
		return Math.min(super.getCount(), maxCount);
	}
}

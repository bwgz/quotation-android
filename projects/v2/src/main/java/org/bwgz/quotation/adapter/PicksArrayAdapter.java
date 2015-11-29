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

import java.util.List;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;
import org.bwgz.quotation.core.CursorLoaderManager;
import org.bwgz.quotation.model.picks.Pick;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public abstract class PicksArrayAdapter extends ArrayAdapter<Pick> {
	static public final String TAG = PicksArrayAdapter.class.getSimpleName();

	private int resId;
	private CursorLoaderManager cursorLoaderManager;
	private ImageLoader imageLoader;
	private FreebaseHelper freebaseHelper;

	public PicksArrayAdapter(Context context, int resId, List<Pick> picks, CursorLoaderManager cursorLoaderManager, ImageLoader imageLoader) {
		super(context, resId, picks);
		Log.d(TAG, String.format("PicksCursorAdapter - context: %s  picks: %s (%d)  resId: %d  loaderManager: %s", context, picks, picks.size(), resId, cursorLoaderManager));
		this.resId = resId;
		this.cursorLoaderManager = cursorLoaderManager;
		this.imageLoader = imageLoader;
		this.freebaseHelper = FreebaseHelper.makeInstance(context);
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public CursorLoaderManager getLoaderManager() {
		return cursorLoaderManager;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

    protected void setTextView(TextView view, int resId) {
        if (view != null) {
            view.setText(resId);
        }
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

	protected void setImageViewBitmap(ImageView view, Bitmap value) {
        if (view != null) {
        	view.setImageBitmap(value);
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
}

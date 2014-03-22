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

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.PickViewPagerActivity;
import org.bwgz.quotation.model.picks.Pick;

import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.analytics.tracking.android.MapBuilder;

public abstract class PickFragment extends CursorLoaderManagerFragment {
	static public final String TAG = PickFragment.class.getSimpleName();
	
	static private final String EVENT_ACTION_START	= "start";
	static private final String EVENT_ACTION_STOP	= "stop";

	private FreebaseHelper freebaseHelper;
	private Pick pick;
	private boolean bookmarked;

	abstract protected String getTrackerCategory();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));

		freebaseHelper = FreebaseHelper.makeInstance(getActivity());

		Bundle bundle = getArguments();
		String id = bundle.getString(PickViewPagerActivity.BUNDLE_ID);
		Log.d(TAG, String.format("onCreate - id: %s", id));
		
		setPick(new Pick(id));
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		Log.d(TAG, String.format("onPrepareOptionsMenu - menu: %s", menu));
		
		MenuItem item = menu.findItem(R.id.bookmark);
		if (item != null) {
			item.setIcon(bookmarked ? R.drawable.ic_bookmark_on : R.drawable.ic_bookmark_off);
			item.setChecked(bookmarked);
		}
	}

    @Override
    public void onStart() {
        super.onStart();
		Log.d(TAG, String.format("onStart - this: %s", this));

	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), EVENT_ACTION_START, getPick().getId(), null).build());
    }

    @Override
    public void onStop() {
        super.onStop();
		Log.d(TAG, String.format("onStop - this: %s", this));

	    getTracker().send(MapBuilder.createEvent(getTrackerCategory(), EVENT_ACTION_STOP, getPick().getId(), null).build());
    }

	protected Pick getPick() {
		return pick;
	}

	protected void setPick(Pick pick) {
		this.pick = pick;
	}
	
	protected boolean isBookmarked() {
		return bookmarked;
	}

	protected void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
		getActivity().invalidateOptionsMenu();
	}

	protected void setTextView(TextView view, String value) {
        if (view != null) {
        	view.setText(value != null ? value : new String());
        }
	}

	protected void setTextView(TextView view, Spanned value) {
		if (view != null) {
			view.setText(value != null ? value : new String());
		}
	}

	protected void setCheckBox(CheckBox view, boolean value) {
		if (view != null) {
			view.setChecked(value);
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
		
		if (view != null && value != null) {
			String url = freebaseHelper.getImageUrl(value, view.getWidth(), view.getHeight());
			Log.d(TAG, String.format("setNetworkImageView - url: %s", url));
			view.setImageUrl(url, getImageLoader());
		}
	}
}

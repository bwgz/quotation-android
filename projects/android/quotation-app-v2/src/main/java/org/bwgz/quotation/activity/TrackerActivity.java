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
package org.bwgz.quotation.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

public class TrackerActivity extends FragmentActivity {
	static public final String TAG = TrackerActivity.class.getSimpleName();

	private EasyTracker tracker;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
		
		tracker = EasyTracker.getInstance(this);
    }

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, String.format("onStart - this: %s", this));
		getTracker().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, String.format("onStop - this: %s", this));
		getTracker().activityStop(this);
	}

	public EasyTracker getTracker() {
		return tracker;
	}
}

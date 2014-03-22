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

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class TrackerFragment extends Fragment {
	static public final String TAG = TrackerFragment.class.getSimpleName();
	private EasyTracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
        
		tracker = EasyTracker.getInstance(getActivity());
    }

	public EasyTracker getTracker() {
		return tracker;
	}
}

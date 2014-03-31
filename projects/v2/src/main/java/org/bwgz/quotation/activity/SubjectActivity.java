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

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.fragment.SubjectFragment;
import org.bwgz.quotation.fragment.VolleyFragment;
import org.bwgz.quotation.search.FreebaseSearch;

import com.google.analytics.tracking.android.EasyTracker;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SubjectActivity extends PickViewPagerActivity {
	static public final String TAG = SubjectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));

        setTitle(R.string.subject_title);
    }
    
	@Override
	public void onStart() {
		super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected String getIdFromUri(Uri uri) {
		return Subject.getId(uri);
	}

	@Override
	protected Class<? extends VolleyFragment> getPickFragmentClass() {
		return SubjectFragment.class;
	}

	@Override
	protected CharSequence getQueryHint() {
		return getString(R.string.search_subject);
	}
	
	@Override
	protected int getSearchType() {
		return FreebaseSearch.SEARCH_TYPE_SUBJECT;
	}
	
	@Override
    protected int getMenuResId() {
    	return R.menu.pick_options_menu;
    }

}

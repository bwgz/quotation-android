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

import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.SubjectActivity;
import org.bwgz.quotation.adapter.PicksArrayAdapter;
import org.bwgz.quotation.adapter.SubjectPicksArrayAdapter;
import org.bwgz.quotation.core.FreebaseIdLoader;
import org.bwgz.quotation.model.picks.Pick;

import android.app.Activity;
import android.content.Context;

public class RandomSubjectsFragment extends RandomFragment {
	static public final String TAG = RandomSubjectsFragment.class.getSimpleName();

	@Override
    protected PicksArrayAdapter getAdapter(Context context) {
		return new SubjectPicksArrayAdapter(context, R.layout.subject_list_pick_view, getPicks(), this, getImageLoader());
    }
    
	@Override
	protected List<Pick> getRandomPicks() {
        FreebaseIdLoader freebaseIdLoader = FreebaseIdLoader.getInstance(getActivity().getApplicationContext());
		return freebaseIdLoader.getRandomSubjectPicks();
	}

	@Override
	protected Class<? extends Activity> getActivityClass() {
		return SubjectActivity.class;
	}
}

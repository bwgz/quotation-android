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

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.QuotationActivity;
import org.bwgz.quotation.adapter.PicksArrayAdapter;
import org.bwgz.quotation.model.picks.Pick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public abstract class RandomFragment extends CursorLoaderManagerFragment {
	static public final String TAG = RandomFragment.class.getSimpleName();

	static private final String	SAVED_INSTANCE_IDS	= "saved.instance.ids";
	
	private List<Pick> picks;

	abstract protected List<Pick> getRandomPicks();
	abstract protected PicksArrayAdapter getAdapter(Context context);
	abstract protected Class<? extends Activity> getActivityClass();
	
	private List<Pick> fromArray(String[] ids) {
		List<Pick> picks = new ArrayList<Pick>();
		
		for (String id: ids) {
			picks.add(new Pick(id));
		}
		
		return picks;
	}
	
	private String[] toArray(List<Pick> picks) {
		String[] ids = new String[picks.size()];
		
		for (int i = 0; i < picks.size(); i++) {
			ids[i] = picks.get(i).getId();
		}
		
		return ids;
	}
	
    public List<Pick> getPicks() {
		return picks;
	}

	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
		
		if (savedInstanceState != null) {
			String[] ids = savedInstanceState.getStringArray(SAVED_INSTANCE_IDS);
			if (ids != null) {
				picks = fromArray(ids);
			}
		}
		
		if (picks == null) {
			picks = getRandomPicks();
		}
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - this: %s  inflater: %s  container: %s  savedInstanceState: %s", this, inflater, container, savedInstanceState));
		View view = inflater.inflate(R.layout.standard_list_view, container, false);
		
		final String[] ids = toArray(getPicks());
		ListView listView = (ListView) view.findViewById(R.id.list_view);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, String.format("onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
				Intent intent = new Intent(view.getContext(), getActivityClass());
				intent.putExtra(QuotationActivity.EXTRA_IDS, ids);
				intent.putExtra(QuotationActivity.EXTRA_POSITION,  position);
				
				startActivity(intent);
			}
		});
		
		PicksArrayAdapter adapter = getAdapter(listView.getContext());
		listView.setAdapter(adapter);
		
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	outState.putStringArray(SAVED_INSTANCE_IDS, toArray(picks));
    }
}

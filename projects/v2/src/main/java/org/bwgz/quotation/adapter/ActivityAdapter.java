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

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ActivityAdapter extends BaseAdapter {
	static public final String TAG = ActivityAdapter.class.getSimpleName();
	
	private View[] views;
	
	public ActivityAdapter(View[] views) {
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public Object getItem(int position) {
		return views[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return views[position];
	}

}

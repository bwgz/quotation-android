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

import org.bwgz.quotation.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class LoadingAdapter extends BaseAdapter {
	static public final String TAG = LoadingAdapter.class.getSimpleName();

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, String.format("getView - position: %d  convertView: %s  parent: %s", position, convertView, parent));
		
		View view = null;
		
		if (convertView != null) {
			view = convertView;
		}
		else {
	    	LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.waiting_pick, parent, false);
			
			ImageView imageView = (ImageView) view.findViewById(R.id.loading);
			Animation animation = AnimationUtils.loadAnimation(imageView.getContext(), R.anim.simple_rotate);
			imageView.startAnimation(animation);
		}

        return view;
	}

}

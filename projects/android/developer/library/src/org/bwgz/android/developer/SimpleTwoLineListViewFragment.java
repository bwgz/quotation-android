/*
 * Copyright (C) 2013 bwgz.org
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

package org.bwgz.android.developer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;

public abstract class SimpleTwoLineListViewFragment<T extends TwoLineData> extends SimpleListViewFragment<T> {
	abstract protected List<T> getList();
	
	protected ArrayAdapter<T> getAdapter(Context context) {
		return new ArrayAdapter<T>(context, android.R.layout.simple_list_item_2, getList()){
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent){
	            TwoLineListItem view;
	            if(convertView == null){
	                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
	            }else{
	                view = (TwoLineListItem)convertView;
	            }
	            T data = getItem(position);
	            view.getText1().setText(data.getName());
	            view.getText2().setText(data.getValue());

	            return view;
	        }
	    };
	}
}


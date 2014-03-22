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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter {
	private List<DrawerItem> items;

	public class DrawerItem {
		private String	text;
		private boolean selected;
		
		public DrawerItem(String text, boolean selected) {
			this.text = text;
			this.selected = selected;
		}
		
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}
	
	public DrawerAdapter(String[] items, int selected) {
		List<DrawerItem> list = new ArrayList<DrawerItem>();
		
		for (int i = 0; i < items.length; i++) {
			list.add(new DrawerItem(items[i], i == selected));
		}
		
		setItems(list);
	}
	
	public List<DrawerItem> getItems() {
		return items;
	}

	public void setItems(List<DrawerItem> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;

		if (convertView == null) {
			DrawerItem item = (DrawerItem) getItem(position);
        	
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            
            view.setText(item.getText());
            if (item.isSelected()) {
            	view.setTypeface(null, Typeface.BOLD);
            }
        } else {
            view = (TextView) convertView;
        }

        return view;
	}

}

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
package org.bwgz.android.common;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TwoLineListItem;

public abstract class TwoLineListActivity extends Activity {
	private ListView listView;
	
	abstract public List<TwoLineItem> getList();
	
	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	protected ArrayAdapter<TwoLineItem> getAdapter() {
		return new ArrayAdapter<TwoLineItem>(this, android.R.layout.simple_list_item_2, getList()){
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent){
	            TwoLineListItem view;
	            if(convertView == null){
	                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
	            }else{
	                view = (TwoLineListItem)convertView;
	            }
	            
	            TwoLineItem item = getItem(position);
	            view.getText1().setText(item.getLineOne());
	            view.getText2().setText(item.getLineTwo());

	            return view;
	        }
	    };
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

		listView = new ListView(this);
        layout.addView(listView);

        setContentView(layout);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		listView.setAdapter(getAdapter());
	}

}

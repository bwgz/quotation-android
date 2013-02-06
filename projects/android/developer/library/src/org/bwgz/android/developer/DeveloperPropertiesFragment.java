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

import java.util.ArrayList;
import java.util.List;

import org.bwgz.android.developer.DisplayPropertiesFragment;
import org.bwgz.android.developer.OSPropertiesFragment;
import org.bwgz.android.developer.SimpleListViewFragment;
import org.bwgz.android.developer.SystemPropertiesFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;

class ListData {
    private String title;
    private String description;
    private String fragment;
    
    public ListData(String title, String description, String fragment) {
    	this.setTitle(title);
    	this.setDescription(description);
    	this.setFragment(fragment);
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
}

public class DeveloperPropertiesFragment extends SimpleListViewFragment<ListData> {
	private List<ListData> list;
		
	private List<ListData> getList() {
		return list;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		list = new ArrayList<ListData>();
		list.add(new ListData(getString(R.string.display_item_title), getString(R.string.display_item_description), DisplayPropertiesFragment.class.getName()));
		list.add(new ListData(getString(R.string.os_item_title), getString(R.string.os_item_description), OSPropertiesFragment.class.getName()));
		list.add(new ListData(getString(R.string.system_item_title), getString(R.string.system_item_description), SystemPropertiesFragment.class.getName()));
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    Intent intent = new Intent(view.getContext(), DeveloperFragmentActivity.class);
			    intent.putExtra("fragment", getList().get(position).getFragment());
			    startActivity(intent); 
			}
		});
		
		return view;
	}

	protected ArrayAdapter<ListData> getAdapter(Context context) {
		return new ArrayAdapter<ListData>(context, android.R.layout.simple_list_item_2, getList()){
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent){
	        	TwoLineListItem view;
	            
	            if(convertView == null){
	                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
	            }else{
	                view = (TwoLineListItem)convertView;
	            }
	            
	            ListData data = getItem(position);
	            view.getText1().setText(data.getTitle());
	            view.getText2().setText(data.getDescription());

	            return view;
	        }
	    };
	}
}


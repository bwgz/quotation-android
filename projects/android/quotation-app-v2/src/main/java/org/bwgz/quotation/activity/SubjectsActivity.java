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

import java.util.Arrays;

import org.bwgz.quotation.R;
import org.bwgz.quotation.adapter.DrawerAdapter;
import org.bwgz.quotation.fragment.BookmarkSubjectsFragment;
import org.bwgz.quotation.fragment.HomeSubjectsFragment;
import org.bwgz.quotation.fragment.RandomSubjectsFragment;
import org.bwgz.quotation.search.FreebaseSearch;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

public class SubjectsActivity extends CategoryActivity {
	static public final String TAG = SubjectsActivity.class.getSimpleName();

	private final CategoryPage[] pages = new CategoryPage[] {
			new CategoryPage(R.string.title_home, HomeSubjectsFragment.class),
			new CategoryPage(R.string.title_random, RandomSubjectsFragment.class),
			new CategoryPage(R.string.title_bookmarks, BookmarkSubjectsFragment.class)
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.standard_category_layout);
        setTitle(R.string.subjects_title);
        
        super.onCreate(savedInstanceState);
	}
    
    public boolean onCreateOptionsMenu(final Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
		
        getMenuInflater().inflate(R.menu.options_menu, menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_subject));
       
        final ListView drawerView = (ListView) findViewById(R.id.drawer_view);
        DrawerAdapter adapter = new DrawerAdapter(getResources().getStringArray(R.array.subjects_drawer), 1);
        drawerView.setAdapter(adapter);
        drawerView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, String.format("drawer - onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
				
				if (id == 0) {
					drawerLayout.closeDrawer(drawerView);
					startActivity(new Intent(parent.getContext(), HomeActivity.class));
				}
			}
		});
        
        return true;
    }

	@Override
	FragmentStatePagerAdapter getPagerAdapter(FragmentManager fm) {
		return new CategoryPagerAdapter(fm, Arrays.asList(pages));
	}
	
    @Override
    public void startActivity(Intent intent) {      
		Log.d(TAG, String.format("startActivity - intent: %s", intent));
		
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_SUBJECT);
        }

        super.startActivity(intent);
    }

}

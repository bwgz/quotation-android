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
import org.bwgz.quotation.adapter.AuthorPicksCursorAdapter;
import org.bwgz.quotation.adapter.DrawerAdapter;
import org.bwgz.quotation.adapter.QuotationPicksCursorAdapter;
import org.bwgz.quotation.adapter.SubjectPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationQuery;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.search.FreebaseSearch;
import org.bwgz.quotation.widget.picks.PicksView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

public class HomeActivity extends CursorLoaderManagerActivity implements CursorLoaderListener {
	static public final String TAG = HomeActivity.class.getSimpleName();
	
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    
    private PicksView quotationPicksView;
    private PicksView authorPicksView;
    private PicksView subjectPicksView;
    
    private int quotationLoaderId;
    private int authorLoaderId;
    private int subjectLoaderId;

    private class NavigationOnClickListener implements OnClickListener {
    	private Class<?> activityClass;
    	
    	public NavigationOnClickListener(Class<?> activityClass) {
    		this.activityClass = activityClass;
    	}
    	
		@Override
		public void onClick(View view) {
			Log.d(TAG, String.format("navigation - onClick - view: %s", view));
			startActivity(new Intent(view.getContext(), activityClass));
		}
    }
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - bundle: %s", bundle));

		setContentView(R.layout.home_activity);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.home);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        
        LinearLayout quotationsNavigation = (LinearLayout) findViewById(R.id.navigation_quotations);
        quotationsNavigation.setOnClickListener(new NavigationOnClickListener(QuotationsActivity.class));
        LinearLayout authorsNavigation = (LinearLayout) findViewById(R.id.navigation_authors);
        authorsNavigation.setOnClickListener(new NavigationOnClickListener(AuthorsActivity.class));
        LinearLayout subjectsNavigation = (LinearLayout) findViewById(R.id.navigation_subjects);
        subjectsNavigation.setOnClickListener(new NavigationOnClickListener(SubjectsActivity.class));
      
		quotationPicksView = (PicksView) findViewById(R.id.quotation_picks);
		quotationPicksView.setActivityClass(QuotationActivity.class);
		quotationLoaderId = initLoader(this, PickQuotation.CONTENT_URI, QuotationQuery.PROJECTION, null, null, null);
		
        authorPicksView = (PicksView) findViewById(R.id.author_picks);
		authorPicksView.setActivityClass(AuthorActivity.class);
        authorLoaderId = initLoader(this, PickPerson.CONTENT_URI, new String[] { Person.FULL_ID, Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.QUOTATION_COUNT, BookmarkPerson.BOOKMARK_ID }, null, null, null);

        subjectPicksView = (PicksView) findViewById(R.id.subject_picks);
		subjectPicksView.setActivityClass(SubjectActivity.class);
        subjectLoaderId = initLoader(this, PickSubject.CONTENT_URI, new String[] { Subject.FULL_ID, Subject.NAME, Subject.DESCRIPTION, Subject.IMAGE_ID, Subject.QUOTATION_COUNT, BookmarkSubject.BOOKMARK_ID }, null, null, null);

        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
		Log.d(TAG, String.format("onConfigurationChanged"));
		
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
		
        getMenuInflater().inflate(R.menu.options_menu, menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_keyword));
        
        final ListView drawerView = (ListView) findViewById(R.id.drawer);
        DrawerAdapter adapter = new DrawerAdapter(getResources().getStringArray(R.array.home_drawer), 0);
        drawerView.setAdapter(adapter);
        drawerView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, String.format("drawer - onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
				
				drawerLayout.closeDrawer(drawerView);
				
				if (id == 1) {
					Intent intent = new Intent(parent.getContext(), QuotationsActivity.class);
					intent.putExtra(CategoryActivity.EXTRA_ITEM, QuotationsActivity.QUOTATIONS_ITEM_BOOKMARKS);
					startActivity(intent);
				}
			}
		});
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, String.format("onOptionsItemSelected - item: %s", item));
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
    		Log.d(TAG, String.format("onOptionsItemSelected - mDrawerToggle.onOptionsItemSelected"));
			return true;
		}
		
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		Log.d(TAG, String.format("onPostCreate"));
		
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, String.format("onPrepareOptionsMenu - menu: %s", menu));
        boolean drawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.drawer));
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public void startActivity(Intent intent) {      
		Log.d(TAG, String.format("startActivity - intent: %s", intent));
		
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_KEYWORD);
        }

        super.startActivity(intent);
    }

	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
		
		if (loader.getId() == quotationLoaderId) {
			Log.d(TAG, String.format("onLoadFinished quotation - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			QuotationPicksCursorAdapter adapter = quotationPicksView.getAdapter();
			if (adapter == null) {
				adapter = new QuotationPicksCursorAdapter(this, cursor, R.layout.quotation_pick_view, getImageLoader(), quotationPicksView.getNumberOfPicks());
				quotationPicksView.setAdapter(adapter);
			}
			else {
				adapter.swapCursor(cursor);
			}
		}
		else if (loader.getId() == authorLoaderId) {
			Log.d(TAG, String.format("onLoadFinished author - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			AuthorPicksCursorAdapter adapter = authorPicksView.getAdapter();
			if (adapter == null) {
				adapter = new AuthorPicksCursorAdapter(this, cursor, R.layout.author_pick_view, getImageLoader(), authorPicksView.getNumberOfPicks());
				authorPicksView.setAdapter(adapter);
			}
			else {
				adapter.swapCursor(cursor);
			}
		}
		else if (loader.getId() == subjectLoaderId) {
			Log.d(TAG, String.format("onLoadFinished subject - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
			SubjectPicksCursorAdapter adapter = subjectPicksView.getAdapter();
			if (adapter == null) {
				adapter = new SubjectPicksCursorAdapter(this, cursor, R.layout.subject_pick_view, getImageLoader(), subjectPicksView.getNumberOfPicks());
				subjectPicksView.setAdapter(adapter);
			}
			else {
				adapter.swapCursor(cursor);
			}
		}
	}

}
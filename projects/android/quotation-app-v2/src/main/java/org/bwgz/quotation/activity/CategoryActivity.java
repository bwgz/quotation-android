package org.bwgz.quotation.activity;

import java.util.List;
import java.util.Locale;

import org.bwgz.quotation.R;
import org.bwgz.quotation.fragment.VolleyFragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public abstract class CategoryActivity extends VolleyActivity implements ActionBar.TabListener {
	static public final String TAG = CategoryActivity.class.getSimpleName();
	
	public static final String EXTRA_ITEM	= "extra.category.item";

    private ActionBarDrawerToggle drawerToggle;
    protected DrawerLayout drawerLayout;
    protected ViewPager viewPager;
    protected FragmentStatePagerAdapter sectionsPagerAdapter;

    abstract FragmentStatePagerAdapter getPagerAdapter(FragmentManager fm);

    public class CategoryPage {
		private int resId;
		private Class<? extends VolleyFragment> fragment;
		
		public int getResId() {
			return resId;
		}
		public void setResId(int resId) {
			this.resId = resId;
		}
	   	public Class<? extends VolleyFragment> getFragment() {
			return fragment;
		}
		public void setFragment(Class<? extends VolleyFragment> fragment) {
			this.fragment = fragment;
		}
		
		public CategoryPage(int resId, Class<? extends VolleyFragment> fragment) {
			this.resId = resId;
			this.fragment = fragment;
		}
	}
    
    public class CategoryPagerAdapter extends FragmentStatePagerAdapter {
    	private List<CategoryPage> pages;
    	
        public CategoryPagerAdapter(FragmentManager fm, List<CategoryPage> pages) {
            super(fm);
            
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
        	try {
        		VolleyFragment fragment = pages.get(position).getFragment().newInstance();
				return fragment;
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
        	
			return null;
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return getString(pages.get(position).getResId()).toUpperCase(Locale.getDefault());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
       
    	Intent intent = getIntent();
    	int item = intent.getIntExtra(EXTRA_ITEM, 0);
		Log.d(TAG, String.format("onCreate - item: %d", item));

      	final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        sectionsPagerAdapter = getPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager_view);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this), i == item);
        }
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
		Log.d(TAG, String.format("onConfigurationChanged"));
		
        drawerToggle.onConfigurationChanged(newConfig);
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
        boolean drawerOpen = drawerLayout.isDrawerOpen(findViewById(R.id.drawer_view));
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		Log.d(TAG, String.format("onTabSelected - tab: %s (%d)  fragmentTransaction: %s", tab, tab.getPosition(), fragmentTransaction));
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}

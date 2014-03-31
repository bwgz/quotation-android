package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.fragment.VolleyFragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

public abstract class PickViewPagerActivity extends VolleyActivity {
	static public final String TAG = PickViewPagerActivity.class.getSimpleName();

	public static final String EXTRA_ID			= "extra.pick.id";
	public static final String EXTRA_IDS		= "extra.pick.ids";
	public static final String EXTRA_POSITION	= "extra.pick.position";

	public static final String BUNDLE_ID		= "bundle.pick.id";
    
	abstract protected String getIdFromUri(Uri uri);
	abstract protected Class<? extends VolleyFragment> getPickFragmentClass();
	abstract protected CharSequence getQueryHint();
	abstract protected int getSearchType();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
        
        Intent intent = getIntent();
        
		String[] ids = intent.getStringArrayExtra(EXTRA_IDS);
		int position = intent.getIntExtra(EXTRA_POSITION, -1);
		Log.d(TAG, String.format("onCreate - ids: %s (%d)  position: %d", ids, ids != null ? ids.length : 0, position));
       
		if (ids == null || position == -1) {
			String id = intent.getStringExtra(EXTRA_ID);
			
			if (id == null) {
				Uri uri = intent.getData();
				
				if (uri != null) {
					id = Quotation.getId(uri);
				}
			}
			
			if (id != null) {
				ids = new String[1];
				ids[0] = id;
				position = 0;
			}
		}
       
        setContentView(R.layout.standard_view_pager);

        if (ids != null) {
        	final PickViewPagerAdapter pickViewPagerAdapter = new PickViewPagerAdapter(getSupportFragmentManager(), ids);
        	ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        	
        	boolean debug = false;
        	if (debug) {
				getActionBar().setSubtitle(pickViewPagerAdapter.getId(position));
		        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageScrollStateChanged(int state) {
					}
	
					@Override
					public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					}
	
					@Override
					public void onPageSelected(int position) {
						getActionBar().setSubtitle(pickViewPagerAdapter.getId(position));
					}
		        });
        	}
			
	        viewPager.setAdapter(pickViewPagerAdapter);
	        viewPager.setCurrentItem(position);
        }
    	
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    protected ViewPager getViewPager() {
    	return (ViewPager) findViewById(R.id.pager);
    }
    
    protected int getMenuResId() {
    	return R.menu.options_menu;
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
		
        getMenuInflater().inflate(getMenuResId(), menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getQueryHint());
     
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent upIntent = NavUtils.getParentActivityIntent(this);
	    	Log.d(TAG, String.format("onOptionsItemSelected - upIntent: %s", upIntent));
            if (upIntent == null) {
            	onBackPressed();
            }
            else {
	            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	                // This activity is NOT part of this app's task, so create a new task
	                // when navigating up, with a synthesized back stack.
	        		Log.d(TAG, String.format("onOptionsItemSelected - TaskStackBuilder.create"));
	        		TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
	            } else {
	                // This activity is part of this app's task, so simply
	                // navigate up to the logical parent activity.
	        		Log.d(TAG, String.format("onOptionsItemSelected - NavUtils.navigateUpTo"));
	                NavUtils.navigateUpTo(this, upIntent);
	            }
            }
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

	protected class PickViewPagerAdapter extends FragmentStatePagerAdapter {
		private String[] ids;

        public PickViewPagerAdapter(FragmentManager fm, String[] ids) {
            super(fm);
            
            this.ids = ids;
        }

        public String getId(int position) {
        	return ids[position];
        }
        
        @Override
        public Fragment getItem(int position) {
        	Bundle bundle = new Bundle();
        	bundle.putString(BUNDLE_ID, ids[position]);
       	
    		VolleyFragment fragment = (VolleyFragment) Fragment.instantiate(PickViewPagerActivity.this, getPickFragmentClass().getName(), bundle);
			return fragment;
        }

        @Override
        public int getCount() {
            return ids.length;
        }
    }
	
    @Override
    public void startActivity(Intent intent) {      
		Log.d(TAG, String.format("startActivity - intent: %s", intent));
		
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, getSearchType());
        }

        super.startActivity(intent);
    }
}

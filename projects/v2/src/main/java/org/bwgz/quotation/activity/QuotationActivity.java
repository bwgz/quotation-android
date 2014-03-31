package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.fragment.QuotationFragment;
import org.bwgz.quotation.fragment.VolleyFragment;
import org.bwgz.quotation.search.FreebaseSearch;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class QuotationActivity extends PickViewPagerActivity {
	static public final String TAG = QuotationActivity.class.getSimpleName();

	public static final String	LOADER_BUNDLE_URI				= "loader.bundle.uri";
	public static final String	LOADER_BUNDLE_PROJECTION		= "loader.bundle.projection";
	public static final String	LOADER_BUNDLE_SELECTION			= "loader.bundle.selection";
	public static final String	LOADER_BUNDLE_SELECTION_ARGS	= "loader.bundle.selection.args";
	public static final String	LOADER_BUNDLE_SORT_ORDER		= "loader.bundle.sort.order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));

        setTitle(R.string.quotation_title);
    }

	@Override
	protected String getIdFromUri(Uri uri) {
		return Quotation.getId(uri);
	}
    
	@Override
	protected Class<? extends VolleyFragment> getPickFragmentClass() {
		return QuotationFragment.class;
	}
	
	@Override
	protected CharSequence getQueryHint() {
		return getString(R.string.search_keyword);
	}
	
	@Override
	protected int getSearchType() {
		return FreebaseSearch.SEARCH_TYPE_KEYWORD;
	}
	
	@Override
    protected int getMenuResId() {
    	return R.menu.quotation_pick_options_menu;
    }

    protected String getId() {
    	String id = null;
    	
    	ViewPager viewPager = getViewPager();
    	PickViewPagerAdapter adapter = (PickViewPagerAdapter) viewPager.getAdapter();
    	id = adapter.getId(viewPager.getCurrentItem());
    	
    	return id;
    }
}

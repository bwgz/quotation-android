package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.fragment.AuthorFragment;
import org.bwgz.quotation.fragment.VolleyFragment;
import org.bwgz.quotation.search.FreebaseSearch;

import com.google.analytics.tracking.android.EasyTracker;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class AuthorActivity extends PickViewPagerActivity {
	static public final String TAG = AuthorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));

        setTitle(R.string.author_title);
    }

	@Override
	protected String getIdFromUri(Uri uri) {
		return Person.getId(uri);
	}

	@Override
	protected Class<? extends VolleyFragment> getPickFragmentClass() {
		return AuthorFragment.class;
	}

	@Override
	protected CharSequence getQueryHint() {
		return getString(R.string.search_author);
	}

	@Override
	protected int getSearchType() {
		return FreebaseSearch.SEARCH_TYPE_AUTHOR;
	}
	
	@Override
    protected int getMenuResId() {
    	return R.menu.pick_options_menu;
    }

}

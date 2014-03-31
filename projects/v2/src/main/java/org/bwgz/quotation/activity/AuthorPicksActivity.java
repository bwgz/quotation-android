package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;
import org.bwgz.quotation.adapter.ActivityAdapter;
import org.bwgz.quotation.adapter.AuthorPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.search.FreebaseSearch;
import org.bwgz.quotation.widget.picks.PicksView;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

public class AuthorPicksActivity extends PicksActivity implements CursorLoaderListener {
	static public final String TAG = AuthorPicksActivity.class.getSimpleName();

	private PicksView picksView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.standard_grid_view);
        setTitle(R.string.authors_title);
  
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		picksView = (PicksView) inflater.inflate(R.layout.author_picks_expanded_view, null);
		picksView.setActivityClass(AuthorActivity.class);
		picksView.setExpand(true);
        initLoader(this, PickPerson.CONTENT_URI, new String[] { Person.FULL_ID, Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.QUOTATION_COUNT, BookmarkPerson.BOOKMARK_ID }, null, null, null);
	    
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));
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
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		AuthorPicksCursorAdapter adapter = picksView.getAdapter();
		if (adapter == null) {
			adapter = new AuthorPicksCursorAdapter(this, cursor, R.layout.author_pick_view, getImageLoader());
			picksView.setAdapter(adapter);
		}
		else {
			adapter.swapCursor(cursor);
		}
	}
}

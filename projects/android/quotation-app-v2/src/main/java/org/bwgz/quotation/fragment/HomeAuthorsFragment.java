package org.bwgz.quotation.fragment;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.AuthorActivity;
import org.bwgz.quotation.adapter.ActivityAdapter;
import org.bwgz.quotation.adapter.AuthorPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;
import org.bwgz.quotation.widget.picks.PicksView;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class HomeAuthorsFragment extends CursorLoaderManagerFragment implements CursorLoaderListener {
	static public final String TAG = HomeAuthorsFragment.class.getSimpleName();

	private PicksView picksView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - this: %s  inflater: %s  container: %s  savedInstanceState: %s", this, inflater, container, savedInstanceState));
		
		GridView gridView = (GridView) inflater.inflate(R.layout.standard_grid_view, container, false);
		
		picksView = (PicksView) inflater.inflate(R.layout.author_picks_unexpanded_view, null);
		picksView.setActivityClass(AuthorActivity.class);
        initLoader(this, PickPerson.CONTENT_URI, new String[] { Person.FULL_ID, Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.QUOTATION_COUNT, BookmarkPerson.BOOKMARK_ID }, null, null, null);

        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));

        return gridView;
    }
    
	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		AuthorPicksCursorAdapter adapter = new AuthorPicksCursorAdapter(getActivity(), cursor, R.layout.author_pick_view, getImageLoader());
		picksView.setAdapter(adapter);
	}
}

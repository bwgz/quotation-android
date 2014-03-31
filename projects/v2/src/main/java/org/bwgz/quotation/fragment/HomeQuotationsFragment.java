package org.bwgz.quotation.fragment;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.QuotationActivity;
import org.bwgz.quotation.adapter.ActivityAdapter;
import org.bwgz.quotation.adapter.QuotationPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationQuery;
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

public class HomeQuotationsFragment extends CursorLoaderManagerFragment implements CursorLoaderListener {
	static public final String TAG = HomeQuotationsFragment.class.getSimpleName();

	private PicksView picksView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - this: %s  inflater: %s  container: %s  savedInstanceState: %s", this, inflater, container, savedInstanceState));
		
		picksView = (PicksView) inflater.inflate(R.layout.quotation_picks_unexpanded_view, null);
		picksView.setActivityClass(QuotationActivity.class);
		initLoader(this, PickQuotation.CONTENT_URI, QuotationQuery.PROJECTION, null, null, null);

		GridView gridView = (GridView) inflater.inflate(R.layout.standard_grid_view, container, false);
        gridView.setAdapter(new ActivityAdapter(new View[] { picksView }));

        return gridView;
    }
    
	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		QuotationPicksCursorAdapter adapter = picksView.getAdapter();
		if (adapter == null) {
			adapter = new QuotationPicksCursorAdapter(getActivity(), cursor, R.layout.quotation_pick_view, getImageLoader());
			picksView.setAdapter(adapter);
		}
		else {
			adapter.swapCursor(cursor);
		}
	}
}

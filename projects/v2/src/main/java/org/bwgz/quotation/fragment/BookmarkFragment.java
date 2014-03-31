package org.bwgz.quotation.fragment;

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.model.picks.Pick;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class BookmarkFragment extends CursorLoaderManagerFragment {
	static public final String TAG = BookmarkFragment.class.getSimpleName();

	private int loaderId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
		
		loaderId = getNextLoaderId();
	}
	
	protected int getLoaderId() {
		return loaderId;
	}

	protected void setLoaderId(int loaderId) {
		this.loaderId = loaderId;
	}
	
	protected List<Pick> toPicks(Cursor cursor, String columnName) {
		Log.d(TAG, String.format("toPicks - cursor: %s (%d)", cursor, cursor.getCount()));
		List<Pick> picks = new ArrayList<Pick>();
		
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.moveToPosition(i)) {
				String id = cursor.getString(cursor.getColumnIndex(columnName));
				Log.d(TAG, String.format("toPicks - id: %s", id));
				Pick pick = new Pick();
				pick.setId(id);
				
				picks.add(pick);
			}
		}
		
		return picks;
	}
}

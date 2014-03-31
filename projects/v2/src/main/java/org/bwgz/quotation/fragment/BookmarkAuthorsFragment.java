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
package org.bwgz.quotation.fragment;

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.AuthorActivity;
import org.bwgz.quotation.activity.PickViewPagerActivity;
import org.bwgz.quotation.adapter.AuthorPicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.core.CursorLoaderManager.CursorLoaderListener;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BookmarkAuthorsFragment extends CursorLoaderManagerFragment implements CursorLoaderListener {
	static public final String TAG = BookmarkAuthorsFragment.class.getSimpleName();
	
	private AuthorPicksCursorAdapter adapter;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, String.format("onCreateView - this: %s  inflater: %s  container: %s  savedInstanceState: %s", this, inflater, container, savedInstanceState));
		View view = inflater.inflate(R.layout.standard_list_view, container, false);
 
		ListView listView = (ListView) view.findViewById(R.id.list_view);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, String.format("onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
				List<String> ids = new ArrayList<String>();
				
				Cursor cursor = (Cursor) adapter.getItem(position);
				Log.d(TAG, String.format("onItemClick - authorId: %s", cursor.getString(cursor.getColumnIndex(Person._ID))));
				
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					ids.add(cursor.getString(cursor.getColumnIndex(Person._ID)));
				}
				
				Intent intent = new Intent(view.getContext(), AuthorActivity.class);
				intent.putExtra(PickViewPagerActivity.EXTRA_IDS, ids.toArray(new String[ids.size()]));
				intent.putExtra(PickViewPagerActivity.EXTRA_POSITION, position);
				
				view.getContext().startActivity(intent);
			}
		});
		
        initLoader(this, BookmarkPerson.CONTENT_URI, new String[] { Person.FULL_ID, Person.NAME, Person.DESCRIPTION, Person.NOTABLE_FOR, Person.IMAGE_ID, Person.QUOTATION_COUNT, BookmarkPerson.BOOKMARK_ID }, null, null, null);

        return view;
    }
 
	@Override
	public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor.getCount()));
		
		ListView listView = (ListView) getView().findViewById(R.id.list_view);
		TextView textView = (TextView) getView().findViewById(R.id.text_view);

		if (cursor.getCount() == 0) {
			listView.setVisibility(View.GONE);
			textView.setVisibility(View.VISIBLE);
		}
		else {
			listView.setVisibility(View.VISIBLE);
			textView.setVisibility(View.GONE);
			
			adapter = (AuthorPicksCursorAdapter) listView.getAdapter();
			if (adapter == null) {
				adapter = new AuthorPicksCursorAdapter(getView().getContext(), cursor, R.layout.author_list_pick_view, getImageLoader());
				listView.setAdapter(adapter);
			}
			else {
				adapter.swapCursor(cursor);
			}
		}
	}
}

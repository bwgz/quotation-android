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
package org.bwgz.quotation.widget.picks;

import java.util.ArrayList;
import java.util.List;

import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.PickViewPagerActivity;
import org.bwgz.quotation.adapter.PicksCursorAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.widget.ExpandableHeightGridView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicksView extends LinearLayout {
	static public final String TAG = PicksView.class.getSimpleName();

	private int numberOfPicks = Integer.MAX_VALUE;
	private GridView cardView;
	Class<? extends Activity> activityClass;

	public PicksView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		Log.d(TAG, String.format("PicksView - context: %s  attrs: %s", context, attrs));
	}

	public PicksView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, String.format("PicksView - context: %s  attrs: %s  defStyle: %d", context, attrs, defStyle));
		
		View view = inflate(context, R.layout.picks_view, this);
		setOrientation(LinearLayout.VERTICAL);
		
		cardView = (GridView) view.findViewById(R.id.cardview);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Quotation, 0, 0);
		if (array != null) {
			setTextViewText(R.id.header, R.id.title, array, R.styleable.Quotation_picks_title);
			setTextViewText(R.id.header, R.id.subtitle, array, R.styleable.Quotation_picks_subtitle);
			setSeeMoreListener(context, R.id.header, R.id.see_more, array, R.styleable.Quotation_picks_seeMoreActivityClass);
			
			numberOfPicks = array.getInteger(R.styleable.Quotation_picks_numberOfPicks, numberOfPicks);
			cardView.setNumColumns(array.getInteger(R.styleable.Quotation_picks_numberOfColumns, 1));
			
			array.recycle();
		}
		
		cardView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, String.format("onItemClick - parent: %s  view: %s  position: %d  id: %d", parent, view, position, id));
				List<String> ids = new ArrayList<String>();
				
				Cursor cursor = (Cursor) getAdapter().getItem(position);
				Log.d(TAG, String.format("onItemClick - quotationId: %s", cursor.getString(cursor.getColumnIndex(Quotation._ID))));
				
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					ids.add(cursor.getString(cursor.getColumnIndex(Quotation._ID)));
				}
				
				Intent intent = new Intent(view.getContext(), getActivityClass());
				intent.putExtra(PickViewPagerActivity.EXTRA_IDS, ids.toArray(new String[ids.size()]));
				intent.putExtra(PickViewPagerActivity.EXTRA_POSITION, position);
				
				view.getContext().startActivity(intent);
			}
		});
	}

	public Class<? extends Activity> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<? extends Activity> activityClass) {
		this.activityClass = activityClass;
	}

	public void setExpand(boolean b) {
	    ExpandableHeightGridView picks = (ExpandableHeightGridView) findViewById(R.id.cardview);
	    picks.setExpanded(true);
	}

	public int getNumberOfPicks() {
		return numberOfPicks;
	}

	public void setNumberOfPicks(int numberOfPicks) {
		this.numberOfPicks = numberOfPicks;
	}

	private void setTextViewText(int parentId, int viewId, TypedArray array, int resId) {
		TextView view = (TextView) findViewById(viewId);
		if (view != null) {
			String text = array.getString(resId);
			
			if (text != null) {
				view.setText(text);
			}
			else {
				RelativeLayout parent = (RelativeLayout) findViewById(parentId);
				if (parent != null) {
					parent.removeView(view);
				}
			}
		}
	}
	
	private void setSeeMoreListener(final Context context, int parentId, int viewId, TypedArray array, int resId) {
		TextView view = (TextView) findViewById(viewId);
		if (view != null) {
			final String string = array.getString(resId);
			
			if (string != null) {
				try {
					final Class<?> clazz = Class.forName(string);
				
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							Log.d(TAG, String.format("onClick - view: %s ", view));
							context.startActivity(new Intent(view.getContext(), clazz));
						}
			        });
				} catch (ClassNotFoundException e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
			else {
				RelativeLayout parent = (RelativeLayout) findViewById(parentId);
				if (parent != null) {
					parent.removeView(view);
				}
			}
		}
	}
	
	public <T extends PicksCursorAdapter> T getAdapter() {
		Log.d(TAG, String.format("getAdapter"));
		return (T) cardView.getAdapter();
	}

	public <T extends PicksCursorAdapter> void setAdapter(T adapter) {
		Log.d(TAG, String.format("setAdapter - adapter: %s (%d)", adapter, adapter.getCount()));
		cardView.setAdapter(adapter);
	}
}

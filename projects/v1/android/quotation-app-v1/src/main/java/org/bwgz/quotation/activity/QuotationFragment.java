/*
 * Copyright (C) 2013 bwgz.org
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
package org.bwgz.quotation.activity;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class QuotationFragment extends SherlockFragment {
	static public final String TAG = QuotationFragment.class.getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.quotation_fragment, container, false);
		 
		 return view;
	}

	public void setQuotation(String string) {
		Log.d(TAG, String.format("updateQuotation - string: %s", string));
		TextView textView = (TextView) getActivity().findViewById(R.id.quotation);
		textView.setText(string);
    }

	public void setQuotation(Cursor cursor) {
		Log.d(TAG, String.format("updateQuotation - cursor: %s", cursor));
		
		setQuotation(cursor.moveToFirst() ? cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION)) : new String());
    }
}

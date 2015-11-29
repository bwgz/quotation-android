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
package org.bwgz.quotation.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;
import org.bwgz.quotation.activity.SearchResultsActivity;
import org.bwgz.quotation.adapter.SearchResultsAdapter;
import org.bwgz.quotation.model.picks.Pick;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.freebase.Freebase.Search;

public class SearchResultsTask extends AsyncTask<Intent, Integer, SearchResults> {
	static private final String TAG = SearchResultsTask.class.getSimpleName();
	
	private Activity activity;
	private SearchResultsAdapter adapter;
	private int cursor;
	private int limit;
	private ProgressDialog progressDialog;
	
	public SearchResultsTask(Activity activity, SearchResultsAdapter adapter, int cursor, int limit) {
		this.activity = activity;
		this.adapter = adapter;
		this.cursor = cursor;
		this.limit = limit;
	}
	
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Searching ...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

	@Override
	protected SearchResults doInBackground(Intent... params) {
		BigDecimal hits = BigDecimal.ZERO;
		List<Pick> picks = new ArrayList<Pick>();
        
        Intent intent = params[0];
        String query = intent.getStringExtra(SearchManager.QUERY);
        int type = intent.getIntExtra(SearchResultsActivity.EXTRA_SEARCH_TYPE, FreebaseSearch.SEARCH_TYPE_KEYWORD);
        
		Log.d(TAG, String.format("doInBackground - query: %s  type: %x", query, type));
        
		try {
			String[] keys = activity.getResources().getStringArray(R.array.freebase_api_keys);
	        FreebaseHelper freebaseHelper = new FreebaseHelper("org.bwgz.quotation", keys);

			Search search = freebaseHelper.getFreebase().search();
			
			switch (type) {
			case FreebaseSearch.SEARCH_TYPE_KEYWORD:
	    		Log.d(TAG, String.format("doInBackground - type: %s", "keyword search"));
				search.setQuery(query);
				search.setFilter(new ArrayList<String>(Arrays.asList(new String[] {"(any type:/media_common/quotation)"})));
				break;
			case FreebaseSearch.SEARCH_TYPE_AUTHOR:
	    		Log.d(TAG, String.format("doInBackground - type: %s", "author search"));
				search.setFilter(new ArrayList<String>(Arrays.asList(new String[] {"(all type:/media_common/quotation /media_common/quotation/author:\"" + query + "\")"})));
				break;
			case FreebaseSearch.SEARCH_TYPE_SUBJECT:
	    		Log.d(TAG, String.format("doInBackground - type: %s", "subject search"));
				search.setFilter(new ArrayList<String>(Arrays.asList(new String[] {"(all type:/media_common/quotation /media_common/quotation/subjects:\"" + query + "\")"})));
				break;
			}
			
			search.setLimit(limit);
			search.setCursor(cursor);
	      
			GenericJson json = search.execute();
			Log.d(TAG, String.format("json: %s\n",  json));
			
			hits = (BigDecimal) json.get("hits");
			@SuppressWarnings("unchecked")
			List<ArrayMap<String, ?>> result = (List<ArrayMap<String, ?>>) json.get("result");
			for (ArrayMap<String, ?> map : result) {
				Log.d(TAG, String.format("result: %s\n",  map));
				
				String mid = (String) map.get("mid");
				Pick pick = new Pick();
				pick.setId(mid);
				picks.add(pick);
			}

		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}

		return new SearchResults(query, hits, picks);
	}
	
	@Override
	protected void onPostExecute(final SearchResults results) {
        progressDialog.dismiss();
	
		adapter.setHits(results.getHits().toBigInteger().intValue());
		adapter.addAll(results.getPicks());
		
		TextView textView = (TextView) activity.findViewById(R.id.result_count);
		textView.setText(String.valueOf(adapter.getCount()));

        TextView tv = (TextView) activity.findViewById(R.id.text_view);
        ListView lv = (ListView) activity.findViewById(R.id.result_list);

        if (adapter.getCount() == 0) {
            Resources resources = tv.getResources();
            String text = String.format(resources.getString(R.string.no_results), results.getQuery());
            tv.setText(text);

            tv.setVisibility(tv.VISIBLE);
            lv.setVisibility(lv.GONE);
        }
        else {
            tv.setVisibility(tv.GONE);
            lv.setVisibility(lv.VISIBLE);
        }
	}
}

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressLint("ShowToast")
public class QuotationActivity extends SherlockFragmentActivity implements OnLoadCompleteListener<Cursor>, LoaderCallbacks<Cursor> {
	static public final String TAG = QuotationActivity.class.getSimpleName();
	
	static private final String HISTORY		= "quotation.history";
	static private final String LOAD_STATE	= "quotation.load.state";
	static private final String LOAD_URI	= "quotation.load.uri";
	
	static private final String authorsQuery = String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
			Person._ID, QuotationPerson.PERSON_ID, QuotationPerson.TABLE, QuotationPerson.QUOTATION_ID);

	static private final int LOADER_ID_QUTOATION	= 1;
	static private final int LOADER_ID_AUTHOR		= 2;
	static private final int[] LOADER_IDS			= { LOADER_ID_QUTOATION, LOADER_ID_AUTHOR };
	
	private enum LoadState { LOADING, LOADED }
	
	private LoadState state = LoadState.LOADED;
	private LazyLoadMessageHandler lazyLoadMessageHandler;
	private History history;
	private Menu menu;

	static private class LazyLoadMessageHandler extends Handler {
		private Toast toast;
		
		public LazyLoadMessageHandler(Toast toast) {
			this.toast = toast;
		}
		
		@Override
		public void handleMessage(Message message) {
			if (LoadState.valueOf(message.getData().getString(LOAD_STATE)) == LoadState.LOADING) {
				toast.show();
			}
			else {
				toast.cancel();
			}
		}
	}

	private class LazyLoadMessage extends TimerTask {
		private Handler handler;
		
		public LazyLoadMessage(Handler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString(LOAD_STATE, state.toString());
			message.setData(bundle);
			handler.sendMessage(message);
		}
	}

	private static class History {
		@JsonProperty
		private int index = -1;
		@JsonProperty
		private List<String> list = new ArrayList<String>();
		
		public void add(String uri) {
			if (index == -1 || !list.get(index).equals(uri)) {
				list = list.subList(0,  ++index);
				list.add(uri);
			}
		}
		
		public void add(Uri uri) {
			add(uri.toString());
		}
		
		public boolean hasBack() {
			return (index > 0);
		}
		
		public boolean hasForward() {
			return (index < (list.size() - 1));
		}
		
		public Uri current() {
			return (index != -1) ? Uri.parse(list.get(index)) : null;
		}
		
		public Uri back() {
			return (index > 0) ? Uri.parse(list.get(--index)) : null;
		}
		
		public Uri forward() {
			return (index < (list.size() - 1)) ? Uri.parse(list.get(++index)) : null;
		}
	
		@SuppressWarnings("unused")
		void dump() {
			Log.d(TAG, String.format("history - index: %d  size: %d", index, list.size()));
			for (int i = 0; i < list.size(); i++) {
				Log.d(TAG, String.format("history[%2d]: %s %s", i, list.get(i), (i == index) ? "<---" : ""));
			}
		}
	}
	
	private void startLoadMessage() {
		new Timer().schedule(new LazyLoadMessage(lazyLoadMessageHandler), TimeUnit.SECONDS.toMillis(1));
	}
	
	private void stopLoadMessage() {
		Bundle bundle = new Bundle();
		bundle.putString(LOAD_STATE, LoadState.LOADED.toString());
		Message message = new Message();
		message.setData(bundle);
		lazyLoadMessageHandler.sendMessage(message);
	}
	
	private void startLoadAnimation() {
		if (menu != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
			
			Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);
			
			MenuItem item = menu.findItem(R.id.actionbar_new);
	        item.setActionView(iv);
	        item.setEnabled(false);
		}
	}
	
	private void stopLoadAnimation() {
		if (menu != null) {
			MenuItem item = menu.findItem(R.id.actionbar_new);
			View view = item.getActionView();
			if (view != null) {
		        view.clearAnimation();
		        item.setActionView(null);
		        item.setEnabled(true);
			}
		}
	}
	
	private void setLoadState(LoadState state) {
		if (this.state != state) {
			this.state = state;
			
			if (state == LoadState.LOADING) {
				startLoadAnimation();
				startLoadMessage();
			}
			else {
				stopLoadAnimation();
				stopLoadMessage();
			}
		}
	}
	
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", bundle));

		history = new History();
        lazyLoadMessageHandler = new LazyLoadMessageHandler(Toast.makeText(this, R.string.loading_quote, (int) TimeUnit.SECONDS.toMillis(10)));

		setContentView(R.layout.quote_activity);
    }
    
	@Override
    protected void onRestoreInstanceState(Bundle bundle) {
    	super.onRestoreInstanceState(bundle);
		Log.d(TAG, String.format("onRestoreInstanceState - outState: %s", bundle));
		if (bundle != null) {
	    	String json = bundle.getString(HISTORY);
			Log.d(TAG, String.format("onRestoreInstanceState - history in: %s", json));
			if (json != null) {
		    	try {
		    		ObjectMapper mapper = new ObjectMapper();
					history = mapper.readValue(json, History.class);
				} catch (JsonParseException e) {
					Log.e(TAG, String.format("Cannot retrieve history - %s", e.getMessage()));
				} catch (JsonMappingException e) {
					Log.e(TAG, String.format("Cannot retrieve history - %s", e.getMessage()));
				} catch (IOException e) {
					Log.e(TAG, String.format("Cannot retrieve history - %s", e.getMessage()));
				}
			}
		}
	}
    
   @Override
    protected void onResume() {
    	super.onResume();
    	
    	Uri uri = history.current();
		Log.d(TAG, String.format("onResume - uri: %s", uri));
		
		Intent intent = getIntent();
		Log.d(TAG, String.format("onResume - intent: %s", intent));
		if (intent != null && intent.getData() != null) {
			uri = intent.getData();
		}
	
		Log.d(TAG, String.format("onResume - uri: %s", uri));
		
		if (uri == null) {
			loadRandomQuote();
		}
		else {
			history.add(uri);
			if (menu != null) {
				menu.findItem(R.id.actionbar_back).setEnabled(history.hasBack());
				menu.findItem(R.id.actionbar_forward).setEnabled(history.hasForward());
			}
			loadQuote(uri);
		}
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d(TAG, String.format("onPause"));
    }

    @Override
	protected void onSaveInstanceState(Bundle bundle) {
    	super.onSaveInstanceState(bundle);
		Log.d(TAG, String.format("onSaveInstanceState - outState: %s", bundle));
		
	    try {
	    	ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(history);
			Log.d(TAG, String.format("history out: %s", json));
			bundle.putString(HISTORY, json);
		} catch (JsonProcessingException e) {
			Log.e(TAG, String.format("Cannot save history - %s", e.getMessage()));
		}
	}

    @Override
    protected void onStop() {
    	super.onStop();
  		Log.d(TAG, String.format("onStop"));
		for (int id : LOADER_IDS) {
			if (getSupportLoaderManager().getLoader(id) != null) {
				getSupportLoaderManager().destroyLoader(id);
			}
		}
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
		Log.d(TAG, String.format("onDestroy"));
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, String.format("onCreateOptionsMenu - menu: %s", menu));
		this.menu = menu;
 
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		if (state == LoadState.LOADING) {
			startLoadAnimation();
		}
		
		menu.findItem(R.id.actionbar_back).setEnabled(history.hasBack());
		menu.findItem(R.id.actionbar_forward).setEnabled(history.hasForward());
		
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = false;
    	
    	switch (item.getItemId()) {
    	case R.id.actionbar_back:
			{
			Log.d(TAG, String.format("onOptionsItemSelected - actionbar_back"));
			if (history.hasBack()) {
				loadQuote(history.back());
			}
			
			menu.findItem(R.id.actionbar_back).setEnabled(history.hasBack());
			menu.findItem(R.id.actionbar_forward).setEnabled(history.hasForward());
			
			result = true;
			break;
			}
    	case R.id.actionbar_forward:
			{
			Log.d(TAG, String.format("onOptionsItemSelected - actionbar_forward"));
			if (history.hasForward()) {
				loadQuote(history.forward());
			}
			
			menu.findItem(R.id.actionbar_back).setEnabled(history.hasBack());
			menu.findItem(R.id.actionbar_forward).setEnabled(history.hasForward());

			result = true;
			break;
			}
    	case R.id.actionbar_new:
			{
		    loadRandomQuote();
			result = true;
			break;
			}
    	case R.id.actionbar_share:
    		{
    		CharSequence text = ((TextView) findViewById(R.id.quotation)).getText();
    		if (text != null && text.length() != 0) {
	            StringBuilder buffer = new StringBuilder();
	            buffer.append(text);
	            
	            text = ((TextView)findViewById(R.id.author)).getText();
	    		if (text != null && text.length() != 0) {
	            	buffer.append(" ... ");
	            	buffer.append(text);
	            }
		
	    		Intent intent = new Intent(Intent.ACTION_SEND);
	    		intent.setType("text/plain");
	    		intent.putExtra(Intent.EXTRA_TEXT, buffer.toString());
	    		startActivity(Intent.createChooser(intent, getResources().getText(R.string.sharing_quote)));
    		}
	    	result = true;
    		break;
    		}
    	case R.id.settings:
    		{
	        Intent intent = new Intent().setClass(this, SettingsActivity.class);
    	    startActivity(intent);
    		break;
    		}
    	}
    	
        return result;
    }
    
    private <T> void initLoader(LoaderManager loaderManager, int loaderId, Bundle args, LoaderCallbacks<T> callbacks) {
        final Loader<T> loader = loaderManager.getLoader(loaderId);
        if (loader == null || loader.isReset()) {
			Log.d(TAG, String.format("initLoader - initLoader: %d", loaderId));
            loaderManager.initLoader(loaderId, args, callbacks);
        } else {
			Log.d(TAG, String.format("initLoader - restartLoader: %d", loaderId));
            loaderManager.restartLoader(loaderId, args, callbacks);
        }
    }

    private void initLoaders(LoaderManager loaderManager, Uri uri) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(LOAD_URI, uri);
		
		for (int id : LOADER_IDS) {
			initLoader(loaderManager, id, bundle, this);
		}
    }
    
    private void loadQuote(Uri uri) {
		Log.d(TAG, String.format("loadQuote - uri: %s", uri));
		
		setLoadState(LoadState.LOADING);

		Intent intent = getIntent();
		if (intent != null) {
			intent.setData(uri);
		}

		initLoaders(getSupportLoaderManager(), uri);
	}

    private void loadRandomQuote() {
		setLoadState(LoadState.LOADING);
		
        CursorLoader loader = new CursorLoader(
                this,		
                Quotation.withAppendedId("/random"),        
                new String[] { Quotation.QUOTATION },
                null,      
                null,      
                null       
        		);
        
        loader.registerListener(0, this);
        loader.startLoading();
    }
    
	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadComplete - loader: %s  cursor: %s", loader, cursor));
		
		if (cursor.moveToFirst()) {
			Uri uri = Quotation.withAppendedId(cursor.getString(cursor.getColumnIndex(Quotation._ID)));
			Log.d(TAG, String.format("onLoadComplete - uri: %s", uri));
			
			history.add(uri);
			if (menu != null) {
				menu.findItem(R.id.actionbar_back).setEnabled(history.hasBack());
				menu.findItem(R.id.actionbar_forward).setEnabled(history.hasForward());
			}
			loadQuote(uri);
		}
		cursor.close();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d(TAG, String.format("onCreateLoader - loaderID: %d  bundle: %s", loaderID, bundle));
		Loader<Cursor> loader = null;
		
		Uri uri = bundle.getParcelable(LOAD_URI);

		if (loaderID == LOADER_ID_QUTOATION) {
	        loader = new CursorLoader(
	        		this,		
	                uri,        
	                new String[] { Quotation.QUOTATION },
	                null,      
	                null,      
	                null
	        		);
		}
		else if (loaderID == LOADER_ID_AUTHOR) {
	        loader = new CursorLoader(
	                this,		
	                Person.CONTENT_URI, 
	                null,
	                authorsQuery,
	                new String[] { Quotation.getId(uri) },
	                null       
	        		);
		}
		
		Log.d(TAG, String.format("onCreateLoader - return loader: %s", loader));

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (count: %d)", loader, cursor, cursor != null ? cursor.getCount() : 0));
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		switch (loader.getId()) {
		case LOADER_ID_QUTOATION:
			{
				QuotationFragment fragment = (QuotationFragment) fragmentManager.findFragmentById(R.id.quotationFragment);
				fragment.setQuotation(cursor);
				if (cursor.getCount() != 0) {
					setLoadState(LoadState.LOADED);
				}
			}
			break;
		case LOADER_ID_AUTHOR:
			{
				AuthorFragment fragment = (AuthorFragment) fragmentManager.findFragmentById(R.id.authorFragment);
				fragment.setAuthors(cursor);
			}
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, String.format("onLoaderReset - loader: %s", loader));
	}
}

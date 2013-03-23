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

package org.bwgz.qotd.activity;

import java.io.InputStream;
import java.util.Random;

import org.bwgz.android.developer.DeveloperActivity;
import org.bwgz.qotd.R;
import org.bwgz.quotation.content.provider.QuotationAccount;
import org.bwgz.quotation.content.provider.QuotationContract;
import org.bwgz.quotation.content.provider.QuotationSyncAdapter;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class QuoteActivity extends Activity {
	static private String TAG = QuoteActivity.class.getSimpleName();
    
	public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap bitmap = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            bitmap = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	        }
	        return bitmap;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}

	class QuotationContentObserver extends ContentObserver {
		private Uri uri;

		public QuotationContentObserver() {
	        super(new Handler());
	    }

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}

	    @Override
	    public void onChange(boolean selfChange) {
	        super.onChange(selfChange);
	        Log.d(TAG, "QuoteContentObserver.onChange( " + selfChange + ")");
	        
	        String quotation = getQuotation(uri);
	        if (quotation != null) {
	        	setQuote(quotation);
	        }
	        
	        String author = getAuthor(uri);
	        if (author != null) {
	        	setAuthor(author);
	        }
	        
	        String authorImage = getAuthorImage(uri);
	        if (authorImage != null) {
	        	setAuthorImage(authorImage);
	        }
	    }
	}

	private final Random random = new Random();
	private QuotationContentObserver quotationObserver = new QuotationContentObserver();
	
	public void setQuote(String quote) {
    	TextView textView = (TextView) findViewById(R.id.quote);
    	textView.setText(quote);
	}
	
	public void setAuthor(String author) {
    	TextView textView = (TextView) findViewById(R.id.author);
    	textView.setText(author);
	}
	
	public void setAuthorImage(String image) {
		Log.d(TAG, String.format("setAuthorImage - image: %s", image));
		ImageView imageView = (ImageView) findViewById(R.id.image);
		Uri uri = Uri.parse("https://usercontent.googleapis.com/freebase/v1/image" + image + "?maxwidth=200&maxheight=200&pad=true");
		Log.d(TAG, String.format("setAuthorImage - uri: %s", uri));
		new DownloadImageTask(imageView).execute(uri.toString());
	}

	private Uri getRandomQuotationUri() {
        Cursor cursor = getContentResolver().query(Quotation.CONTENT_URI, new String[] { Quotation._ID }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", Quotation.CONTENT_URI, cursor, cursor.getCount()));
		
		cursor.moveToPosition(random.nextInt(cursor.getCount()));
		return Quotation.getUri(cursor.getString(0));
	}

	private boolean isQuotationInitialized(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.STATE }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor.getCount()));
		
		cursor.moveToFirst();
		int state = cursor.getInt(0);
		Log.d(TAG, String.format("state: %d", state));
		
		return state != 0;
	}

    private String getQuotation(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.QUOTATION }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor.getCount()));
		
		cursor.moveToFirst();
		String quotation = cursor.getString(0);
		Log.d(TAG, String.format("quotation: %s", quotation));
		
		return quotation;
    }
    
    private String getAuthor(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.AUTHOR_NAME }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor.getCount()));
		
		cursor.moveToFirst();
		String author = cursor.getString(0);
		Log.d(TAG, String.format("author: %s", author));
		
		return author;
    }
    
    private String getAuthorImage(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.AUTHOR_IMAGE }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor.getCount()));
		
		cursor.moveToFirst();
		String image = cursor.getString(0);
		Log.d(TAG, String.format("image: %s", image));
		
		return image;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));

        setContentView(R.layout.activity_quote);
	}
    
    @Override
    protected void onStart() {
        super.onStart();
		Log.d(TAG, String.format("onStart"));
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		Log.d(TAG, String.format("onResume"));
			
		Intent intent = getIntent();
		Uri uri = intent.getData();
		Log.d(TAG, String.format("intent: %s  uri: %s", intent, uri));

		if (uri == null) {
			uri = getRandomQuotationUri();
			Log.d(TAG, String.format("random uri: %s", uri));
		}
		
		if (isQuotationInitialized(uri)) {
			setQuote(getQuotation(uri));
			setAuthor(getAuthor(uri));
	        setAuthorImage(getAuthorImage(uri));
		}
		else {
			quotationObserver.setUri(uri);
			getContentResolver().registerContentObserver(uri, false, quotationObserver);
			
			setQuote("Waiting for quotation to load ...");
			setAuthor("");

			Bundle extras = new Bundle();
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
	        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
	        extras.putString(QuotationSyncAdapter.SYNC_EXTRAS_QUOTATION_UPDATE, uri.toString());
			ContentResolver.requestSync(new QuotationAccount(), QuotationContract.AUTHORITY, extras);
		}
    }
    
	@Override
    protected void onPause() {	
		super.onPause();
		Log.d(TAG, String.format("onPause"));
		
		getContentResolver().unregisterContentObserver(quotationObserver);
    }

    @Override
    protected void onStop() {
		super.onStop();
		Log.d(TAG, String.format("onStop"));
    }
    
    @Override
    protected void onDestroy() {	
		super.onDestroy();
		Log.d(TAG, String.format("onDestroy"));
	}
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
	            return false;
		}
		if (item.getItemId() == R.id.developer) {
	    	    Intent intent = new Intent(this, DeveloperActivity.class);
	    	    startActivity(intent);
	    }
	        
		return true;
    }
}

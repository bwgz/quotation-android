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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class QuotationFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {
	static public final String TAG = QuotationFragment.class.getSimpleName();
	
	static private final String authorsQuery = String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
																Person._ID, QuotationPerson.PERSON_ID, QuotationPerson.TABLE, QuotationPerson.QUOTATION_ID);
	
	static private final int LOADER_ID_QUTOATION	= 1;
	static private final int LOADER_ID_AUTHOR		= 2;
	static private final int[] LOADER_IDS			= { LOADER_ID_QUTOATION, LOADER_ID_AUTHOR };

	private enum State { LOADING, LOADED }
	
	private State state = State.LOADED;
	private LazyProgessHandler progressHandler;
	private ProgressDialog progressDialog;
	private Uri uri;

	private class LazyProgessHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			if (state == State.LOADING) {
				progressDialog.show();
			}
			else {
				progressDialog.dismiss();
			}
		}
	}

	private class LazyProgess extends TimerTask {
		private Handler handler;
		
		public LazyProgess(Handler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			handler.sendMessage(new Message());
		}
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		if (this.uri == null || !this.uri.equals(uri)) {
			this.uri = uri;
			
			TextView textView;
			
			textView = (TextView) getActivity().findViewById(R.id.quote);
			textView.setText(new String());
			
			textView = (TextView) getActivity().findViewById(R.id.author);
			textView.setVisibility(View.GONE);
			textView.setText(new String());
			
			textView = (TextView) getActivity().findViewById(R.id.description);
			textView.setVisibility(View.GONE);
			textView.setText(new String());
			
			ImageView imageView = (ImageView) getActivity().findViewById(R.id.image);
			imageView.setVisibility(View.GONE);
			imageView.setImageResource(R.drawable.person_placeholder);

			if (uri != null) {
				state = State.LOADING;
				
				new Timer().schedule(new LazyProgess(progressHandler), TimeUnit.SECONDS.toMillis(1));
				
				for (int id : LOADER_IDS) {
					if (getLoaderManager().getLoader(id) == null) {
						getLoaderManager().initLoader(id, null, this);
					}
					else {
				        getLoaderManager().restartLoader(id, null, this);
					}
				}
			}
		}
	}

	private void updateQuotation(Cursor cursor) {
		if (cursor.moveToFirst()) {
			TextView textView = (TextView) getActivity().findViewById(R.id.quote);
			textView.setText(cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION)));
			state = State.LOADED;
	        progressDialog.dismiss();
		}
    }

    private String generateCitation(String provider, String statement, String uri) {
    	String citation;
    	
    	if (uri != null) {
    		citation = String.format("<a href=\"%s\" target=\"_new\" title=\"%s\">%s</a>", uri, statement != null ? statement : provider, provider);
    	}
    	else {
    		citation = String.format("[%s]", provider);
    	}
    	
    	return citation;
    }

    private void updateAuthors(Cursor cursor) {
        int authorCount = 0;
        int descriptionCount = 0;
        
        StringBuilder authorBuffer = new StringBuilder();
        StringBuilder descriptionBuffer = new StringBuilder();
	    Bitmap image = null;
	    
        for (int i = 0; cursor.moveToPosition(i); i++) {
        	String name = cursor.getString(cursor.getColumnIndex(Person.NAME));
    		if (name != null) {
				Log.d(TAG, String.format("person name: %s",name));
        		if (authorCount != 0) {
        			authorBuffer.append(", ");
        		}
    			authorBuffer.append(name);
				authorCount++;
    		}
    		
    		String description = cursor.getString(cursor.getColumnIndex(Person.DESCRIPTION));
			if (description != null) {
				Log.d(TAG, String.format("person description: %s", description));
        		if (descriptionCount != 0) {
        			descriptionBuffer.append("\n");
        		}
        		descriptionBuffer.append(description);
        		
        		String citation_provider = cursor.getString(cursor.getColumnIndex(Person.CITATION_PROVIDER));
        		
        		if (citation_provider != null) {
            		String citation_statement = cursor.getString(cursor.getColumnIndex(Person.CITATION_STATEMENT));
            		String citation_uri = cursor.getString(cursor.getColumnIndex(Person.CITATION_URI));
        			descriptionBuffer.append(" ");
        			descriptionBuffer.append(generateCitation(citation_provider, citation_statement, citation_uri));
        		}
				descriptionCount++;
			}
			
			if (image == null) {
				byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Person.IMAGE));
				if (bytes != null && bytes.length != 0) {
			        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			        image = BitmapFactory.decodeStream(in);
			        
					DisplayMetrics metrics = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
					
			        Matrix matrix = new Matrix();
			        matrix.postScale(metrics.scaledDensity, metrics.scaledDensity);

			        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
			        try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
        }
        
        TextView textView;
        
        if (authorBuffer.length() != 0 || descriptionBuffer.length() != 0) {
			textView = (TextView) getActivity().findViewById(R.id.author);
			textView.setText(authorBuffer.toString());
			textView.setVisibility(View.VISIBLE);
        }
		
        if (descriptionBuffer.length() != 0) {
			textView = (TextView) getActivity().findViewById(R.id.description);
			textView.setText(Html.fromHtml(descriptionBuffer.toString()));
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setVisibility(View.VISIBLE);
        }
		
		ImageView imageView = (ImageView) getActivity().findViewById(R.id.image);
		if (image != null) {
			imageView.setImageBitmap(image);
			imageView.setVisibility(View.VISIBLE);
		}
		else {
			imageView.setVisibility(View.GONE);
		}
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading_quote));
        
        progressHandler = new LazyProgessHandler();
    }

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		Log.d(TAG, String.format("onCreateLoader - loaderID: %d  bundle: %s", loaderID, bundle));
		Loader<Cursor> loader = null;

		if (loaderID == LOADER_ID_QUTOATION) {
	        loader = new CursorLoader(
	                getActivity(),		
	                uri,        
	                new String[] { Quotation.QUOTATION },
	                null,      
	                null,      
	                null
	        		);
		}
		else if (loaderID == LOADER_ID_AUTHOR) {
	        loader = new CursorLoader(
	                getActivity(),		
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
		Log.d(TAG, String.format("onLoadFinished - loader: %s  cursor: %s (%d)", loader, cursor, cursor != null ? cursor.getCount() : 0));
		
		if (loader.getId() == LOADER_ID_QUTOATION) {
			updateQuotation(cursor);
		}
		else if (loader.getId() == LOADER_ID_AUTHOR) {
			updateAuthors(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, String.format("onLoaderReset - loader: %s", loader));
	}

}

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
package org.bwgz.quotation.widget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

public class QuoteWidgetService extends IntentService {
	static private final String TAG = QuoteWidgetService.class.getSimpleName();

	static private final String authorsQuery = String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
			Person._ID, QuotationPerson.PERSON_ID, QuotationPerson.TABLE, QuotationPerson.QUOTATION_ID);

	static public final String ACTION_UPDATE_WIDGETS	= "android.intent.action.ACTION_UPDATE_WIDGETS";
	static public final String ACTION_REFRESH_WIDGET	= "android.intent.action.ACTION_UPDATE_WIDGET";
    static public final String EXTRA_UPDATE_LAYOUT_ID	= "intent.extra.EXTRA_UPDATE_LAYOUT_ID";
    static public final String EXTRA_UPDATE_WIDGET_IDS	= "intent.extra.EXTRA_UPDATE_WIDGET_IDS";

    @SuppressWarnings("unused")
    private static class CursorContentObserver extends ContentObserver {
        private boolean hasChanged;
        private boolean selfChange;

        public CursorContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized(this) {
                hasChanged = true;
                this.selfChange = selfChange;
                notifyAll();
            }
        }

        protected synchronized boolean hasChanged(long timeout) throws InterruptedException {
            if (!hasChanged) {
                wait(timeout);
            }
            return hasChanged;
        }

		protected boolean hasChanged() {
            return hasChanged;
        }

        protected void resetStatus() {
            hasChanged = false;
            selfChange = false;
        }

        protected boolean getSelfChangeState() {
            return selfChange;
        }

        protected void setSelfChangeState(boolean state) {
            selfChange = state;
        }
    }

    @SuppressLint("NewApi")
	private String getQuotation(int layout, int appWidgetId, Uri uri) {
		String quotation = null;
		
	    final Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.QUOTATION }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor != null ? cursor.getCount() : 0));
		
		if (cursor != null) {
			if (cursor.getCount() == 0) {
			    RemoteViews remoteViews = new RemoteViews(getPackageName(), layout);
		        AppWidgetManager manager = AppWidgetManager.getInstance(this);
		        
				CursorContentObserver observer = new CursorContentObserver(null);
				cursor.registerContentObserver(observer);
				
				long timeout = TimeUnit.MILLISECONDS.toMillis(100);
				long retries = TimeUnit.MINUTES.toMillis(10) / timeout;
				
				for (int tries = 0; tries < retries; tries++) {
					try {
						if (observer.hasChanged(timeout)) {
							cursor.requery();
							break;
						}
				        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && (tries % 3) == 0) {
						    remoteViews.showNext(R.id.refresher);
					    	manager.updateAppWidget(appWidgetId, remoteViews);
				        }
					} catch (InterruptedException e) {
						Log.e(TAG, String.format("error waiting for quotation - uri : %s", uri), e);
					}
				}
			}
			
			if (cursor.moveToFirst()) {
				quotation = cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION));
			}
			
			cursor.close();
		}
		
		return quotation;
	}

    private String getAuthor(Uri uri) {
        StringBuilder authorBuffer = new StringBuilder();
              
		final Cursor cursor = getContentResolver().query(Person.CONTENT_URI, new String[] { Person.NAME }, authorsQuery,  new String[] { Quotation.getId(uri) }, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor != null ? cursor.getCount() : 0));
		
		if (cursor != null) {
			int authorCount = 0;
	        for (int i = 0; cursor.moveToPosition(i); i++) {
	        	String name = cursor.getString(cursor.getColumnIndex(Person.NAME));
	    		if (name != null) {
					if (authorCount != 0) {
	        			authorBuffer.append(", ");
	        		}
	    			authorBuffer.append(name);
					authorCount++;
	    		}
	        }
			
			cursor.close();
		}
		
		return authorBuffer.toString();
	}
    
    private Bitmap getImage(Uri uri) {
    	Bitmap image = null;
              
		final Cursor cursor = getContentResolver().query(Person.CONTENT_URI, new String[] { Person.IMAGE }, authorsQuery, new String[] { Quotation.getId(uri) }, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor != null ? cursor.getCount() : 0));
		
		if (cursor != null) {
	        for (int i = 0; cursor.moveToPosition(i); i++) {
				byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Person.IMAGE));
				if (bytes != null && bytes.length != 0) {
			        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			        image = BitmapFactory.decodeStream(in);
			        
					DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
					
			        Matrix matrix = new Matrix();
			        float scale = (float) (metrics.scaledDensity * 0.5);
			        matrix.postScale(scale, scale);

			        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
			        try {
						in.close();
					} catch (IOException e) {
					}
				}
	        }
			
			cursor.close();
		}
		
		return image;
	}

    private Uri getRandomQuotationUri() {
        Log.d(TAG, String.format("getRandomQuotationUri"));
		Uri uri = null;
		
		Cursor cursor = getContentResolver().query(Quotation.withAppendedId("/random"), new String[] { Quotation._ID }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			uri = Quotation.withAppendedId(cursor.getString(cursor.getColumnIndex(Quotation._ID)));
	    	Log.d(TAG, String.format("random uri: %s", uri));
			cursor.close();
		}
		
		return uri;
	}

	private void updateWidget(int layout, int appWidgetId, Uri uri, String quotation, String author, Bitmap image) {
        Log.d(TAG, String.format("updateWidget appWidgetId: %d  uri: %s  quotation: %s", appWidgetId, uri, quotation));
		
	    RemoteViews remoteViews = new RemoteViews(getPackageName(), layout);
	    remoteViews.setTextViewText(R.id.quote, quotation);
	    
     	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        if (layout == R.layout.small_quote_widget) {
	        remoteViews.setOnClickPendingIntent(R.id.small_quote_widget, pendingIntent);
        }
        else if (layout == R.layout.large_quote_widget) {
	        remoteViews.setOnClickPendingIntent(R.id.quote, pendingIntent);
	        remoteViews.setOnClickPendingIntent(R.id.image, pendingIntent);
		    
		    remoteViews.setTextViewText(R.id.author, author);
		    if (image == null) {
		    	remoteViews.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		    }
		    else {
		    	remoteViews.setImageViewBitmap(R.id.image, image);
		    }
	    
	        intent = new Intent(this, QuoteWidgetService.class);
	        intent.setAction(QuoteWidgetService.ACTION_UPDATE_WIDGETS);
	        intent.putExtra(QuoteWidgetService.EXTRA_UPDATE_LAYOUT_ID, layout);
	        intent.putExtra(QuoteWidgetService.EXTRA_UPDATE_WIDGET_IDS, new int[] { appWidgetId });
	               
	        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        remoteViews.setOnClickPendingIntent(R.id.refresher, pendingIntent);
        }
	    	
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
    	manager.updateAppWidget(appWidgetId, remoteViews);
	}

	private void onUpdate(AppWidgetManager appWidgetManager, int layout, int appWidgetId) {
    	Log.d(TAG, String.format("onUpdate - appWidgetManager: %s  appWidgetId: %d", appWidgetManager, appWidgetId));
    	
    	Uri uri = getRandomQuotationUri();
    	if (uri == null) {
        	Log.e(TAG, String.format("cannot get random quotation uri for appWidgetId: %d", appWidgetId));
    	}
    	else {
			String quotation = getQuotation(layout, appWidgetId, uri);
			
			if (quotation == null) {
	        	Log.e(TAG, String.format("cannot get quotation for appWidgetId: %d", appWidgetId));
			}
			else {
				String author = null;
				Bitmap image = null;
				
		        if (layout == R.layout.large_quote_widget) {
					author = getAuthor(uri);
					image = getImage(uri);
		        }
		        
				updateWidget(layout, appWidgetId, uri, quotation, author, image);
			}
		}
	}

    private void onUpdate(AppWidgetManager appWidgetManager, int layout, int[] appWidgetIds) {
		Log.d(TAG, String.format("onUpdate - appWidgetManager: %s  appWidgetIds: %s", appWidgetManager, appWidgetIds));
    	for (int appWidgetId : appWidgetIds) {
    		onUpdate(appWidgetManager, layout, appWidgetId);
    	}
    }

	public QuoteWidgetService() {
		super(TAG);
	}

	@Override
    public void onCreate() {
		super.onCreate();
		Log.d(TAG, String.format("onCreate"));
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
		Log.d(TAG, String.format("onDestroy"));
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, String.format("onHandleIntent - intent: %s", intent));
		
		String action = intent.getAction();
		Log.d(TAG, String.format("action: %s", action));
		if (action == null) {
			Log.e(TAG, "intent has no action");
		}
		else if (action.equals(ACTION_UPDATE_WIDGETS)) {
			int layout = intent.getIntExtra(EXTRA_UPDATE_LAYOUT_ID, 0);
		    int[] appWidgetIds = intent.getIntArrayExtra(EXTRA_UPDATE_WIDGET_IDS);
			Log.d(TAG, String.format("layout: 0x%08x  appWidgetIds: %s", layout, appWidgetIds));
		
			onUpdate(AppWidgetManager.getInstance(this), layout, appWidgetIds);
		}
		else {
			Log.e(TAG, String.format("intent has unknown action: %s", action));
		}
	}
}

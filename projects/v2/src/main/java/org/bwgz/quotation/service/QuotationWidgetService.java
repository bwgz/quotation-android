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
package org.bwgz.quotation.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;
import org.bwgz.quotation.core.FreebaseIdLoader;
import org.bwgz.quotation.model.picks.Pick;
import org.bwgz.quotation.widget.picks.PickView;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class QuotationWidgetService extends IntentService {
	static private final String TAG = QuotationWidgetService.class.getSimpleName();

	static private final String authorsQuery = String.format("%s IN (SELECT %s FROM %s WHERE %s = ?)",
			Person._ID, QuotationPerson.PERSON_ID, QuotationPerson.TABLE, QuotationPerson.QUOTATION_ID);

	static public final String ACTION_UPDATE_WIDGETS	= "android.intent.action.ACTION_UPDATE_WIDGETS";
	static public final String ACTION_DELETED_WIDGETS	= "android.intent.action.ACTION_DELETED_WIDGETS";
    static public final String EXTRA_UPDATE_LAYOUT_ID	= "intent.extra.EXTRA_UPDATE_LAYOUT_ID";
    static public final String EXTRA_UPDATE_WIDGET_IDS	= "intent.extra.EXTRA_UPDATE_WIDGET_IDS";

	private static final int IMAGE_WIDTH_DP		= 100;
	private static final int IMAGE_HEIGHT_DP	= 100;

    static private Map<Integer, List<ContentObserver>> contentProviderMap = new ConcurrentHashMap<Integer, List<ContentObserver>>();
    
    static abstract class Updater {
    	abstract boolean update();
    }
    
    static class QuotationUpdater extends Updater {
        private final String TAG = QuotationUpdater.class.getSimpleName();
        
        private Context context;
    	private AppWidgetManager appWidgetManager;
    	private int appWidgetId;
    	private RemoteViews remoteViews;
    	private Uri uri;

		public QuotationUpdater(Context context, AppWidgetManager appWidgetManager, RemoteViews remoteViews, int appWidgetId, Uri uri) {
            Log.d(TAG, String.format("QuotationContentObserver - context: %s  appWidgetManager: %s  appWidgetId: %d  uri: %s", context, appWidgetManager, appWidgetId, uri));
            
            this.context = context;
            this.uri = uri;
            this.appWidgetManager = appWidgetManager;
            this.remoteViews = remoteViews;
            this.appWidgetId = appWidgetId;
		}
 	
    	public boolean update() {
    		boolean result = false;
    		
    		String quotation = new String();
    		
    	    Cursor cursor = context.getContentResolver().query(uri, new String[] { Quotation.QUOTATION }, null, null, null);
    		Log.d(TAG, String.format("onChange - uri: %s  cursor: %s  count: %d", uri, cursor, cursor != null ? cursor.getCount() : 0));
			if (cursor.moveToFirst()) {
				quotation = cursor.getString(cursor.getColumnIndex(Quotation.QUOTATION));
	    		Log.d(TAG, String.format("onChange - quotation: %s", quotation));

	     		remoteViews.setViewVisibility(R.id.refresher, View.VISIBLE);
	     		remoteViews.setViewVisibility(R.id.spinner, View.GONE);

			    result = true;
			}
			cursor.close();
			
 			remoteViews.setTextViewText(R.id.quotation, quotation);
 			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
  		
    		return result;
    	}
    }
 
    static class AuthorUpdater extends Updater {
        private final String TAG = AuthorUpdater.class.getSimpleName();
        
        private Context context;
    	private AppWidgetManager appWidgetManager;
    	private int appWidgetId;
    	private RemoteViews remoteViews;
    	private Uri uri;
 
		private int imageWidth;
		private int imageHeight;

		public AuthorUpdater(Context context, AppWidgetManager appWidgetManager, RemoteViews remoteViews, int appWidgetId, Uri uri) {
            Log.d(TAG, String.format("AuthorContentObserver - context: %s  appWidgetManager: %s  appWidgetId: %d  uri: %s", context, appWidgetManager, appWidgetId, uri));
            
            this.context = context;
            this.uri = uri;
            this.appWidgetManager = appWidgetManager;
            this.remoteViews = remoteViews;
            this.appWidgetId = appWidgetId;
            
            imageWidth = (int) PickView.convertDpToPixel(context, IMAGE_WIDTH_DP);
            imageHeight = (int) PickView.convertDpToPixel(context, IMAGE_HEIGHT_DP);
		}
 	
    	public boolean update() {
    		boolean result = false;
    		
    		String author = new String();
    		Bitmap bitmap = null;
    		
			Cursor cursor = context.getContentResolver().query(Person.CONTENT_URI, new String[] { Person.NAME, Person.IMAGE_ID }, authorsQuery, new String[] { QuotationPerson.getId(uri) }, null);
			Log.d(TAG, String.format("onChange - uri: %s  cursor: %s  count: %d", Person.CONTENT_URI, cursor, cursor != null ? cursor.getCount() : 0));
			if (cursor.moveToFirst()) {
				author = cursor.getString(cursor.getColumnIndex(Person.NAME));
	    		Log.d(TAG, String.format("onChange - author: %s", author));
			
				String image_id = cursor.getString(cursor.getColumnIndex(Person.IMAGE_ID));
				if (image_id != null) {
					FreebaseHelper freebaseHelper = new FreebaseHelper("org.bwgz.quotation", context.getResources().getStringArray(R.array.freebase_api_keys));
					try {
						byte[] bytes = freebaseHelper.fetchImage(image_id, imageWidth, imageHeight);
			    		Log.d(TAG, String.format("onChange - bytes: %s", bytes));
			    		if (bytes != null && bytes.length != 0) {
					        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
					    	bitmap = BitmapFactory.decodeStream(in);
							in.close();
			    		}
					} catch (IOException e) {
			            Log.e(TAG, e.getLocalizedMessage());
					}
				}
				
			    result = true;
			}
			cursor.close();
			
			remoteViews.setTextViewText(R.id.author, author);
			if (bitmap == null) {
				remoteViews.setImageViewResource(R.id.image, R.drawable.ic_launcher);
			}
			else {
				remoteViews.setImageViewBitmap(R.id.image, bitmap);
			}
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    		
    		return result;
    	}
    }

    class AppWidgetContentObserver extends ContentObserver {
        private final String TAG = AppWidgetContentObserver.class.getSimpleName();
        private Updater updater;
    	
		public AppWidgetContentObserver(Handler handler, Updater updater) {
			super(handler);
            Log.d(TAG, String.format("AppWidgetContentObserver - handler %s  updater: %s", handler, updater));
            
            this.updater = updater;
		}
		
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            
            updater.update();
        }
    }
    
    private void onUpdate(AppWidgetManager appWidgetManager, int layout, int appWidgetId) {
        Log.d(TAG, String.format("onUpdate - appWidgetManager: %s  layout: %x  appWidgetIds: %d", appWidgetManager, layout, appWidgetId));
        

    	Pick pick = FreebaseIdLoader.getInstance(getApplicationContext()).getRandomQuotationPick();
    	//pick.setId("/m/048bt58");
		Log.d(TAG, String.format("onUpdate - pick: %s", pick));
	
		Uri quotationUri = Quotation.withAppendedId(pick.getId());
		Log.d(TAG, String.format("onUpdate - uri: %s", quotationUri));
		
	    List<ContentObserver> observers = new ArrayList<ContentObserver>();
 		RemoteViews remoteViews = new RemoteViews(getPackageName(), layout);
 		remoteViews.setViewVisibility(R.id.refresher, View.GONE);
 		remoteViews.setViewVisibility(R.id.spinner, View.VISIBLE);
	    
	    QuotationUpdater quotationUpdater = new QuotationUpdater(getApplicationContext(), appWidgetManager, remoteViews, appWidgetId, quotationUri);
	    if (!quotationUpdater.update()) {
			AppWidgetContentObserver quotationContentObserver = new AppWidgetContentObserver(null, quotationUpdater);
			getContentResolver().registerContentObserver(quotationUri, false, quotationContentObserver);
			observers.add(quotationContentObserver);
			contentProviderMap.put(appWidgetId, observers);
	    }

	    if (layout == R.layout.quotation_widget_large) {
		    Uri quotationPersonUri = QuotationPerson.withAppendedId(pick.getId());
		    AuthorUpdater authorUpdater = new AuthorUpdater(getApplicationContext(), appWidgetManager, remoteViews, appWidgetId, quotationPersonUri);
		    if (!authorUpdater.update()) {
				Cursor cursor = getContentResolver().query(quotationPersonUri, new String[] { QuotationPerson.PERSON_ID }, null, null, null);
				cursor.close();
				
				AppWidgetContentObserver quotationContentObserver = new AppWidgetContentObserver(null, authorUpdater);
				getContentResolver().registerContentObserver(quotationPersonUri, false, quotationContentObserver);
				observers.add(quotationContentObserver);
				contentProviderMap.put(appWidgetId, observers);
		    }
	
			Intent intent = new Intent(this, QuotationWidgetService.class);
	        intent.setAction(QuotationWidgetService.ACTION_UPDATE_WIDGETS);
	        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_LAYOUT_ID, layout);
	        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_WIDGET_IDS, new int[] { appWidgetId });
	               
	        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        remoteViews.setOnClickPendingIntent(R.id.refresher, pendingIntent);
	    }

     	Intent intent = new Intent(Intent.ACTION_VIEW, quotationUri);
     	PendingIntent pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
	    
	    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

    private void onUpdate(AppWidgetManager appWidgetManager, int layout, int[] appWidgetIds) {
		Log.d(TAG, String.format("onUpdate - appWidgetManager: %s  appWidgetIds: %s", appWidgetManager, appWidgetIds));
    	for (int appWidgetId : appWidgetIds) {
    		onUpdate(appWidgetManager, layout, appWidgetId);
    	}
    }

    private void onDeleted(int appWidgetId) {
        Log.d(TAG, String.format("onDeleted - appWidgetIds: %d", appWidgetId));
        
		List<ContentObserver> observers = contentProviderMap.get(appWidgetId);
		if (observers != null) {
			for (ContentObserver observer : observers) {
		        Log.d(TAG, String.format("onDeleted - observer %s", observer));
				getContentResolver().unregisterContentObserver(observer);
			}
		}
    }

    private void onDeleted(int[] appWidgetIds) {
        Log.d(TAG, String.format("onDeleted - appWidgetIds: %s", appWidgetIds));
        
    	for (int appWidgetId : appWidgetIds) {
    		onDeleted(appWidgetId);
    	}
    }

	public QuotationWidgetService() {
		super(TAG);
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
		
			onDeleted(appWidgetIds);
			onUpdate(AppWidgetManager.getInstance(this), layout, appWidgetIds);
		}
		else if (action.equals(ACTION_DELETED_WIDGETS)) {
			int layout = intent.getIntExtra(EXTRA_UPDATE_LAYOUT_ID, 0);
		    int[] appWidgetIds = intent.getIntArrayExtra(EXTRA_UPDATE_WIDGET_IDS);
			Log.d(TAG, String.format("layout: 0x%08x  appWidgetIds: %s", layout, appWidgetIds));
		
			onDeleted(appWidgetIds);
		}
		else {
			Log.e(TAG, String.format("intent has unknown action: %s", action));
		}
	}
}

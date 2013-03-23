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

package org.bwgz.qotd.service;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.bwgz.qotd.R;
import org.bwgz.qotd.activity.QuoteActivity;
import org.bwgz.qotd.activity.QuoteActivity.DownloadImageTask;
import org.bwgz.qotd.widget.QuoteWidgetProvider;
import org.bwgz.quotation.content.provider.QuotationAccount;
import org.bwgz.quotation.content.provider.QuotationContract;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationSyncAdapter;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class QuoteOfTheDayService extends IntentService {
	static private String TAG = QuoteOfTheDayService.class.getSimpleName();
	static public String APP_WIDGET_IDS = "org.bwgz.qotd.service.QuoteOfTheDayService.APP_WIDGET_IDS";
	
	static private class AlarmHandler extends Handler {
    	private QuoteOfTheDayService qotdService;
    	
		public AlarmHandler(QuoteOfTheDayService service) {
    		this.qotdService = service;
    	}
		
        @Override
        public void handleMessage(Message msg) { 
            ComponentName widget = new ComponentName(qotdService, QuoteWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(qotdService);
            
            qotdService.updateAppWidgets(manager.getAppWidgetIds(widget));
            qotdService.setAlarm();
        }
    }
    
    private AlarmHandler handler = new AlarmHandler(this);
	private final Random random = new Random();
 
	private long getTest() {
		Calendar calendar = GregorianCalendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
	
		calendar = new GregorianCalendar(year, month, day, hour, minute + 3, second);
		Log.d(TAG, String.format("next: %s", calendar));
		
		return calendar.getTimeInMillis();
	}
	
	@SuppressWarnings("unused")
	private long getMidnight() {
		Calendar calendar = GregorianCalendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		calendar = new GregorianCalendar(year, month, day);
		Log.d(TAG, String.format("today: %s", calendar));
		calendar.roll(Calendar.DATE, true);
		Log.d(TAG, String.format("tomorrow: %s", calendar));
		
		return calendar.getTimeInMillis();
	}
	
	public void setAlarm() {
		long next = getTest();
		long now = System.currentTimeMillis();
		long delta = next - now;
		Log.d(TAG, String.format(" next: %d", next));
		Log.d(TAG, String.format("  now: %d", now));
		Log.d(TAG, String.format("delta: %d", delta));
	
		handler.sendMessageDelayed(handler.obtainMessage(), delta);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

	class QuoteContentObserver extends ContentObserver {
		private Uri uri;
		private int appWidgetId;
		
		public QuoteContentObserver(Uri uri, int appWidgetId) {
	        super(new Handler());
	        
	        this.uri = uri;
	        this.appWidgetId = appWidgetId;
		}

	    @Override
	    public void onChange(boolean selfChange) {
	        super.onChange(selfChange);
	        Log.d(TAG, "QuoteContentObserver.onChange( " + selfChange + ")");
	        
	        String quotation = getQuotation(uri);
	        if (quotation != null) {
	        	updateAppWidgets(new int[] { appWidgetId });
	        }
	        
    	    getContentResolver().unregisterContentObserver(this); 
	    }
	}

	public QuoteOfTheDayService() {
		super(TAG);
	}
	
    private Uri getRandomQuotationUri() {
        Cursor cursor = getContentResolver().query(Quotation.CONTENT_URI, new String[] { Quotation._ID }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", Quotation.CONTENT_URI, cursor, cursor.getCount()));
		
		cursor.moveToPosition(random.nextInt(cursor.getCount()));
		return Quotation.getUri(cursor.getString(0));
	}

    private String getQuotation(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[] { Quotation.QUOTATION }, null, null, null);
		Log.d(TAG, String.format("%s - cursor: %s  count: %d", uri, cursor, cursor.getCount()));
		
		cursor.moveToFirst();
		String quotation = cursor.getString(0);
		Log.d(TAG, String.format("quotation: %s", quotation));
		
		return quotation;
    }
    
	private void updateAppWidgets(int[] appWidgetIds) {
		Log.d(TAG, String.format("updateWidgets - appWidgetIds: %s", appWidgetIds));
		
		if (appWidgetIds!= null) {
	        for (int appWidgetId : appWidgetIds) {
	    		Log.d(TAG, String.format("widgetId: %s", appWidgetId));
	    		
	    		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_quote);
	    		
	    		Uri uri = getRandomQuotationUri();
	    		String quotation = getQuotation(uri);

	    		if (quotation == null) {
		    	    getContentResolver().registerContentObserver(uri, false, new QuoteContentObserver(uri, appWidgetId)); 
		    	    
		    	    quotation = "Waiting for quotation to load ...";

					Bundle extras = new Bundle();
			        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
			        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
			        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
			        extras.putString(QuotationSyncAdapter.SYNC_EXTRAS_QUOTATION_UPDATE, uri.toString());
					ContentResolver.requestSync(new QuotationAccount(), QuotationContract.AUTHORITY, extras);
	    		}

	        	remoteViews.setTextViewText(R.id.quote, quotation);
	
	        	Intent intent = new Intent(this, QuoteActivity.class);
	        	intent.setData(uri);
	            PendingIntent pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	
	        	remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
	        	
	            AppWidgetManager manager = AppWidgetManager.getInstance(QuoteOfTheDayService.this);
	        	manager.updateAppWidget(appWidgetId, remoteViews);
	        }
		}
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, String.format("onCreate"));
		
		setAlarm();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, String.format("onStartCommand - intent: %s  flags: %d  startId: %d", intent, flags, startId));	
		
		int[] appWidgetIds = intent.getIntArrayExtra(APP_WIDGET_IDS);
		updateAppWidgets(appWidgetIds);
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, String.format("onDestroy"));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}
}

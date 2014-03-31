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
package org.bwgz.quotation.content.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bwgz.google.freebase.cache.QuotationMidCacheLoader;
import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class RandomUriProducer {
	static private final String TAG = RandomUriProducer.class.getSimpleName();
	
	static private final Random random = new Random();
	
	private Context context;
	private QuotationMidCacheLoader midLoader;
	private LoadingCache<Integer, String> midCache;
	
	private boolean isConnected() {
		boolean result = false;
		
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			Log.e(TAG, "Cannot get connectivity manager.");
		}
		else {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info != null) {
				Log.d(TAG, String.format("info: %s", info.toString()));
				result = info.isConnected();
			}
			
			Log.d(TAG, String.format("connected: %s", result));
		}
		
		return result;
	}
	
	public RandomUriProducer(Context context, FreebaseHelper freebaseHelper) {
		Log.d(TAG, String.format("RandomUriProducer"));
		
		this.context = context;
		
		try {
			InputStream in = RandomUriProducer.class.getResourceAsStream("/quotation-mid-cursors.json");
			Log.d(TAG, String.format("in: %s", in));
			midLoader = new QuotationMidCacheLoader(in, freebaseHelper);
			Log.d(TAG, String.format("midLoader: %s (%d)", midLoader, midLoader.getMaxResults()));
			midCache = CacheBuilder.newBuilder().build(midLoader);
			Log.d(TAG, String.format("midCache: %s", midCache));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private String getNextIdFromFreebase() {
		Log.d(TAG, String.format("getNextUriFromFreebase"));
		
		String id = null;
		
		try {
			id = midCache.get(random.nextInt(midLoader.getMaxResults()));
			Log.d(TAG, String.format("network id: %s", id));
		} catch (ExecutionException e) {
			Log.w(TAG, e.getMessage());
		}
		
		return id;
	}
	
	private String getNextIdFromDatabase() {
		Log.d(TAG, String.format("getNextIdFromDatabase"));
		
		String id = null;
		
		Cursor cursor = context.getContentResolver().query(Quotation.CONTENT_URI, 
									        		new String[] { Quotation._ID },
									        		null, null, null);

		if (cursor != null) {
			Log.d(TAG, String.format("cursor count: %s", cursor.getCount()));
			
			if (cursor.getCount() != 0) {
		        if (cursor.moveToPosition(random.nextInt(cursor.getCount()))) {
		        	id = cursor.getString(0);
					Log.d(TAG, String.format("database id: %s", id));
		        }
			}
		        
	        cursor.close();
		}
	        
		return id;
	}
	
	private String getNextIdFromDatabaseWithRetry(int retries, long timeout) {
		Log.d(TAG, String.format("getNextIdFromDatabaseWithRetry - retries: %d  timeout: %d", retries, timeout));
		String id = null;
		
		for (int tries = 0; tries < retries; tries++) {
			id = getNextIdFromDatabase();
			if (id != null) {
				break;
			}
			
			SystemClock.sleep(timeout);
		}
		
		return id;
	}

	public Uri getNextUri() {
		Log.d(TAG, String.format("getNextUri"));
		Uri uri = null;
		
		String id = null;
		if (isConnected()) {
			id = getNextIdFromFreebase();
		}
		if (id == null) {
			id = getNextIdFromDatabaseWithRetry(12, TimeUnit.SECONDS.toMillis(5));
		}
		if (id != null) {
			uri = Quotation.withAppendedId(id);
		}
		
		return uri;
	}
}

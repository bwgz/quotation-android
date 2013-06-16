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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bwgz.google.freebase.client.FreebaseHelper;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class RandomUriProducerTask extends AsyncTask<ArrayBlockingQueue<Uri>, Void, Void> {
	private static final String TAG = RandomUriProducerTask.class.getSimpleName();
	
	private Context context;
	private RandomUriProducer producer;

    public RandomUriProducerTask(Context context, FreebaseHelper freebaseHelper) {
    	this.context = context;
    	producer = new RandomUriProducer(context, freebaseHelper);
    }

    @Override
	protected Void doInBackground(ArrayBlockingQueue<Uri>... params) {
		Log.d(TAG, String.format("doInBackground"));
		ArrayBlockingQueue<Uri> queue = params[0];
		
		Uri uri = null;
		while (true) {
			if (isCancelled()) {
				Log.d(TAG, String.format("cancelled: %s", isCancelled()));
				break;
			}
			if (queue.remainingCapacity() == 0) {
				Log.d(TAG, String.format("queue at capacity: %d", queue.size()));
				break;
			}
			
			Log.d(TAG, String.format("uri: %s", uri));
			
			if (uri == null) {
				uri = producer.getNextUri();
				Log.d(TAG, String.format("fetched uri: %s", uri));
		        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		        cursor.close();
			}
			
			if (uri == null) {
				Log.w(TAG, "not producing random uri");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			else {
				try {
					Log.d(TAG, String.format("queue before: %s (%d)", queue, queue.size()));
					boolean result = queue.offer(uri, 1000, TimeUnit.MILLISECONDS);
					Log.d(TAG, String.format("offer result: %s  queue after: %s (%d)", result, queue, queue.size()));
					if (result) {
						uri = null;
					}
				} catch (InterruptedException e) {
					Log.d(TAG, e.getMessage());
				}
			}
		}
		
		return null;
	}
}


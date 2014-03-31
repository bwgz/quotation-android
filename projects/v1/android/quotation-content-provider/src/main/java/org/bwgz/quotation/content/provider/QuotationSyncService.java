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

import org.bwgz.google.freebase.client.FreebaseHelper;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.util.Log;

public class QuotationSyncService extends Service {
	static private final String TAG = QuotationSyncService.class.getSimpleName();
	private AbstractThreadedSyncAdapter syncAdapter;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	    if (syncAdapter == null) {
			String name = getPackageName();
			String keys[] = null;
			
	        try {
	        	ServiceInfo info = getPackageManager().getServiceInfo(new ComponentName(getPackageName(), getClass().getName()), PackageManager.GET_META_DATA);
				Log.d(TAG, String.format("onCreate - info: %s", info));
		    	
				if (info != null && info.metaData != null) {
					Log.d(TAG, String.format("onCreate - info.metaData: %s", info.metaData));
					
					int id = info.metaData.getInt("freebase.api.keys");
					Log.d(TAG, String.format("onCreate - metaData.id: %x", id));
					if (id != 0) {
						keys = getResources().getStringArray(id);
						Log.d(TAG, String.format("onCreate - freebase.api.keys: %s", keys.toString()));
						for (String key : keys) {
							Log.d(TAG, String.format("onCreate - freebase.api.key: %s", key));
						}
					}
					
					if (keys == null) {
						String key = info.metaData.getString("freebase.api.key");
						Log.d(TAG, String.format("onCreate - freebase.api.key: %s", key));
						
						if (key != null) {
							keys = new String[] { key };
						}
					}
				}
			} catch (NameNotFoundException e) {
				Log.e(TAG, e.getMessage());
			}
			
	        if (keys == null) {
	        	Log.w(TAG, "working without Freebase API key");
	        }

	        FreebaseHelper freebaseHelper = new FreebaseHelper(name, keys);
	    	syncAdapter = new QuotationSyncAdapter(getApplicationContext(), true, freebaseHelper);
	    }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}

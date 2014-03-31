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
package org.bwgz.quotation.content.provider;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.R;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QuotationSyncService extends Service {
	static private final String TAG = QuotationSyncService.class.getSimpleName();
	private AbstractThreadedSyncAdapter syncAdapter;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		
	    if (syncAdapter == null) {
	    	syncAdapter = new QuotationSyncAdapter(getApplicationContext(), true, new FreebaseHelper("org.bwgz.quotation", getResources().getStringArray(R.array.freebase_api_keys)));
	    }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}

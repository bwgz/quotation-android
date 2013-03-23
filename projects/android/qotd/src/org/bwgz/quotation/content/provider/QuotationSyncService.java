package org.bwgz.quotation.content.provider;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QuotationSyncService extends Service {
	static private String TAG = QuotationSyncService.class.getSimpleName();
	private AbstractThreadedSyncAdapter syncAdapter;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	    if (syncAdapter == null) {
	    	syncAdapter = new QuotationSyncAdapter(getApplicationContext(), true);
	    }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}

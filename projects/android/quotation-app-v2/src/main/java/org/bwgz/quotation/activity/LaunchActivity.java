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
package org.bwgz.quotation.activity;

import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.R;
import org.bwgz.quotation.service.QuotationService;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LaunchActivity extends BaseActivity {
	static public final String TAG = LaunchActivity.class.getSimpleName();

	private ResponseReceiver receiver;
	
	private class ResponseReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
	    	Log.d(TAG, String.format("onReceive - context: %s  intent: %s", context, intent));
			startActivity(new Intent(LaunchActivity.this, HomeActivity.class));
		}
	}

	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - savedInstanceState: %s", bundle));

		setContentView(R.layout.launch_activity);
		
		receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(QuotationService.BROADCAST_ACTION));
		startService(new Intent(this, QuotationService.class));
	}
	
	@Override
    protected void onResume() {
		super.onResume();
		Log.d(TAG, String.format("onResume"));
		
    	LinearLayout layout = (LinearLayout) findViewById(R.id.quotation_layout);
    	ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);
    	animator.setDuration(TimeUnit.SECONDS.toMillis(6));
    	animator.start();
        
	    Toast toast = Toast.makeText(this, R.string.launch_message, (int) TimeUnit.SECONDS.toMillis(6));
		toast.show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, String.format("onDestroy"));
		
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
}

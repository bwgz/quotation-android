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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class QuotationAlarmReceiver extends BroadcastReceiver {
	static private final String TAG = QuotationAlarmReceiver.class.getSimpleName();
	
    @Override
    public void onReceive(Context context, Intent intent) {
		Log.d(TAG, String.format("onReceive - context: %s  intent: %s", context, intent));
    	context.startService(new Intent(context, QuotationService.class));
    }
}

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

package org.bwgz.qotd.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class QuoteApplication extends Application {
	static private String TAG = QuoteApplication.class.getSimpleName();
	
	static public String APPLICATION_PREFERENCES	= "application.preferences";
	static public String PREFERENCE_INITIALIZED		= "application.initialized";
	
	@Override
	public  void onCreate() {
		super.onCreate();
    	Log.d(TAG, String.format("onCreate"));
		
		SharedPreferences preferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

		if (preferences.getBoolean(PREFERENCE_INITIALIZED, false) == false) {
		        
			preferences.edit().putBoolean(PREFERENCE_INITIALIZED, true);
		}
		
		preferences.edit().commit();
	}
}

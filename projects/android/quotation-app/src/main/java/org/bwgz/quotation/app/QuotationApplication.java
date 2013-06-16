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
package org.bwgz.quotation.app;

import org.bwgz.quotation.content.provider.QuotationAccount;
import org.bwgz.quotation.content.provider.QuotationContract;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class QuotationApplication extends Application {
	static private String TAG = QuotationApplication.class.getSimpleName();
	
	static public String APPLICATION_PREFERENCES	= "application.preferences";
	static public String PREFERENCE_INITIALIZED		= "application.initialized";

	@Override
	public void onCreate() {
		super.onCreate();
    	Log.d(TAG, String.format("onCreate"));
		
    	SharedPreferences preferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);

		if (preferences.getBoolean(PREFERENCE_INITIALIZED, false) == false) {
	        Account account = new QuotationAccount();
	        AccountManager accountManager = AccountManager.get(getBaseContext());
	        
	        if (accountManager.addAccountExplicitly(account, null, null)) {
	        	Log.i(TAG, String.format("added account %s", account.name));
	        	ContentResolver.setIsSyncable(account, QuotationContract.AUTHORITY, 1);
	        	ContentResolver.setSyncAutomatically(account, QuotationContract.AUTHORITY, true);
 	        	
		        preferences.edit().putBoolean(PREFERENCE_INITIALIZED, true);
	        }
	        
			preferences.edit().commit();
		}
	}
}

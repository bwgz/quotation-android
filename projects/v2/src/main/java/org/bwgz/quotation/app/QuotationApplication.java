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
package org.bwgz.quotation.app;

import org.bwgz.quotation.content.provider.QuotationAccount;
import org.bwgz.quotation.content.provider.QuotationContract;
import org.bwgz.quotation.core.FreebaseIdLoader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class QuotationApplication extends Application {
	static private String TAG = QuotationApplication.class.getSimpleName();
	
	static public String APPLICATION_PREFERENCES							= "quotation.preferences";
	static public String APPLICATION_PREFERENCE_INITIALIZED_DATE			= "quotation.initialized.date";
	static public String APPLICATION_PREFERENCE_QUOTATION_PICKS_MODIFIED	= "quotation.picks.modified";
	static public String APPLICATION_PREFERENCE_PERSON_PICKS_MODIFIED		= "person.picks.modified";
	static public String APPLICATION_PREFERENCE_SUBJECT_PICKS_MODIFIED		= "subject.picks.modified";

	private boolean hasQuotationAccount(AccountManager accountManager) {
		boolean result = false;
		
		for (Account account : accountManager.getAccountsByType(QuotationAccount.TYPE)) {
			if (account.name.equals(QuotationAccount.NAME)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	private void initializeQuotationAccount(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		
		if (!hasQuotationAccount(accountManager)) {
	        Account account = new QuotationAccount();
	        
	        if (accountManager.addAccountExplicitly(account, null, null)) {
	        	Log.i(TAG, String.format("added account %s", account.name));
	        	ContentResolver.setIsSyncable(account, QuotationContract.AUTHORITY, 1);
	        	ContentResolver.setSyncAutomatically(account, QuotationContract.AUTHORITY, true);
	        }
	        else {
	        	Log.e(TAG, "failed to create quotation account");
	        }
		}
	}
	
	private void initialize(Context context) {
    	SharedPreferences preferences = getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
		
		if (!preferences.contains(APPLICATION_PREFERENCE_INITIALIZED_DATE)) {
			initializeQuotationAccount(context);
			
			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(APPLICATION_PREFERENCE_INITIALIZED_DATE, System.currentTimeMillis());
			editor.commit();
		}
		
		FreebaseIdLoader.getInstance(getApplicationContext());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
    	Log.d(TAG, String.format("onCreate"));
    	
    	initialize(getBaseContext());
	}
}

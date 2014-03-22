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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class AccountAuthenticatorService extends Service {
	 private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {

		public AccountAuthenticatorImpl(Context context) {
			super(context);
		}

		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
			return null;
		}

		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public String getAuthTokenLabel(String authTokenType) {
			return null;
		}

		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
			return null;
		}
	 }

	private AbstractAccountAuthenticator accountAuthenticator = null;

	@Override
	public void onCreate() {
		accountAuthenticator = new AccountAuthenticatorImpl(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		IBinder binder = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
			binder = accountAuthenticator.getIBinder();
		}
		return binder;
	}

}

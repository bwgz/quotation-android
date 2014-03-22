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
package org.bwgz.android.environment;

import java.util.ArrayList;

import org.bwgz.android.common.TwoLineItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoTwoLineList extends ArrayList<TwoLineItem> {
	private static final long serialVersionUID = 8136275720486127251L;

	public static final String EXTRA_TYPE_NAME	= "type.name";
	
	public NetworkInfoTwoLineList(Activity activity, Intent intent) {
		super();
		
		ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			String typeName = intent.getStringExtra(EXTRA_TYPE_NAME);
			
			for (NetworkInfo info : manager.getAllNetworkInfo()) {
				
				if (info.getTypeName().equals(typeName)) {
					add(new EnvironmentTwoLineItem("Type", String.valueOf(info.getType())));
					add(new EnvironmentTwoLineItem("Type Name", info.getTypeName()));
					add(new EnvironmentTwoLineItem("Subtype", String.valueOf(info.getSubtype())));
					add(new EnvironmentTwoLineItem("Subtype Name", info.getSubtypeName()));
					add(new EnvironmentTwoLineItem("State", info.getState().toString()));
					add(new EnvironmentTwoLineItem("Detailed State", info.getDetailedState().toString()));
					add(new EnvironmentTwoLineItem("Extra Info", info.getExtraInfo()));
					add(new EnvironmentTwoLineItem("Is Available", String.valueOf(info.isAvailable())));
					add(new EnvironmentTwoLineItem("Is Connect", String.valueOf(info.isConnected())));
					add(new EnvironmentTwoLineItem("Is Connected or Connecting", String.valueOf(info.isConnectedOrConnecting())));
					add(new EnvironmentTwoLineItem("Is Failover", String.valueOf(info.isFailover())));
					add(new EnvironmentTwoLineItem("Is Roaming", String.valueOf(info.isRoaming())));
					add(new EnvironmentTwoLineItem("Reason", info.getReason()));
				    
				    break;
				}
			}
		}
	}
}

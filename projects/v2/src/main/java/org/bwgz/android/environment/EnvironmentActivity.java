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
import java.util.List;

import org.bwgz.android.common.ActivityItem;
import org.bwgz.android.common.ActivityListActivity;
import org.bwgz.android.common.TwoLineItem;

import android.os.Bundle;

public class EnvironmentActivity extends ActivityListActivity {
	private List<TwoLineItem> list = new ArrayList<TwoLineItem>();
	
	public EnvironmentActivity() {
		list.add(new ActivityItem("Configuration", "Device configuration information that can impact the resources the application retrieves", ConfigurationActivity.class));
		list.add(new ActivityItem("Display", "General information about a display, such as its size, density, and font scaling", DisplayActivity.class));
		list.add(new ActivityItem("Operating System", "Information about the current build, extracted from system properties", OperatingSystemActivity.class));
		list.add(new ActivityItem("System", "System related information", SystemActivity.class));
		list.add(new ActivityItem("Connectivity", "Status of network interfaces", ConnectivityActivity.class));
	}
	
	@Override
	public List<TwoLineItem> getList() {
		return list;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    }
}

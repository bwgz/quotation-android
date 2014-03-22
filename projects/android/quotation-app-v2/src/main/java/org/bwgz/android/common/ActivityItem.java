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
package org.bwgz.android.common;

import android.app.Activity;

public class ActivityItem implements TwoLineItem {
    private String title;
    private String description;
    private Class<? extends Activity> activity;
    
    public ActivityItem(String title, String description, Class<? extends Activity> activity) {
    	this.title = title;
    	this.description = description;
    	this.activity = activity;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class<? extends Activity> getActivity() {
		return activity;
	}

	public void setActivity(Class<? extends Activity> activity) {
		this.activity = activity;
	}

	@Override
	public String getLineOne() {
		return getTitle();
	}

	@Override
	public String getLineTwo() {
		return getDescription();
	}
}


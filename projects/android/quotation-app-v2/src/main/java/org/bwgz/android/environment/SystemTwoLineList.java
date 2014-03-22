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
import java.util.Properties;

import org.bwgz.android.common.TwoLineItem;

public class SystemTwoLineList extends ArrayList<TwoLineItem> {
	private static final long serialVersionUID = 4655203176878703218L;

	public SystemTwoLineList() {
		super();
		
		Properties properties = System.getProperties(); 
		
		for (Object key : properties.keySet()) {
			add(new EnvironmentTwoLineItem(key.toString(), properties.getProperty(key.toString()).toString()));
		}
	}

}

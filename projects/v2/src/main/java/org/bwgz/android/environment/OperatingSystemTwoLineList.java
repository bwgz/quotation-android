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
import java.util.HashMap;

import org.bwgz.android.common.TwoLineItem;

import android.os.Build;

public class OperatingSystemTwoLineList extends ArrayList<TwoLineItem> {
	private static final long serialVersionUID = -8754999754851526606L;
	
	private static int JELLY_BEAN_MR1 = 17;
	
	private static final HashMap<Integer, String> map = new HashMap<Integer, String>();
	
	{
		map.put(Integer.valueOf(Build.VERSION_CODES.BASE), "Base");
		map.put(Integer.valueOf(Build.VERSION_CODES.BASE_1_1), "Base 1.1");
		map.put(Integer.valueOf(Build.VERSION_CODES.CUPCAKE), "Cupcake");
		map.put(Integer.valueOf(Build.VERSION_CODES.DONUT), "Donut");
		map.put(Integer.valueOf(Build.VERSION_CODES.ECLAIR), "Eclair");
		map.put(Integer.valueOf(Build.VERSION_CODES.ECLAIR_0_1), "Eclair 0.1");
		map.put(Integer.valueOf(Build.VERSION_CODES.ECLAIR_MR1), "Eclair MR1");
		map.put(Integer.valueOf(Build.VERSION_CODES.FROYO), "Froyo");
		map.put(Integer.valueOf(Build.VERSION_CODES.GINGERBREAD), "Gingerbread");
		map.put(Integer.valueOf(Build.VERSION_CODES.GINGERBREAD_MR1), "Gingerbread MR1");
		map.put(Integer.valueOf(Build.VERSION_CODES.HONEYCOMB), "Honeycomb");
		map.put(Integer.valueOf(Build.VERSION_CODES.HONEYCOMB_MR1), "Honeycomb MR1");
		map.put(Integer.valueOf(Build.VERSION_CODES.HONEYCOMB_MR2), "Honeycomb MR2");
		map.put(Integer.valueOf(Build.VERSION_CODES.ICE_CREAM_SANDWICH), "Ice Cream Sandwich");
		map.put(Integer.valueOf(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1), "Ice Cream Sandwich MR1");
		map.put(Integer.valueOf(Build.VERSION_CODES.JELLY_BEAN), "Jelly Bean");
		map.put(Integer.valueOf(JELLY_BEAN_MR1), "Jelly Bean MR 1");
	}

	private String getSDKName(int sdk) {
		String name;
		
		name = map.get(Integer.valueOf(sdk));
		if (name == null) {
			name = "unknown";
		}
		
		return name;
	}

	public OperatingSystemTwoLineList() {
		super();
		
		add(new EnvironmentTwoLineItem("Incremental", Build.VERSION.INCREMENTAL));
		add(new EnvironmentTwoLineItem("Release", Build.VERSION.RELEASE));
		add(new EnvironmentTwoLineItem("SDK", String.format("%s (%s)", Build.VERSION.SDK, getSDKName(Build.VERSION.SDK_INT))));
		add(new EnvironmentTwoLineItem("Code Name", Build.VERSION.CODENAME));

		add(new EnvironmentTwoLineItem("ID", Build.ID));
		add(new EnvironmentTwoLineItem("Display", Build.DISPLAY));
		add(new EnvironmentTwoLineItem("Product", Build.PRODUCT));
		add(new EnvironmentTwoLineItem("Device", Build.DEVICE));
		add(new EnvironmentTwoLineItem("Board", Build.BOARD));
		add(new EnvironmentTwoLineItem("CPU ABI", Build.CPU_ABI));
		add(new EnvironmentTwoLineItem("CPU ABI2", Build.CPU_ABI2));
		add(new EnvironmentTwoLineItem("Manufacturer", Build.MANUFACTURER));
		add(new EnvironmentTwoLineItem("Brand", Build.BRAND));
		add(new EnvironmentTwoLineItem("Model", Build.MODEL));
		add(new EnvironmentTwoLineItem("Boot Loader", Build.BOOTLOADER));
		add(new EnvironmentTwoLineItem("Radio", Build.RADIO));
		add(new EnvironmentTwoLineItem("Hardware", Build.HARDWARE));
		add(new EnvironmentTwoLineItem("Serial", Build.SERIAL));
		add(new EnvironmentTwoLineItem("Type", Build.TYPE));
		add(new EnvironmentTwoLineItem("Tags", Build.TAGS));
		add(new EnvironmentTwoLineItem("Fingerprint", Build.FINGERPRINT));
		add(new EnvironmentTwoLineItem("Time", Long.toString(Build.TIME)));
		add(new EnvironmentTwoLineItem("User", Build.USER));
		add(new EnvironmentTwoLineItem("Host", Build.HOST));
	}
}

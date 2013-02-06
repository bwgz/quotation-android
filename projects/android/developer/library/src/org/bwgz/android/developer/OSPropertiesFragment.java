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

package org.bwgz.android.developer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Build;

public class OSPropertiesFragment extends SimpleTwoLineListViewFragment<TwoLineData> {
	private static int ICE_CREAM_SANDWICH_MR1 = 15;
	private static int JELLY_BEAN = 16;
	private static int JELLY_BEAN_MR1 = 17;
	
	private static HashMap<Integer, String> map = new HashMap<Integer, String>();
	
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
		map.put(Integer.valueOf(ICE_CREAM_SANDWICH_MR1), "Ice Cream Sandwich MR1");
		map.put(Integer.valueOf(JELLY_BEAN), "Jelly Bean");
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

	@Override
	protected List<TwoLineData> getList() {
		List<TwoLineData> list = new ArrayList<TwoLineData>();

		list.add(new TwoLineData("Incremental", Build.VERSION.INCREMENTAL));
		list.add(new TwoLineData("Release", Build.VERSION.RELEASE));
		list.add(new TwoLineData("SDK", String.format("%s (%s)", Build.VERSION.SDK, getSDKName(Build.VERSION.SDK_INT))));
		list.add(new TwoLineData("Code Name", Build.VERSION.CODENAME));

		list.add(new TwoLineData("ID", Build.ID));
		list.add(new TwoLineData("Display", Build.DISPLAY));
		list.add(new TwoLineData("Product", Build.PRODUCT));
		list.add(new TwoLineData("Device", Build.DEVICE));
		list.add(new TwoLineData("Board", Build.BOARD));
		list.add(new TwoLineData("CPU ABI", Build.CPU_ABI));
		list.add(new TwoLineData("CPU ABI2", Build.CPU_ABI2));
		list.add(new TwoLineData("Manufacturer", Build.MANUFACTURER));
		list.add(new TwoLineData("Brand", Build.BRAND));
		list.add(new TwoLineData("Model", Build.MODEL));
		list.add(new TwoLineData("Boot Loader", Build.BOOTLOADER));
		list.add(new TwoLineData("Radio", Build.RADIO));
		list.add(new TwoLineData("Hardware", Build.HARDWARE));
		list.add(new TwoLineData("Serial", Build.SERIAL));
		list.add(new TwoLineData("Type", Build.TYPE));
		list.add(new TwoLineData("Tags", Build.TAGS));
		list.add(new TwoLineData("Fingerprint", Build.FINGERPRINT));
		list.add(new TwoLineData("Time", Long.toString(Build.TIME)));
		list.add(new TwoLineData("User", Build.USER));
		list.add(new TwoLineData("Host", Build.HOST));

		return list;
	}
}


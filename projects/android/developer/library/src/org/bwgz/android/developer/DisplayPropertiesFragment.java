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
import java.util.List;

import android.util.DisplayMetrics;

public class DisplayPropertiesFragment extends SimpleTwoLineListViewFragment<TwoLineData> {
	private String getDensityString(int density) {
		String string;
		
		if (density <= DisplayMetrics.DENSITY_LOW) {
			string = "low";
		}
		else if (density <= DisplayMetrics.DENSITY_MEDIUM) {
			string = "medium";
		}
		else if (density <= DisplayMetrics.DENSITY_TV) {
			string = "tv";
		}
		else if (density <= DisplayMetrics.DENSITY_HIGH) {
			string = "high";
		}
		else {
			string = "xhigh";
		}
		
		return string;
	}

	@Override
	protected List<TwoLineData> getList() {
		List<TwoLineData> list = new ArrayList<TwoLineData>();
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		
		list.add(new TwoLineData("Width (pixels)", Integer.toString(metrics.widthPixels)));
		list.add(new TwoLineData("Height (pixels)", Integer.toString(metrics.heightPixels)));
		list.add(new TwoLineData("Density", Double.toString(metrics.density)));
		list.add(new TwoLineData("Density DPI", String.format("%d (%s)", metrics.densityDpi, getDensityString(metrics.densityDpi))));
		list.add(new TwoLineData("Scaled Density", Double.toString(metrics.scaledDensity)));
		list.add(new TwoLineData("xdpi", Double.toString(metrics.xdpi)));
		list.add(new TwoLineData("ydpi", Double.toString(metrics.ydpi)));

		return list;
	}
}


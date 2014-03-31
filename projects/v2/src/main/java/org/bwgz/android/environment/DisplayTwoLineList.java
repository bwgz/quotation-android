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

import android.util.DisplayMetrics;

public class DisplayTwoLineList extends ArrayList<TwoLineItem> {
	private static final long serialVersionUID = -4812004615155323598L;
	
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
	
	public DisplayTwoLineList(DisplayMetrics metrics) {
		super();
		
		add(new EnvironmentTwoLineItem("Width", String.format("%dpx %ddp", metrics.widthPixels, (int) ((metrics.widthPixels / metrics.density) + 0.5))));
		add(new EnvironmentTwoLineItem("Height", String.format("%dpx %ddp", metrics.heightPixels, (int) ((metrics.heightPixels / metrics.density) + 0.5))));
		add(new EnvironmentTwoLineItem("Density", Double.toString(metrics.density)));
		add(new EnvironmentTwoLineItem("Density DPI", String.format("%d (%s)", metrics.densityDpi, getDensityString(metrics.densityDpi))));
		add(new EnvironmentTwoLineItem("Scaled Density", Double.toString(metrics.scaledDensity)));
		add(new EnvironmentTwoLineItem("xdpi", Double.toString(metrics.xdpi)));
		add(new EnvironmentTwoLineItem("ydpi", Double.toString(metrics.ydpi)));
	}

}

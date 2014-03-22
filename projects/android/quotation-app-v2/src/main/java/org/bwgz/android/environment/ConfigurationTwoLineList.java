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

import android.content.res.Configuration;

public class ConfigurationTwoLineList extends ArrayList<TwoLineItem> {
	private static final long serialVersionUID = -4812004615155323598L;
	
	private String getHardKeyboardHidden(int value) {
		String string = new String();
		
		if (value == Configuration.HARDKEYBOARDHIDDEN_NO) {
			string = "no";
		}
		else if (value == Configuration.HARDKEYBOARDHIDDEN_NO) {
			string = "yes";
		}
		else if (value == Configuration.HARDKEYBOARDHIDDEN_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	
	
	private String getKeyboard(int value) {
		String string = new String();
		
		if (value == Configuration.KEYBOARD_12KEY) {
			string = "12 key";
		}
		else if (value == Configuration.KEYBOARD_NOKEYS) {
			string = "no keys";
		}
		else if (value == Configuration.KEYBOARD_QWERTY) {
			string = "qwerty";
		}
		else if (value == Configuration.KEYBOARD_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	

	private String getKeyboardHidden(int value) {
		String string = new String();
		
		if (value == Configuration.KEYBOARDHIDDEN_NO) {
			string = "no";
		}
		else if (value == Configuration.KEYBOARDHIDDEN_NO) {
			string = "yes";
		}
		else if (value == Configuration.KEYBOARDHIDDEN_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	

	private String getNavigation(int value) {
		String string = new String();
		
		if (value == Configuration.NAVIGATION_DPAD) {
			string = "device pad";
		}
		else if (value == Configuration.NAVIGATION_NONAV) {
			string = "no navigation";
		}
		else if (value == Configuration.NAVIGATION_TRACKBALL) {
			string = "track ball";
		}
		else if (value == Configuration.NAVIGATION_UNDEFINED) {
			string = "undefined";
		}
		else if (value == Configuration.NAVIGATION_WHEEL) {
			string = "wheel";
		}
		
		return string;
	}	

	private String getNavigationHidden(int value) {
		String string = new String();
		
		if (value == Configuration.NAVIGATIONHIDDEN_NO) {
			string = "no";
		}
		else if (value == Configuration.NAVIGATIONHIDDEN_YES) {
			string = "yes";
		}
		else if (value == Configuration.NAVIGATIONHIDDEN_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	

	private String getOrientation(int value) {
		String string = new String();
		
		if (value == Configuration.ORIENTATION_LANDSCAPE) {
			string = "landscape";
		}
		else if (value == Configuration.ORIENTATION_PORTRAIT) {
			string = "portrait";
		}
		
		return string;
	}	

	private String getScreenLayoutSize(int value) {
		String string = new String();
		
		if (value == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			string = "normal";
		}
		else if (value == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			string = "normal";
		}
		else if (value == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			string = "small";
		}
		else if (value == Configuration.SCREENLAYOUT_SIZE_UNDEFINED) {
			string = "undefined";
		}
		else if (value == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			string = "xlarge";
		}
		
		return string;
	}	
	
	private String getScreenLayoutLong(int value) {
		String string = new String();
		
		if (value == Configuration.SCREENLAYOUT_LONG_NO) {
			string = "portrait";
		}
		else if (value == Configuration.SCREENLAYOUT_LONG_UNDEFINED) {
			string = "undefined";
		}
		else if (value == Configuration.SCREENLAYOUT_LONG_YES) {
			string = "yes";
		}
		
		return string;
	}	
	
	private String getTouchScreen(int value) {
		String string = new String();
		
		if (value == Configuration.TOUCHSCREEN_NOTOUCH) {
			string = "no touch";
		}
		else if (value == Configuration.TOUCHSCREEN_FINGER) {
			string = "finger";
		}
		else if (value == Configuration.TOUCHSCREEN_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	
	
	
	private String getUiType(int value) {
		String string = new String();
		
		if (value == Configuration.UI_MODE_TYPE_APPLIANCE) {
			string = "appliance";
		}
		else if (value == Configuration.UI_MODE_TYPE_CAR) {
			string = "car";
		}
		else if (value == Configuration.UI_MODE_TYPE_DESK) {
			string = "desk";
		}
		else if (value == Configuration.UI_MODE_TYPE_NORMAL) {
			string = "normal";
		}
		else if (value == Configuration.UI_MODE_TYPE_TELEVISION) {
			string = "television";
		}
		else if (value == Configuration.UI_MODE_TYPE_UNDEFINED) {
			string = "undefined";
		}
		
		return string;
	}	

	private String getUiNight(int value) {
		String string = new String();
		
		if (value == Configuration.UI_MODE_NIGHT_NO) {
			string = "no";
		}
		else if (value == Configuration.UI_MODE_NIGHT_UNDEFINED) {
			string = "undefined";
		}
		else if (value == Configuration.UI_MODE_NIGHT_YES) {
			string = "yes";
		}
		
		return string;
	}	

	public ConfigurationTwoLineList(Configuration configuration ) {
		super();
		
		add(new EnvironmentTwoLineItem("Scaling factor for fonts", Float.toString(configuration.fontScale)));
		add(new EnvironmentTwoLineItem("Whether the hard keyboard has been hidden", getHardKeyboardHidden(configuration.hardKeyboardHidden)));
		add(new EnvironmentTwoLineItem("Kind of keyboard attached to the device", getKeyboard(configuration.keyboard)));
		add(new EnvironmentTwoLineItem("Whether any keyboard is available", getKeyboardHidden(configuration.keyboardHidden)));
		add(new EnvironmentTwoLineItem("Current user preference for the locale", configuration.locale.toString()));
		add(new EnvironmentTwoLineItem("IMSI MCC (Mobile Country Code)", Integer.toString(configuration.mcc)));
		add(new EnvironmentTwoLineItem("IMSI MNC (Mobile Network Code)", Integer.toString(configuration.mnc)));
		add(new EnvironmentTwoLineItem("Kind of navigation method available on the device", getNavigation(configuration.navigation)));
		add(new EnvironmentTwoLineItem("Whether any 5-way or DPAD navigation available", getNavigationHidden(configuration.navigationHidden)));
		add(new EnvironmentTwoLineItem("Overall orientation of the screen", getOrientation(configuration.orientation)));
		add(new EnvironmentTwoLineItem("Screen layout size", getScreenLayoutSize(configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)));
		add(new EnvironmentTwoLineItem("Screen layout long", getScreenLayoutLong(configuration.screenLayout & Configuration.SCREENLAYOUT_LONG_MASK)));
		add(new EnvironmentTwoLineItem("Kind of touch screen attached to the device", getTouchScreen(configuration.touchscreen)));
		add(new EnvironmentTwoLineItem("UI type", getUiType(configuration.uiMode & Configuration.UI_MODE_TYPE_MASK)));
		add(new EnvironmentTwoLineItem("UI night", getUiNight(configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK)));
	}

}

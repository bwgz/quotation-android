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
package org.bwgz.quotation.activity;

import org.bwgz.quotation.core.VolleySingleton;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

public class VolleyActivity extends BaseActivity {
	private ImageLoader	ImageLoader;
	
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Log.d(TAG, String.format("onCreate - bundle: %s", bundle));
        
		ImageLoader = VolleySingleton.getInstance(this).getImageLoader();
    }
	
	protected ImageLoader getImageLoader() {
		return ImageLoader;
	}
}

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

import org.bwgz.quotation.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdMobActivity extends TrackerActivity {
	private AdView adView;

	@Override
	public void onStart() {
        adView = (AdView) findViewById(R.id.adView);
        if (adView != null) {
        	AdRequest adRequest = new AdRequest.Builder()
	            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	            .build();
        			
        	adView.loadAd(adRequest);
        }
		
		super.onStart();
	}

    @Override
    protected void onPause() {
        if (adView != null) {
        	adView.pause();
        }
        
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (adView != null) {
        	adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
        	adView.destroy();
        }
        
        super.onDestroy();
    }
}

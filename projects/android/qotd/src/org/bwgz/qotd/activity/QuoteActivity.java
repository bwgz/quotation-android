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

package org.bwgz.qotd.activity;

import org.bwgz.android.developer.DeveloperActivity;
import org.bwgz.qotd.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class QuoteActivity extends Activity {
    static private String TAG = QuoteActivity.class.getSimpleName();

    static public final String qotd = "Worry a little bit every day and in a lifetime you will lose a couple of years. If something is wrong, fix it if you can. But train yourself not to worry. Worry never fixes anything.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.i(TAG, String.format("onCreate - savedInstanceState: %s", savedInstanceState));
        
        setContentView(R.layout.activity_quote);
        
        TextView textView = (TextView)findViewById(R.id.quote);
        textView.setText(qotd);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            return false;
        }
        if (item.getItemId() == R.id.developer) {
    	    Intent intent = new Intent(this, DeveloperActivity.class);
    	    startActivity(intent);
    	}
        
        return true;
    }
}

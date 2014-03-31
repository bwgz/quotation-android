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
package org.bwgz.quotation.widget;

import org.bwgz.quotation.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmallQuoteWidgetProvider extends AppWidgetProvider {
    static private final String TAG = SmallQuoteWidgetProvider.class.getSimpleName();
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, String.format("onUpdate - context %s  appWidgetManager: %s  appWidgetIds: %s", context, appWidgetManager, appWidgetIds));
        
        Intent intent = new Intent(context, QuoteWidgetService.class);
        intent.setAction(QuoteWidgetService.ACTION_UPDATE_WIDGETS);
        intent.putExtra(QuoteWidgetService.EXTRA_UPDATE_LAYOUT_ID, R.layout.small_quote_widget);
        intent.putExtra(QuoteWidgetService.EXTRA_UPDATE_WIDGET_IDS, appWidgetIds);
        Log.d(TAG, String.format("intent: %s", intent));
        ComponentName name = context.startService(intent);
        Log.d(TAG, String.format("component name: %s", name));
    }
}


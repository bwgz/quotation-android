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

package org.bwgz.qotd.widget;

import org.bwgz.qotd.service.QuoteOfTheDayService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class QuoteWidgetProvider extends AppWidgetProvider {
    static private String TAG = QuoteWidgetProvider.class.getSimpleName();
   
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, String.format("onEnabled  - context %s", context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, String.format("onUpdate - context %s  appWidgetManager: %s  appWidgetIds: %s", context, appWidgetManager, appWidgetIds));

        Intent intent = new Intent(context, QuoteOfTheDayService.class);
        intent.putExtra(QuoteOfTheDayService.APP_WIDGET_IDS, appWidgetIds);
        context.startService(intent);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, String.format("onDeleted - context %s  appWidgetIds: %s", context, appWidgetIds));
    }

    @Override
    public void onDisabled (Context context) {
        Log.d(TAG, String.format("onDisabled   - context %s", context));
        
        context.stopService(new Intent(context, QuoteOfTheDayService.class));
    }
}
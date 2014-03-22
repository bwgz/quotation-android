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
package org.bwgz.quotation.appwidget;

import org.bwgz.quotation.R;
import org.bwgz.quotation.service.QuotationWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmallQuotationWidgetProvider extends AppWidgetProvider {
    static private final String TAG = SmallQuotationWidgetProvider.class.getSimpleName();
  
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, String.format("onUpdate - context %s  appWidgetManager: %s  appWidgetIds: %s", context, appWidgetManager, appWidgetIds));
 
        Intent intent = new Intent(context, QuotationWidgetService.class);
        intent.setAction(QuotationWidgetService.ACTION_UPDATE_WIDGETS);
        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_LAYOUT_ID, R.layout.quotation_widget_small);
        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_WIDGET_IDS, appWidgetIds);
        Log.d(TAG, String.format("intent: %s", intent));
        ComponentName name = context.startService(intent);
        Log.d(TAG, String.format("component name: %s", name));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, String.format("onDeleted - context %s  appWidgetIds: %s", context, appWidgetIds));
        
        Intent intent = new Intent(context, QuotationWidgetService.class);
        intent.setAction(QuotationWidgetService.ACTION_DELETED_WIDGETS);
        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_LAYOUT_ID, R.layout.quotation_widget_small);
        intent.putExtra(QuotationWidgetService.EXTRA_UPDATE_WIDGET_IDS, appWidgetIds);
        Log.d(TAG, String.format("intent: %s", intent));
        ComponentName name = context.startService(intent);
        Log.d(TAG, String.format("component name: %s", name));
    }
}


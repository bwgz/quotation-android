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

import org.bwgz.qotd.R;
import org.bwgz.qotd.activity.QuoteActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class QuoteWidgetProvider extends AppWidgetProvider {
    static private String TAG = QuoteWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, String.format("onUpdate - context %s  appWidgetManager: %s  appWidgetIds: %s", context, appWidgetManager, appWidgetIds));

        ComponentName thisWidget = new ComponentName(context, QuoteWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
          RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_quote);
          remoteViews.setTextViewText(R.id.quote, QuoteActivity.qotd);

          Intent intent = new Intent(context, QuoteActivity.class);
          PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

          remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

          appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
    
}
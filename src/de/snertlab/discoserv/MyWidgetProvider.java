package de.snertlab.discoserv;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	DiscoServDataHelper dh = new DiscoServDataHelper();	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		int widgetId = appWidgetIds[0];
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		views.setTextViewText(R.id.txtViewWidget, dh.getLastGuthabenFromDb(context).getGuthaben());
		appWidgetManager.updateAppWidget(widgetId, views);
	}
}

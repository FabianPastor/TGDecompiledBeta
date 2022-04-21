package org.telegram.messenger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import org.telegram.ui.LaunchActivity;

public class FeedWidgetProvider extends AppWidgetProvider {
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int a = 0; a < appWidgetIds.length; a++) {
            SharedPreferences.Editor edit = context.getSharedPreferences("shortcut_widget", 0).edit();
            SharedPreferences.Editor remove = edit.remove("account" + appWidgetIds[a]);
            remove.remove("dialogId" + appWidgetIds[a]).commit();
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent2 = new Intent(context, FeedWidgetService.class);
        intent2.putExtra("appWidgetId", appWidgetId);
        intent2.setData(Uri.parse(intent2.toUri(1)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), NUM);
        rv.setRemoteAdapter(appWidgetId, NUM, intent2);
        rv.setEmptyView(NUM, NUM);
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.addFlags(67108864);
        intent.addCategory("android.intent.category.LAUNCHER");
        rv.setPendingIntentTemplate(NUM, PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM));
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
}

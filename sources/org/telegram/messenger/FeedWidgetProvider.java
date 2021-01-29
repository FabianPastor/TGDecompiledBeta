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

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        super.onUpdate(context, appWidgetManager, iArr);
        for (int updateWidget : iArr) {
            updateWidget(context, appWidgetManager, updateWidget);
        }
    }

    public void onDeleted(Context context, int[] iArr) {
        super.onDeleted(context, iArr);
        for (int i = 0; i < iArr.length; i++) {
            SharedPreferences.Editor edit = context.getSharedPreferences("feed_widget", 0).edit();
            SharedPreferences.Editor remove = edit.remove("account" + iArr[i]);
            remove.remove("dialogId" + iArr[i]).commit();
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int i) {
        Intent intent = new Intent(context, FeedWidgetService.class);
        intent.putExtra("appWidgetId", i);
        intent.setData(Uri.parse(intent.toUri(1)));
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), NUM);
        remoteViews.setRemoteAdapter(i, NUM, intent);
        remoteViews.setEmptyView(NUM, NUM);
        Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent2.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent2.setFlags(32768);
        intent2.addCategory("android.intent.category.LAUNCHER");
        remoteViews.setPendingIntentTemplate(NUM, PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, NUM));
        appWidgetManager.updateAppWidget(i, remoteViews);
    }
}

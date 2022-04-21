package org.telegram.messenger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public class ContactsWidgetProvider extends AppWidgetProvider {
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
        ApplicationLoader.postInitApplication();
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor editor = preferences.edit();
        for (int a = 0; a < appWidgetIds.length; a++) {
            int accountId = preferences.getInt("account" + appWidgetIds[a], -1);
            if (accountId >= 0) {
                AccountInstance.getInstance(accountId).getMessagesStorage().clearWidgetDialogs(appWidgetIds[a]);
            }
            editor.remove("account" + appWidgetIds[a]);
            editor.remove("type" + appWidgetIds[a]);
            editor.remove("deleted" + appWidgetIds[a]);
        }
        editor.commit();
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static int getCellsForSize(int size) {
        int n = 2;
        while (n * 86 < size) {
            n++;
        }
        return n - 1;
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        int id;
        Context context2 = context;
        AppWidgetManager appWidgetManager2 = appWidgetManager;
        int i = appWidgetId;
        ApplicationLoader.postInitApplication();
        int rows = getCellsForSize(appWidgetManager.getAppWidgetOptions(appWidgetId).getInt("appWidgetMaxHeight"));
        Intent intent2 = new Intent(context2, ContactsWidgetService.class);
        intent2.putExtra("appWidgetId", i);
        intent2.setData(Uri.parse(intent2.toUri(1)));
        SharedPreferences preferences = context2.getSharedPreferences("shortcut_widget", 0);
        if (!preferences.getBoolean("deleted" + i, false)) {
            int accountId = preferences.getInt("account" + i, -1);
            if (accountId == -1) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("account" + i, UserConfig.selectedAccount);
                editor.putInt("type" + i, 0).commit();
            }
            ArrayList<Long> selectedDialogs = new ArrayList<>();
            if (accountId >= 0) {
                int i2 = accountId;
                SharedPreferences sharedPreferences = preferences;
                AccountInstance.getInstance(accountId).getMessagesStorage().getWidgetDialogIds(appWidgetId, 1, selectedDialogs, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false);
            } else {
                SharedPreferences sharedPreferences2 = preferences;
            }
            int count = (int) Math.ceil((double) (((float) selectedDialogs.size()) / 2.0f));
            if (rows == 1 || count <= 1) {
                id = NUM;
            } else if (rows == 2 || count <= 2) {
                id = NUM;
            } else if (rows == 3 || count <= 3) {
                id = NUM;
            } else {
                id = NUM;
            }
        } else {
            id = NUM;
        }
        RemoteViews rv = new RemoteViews(context.getPackageName(), id);
        rv.setRemoteAdapter(i, NUM, intent2);
        rv.setEmptyView(NUM, NUM);
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.addFlags(67108864);
        intent.addCategory("android.intent.category.LAUNCHER");
        rv.setPendingIntentTemplate(NUM, PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM));
        appWidgetManager2.updateAppWidget(i, rv);
        appWidgetManager2.notifyAppWidgetViewDataChanged(i, NUM);
    }
}

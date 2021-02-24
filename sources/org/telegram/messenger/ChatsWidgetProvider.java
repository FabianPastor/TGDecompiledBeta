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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.LaunchActivity;

public class ChatsWidgetProvider extends AppWidgetProvider {
    private static int getCellsForSize(int i) {
        int i2 = 2;
        while (i2 * 72 < i) {
            i2++;
        }
        return i2 - 1;
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        super.onUpdate(context, appWidgetManager, iArr);
        for (int updateWidget : iArr) {
            updateWidget(context, appWidgetManager, updateWidget, false);
        }
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
        updateWidget(context, appWidgetManager, i, true);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, i, bundle);
    }

    public void onDeleted(Context context, int[] iArr) {
        super.onDeleted(context, iArr);
        ApplicationLoader.postInitApplication();
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (int i = 0; i < iArr.length; i++) {
            int i2 = sharedPreferences.getInt("account" + iArr[i], -1);
            if (i2 >= 0) {
                AccountInstance.getInstance(i2).getMessagesStorage().clearWidgetDialogs(iArr[i]);
            }
            edit.remove("account" + iArr[i]);
            edit.remove("type" + iArr[i]);
            edit.remove("deleted" + iArr[i]);
        }
        edit.commit();
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int i, boolean z) {
        Context context2 = context;
        AppWidgetManager appWidgetManager2 = appWidgetManager;
        int i2 = i;
        ApplicationLoader.postInitApplication();
        int cellsForSize = getCellsForSize(appWidgetManager.getAppWidgetOptions(i).getInt("appWidgetMaxHeight"));
        Intent intent = new Intent(context2, ChatsWidgetService.class);
        intent.putExtra("appWidgetId", i2);
        intent.setData(Uri.parse(intent.toUri(1)));
        SharedPreferences sharedPreferences = context2.getSharedPreferences("shortcut_widget", 0);
        int i3 = NUM;
        if (!sharedPreferences.getBoolean("deleted" + i2, false)) {
            int i4 = sharedPreferences.getInt("account" + i2, -1);
            ArrayList arrayList = new ArrayList();
            if (i4 >= 0) {
                AccountInstance.getInstance(i4).getMessagesStorage().getWidgetDialogIds(i, 0, arrayList, (ArrayList<TLRPC$User>) null, (ArrayList<TLRPC$Chat>) null, false);
            }
            if (cellsForSize != 1 && arrayList.size() > 1) {
                i3 = (cellsForSize == 2 || arrayList.size() <= 2) ? NUM : (cellsForSize == 3 || arrayList.size() <= 3) ? NUM : NUM;
            }
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), i3);
        remoteViews.setRemoteAdapter(i2, NUM, intent);
        remoteViews.setEmptyView(NUM, NUM);
        Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent2.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent2.addFlags(67108864);
        intent2.addCategory("android.intent.category.LAUNCHER");
        remoteViews.setPendingIntentTemplate(NUM, PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, NUM));
        appWidgetManager2.updateAppWidget(i2, remoteViews);
        if (z) {
            appWidgetManager2.notifyAppWidgetViewDataChanged(i2, NUM);
        }
    }
}

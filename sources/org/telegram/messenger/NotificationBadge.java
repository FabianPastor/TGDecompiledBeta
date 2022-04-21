package org.telegram.messenger;

import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NotificationBadge {
    private static final List<Class<? extends Badger>> BADGERS;
    private static Badger badger;
    /* access modifiers changed from: private */
    public static ComponentName componentName;
    private static boolean initied;

    public interface Badger {
        void executeBadge(int i);

        List<String> getSupportLaunchers();
    }

    static {
        LinkedList linkedList = new LinkedList();
        BADGERS = linkedList;
        linkedList.add(AdwHomeBadger.class);
        linkedList.add(ApexHomeBadger.class);
        linkedList.add(NewHtcHomeBadger.class);
        linkedList.add(NovaHomeBadger.class);
        linkedList.add(SonyHomeBadger.class);
        linkedList.add(XiaomiHomeBadger.class);
        linkedList.add(AsusHomeBadger.class);
        linkedList.add(HuaweiHomeBadger.class);
        linkedList.add(OPPOHomeBader.class);
        linkedList.add(SamsungHomeBadger.class);
        linkedList.add(ZukHomeBadger.class);
        linkedList.add(VivoHomeBadger.class);
    }

    public static class AdwHomeBadger implements Badger {
        public static final String CLASSNAME = "CNAME";
        public static final String COUNT = "COUNT";
        public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
        public static final String PACKAGENAME = "PNAME";

        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("org.adw.launcher.counter.SEND");
            intent.putExtra("PNAME", NotificationBadge.componentName.getPackageName());
            intent.putExtra("CNAME", NotificationBadge.componentName.getClassName());
            intent.putExtra("COUNT", badgeCount);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new NotificationBadge$AdwHomeBadger$$ExternalSyntheticLambda0(intent));
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"org.adw.launcher", "org.adwfreak.launcher"});
        }
    }

    public static class ApexHomeBadger implements Badger {
        private static final String CLASS = "class";
        private static final String COUNT = "count";
        private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
        private static final String PACKAGENAME = "package";

        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("com.anddoes.launcher.COUNTER_CHANGED");
            intent.putExtra("package", NotificationBadge.componentName.getPackageName());
            intent.putExtra("count", badgeCount);
            intent.putExtra("class", NotificationBadge.componentName.getClassName());
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new NotificationBadge$ApexHomeBadger$$ExternalSyntheticLambda0(intent));
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.anddoes.launcher"});
        }
    }

    public static class AsusHomeBadger implements Badger {
        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", badgeCount);
            intent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
            intent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
            intent.putExtra("badge_vip_count", 0);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new NotificationBadge$AsusHomeBadger$$ExternalSyntheticLambda0(intent));
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.asus.launcher"});
        }
    }

    public static class DefaultBadger implements Badger {
        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", badgeCount);
            intent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
            intent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
            AndroidUtilities.runOnUIThread(new NotificationBadge$DefaultBadger$$ExternalSyntheticLambda0(intent));
        }

        static /* synthetic */ void lambda$executeBadge$0(Intent intent) {
            try {
                ApplicationLoader.applicationContext.sendBroadcast(intent);
            } catch (Exception e) {
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"fr.neamar.kiss", "com.quaap.launchtime", "com.quaap.launchtime_official"});
        }
    }

    public static class HuaweiHomeBadger implements Badger {
        public void executeBadge(int badgeCount) {
            Bundle localBundle = new Bundle();
            localBundle.putString("package", ApplicationLoader.applicationContext.getPackageName());
            localBundle.putString("class", NotificationBadge.componentName.getClassName());
            localBundle.putInt("badgenumber", badgeCount);
            AndroidUtilities.runOnUIThread(new NotificationBadge$HuaweiHomeBadger$$ExternalSyntheticLambda0(localBundle));
        }

        static /* synthetic */ void lambda$executeBadge$0(Bundle localBundle) {
            try {
                ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", (String) null, localBundle);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.huawei.android.launcher"});
        }
    }

    public static class NewHtcHomeBadger implements Badger {
        public static final String COUNT = "count";
        public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
        public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";
        public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
        public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
        public static final String PACKAGENAME = "packagename";

        public void executeBadge(int badgeCount) {
            Intent intent1 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            intent1.putExtra("com.htc.launcher.extra.COMPONENT", NotificationBadge.componentName.flattenToShortString());
            intent1.putExtra("com.htc.launcher.extra.COUNT", badgeCount);
            Intent intent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intent.putExtra("packagename", NotificationBadge.componentName.getPackageName());
            intent.putExtra("count", badgeCount);
            if (NotificationBadge.canResolveBroadcast(intent1) || NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new NotificationBadge$NewHtcHomeBadger$$ExternalSyntheticLambda0(intent1, intent));
            }
        }

        static /* synthetic */ void lambda$executeBadge$0(Intent intent1, Intent intent) {
            ApplicationLoader.applicationContext.sendBroadcast(intent1);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.htc.launcher"});
        }
    }

    public static class NovaHomeBadger implements Badger {
        private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
        private static final String COUNT = "count";
        private static final String TAG = "tag";

        public void executeBadge(int badgeCount) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
            contentValues.put("count", Integer.valueOf(badgeCount));
            ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.teslacoilsw.launcher"});
        }
    }

    public static class OPPOHomeBader implements Badger {
        private static final String INTENT_ACTION = "com.oppo.unsettledevent";
        private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
        private static final String INTENT_EXTRA_BADGE_COUNT = "number";
        private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
        private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
        private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
        private int mCurrentTotalCount = -1;

        public void executeBadge(int badgeCount) {
            if (this.mCurrentTotalCount != badgeCount) {
                this.mCurrentTotalCount = badgeCount;
                executeBadgeByContentProvider(badgeCount);
            }
        }

        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.oppo.launcher");
        }

        private void executeBadgeByContentProvider(int badgeCount) {
            try {
                Bundle extras = new Bundle();
                extras.putInt("app_badge_count", badgeCount);
                ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", (String) null, extras);
            } catch (Throwable th) {
            }
        }
    }

    public static class SamsungHomeBadger implements Badger {
        private static final String[] CONTENT_PROJECTION = {"_id", "class"};
        private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
        private static DefaultBadger defaultBadger;

        public void executeBadge(int badgeCount) {
            try {
                if (defaultBadger == null) {
                    defaultBadger = new DefaultBadger();
                }
                defaultBadger.executeBadge(badgeCount);
            } catch (Exception e) {
            }
            Uri mUri = Uri.parse("content://com.sec.badge/apps?notify=true");
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new String[]{NotificationBadge.componentName.getPackageName()}, (String) null);
                if (cursor != null) {
                    String entryActivityName = NotificationBadge.componentName.getClassName();
                    boolean entryActivityExist = false;
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        contentResolver.update(mUri, getContentValues(NotificationBadge.componentName, badgeCount, false), "_id=?", new String[]{String.valueOf(id)});
                        if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            entryActivityExist = true;
                        }
                    }
                    if (!entryActivityExist) {
                        contentResolver.insert(mUri, getContentValues(NotificationBadge.componentName, badgeCount, true));
                    }
                }
            } finally {
                NotificationBadge.close(cursor);
            }
        }

        private ContentValues getContentValues(ComponentName componentName, int badgeCount, boolean isInsert) {
            ContentValues contentValues = new ContentValues();
            if (isInsert) {
                contentValues.put("package", componentName.getPackageName());
                contentValues.put("class", componentName.getClassName());
            }
            contentValues.put("badgecount", Integer.valueOf(badgeCount));
            return contentValues;
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.sec.android.app.launcher", "com.sec.android.app.twlauncher"});
        }
    }

    public static class SonyHomeBadger implements Badger {
        private static final String INTENT_ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
        private static final String INTENT_EXTRA_MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
        private static final String INTENT_EXTRA_PACKAGE_NAME = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
        private static final String INTENT_EXTRA_SHOW_MESSAGE = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";
        private static final String PROVIDER_COLUMNS_ACTIVITY_NAME = "activity_name";
        private static final String PROVIDER_COLUMNS_BADGE_COUNT = "badge_count";
        private static final String PROVIDER_COLUMNS_PACKAGE_NAME = "package_name";
        private static final String PROVIDER_CONTENT_URI = "content://com.sonymobile.home.resourceprovider/badge";
        private static final String SONY_HOME_PROVIDER_NAME = "com.sonymobile.home.resourceprovider";
        private static AsyncQueryHandler mQueryHandler;
        private final Uri BADGE_CONTENT_URI = Uri.parse("content://com.sonymobile.home.resourceprovider/badge");

        public void executeBadge(int badgeCount) {
            if (sonyBadgeContentProviderExists()) {
                executeBadgeByContentProvider(badgeCount);
            } else {
                executeBadgeByBroadcast(badgeCount);
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.sonyericsson.home", "com.sonymobile.home"});
        }

        private static void executeBadgeByBroadcast(int badgeCount) {
            Intent intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", NotificationBadge.componentName.getPackageName());
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", NotificationBadge.componentName.getClassName());
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(badgeCount));
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", badgeCount > 0);
            AndroidUtilities.runOnUIThread(new NotificationBadge$SonyHomeBadger$$ExternalSyntheticLambda0(intent));
        }

        private void executeBadgeByContentProvider(int badgeCount) {
            if (badgeCount >= 0) {
                if (mQueryHandler == null) {
                    mQueryHandler = new AsyncQueryHandler(ApplicationLoader.applicationContext.getApplicationContext().getContentResolver()) {
                        public void handleMessage(Message msg) {
                            try {
                                super.handleMessage(msg);
                            } catch (Throwable th) {
                            }
                        }
                    };
                }
                insertBadgeAsync(badgeCount, NotificationBadge.componentName.getPackageName(), NotificationBadge.componentName.getClassName());
            }
        }

        private void insertBadgeAsync(int badgeCount, String packageName, String activityName) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("badge_count", Integer.valueOf(badgeCount));
            contentValues.put("package_name", packageName);
            contentValues.put("activity_name", activityName);
            mQueryHandler.startInsert(0, (Object) null, this.BADGE_CONTENT_URI, contentValues);
        }

        private static boolean sonyBadgeContentProviderExists() {
            if (ApplicationLoader.applicationContext.getPackageManager().resolveContentProvider("com.sonymobile.home.resourceprovider", 0) != null) {
                return true;
            }
            return false;
        }
    }

    public static class XiaomiHomeBadger implements Badger {
        public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
        public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
        public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";

        public void executeBadge(int badgeCount) {
            Object obj = "";
            try {
                Object miuiNotification = Class.forName("android.app.MiuiNotification").newInstance();
                Field field = miuiNotification.getClass().getDeclaredField("messageCount");
                field.setAccessible(true);
                field.set(miuiNotification, String.valueOf(badgeCount == 0 ? obj : Integer.valueOf(badgeCount)));
            } catch (Throwable th) {
                final Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
                localIntent.putExtra("android.intent.extra.update_application_component_name", NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
                if (badgeCount != 0) {
                    obj = Integer.valueOf(badgeCount);
                }
                localIntent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(obj));
                if (NotificationBadge.canResolveBroadcast(localIntent)) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ApplicationLoader.applicationContext.sendBroadcast(localIntent);
                        }
                    });
                }
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2"});
        }
    }

    public static class ZukHomeBadger implements Badger {
        private final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");

        public void executeBadge(int badgeCount) {
            Bundle extra = new Bundle();
            extra.putInt("app_badge_count", badgeCount);
            AndroidUtilities.runOnUIThread(new NotificationBadge$ZukHomeBadger$$ExternalSyntheticLambda0(this, extra));
        }

        /* renamed from: lambda$executeBadge$0$org-telegram-messenger-NotificationBadge$ZukHomeBadger  reason: not valid java name */
        public /* synthetic */ void m1035x7f3var_c2(Bundle extra) {
            try {
                ApplicationLoader.applicationContext.getContentResolver().call(this.CONTENT_URI, "setAppBadgeCount", (String) null, extra);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.zui.launcher");
        }
    }

    public static class VivoHomeBadger implements Badger {
        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", ApplicationLoader.applicationContext.getPackageName());
            intent.putExtra("className", NotificationBadge.componentName.getClassName());
            intent.putExtra("notificationNum", badgeCount);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.vivo.launcher"});
        }
    }

    public static boolean applyCount(int badgeCount) {
        try {
            if (badger == null && !initied) {
                initBadger();
                initied = true;
            }
            Badger badger2 = badger;
            if (badger2 == null) {
                return false;
            }
            badger2.executeBadge(badgeCount);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    private static boolean initBadger() {
        Context context = ApplicationLoader.applicationContext;
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            return false;
        }
        componentName = launchIntent.getComponent();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 65536);
        if (resolveInfo != null) {
            String currentHomePackage = resolveInfo.activityInfo.packageName;
            Iterator<Class<? extends Badger>> it = BADGERS.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Badger shortcutBadger = null;
                try {
                    shortcutBadger = (Badger) it.next().newInstance();
                } catch (Exception e) {
                }
                if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                    badger = shortcutBadger;
                    break;
                }
            }
            if (badger != null) {
                return true;
            }
        }
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 65536);
        if (resolveInfos != null) {
            for (int a = 0; a < resolveInfos.size(); a++) {
                String currentHomePackage2 = resolveInfos.get(a).activityInfo.packageName;
                Iterator<Class<? extends Badger>> it2 = BADGERS.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    Badger shortcutBadger2 = null;
                    try {
                        shortcutBadger2 = (Badger) it2.next().newInstance();
                    } catch (Exception e2) {
                    }
                    if (shortcutBadger2 != null && shortcutBadger2.getSupportLaunchers().contains(currentHomePackage2)) {
                        badger = shortcutBadger2;
                        break;
                    }
                }
                if (badger != null) {
                    break;
                }
            }
        }
        if (badger == null) {
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                badger = new XiaomiHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("ZUK")) {
                badger = new ZukHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                badger = new OPPOHomeBader();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("VIVO")) {
                badger = new VivoHomeBadger();
            } else {
                badger = new DefaultBadger();
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean canResolveBroadcast(Intent intent) {
        List<ResolveInfo> receivers = ApplicationLoader.applicationContext.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (receivers == null || receivers.size() <= 0) {
            return false;
        }
        return true;
    }

    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable th) {
            }
        }
    }
}

package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
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
import java.util.LinkedList;
import java.util.List;

public class NotificationBadge {
    private static final List<Class<? extends Badger>> BADGERS = new LinkedList();
    private static Badger badger;
    private static ComponentName componentName;
    private static boolean initied;

    public interface Badger {
        void executeBadge(int i);

        List<String> getSupportLaunchers();
    }

    public static class AdwHomeBadger implements Badger {
        public static final String CLASSNAME = "CNAME";
        public static final String COUNT = "COUNT";
        public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
        public static final String PACKAGENAME = "PNAME";

        public void executeBadge(int i) {
            final Intent intent = new Intent("org.adw.launcher.counter.SEND");
            intent.putExtra("PNAME", NotificationBadge.componentName.getPackageName());
            intent.putExtra("CNAME", NotificationBadge.componentName.getClassName());
            intent.putExtra("COUNT", i);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
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

        public void executeBadge(int i) {
            final Intent intent = new Intent("com.anddoes.launcher.COUNTER_CHANGED");
            intent.putExtra("package", NotificationBadge.componentName.getPackageName());
            intent.putExtra("count", i);
            intent.putExtra("class", NotificationBadge.componentName.getClassName());
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
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

        public void executeBadge(int i) {
            final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", i);
            intent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
            intent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
            intent.putExtra("badge_vip_count", 0);
            if (NotificationBadge.canResolveBroadcast(intent)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    }
                });
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

        public void executeBadge(int i) {
            final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", i);
            intent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
            intent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                    } catch (Exception unused) {
                    }
                }
            });
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"fr.neamar.kiss", "com.quaap.launchtime", "com.quaap.launchtime_official"});
        }
    }

    public static class HuaweiHomeBadger implements Badger {
        public void executeBadge(int i) {
            final Bundle bundle = new Bundle();
            bundle.putString("package", ApplicationLoader.applicationContext.getPackageName());
            bundle.putString("class", NotificationBadge.componentName.getClassName());
            bundle.putInt("badgenumber", i);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
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

        public void executeBadge(int i) {
            final Intent intent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            intent.putExtra("com.htc.launcher.extra.COMPONENT", NotificationBadge.componentName.flattenToShortString());
            intent.putExtra("com.htc.launcher.extra.COUNT", i);
            final Intent intent2 = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            intent2.putExtra("packagename", NotificationBadge.componentName.getPackageName());
            intent2.putExtra("count", i);
            if (NotificationBadge.canResolveBroadcast(intent) || NotificationBadge.canResolveBroadcast(intent2)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ApplicationLoader.applicationContext.sendBroadcast(intent);
                        ApplicationLoader.applicationContext.sendBroadcast(intent2);
                    }
                });
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.htc.launcher"});
        }
    }

    public static class NovaHomeBadger implements Badger {
        private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
        private static final String COUNT = "count";
        private static final String TAG = "tag";

        public void executeBadge(int i) {
            ContentValues contentValues = new ContentValues();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(NotificationBadge.componentName.getPackageName());
            stringBuilder.append("/");
            stringBuilder.append(NotificationBadge.componentName.getClassName());
            contentValues.put("tag", stringBuilder.toString());
            contentValues.put("count", Integer.valueOf(i));
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

        public void executeBadge(int i) {
            if (this.mCurrentTotalCount != i) {
                this.mCurrentTotalCount = i;
                executeBadgeByContentProvider(i);
            }
        }

        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.oppo.launcher");
        }

        @TargetApi(11)
        private void executeBadgeByContentProvider(int i) {
            try {
                Bundle bundle = new Bundle();
                bundle.putInt("app_badge_count", i);
                ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, bundle);
            } catch (Throwable unused) {
            }
        }
    }

    public static class SamsungHomeBadger implements Badger {
        private static final String[] CONTENT_PROJECTION = new String[]{"_id", "class"};
        private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
        private static DefaultBadger defaultBadger;

        /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0088 in {3, 4, 6, 15, 17, 19, 22} preds:[]
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
            	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
            	at java.util.ArrayList.forEach(ArrayList.java:1257)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
            	at java.util.ArrayList.forEach(ArrayList.java:1257)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
            	at jadx.core.ProcessClass.process(ProcessClass.java:32)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
            */
        public void executeBadge(int r12) {
            /*
            r11 = this;
            r0 = defaultBadger;	 Catch:{ Exception -> 0x0010 }
            if (r0 != 0) goto L_0x000b;	 Catch:{ Exception -> 0x0010 }
            r0 = new org.telegram.messenger.NotificationBadge$DefaultBadger;	 Catch:{ Exception -> 0x0010 }
            r0.<init>();	 Catch:{ Exception -> 0x0010 }
            defaultBadger = r0;	 Catch:{ Exception -> 0x0010 }
            r0 = defaultBadger;	 Catch:{ Exception -> 0x0010 }
            r0.executeBadge(r12);	 Catch:{ Exception -> 0x0010 }
            r0 = "content://com.sec.badge/apps?notify=true";
            r0 = android.net.Uri.parse(r0);
            r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r7 = r1.getContentResolver();
            r8 = 0;
            r3 = CONTENT_PROJECTION;	 Catch:{ all -> 0x0083 }
            r4 = "package=?";	 Catch:{ all -> 0x0083 }
            r9 = 1;	 Catch:{ all -> 0x0083 }
            r5 = new java.lang.String[r9];	 Catch:{ all -> 0x0083 }
            r1 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0083 }
            r1 = r1.getPackageName();	 Catch:{ all -> 0x0083 }
            r10 = 0;	 Catch:{ all -> 0x0083 }
            r5[r10] = r1;	 Catch:{ all -> 0x0083 }
            r6 = 0;	 Catch:{ all -> 0x0083 }
            r1 = r7;	 Catch:{ all -> 0x0083 }
            r2 = r0;	 Catch:{ all -> 0x0083 }
            r8 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0083 }
            if (r8 == 0) goto L_0x007f;	 Catch:{ all -> 0x0083 }
            r1 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0083 }
            r1 = r1.getClassName();	 Catch:{ all -> 0x0083 }
            r2 = 0;	 Catch:{ all -> 0x0083 }
            r3 = r8.moveToNext();	 Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0072;	 Catch:{ all -> 0x0083 }
            r3 = r8.getInt(r10);	 Catch:{ all -> 0x0083 }
            r4 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0083 }
            r4 = r11.getContentValues(r4, r12, r10);	 Catch:{ all -> 0x0083 }
            r5 = "_id=?";	 Catch:{ all -> 0x0083 }
            r6 = new java.lang.String[r9];	 Catch:{ all -> 0x0083 }
            r3 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x0083 }
            r6[r10] = r3;	 Catch:{ all -> 0x0083 }
            r7.update(r0, r4, r5, r6);	 Catch:{ all -> 0x0083 }
            r3 = "class";	 Catch:{ all -> 0x0083 }
            r3 = r8.getColumnIndex(r3);	 Catch:{ all -> 0x0083 }
            r3 = r8.getString(r3);	 Catch:{ all -> 0x0083 }
            r3 = r1.equals(r3);	 Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0041;	 Catch:{ all -> 0x0083 }
            r2 = 1;	 Catch:{ all -> 0x0083 }
            goto L_0x0041;	 Catch:{ all -> 0x0083 }
            if (r2 != 0) goto L_0x007f;	 Catch:{ all -> 0x0083 }
            r1 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0083 }
            r12 = r11.getContentValues(r1, r12, r9);	 Catch:{ all -> 0x0083 }
            r7.insert(r0, r12);	 Catch:{ all -> 0x0083 }
            org.telegram.messenger.NotificationBadge.close(r8);
            return;
            r12 = move-exception;
            org.telegram.messenger.NotificationBadge.close(r8);
            throw r12;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge$SamsungHomeBadger.executeBadge(int):void");
        }

        private ContentValues getContentValues(ComponentName componentName, int i, boolean z) {
            ContentValues contentValues = new ContentValues();
            if (z) {
                contentValues.put("package", componentName.getPackageName());
                contentValues.put("class", componentName.getClassName());
            }
            contentValues.put("badgecount", Integer.valueOf(i));
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

        public void executeBadge(int i) {
            if (sonyBadgeContentProviderExists()) {
                executeBadgeByContentProvider(i);
            } else {
                executeBadgeByBroadcast(i);
            }
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.sonyericsson.home", "com.sonymobile.home"});
        }

        private static void executeBadgeByBroadcast(int i) {
            final Intent intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", NotificationBadge.componentName.getPackageName());
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", NotificationBadge.componentName.getClassName());
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(i));
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", i > 0);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                }
            });
        }

        private void executeBadgeByContentProvider(int i) {
            if (i >= 0) {
                if (mQueryHandler == null) {
                    mQueryHandler = new AsyncQueryHandler(ApplicationLoader.applicationContext.getApplicationContext().getContentResolver()) {
                        public void handleMessage(Message message) {
                            try {
                                super.handleMessage(message);
                            } catch (Throwable unused) {
                            }
                        }
                    };
                }
                insertBadgeAsync(i, NotificationBadge.componentName.getPackageName(), NotificationBadge.componentName.getClassName());
            }
        }

        private void insertBadgeAsync(int i, String str, String str2) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("badge_count", Integer.valueOf(i));
            contentValues.put("package_name", str);
            contentValues.put("activity_name", str2);
            mQueryHandler.startInsert(0, null, this.BADGE_CONTENT_URI, contentValues);
        }

        private static boolean sonyBadgeContentProviderExists() {
            return ApplicationLoader.applicationContext.getPackageManager().resolveContentProvider("com.sonymobile.home.resourceprovider", 0) != null;
        }
    }

    public static class VivoHomeBadger implements Badger {
        public void executeBadge(int i) {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", ApplicationLoader.applicationContext.getPackageName());
            intent.putExtra("className", NotificationBadge.componentName.getClassName());
            intent.putExtra("notificationNum", i);
            ApplicationLoader.applicationContext.sendBroadcast(intent);
        }

        public List<String> getSupportLaunchers() {
            return Arrays.asList(new String[]{"com.vivo.launcher"});
        }
    }

    public static class XiaomiHomeBadger implements Badger {
        public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
        public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
        public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";

        public void executeBadge(int i) {
            Object obj = "";
            try {
                Object obj2;
                Object newInstance = Class.forName("android.app.MiuiNotification").newInstance();
                Field declaredField = newInstance.getClass().getDeclaredField("messageCount");
                declaredField.setAccessible(true);
                if (i == 0) {
                    obj2 = obj;
                } else {
                    obj2 = Integer.valueOf(i);
                }
                declaredField.set(newInstance, String.valueOf(obj2));
            } catch (Throwable unused) {
                final Intent intent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(NotificationBadge.componentName.getPackageName());
                stringBuilder.append("/");
                stringBuilder.append(NotificationBadge.componentName.getClassName());
                intent.putExtra("android.intent.extra.update_application_component_name", stringBuilder.toString());
                if (i != 0) {
                    obj = Integer.valueOf(i);
                }
                intent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(obj));
                if (NotificationBadge.canResolveBroadcast(intent)) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ApplicationLoader.applicationContext.sendBroadcast(intent);
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

        @TargetApi(11)
        public void executeBadge(int i) {
            final Bundle bundle = new Bundle();
            bundle.putInt("app_badge_count", i);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        ApplicationLoader.applicationContext.getContentResolver().call(ZukHomeBadger.this.CONTENT_URI, "setAppBadgeCount", null, bundle);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
        }

        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.zui.launcher");
        }
    }

    static {
        BADGERS.add(AdwHomeBadger.class);
        BADGERS.add(ApexHomeBadger.class);
        BADGERS.add(NewHtcHomeBadger.class);
        BADGERS.add(NovaHomeBadger.class);
        BADGERS.add(SonyHomeBadger.class);
        BADGERS.add(XiaomiHomeBadger.class);
        BADGERS.add(AsusHomeBadger.class);
        BADGERS.add(HuaweiHomeBadger.class);
        BADGERS.add(OPPOHomeBader.class);
        BADGERS.add(SamsungHomeBadger.class);
        BADGERS.add(ZukHomeBadger.class);
        BADGERS.add(VivoHomeBadger.class);
    }

    public static boolean applyCount(int i) {
        try {
            if (badger == null && !initied) {
                initBadger();
                initied = true;
            }
            if (badger == null) {
                return false;
            }
            badger.executeBadge(i);
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    private static boolean initBadger() {
        Context context = ApplicationLoader.applicationContext;
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        int i = 0;
        if (launchIntentForPackage == null) {
            return false;
        }
        componentName = launchIntentForPackage.getComponent();
        launchIntentForPackage = new Intent("android.intent.action.MAIN");
        launchIntentForPackage.addCategory("android.intent.category.HOME");
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(launchIntentForPackage, 65536);
        if (resolveActivity != null) {
            String str = resolveActivity.activityInfo.packageName;
            for (Class newInstance : BADGERS) {
                Badger badger;
                try {
                    badger = (Badger) newInstance.newInstance();
                } catch (Exception unused) {
                    badger = null;
                }
                if (badger != null && badger.getSupportLaunchers().contains(str)) {
                    badger = badger;
                    break;
                }
            }
            if (badger != null) {
                return true;
            }
        }
        List queryIntentActivities = context.getPackageManager().queryIntentActivities(launchIntentForPackage, 65536);
        if (queryIntentActivities != null) {
            while (i < queryIntentActivities.size()) {
                String str2 = ((ResolveInfo) queryIntentActivities.get(i)).activityInfo.packageName;
                for (Class newInstance2 : BADGERS) {
                    Badger badger2;
                    try {
                        badger2 = (Badger) newInstance2.newInstance();
                    } catch (Exception unused2) {
                        badger2 = null;
                    }
                    if (badger2 != null && badger2.getSupportLaunchers().contains(str2)) {
                        badger = badger2;
                        break;
                    }
                }
                if (badger != null) {
                    break;
                }
                i++;
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

    private static boolean canResolveBroadcast(Intent intent) {
        List queryBroadcastReceivers = ApplicationLoader.applicationContext.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (queryBroadcastReceivers == null || queryBroadcastReceivers.size() <= 0) {
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
            } catch (Throwable unused) {
            }
        }
    }
}

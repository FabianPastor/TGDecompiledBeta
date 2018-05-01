package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(CLASSNAME, NotificationBadge.componentName.getClassName());
            intent.putExtra(COUNT, i);
            if (NotificationBadge.canResolveBroadcast(intent) != 0) {
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
            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra("count", i);
            intent.putExtra(CLASS, NotificationBadge.componentName.getClassName());
            if (NotificationBadge.canResolveBroadcast(intent) != 0) {
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
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, i);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            intent.putExtra("badge_vip_count", 0);
            if (NotificationBadge.canResolveBroadcast(intent) != 0) {
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
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, i);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                    /*
                    r2 = this;
                    r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0007 }
                    r1 = r0;	 Catch:{ Exception -> 0x0007 }
                    r0.sendBroadcast(r1);	 Catch:{ Exception -> 0x0007 }
                L_0x0007:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.DefaultBadger.1.run():void");
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
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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
            final Intent intent = new Intent(INTENT_SET_NOTIFICATION);
            intent.putExtra(EXTRA_COMPONENT, NotificationBadge.componentName.flattenToShortString());
            intent.putExtra(EXTRA_COUNT, i);
            final Intent intent2 = new Intent(INTENT_UPDATE_SHORTCUT);
            intent2.putExtra(PACKAGENAME, NotificationBadge.componentName.getPackageName());
            intent2.putExtra(COUNT, i);
            if (NotificationBadge.canResolveBroadcast(intent) != 0 || NotificationBadge.canResolveBroadcast(intent2) != 0) {
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
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(NotificationBadge.componentName.getPackageName());
            stringBuilder.append("/");
            stringBuilder.append(NotificationBadge.componentName.getClassName());
            contentValues.put(str, stringBuilder.toString());
            contentValues.put("count", Integer.valueOf(i));
            ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);
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

        @android.annotation.TargetApi(11)
        private void executeBadgeByContentProvider(int r5) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r4 = this;
            r0 = new android.os.Bundle;	 Catch:{ Throwable -> 0x001c }
            r0.<init>();	 Catch:{ Throwable -> 0x001c }
            r1 = "app_badge_count";	 Catch:{ Throwable -> 0x001c }
            r0.putInt(r1, r5);	 Catch:{ Throwable -> 0x001c }
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x001c }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x001c }
            r1 = "content://com.android.badge/badge";	 Catch:{ Throwable -> 0x001c }
            r1 = android.net.Uri.parse(r1);	 Catch:{ Throwable -> 0x001c }
            r2 = "setAppBadgeCount";	 Catch:{ Throwable -> 0x001c }
            r3 = 0;	 Catch:{ Throwable -> 0x001c }
            r5.call(r1, r2, r3, r0);	 Catch:{ Throwable -> 0x001c }
        L_0x001c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.OPPOHomeBader.executeBadgeByContentProvider(int):void");
        }
    }

    public static class SamsungHomeBadger implements Badger {
        private static final String[] CONTENT_PROJECTION = new String[]{"_id", "class"};
        private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
        private static DefaultBadger defaultBadger;

        public void executeBadge(int r12) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r11 = this;
            r0 = defaultBadger;	 Catch:{ Exception -> 0x0010 }
            if (r0 != 0) goto L_0x000b;	 Catch:{ Exception -> 0x0010 }
        L_0x0004:
            r0 = new org.telegram.messenger.NotificationBadge$DefaultBadger;	 Catch:{ Exception -> 0x0010 }
            r0.<init>();	 Catch:{ Exception -> 0x0010 }
            defaultBadger = r0;	 Catch:{ Exception -> 0x0010 }
        L_0x000b:
            r0 = defaultBadger;	 Catch:{ Exception -> 0x0010 }
            r0.executeBadge(r12);	 Catch:{ Exception -> 0x0010 }
        L_0x0010:
            r0 = "content://com.sec.badge/apps?notify=true";
            r0 = android.net.Uri.parse(r0);
            r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
            r7 = r1.getContentResolver();
            r8 = 0;
            r3 = CONTENT_PROJECTION;	 Catch:{ all -> 0x0086 }
            r4 = "package=?";	 Catch:{ all -> 0x0086 }
            r9 = 1;	 Catch:{ all -> 0x0086 }
            r5 = new java.lang.String[r9];	 Catch:{ all -> 0x0086 }
            r1 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0086 }
            r1 = r1.getPackageName();	 Catch:{ all -> 0x0086 }
            r10 = 0;	 Catch:{ all -> 0x0086 }
            r5[r10] = r1;	 Catch:{ all -> 0x0086 }
            r6 = 0;	 Catch:{ all -> 0x0086 }
            r1 = r7;	 Catch:{ all -> 0x0086 }
            r2 = r0;	 Catch:{ all -> 0x0086 }
            r1 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0086 }
            if (r1 == 0) goto L_0x0082;
        L_0x0038:
            r2 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0080 }
            r2 = r2.getClassName();	 Catch:{ all -> 0x0080 }
            r3 = r10;	 Catch:{ all -> 0x0080 }
        L_0x0041:
            r4 = r1.moveToNext();	 Catch:{ all -> 0x0080 }
            if (r4 == 0) goto L_0x0072;	 Catch:{ all -> 0x0080 }
        L_0x0047:
            r4 = r1.getInt(r10);	 Catch:{ all -> 0x0080 }
            r5 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0080 }
            r5 = r11.getContentValues(r5, r12, r10);	 Catch:{ all -> 0x0080 }
            r6 = "_id=?";	 Catch:{ all -> 0x0080 }
            r8 = new java.lang.String[r9];	 Catch:{ all -> 0x0080 }
            r4 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0080 }
            r8[r10] = r4;	 Catch:{ all -> 0x0080 }
            r7.update(r0, r5, r6, r8);	 Catch:{ all -> 0x0080 }
            r4 = "class";	 Catch:{ all -> 0x0080 }
            r4 = r1.getColumnIndex(r4);	 Catch:{ all -> 0x0080 }
            r4 = r1.getString(r4);	 Catch:{ all -> 0x0080 }
            r4 = r2.equals(r4);	 Catch:{ all -> 0x0080 }
            if (r4 == 0) goto L_0x0041;	 Catch:{ all -> 0x0080 }
        L_0x0070:
            r3 = r9;	 Catch:{ all -> 0x0080 }
            goto L_0x0041;	 Catch:{ all -> 0x0080 }
        L_0x0072:
            if (r3 != 0) goto L_0x0082;	 Catch:{ all -> 0x0080 }
        L_0x0074:
            r2 = org.telegram.messenger.NotificationBadge.componentName;	 Catch:{ all -> 0x0080 }
            r12 = r11.getContentValues(r2, r12, r9);	 Catch:{ all -> 0x0080 }
            r7.insert(r0, r12);	 Catch:{ all -> 0x0080 }
            goto L_0x0082;
        L_0x0080:
            r12 = move-exception;
            goto L_0x0088;
        L_0x0082:
            org.telegram.messenger.NotificationBadge.close(r1);
            return;
        L_0x0086:
            r12 = move-exception;
            r1 = r8;
        L_0x0088:
            org.telegram.messenger.NotificationBadge.close(r1);
            throw r12;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.SamsungHomeBadger.executeBadge(int):void");
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
        private final Uri BADGE_CONTENT_URI = Uri.parse(PROVIDER_CONTENT_URI);

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
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, NotificationBadge.componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, NotificationBadge.componentName.getClassName());
            intent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(i));
            intent.putExtra(INTENT_EXTRA_SHOW_MESSAGE, i > 0 ? 1 : 0);
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
                        public void handleMessage(android.os.Message r1) {
                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                            /*
                            r0 = this;
                            super.handleMessage(r1);	 Catch:{ Throwable -> 0x0003 }
                        L_0x0003:
                            return;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.SonyHomeBadger.2.handleMessage(android.os.Message):void");
                        }
                    };
                }
                insertBadgeAsync(i, NotificationBadge.componentName.getPackageName(), NotificationBadge.componentName.getClassName());
            }
        }

        private void insertBadgeAsync(int i, String str, String str2) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PROVIDER_COLUMNS_BADGE_COUNT, Integer.valueOf(i));
            contentValues.put(PROVIDER_COLUMNS_PACKAGE_NAME, str);
            contentValues.put(PROVIDER_COLUMNS_ACTIVITY_NAME, str2);
            mQueryHandler.startInsert(null, null, this.BADGE_CONTENT_URI, contentValues);
        }

        private static boolean sonyBadgeContentProviderExists() {
            return ApplicationLoader.applicationContext.getPackageManager().resolveContentProvider(SONY_HOME_PROVIDER_NAME, 0) != null;
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

        public void executeBadge(int r5) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r4 = this;
            r0 = "android.app.MiuiNotification";	 Catch:{ Throwable -> 0x0029 }
            r0 = java.lang.Class.forName(r0);	 Catch:{ Throwable -> 0x0029 }
            r0 = r0.newInstance();	 Catch:{ Throwable -> 0x0029 }
            r1 = r0.getClass();	 Catch:{ Throwable -> 0x0029 }
            r2 = "messageCount";	 Catch:{ Throwable -> 0x0029 }
            r1 = r1.getDeclaredField(r2);	 Catch:{ Throwable -> 0x0029 }
            r2 = 1;	 Catch:{ Throwable -> 0x0029 }
            r1.setAccessible(r2);	 Catch:{ Throwable -> 0x0029 }
            if (r5 != 0) goto L_0x001d;	 Catch:{ Throwable -> 0x0029 }
        L_0x001a:
            r2 = "";	 Catch:{ Throwable -> 0x0029 }
            goto L_0x0021;	 Catch:{ Throwable -> 0x0029 }
        L_0x001d:
            r2 = java.lang.Integer.valueOf(r5);	 Catch:{ Throwable -> 0x0029 }
        L_0x0021:
            r2 = java.lang.String.valueOf(r2);	 Catch:{ Throwable -> 0x0029 }
            r1.set(r0, r2);	 Catch:{ Throwable -> 0x0029 }
            goto L_0x0079;
        L_0x0029:
            r0 = new android.content.Intent;
            r1 = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
            r0.<init>(r1);
            r1 = "android.intent.extra.update_application_component_name";
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = org.telegram.messenger.NotificationBadge.componentName;
            r3 = r3.getPackageName();
            r2.append(r3);
            r3 = "/";
            r2.append(r3);
            r3 = org.telegram.messenger.NotificationBadge.componentName;
            r3 = r3.getClassName();
            r2.append(r3);
            r2 = r2.toString();
            r0.putExtra(r1, r2);
            r1 = "android.intent.extra.update_application_message_text";
            if (r5 != 0) goto L_0x0060;
        L_0x005d:
            r5 = "";
            goto L_0x0064;
        L_0x0060:
            r5 = java.lang.Integer.valueOf(r5);
        L_0x0064:
            r5 = java.lang.String.valueOf(r5);
            r0.putExtra(r1, r5);
            r5 = org.telegram.messenger.NotificationBadge.canResolveBroadcast(r0);
            if (r5 == 0) goto L_0x0079;
        L_0x0071:
            r5 = new org.telegram.messenger.NotificationBadge$XiaomiHomeBadger$1;
            r5.<init>(r0);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
        L_0x0079:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.XiaomiHomeBadger.executeBadge(int):void");
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
                    } catch (Throwable e) {
                        FileLog.m3e(e);
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

    public static boolean applyCount(int r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 0;
        r1 = badger;	 Catch:{ Throwable -> 0x001a }
        r2 = 1;	 Catch:{ Throwable -> 0x001a }
        if (r1 != 0) goto L_0x000f;	 Catch:{ Throwable -> 0x001a }
    L_0x0006:
        r1 = initied;	 Catch:{ Throwable -> 0x001a }
        if (r1 != 0) goto L_0x000f;	 Catch:{ Throwable -> 0x001a }
    L_0x000a:
        initBadger();	 Catch:{ Throwable -> 0x001a }
        initied = r2;	 Catch:{ Throwable -> 0x001a }
    L_0x000f:
        r1 = badger;	 Catch:{ Throwable -> 0x001a }
        if (r1 != 0) goto L_0x0014;	 Catch:{ Throwable -> 0x001a }
    L_0x0013:
        return r0;	 Catch:{ Throwable -> 0x001a }
    L_0x0014:
        r1 = badger;	 Catch:{ Throwable -> 0x001a }
        r1.executeBadge(r3);	 Catch:{ Throwable -> 0x001a }
        return r2;
    L_0x001a:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.applyCount(int):boolean");
    }

    private static boolean initBadger() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r1 = r0.getPackageManager();
        r2 = r0.getPackageName();
        r1 = r1.getLaunchIntentForPackage(r2);
        r2 = 0;
        if (r1 != 0) goto L_0x0012;
    L_0x0011:
        return r2;
    L_0x0012:
        r1 = r1.getComponent();
        componentName = r1;
        r1 = new android.content.Intent;
        r3 = "android.intent.action.MAIN";
        r1.<init>(r3);
        r3 = "android.intent.category.HOME";
        r1.addCategory(r3);
        r3 = r0.getPackageManager();
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r3 = r3.resolveActivity(r1, r4);
        r5 = 0;
        r6 = 1;
        if (r3 == 0) goto L_0x0063;
    L_0x0032:
        r3 = r3.activityInfo;
        r3 = r3.packageName;
        r7 = BADGERS;
        r7 = r7.iterator();
    L_0x003c:
        r8 = r7.hasNext();
        if (r8 == 0) goto L_0x005e;
    L_0x0042:
        r8 = r7.next();
        r8 = (java.lang.Class) r8;
        r8 = r8.newInstance();	 Catch:{ Exception -> 0x004f }
        r8 = (org.telegram.messenger.NotificationBadge.Badger) r8;	 Catch:{ Exception -> 0x004f }
        goto L_0x0050;
    L_0x004f:
        r8 = r5;
    L_0x0050:
        if (r8 == 0) goto L_0x003c;
    L_0x0052:
        r9 = r8.getSupportLaunchers();
        r9 = r9.contains(r3);
        if (r9 == 0) goto L_0x003c;
    L_0x005c:
        badger = r8;
    L_0x005e:
        r3 = badger;
        if (r3 == 0) goto L_0x0063;
    L_0x0062:
        return r6;
    L_0x0063:
        r0 = r0.getPackageManager();
        r0 = r0.queryIntentActivities(r1, r4);
        if (r0 == 0) goto L_0x00ad;
    L_0x006d:
        r1 = r0.size();
        if (r2 >= r1) goto L_0x00ad;
    L_0x0073:
        r1 = r0.get(r2);
        r1 = (android.content.pm.ResolveInfo) r1;
        r1 = r1.activityInfo;
        r1 = r1.packageName;
        r3 = BADGERS;
        r3 = r3.iterator();
    L_0x0083:
        r4 = r3.hasNext();
        if (r4 == 0) goto L_0x00a5;
    L_0x0089:
        r4 = r3.next();
        r4 = (java.lang.Class) r4;
        r4 = r4.newInstance();	 Catch:{ Exception -> 0x0096 }
        r4 = (org.telegram.messenger.NotificationBadge.Badger) r4;	 Catch:{ Exception -> 0x0096 }
        goto L_0x0097;
    L_0x0096:
        r4 = r5;
    L_0x0097:
        if (r4 == 0) goto L_0x0083;
    L_0x0099:
        r7 = r4.getSupportLaunchers();
        r7 = r7.contains(r1);
        if (r7 == 0) goto L_0x0083;
    L_0x00a3:
        badger = r4;
    L_0x00a5:
        r1 = badger;
        if (r1 == 0) goto L_0x00aa;
    L_0x00a9:
        goto L_0x00ad;
    L_0x00aa:
        r2 = r2 + 1;
        goto L_0x006d;
    L_0x00ad:
        r0 = badger;
        if (r0 != 0) goto L_0x0100;
    L_0x00b1:
        r0 = android.os.Build.MANUFACTURER;
        r1 = "Xiaomi";
        r0 = r0.equalsIgnoreCase(r1);
        if (r0 == 0) goto L_0x00c3;
    L_0x00bb:
        r0 = new org.telegram.messenger.NotificationBadge$XiaomiHomeBadger;
        r0.<init>();
        badger = r0;
        goto L_0x0100;
    L_0x00c3:
        r0 = android.os.Build.MANUFACTURER;
        r1 = "ZUK";
        r0 = r0.equalsIgnoreCase(r1);
        if (r0 == 0) goto L_0x00d5;
    L_0x00cd:
        r0 = new org.telegram.messenger.NotificationBadge$ZukHomeBadger;
        r0.<init>();
        badger = r0;
        goto L_0x0100;
    L_0x00d5:
        r0 = android.os.Build.MANUFACTURER;
        r1 = "OPPO";
        r0 = r0.equalsIgnoreCase(r1);
        if (r0 == 0) goto L_0x00e7;
    L_0x00df:
        r0 = new org.telegram.messenger.NotificationBadge$OPPOHomeBader;
        r0.<init>();
        badger = r0;
        goto L_0x0100;
    L_0x00e7:
        r0 = android.os.Build.MANUFACTURER;
        r1 = "VIVO";
        r0 = r0.equalsIgnoreCase(r1);
        if (r0 == 0) goto L_0x00f9;
    L_0x00f1:
        r0 = new org.telegram.messenger.NotificationBadge$VivoHomeBadger;
        r0.<init>();
        badger = r0;
        goto L_0x0100;
    L_0x00f9:
        r0 = new org.telegram.messenger.NotificationBadge$DefaultBadger;
        r0.<init>();
        badger = r0;
    L_0x0100:
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.initBadger():boolean");
    }

    private static boolean canResolveBroadcast(Intent intent) {
        intent = ApplicationLoader.applicationContext.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (intent == null || intent.size() <= null) {
            return false;
        }
        return true;
    }

    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static void closeQuietly(java.io.Closeable r0) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r0 == 0) goto L_0x0005;
    L_0x0002:
        r0.close();	 Catch:{ Throwable -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationBadge.closeQuietly(java.io.Closeable):void");
    }
}

package org.telegram.messenger.browser;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    /* renamed from: org.telegram.messenger.browser.Browser$1 */
    static class C18311 implements ServiceConnectionCallback {
        C18311() {
        }

        public void onServiceConnected(CustomTabsClient customTabsClient) {
            Browser.customTabsClient = customTabsClient;
            if (SharedConfig.customTabs != null && Browser.customTabsClient != null) {
                try {
                    Browser.customTabsClient.warmup(0);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        public void onServiceDisconnected() {
            Browser.customTabsClient = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        public void onNavigationEvent(int i, Bundle bundle) {
        }

        private NavigationCallback() {
        }
    }

    private static CustomTabsSession getCurrentSession() {
        return customTabsCurrentSession == null ? null : (CustomTabsSession) customTabsCurrentSession.get();
    }

    private static void setCurrentSession(CustomTabsSession customTabsSession) {
        customTabsCurrentSession = new WeakReference(customTabsSession);
    }

    private static CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        Activity activity2 = currentCustomTabsActivity == null ? null : (Activity) currentCustomTabsActivity.get();
        if (!(activity2 == null || activity2 == activity)) {
            unbindCustomTabsService(activity2);
        }
        if (customTabsClient == null) {
            currentCustomTabsActivity = new WeakReference(activity);
            try {
                if (TextUtils.isEmpty(customTabsPackageToBind)) {
                    customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(activity);
                    if (customTabsPackageToBind == null) {
                        return;
                    }
                }
                customTabsServiceConnection = new ServiceConnection(new C18311());
                if (CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection) == null) {
                    customTabsServiceConnection = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static void unbindCustomTabsService(android.app.Activity r2) {
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
        r0 = customTabsServiceConnection;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = currentCustomTabsActivity;
        r1 = 0;
        if (r0 != 0) goto L_0x000c;
    L_0x000a:
        r0 = r1;
        goto L_0x0014;
    L_0x000c:
        r0 = currentCustomTabsActivity;
        r0 = r0.get();
        r0 = (android.app.Activity) r0;
    L_0x0014:
        if (r0 != r2) goto L_0x001b;
    L_0x0016:
        r0 = currentCustomTabsActivity;
        r0.clear();
    L_0x001b:
        r0 = customTabsServiceConnection;	 Catch:{ Exception -> 0x0020 }
        r2.unbindService(r0);	 Catch:{ Exception -> 0x0020 }
    L_0x0020:
        customTabsClient = r1;
        customTabsSession = r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.unbindCustomTabsService(android.app.Activity):void");
    }

    public static void openUrl(Context context, String str) {
        if (str != null) {
            openUrl(context, Uri.parse(str), true);
        }
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String str, boolean z) {
        if (context != null) {
            if (str != null) {
                openUrl(context, Uri.parse(str), z);
            }
        }
    }

    public static void openUrl(Context context, Uri uri, boolean z) {
        openUrl(context, uri, z, true);
    }

    public static void openUrl(android.content.Context r17, android.net.Uri r18, boolean r19, boolean r20) {
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
        r7 = r17;
        r8 = r18;
        if (r7 == 0) goto L_0x0240;
    L_0x0006:
        if (r8 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x0240;
    L_0x000a:
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        r9 = 1;
        r10 = new boolean[r9];
        r11 = 0;
        r10[r11] = r11;
        r12 = isInternalUri(r8, r10);
        if (r20 == 0) goto L_0x006d;
    L_0x0018:
        r1 = r18.getHost();	 Catch:{ Exception -> 0x006d }
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x006d }
        r2 = "telegra.ph";	 Catch:{ Exception -> 0x006d }
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x006d }
        if (r1 != 0) goto L_0x0038;	 Catch:{ Exception -> 0x006d }
    L_0x0028:
        r1 = r18.toString();	 Catch:{ Exception -> 0x006d }
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x006d }
        r2 = "telegram.org/faq";	 Catch:{ Exception -> 0x006d }
        r1 = r1.contains(r2);	 Catch:{ Exception -> 0x006d }
        if (r1 == 0) goto L_0x006d;	 Catch:{ Exception -> 0x006d }
    L_0x0038:
        r13 = new org.telegram.ui.ActionBar.AlertDialog[r9];	 Catch:{ Exception -> 0x006d }
        r1 = new org.telegram.ui.ActionBar.AlertDialog;	 Catch:{ Exception -> 0x006d }
        r1.<init>(r7, r9);	 Catch:{ Exception -> 0x006d }
        r13[r11] = r1;	 Catch:{ Exception -> 0x006d }
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview;	 Catch:{ Exception -> 0x006d }
        r14.<init>();	 Catch:{ Exception -> 0x006d }
        r1 = r18.toString();	 Catch:{ Exception -> 0x006d }
        r14.message = r1;	 Catch:{ Exception -> 0x006d }
        r1 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Exception -> 0x006d }
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x006d }
        r6 = new org.telegram.messenger.browser.Browser$2;	 Catch:{ Exception -> 0x006d }
        r1 = r6;	 Catch:{ Exception -> 0x006d }
        r2 = r13;	 Catch:{ Exception -> 0x006d }
        r4 = r8;	 Catch:{ Exception -> 0x006d }
        r5 = r7;	 Catch:{ Exception -> 0x006d }
        r9 = r6;	 Catch:{ Exception -> 0x006d }
        r6 = r19;	 Catch:{ Exception -> 0x006d }
        r1.<init>(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x006d }
        r1 = r15.sendRequest(r14, r9);	 Catch:{ Exception -> 0x006d }
        r2 = new org.telegram.messenger.browser.Browser$3;	 Catch:{ Exception -> 0x006d }
        r2.<init>(r13, r1);	 Catch:{ Exception -> 0x006d }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x006d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r3);	 Catch:{ Exception -> 0x006d }
        return;
    L_0x006d:
        r1 = r18.getScheme();	 Catch:{ Exception -> 0x0207 }
        if (r1 == 0) goto L_0x007c;	 Catch:{ Exception -> 0x0207 }
    L_0x0073:
        r1 = r18.getScheme();	 Catch:{ Exception -> 0x0207 }
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x0207 }
        goto L_0x007e;	 Catch:{ Exception -> 0x0207 }
    L_0x007c:
        r1 = "";	 Catch:{ Exception -> 0x0207 }
    L_0x007e:
        r2 = r1;	 Catch:{ Exception -> 0x0207 }
        if (r19 == 0) goto L_0x020c;	 Catch:{ Exception -> 0x0207 }
    L_0x0081:
        r1 = org.telegram.messenger.SharedConfig.customTabs;	 Catch:{ Exception -> 0x0207 }
        if (r1 == 0) goto L_0x020c;	 Catch:{ Exception -> 0x0207 }
    L_0x0085:
        if (r12 != 0) goto L_0x020c;	 Catch:{ Exception -> 0x0207 }
    L_0x0087:
        r1 = "tel";	 Catch:{ Exception -> 0x0207 }
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0207 }
        if (r1 != 0) goto L_0x020c;
    L_0x008f:
        r1 = 0;
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x00e3 }
        r3 = "android.intent.action.VIEW";	 Catch:{ Exception -> 0x00e3 }
        r4 = "http://www.google.com";	 Catch:{ Exception -> 0x00e3 }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x00e3 }
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x00e3 }
        r3 = r17.getPackageManager();	 Catch:{ Exception -> 0x00e3 }
        r2 = r3.queryIntentActivities(r2, r11);	 Catch:{ Exception -> 0x00e3 }
        if (r2 == 0) goto L_0x00e3;	 Catch:{ Exception -> 0x00e3 }
    L_0x00a7:
        r3 = r2.isEmpty();	 Catch:{ Exception -> 0x00e3 }
        if (r3 != 0) goto L_0x00e3;	 Catch:{ Exception -> 0x00e3 }
    L_0x00ad:
        r3 = r2.size();	 Catch:{ Exception -> 0x00e3 }
        r3 = new java.lang.String[r3];	 Catch:{ Exception -> 0x00e3 }
        r4 = r11;
    L_0x00b4:
        r5 = r2.size();	 Catch:{ Exception -> 0x00e4 }
        if (r4 >= r5) goto L_0x00e4;	 Catch:{ Exception -> 0x00e4 }
    L_0x00ba:
        r5 = r2.get(r4);	 Catch:{ Exception -> 0x00e4 }
        r5 = (android.content.pm.ResolveInfo) r5;	 Catch:{ Exception -> 0x00e4 }
        r5 = r5.activityInfo;	 Catch:{ Exception -> 0x00e4 }
        r5 = r5.packageName;	 Catch:{ Exception -> 0x00e4 }
        r3[r4] = r5;	 Catch:{ Exception -> 0x00e4 }
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00e4 }
        if (r5 == 0) goto L_0x00e0;	 Catch:{ Exception -> 0x00e4 }
    L_0x00ca:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e4 }
        r5.<init>();	 Catch:{ Exception -> 0x00e4 }
        r6 = "default browser name = ";	 Catch:{ Exception -> 0x00e4 }
        r5.append(r6);	 Catch:{ Exception -> 0x00e4 }
        r6 = r3[r4];	 Catch:{ Exception -> 0x00e4 }
        r5.append(r6);	 Catch:{ Exception -> 0x00e4 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x00e4 }
        org.telegram.messenger.FileLog.m0d(r5);	 Catch:{ Exception -> 0x00e4 }
    L_0x00e0:
        r4 = r4 + 1;
        goto L_0x00b4;
    L_0x00e3:
        r3 = r1;
    L_0x00e4:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0191 }
        r4 = "android.intent.action.VIEW";	 Catch:{ Exception -> 0x0191 }
        r2.<init>(r4, r8);	 Catch:{ Exception -> 0x0191 }
        r4 = r17.getPackageManager();	 Catch:{ Exception -> 0x0191 }
        r2 = r4.queryIntentActivities(r2, r11);	 Catch:{ Exception -> 0x0191 }
        if (r3 == 0) goto L_0x011e;
    L_0x00f5:
        r1 = r11;
    L_0x00f6:
        r4 = r2.size();	 Catch:{ Exception -> 0x0192 }
        if (r1 >= r4) goto L_0x0159;	 Catch:{ Exception -> 0x0192 }
    L_0x00fc:
        r4 = r11;	 Catch:{ Exception -> 0x0192 }
    L_0x00fd:
        r5 = r3.length;	 Catch:{ Exception -> 0x0192 }
        if (r4 >= r5) goto L_0x011b;	 Catch:{ Exception -> 0x0192 }
    L_0x0100:
        r5 = r3[r4];	 Catch:{ Exception -> 0x0192 }
        r6 = r2.get(r1);	 Catch:{ Exception -> 0x0192 }
        r6 = (android.content.pm.ResolveInfo) r6;	 Catch:{ Exception -> 0x0192 }
        r6 = r6.activityInfo;	 Catch:{ Exception -> 0x0192 }
        r6 = r6.packageName;	 Catch:{ Exception -> 0x0192 }
        r5 = r5.equals(r6);	 Catch:{ Exception -> 0x0192 }
        if (r5 == 0) goto L_0x0118;	 Catch:{ Exception -> 0x0192 }
    L_0x0112:
        r2.remove(r1);	 Catch:{ Exception -> 0x0192 }
        r1 = r1 + -1;	 Catch:{ Exception -> 0x0192 }
        goto L_0x011b;	 Catch:{ Exception -> 0x0192 }
    L_0x0118:
        r4 = r4 + 1;	 Catch:{ Exception -> 0x0192 }
        goto L_0x00fd;	 Catch:{ Exception -> 0x0192 }
    L_0x011b:
        r4 = 1;	 Catch:{ Exception -> 0x0192 }
        r1 = r1 + r4;	 Catch:{ Exception -> 0x0192 }
        goto L_0x00f6;	 Catch:{ Exception -> 0x0192 }
    L_0x011e:
        r1 = r11;	 Catch:{ Exception -> 0x0192 }
    L_0x011f:
        r3 = r2.size();	 Catch:{ Exception -> 0x0192 }
        if (r1 >= r3) goto L_0x0159;	 Catch:{ Exception -> 0x0192 }
    L_0x0125:
        r3 = r2.get(r1);	 Catch:{ Exception -> 0x0192 }
        r3 = (android.content.pm.ResolveInfo) r3;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.activityInfo;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.packageName;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.toLowerCase();	 Catch:{ Exception -> 0x0192 }
        r4 = "browser";	 Catch:{ Exception -> 0x0192 }
        r3 = r3.contains(r4);	 Catch:{ Exception -> 0x0192 }
        if (r3 != 0) goto L_0x0151;	 Catch:{ Exception -> 0x0192 }
    L_0x013b:
        r3 = r2.get(r1);	 Catch:{ Exception -> 0x0192 }
        r3 = (android.content.pm.ResolveInfo) r3;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.activityInfo;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.packageName;	 Catch:{ Exception -> 0x0192 }
        r3 = r3.toLowerCase();	 Catch:{ Exception -> 0x0192 }
        r4 = "chrome";	 Catch:{ Exception -> 0x0192 }
        r3 = r3.contains(r4);	 Catch:{ Exception -> 0x0192 }
        if (r3 == 0) goto L_0x0156;	 Catch:{ Exception -> 0x0192 }
    L_0x0151:
        r2.remove(r1);	 Catch:{ Exception -> 0x0192 }
        r1 = r1 + -1;	 Catch:{ Exception -> 0x0192 }
    L_0x0156:
        r3 = 1;	 Catch:{ Exception -> 0x0192 }
        r1 = r1 + r3;	 Catch:{ Exception -> 0x0192 }
        goto L_0x011f;	 Catch:{ Exception -> 0x0192 }
    L_0x0159:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0192 }
        if (r1 == 0) goto L_0x0192;	 Catch:{ Exception -> 0x0192 }
    L_0x015d:
        r1 = r11;	 Catch:{ Exception -> 0x0192 }
    L_0x015e:
        r3 = r2.size();	 Catch:{ Exception -> 0x0192 }
        if (r1 >= r3) goto L_0x0192;	 Catch:{ Exception -> 0x0192 }
    L_0x0164:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0192 }
        r3.<init>();	 Catch:{ Exception -> 0x0192 }
        r4 = "device has ";	 Catch:{ Exception -> 0x0192 }
        r3.append(r4);	 Catch:{ Exception -> 0x0192 }
        r4 = r2.get(r1);	 Catch:{ Exception -> 0x0192 }
        r4 = (android.content.pm.ResolveInfo) r4;	 Catch:{ Exception -> 0x0192 }
        r4 = r4.activityInfo;	 Catch:{ Exception -> 0x0192 }
        r4 = r4.packageName;	 Catch:{ Exception -> 0x0192 }
        r3.append(r4);	 Catch:{ Exception -> 0x0192 }
        r4 = " to open ";	 Catch:{ Exception -> 0x0192 }
        r3.append(r4);	 Catch:{ Exception -> 0x0192 }
        r4 = r18.toString();	 Catch:{ Exception -> 0x0192 }
        r3.append(r4);	 Catch:{ Exception -> 0x0192 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0192 }
        org.telegram.messenger.FileLog.m0d(r3);	 Catch:{ Exception -> 0x0192 }
        r1 = r1 + 1;
        goto L_0x015e;
    L_0x0191:
        r2 = r1;
    L_0x0192:
        r1 = r10[r11];	 Catch:{ Exception -> 0x0207 }
        if (r1 != 0) goto L_0x019e;	 Catch:{ Exception -> 0x0207 }
    L_0x0196:
        if (r2 == 0) goto L_0x019e;	 Catch:{ Exception -> 0x0207 }
    L_0x0198:
        r1 = r2.isEmpty();	 Catch:{ Exception -> 0x0207 }
        if (r1 == 0) goto L_0x020c;	 Catch:{ Exception -> 0x0207 }
    L_0x019e:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0207 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0207 }
        r3 = org.telegram.messenger.ShareBroadcastReceiver.class;	 Catch:{ Exception -> 0x0207 }
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0207 }
        r2 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x0207 }
        r1.setAction(r2);	 Catch:{ Exception -> 0x0207 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0207 }
        r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0207 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0207 }
        r5 = org.telegram.messenger.CustomTabsCopyReceiver.class;	 Catch:{ Exception -> 0x0207 }
        r3.<init>(r4, r5);	 Catch:{ Exception -> 0x0207 }
        r4 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0207 }
        r2 = android.app.PendingIntent.getBroadcast(r2, r11, r3, r4);	 Catch:{ Exception -> 0x0207 }
        r3 = new org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder;	 Catch:{ Exception -> 0x0207 }
        r4 = getSession();	 Catch:{ Exception -> 0x0207 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x0207 }
        r4 = "CopyLink";	 Catch:{ Exception -> 0x0207 }
        r5 = NUM; // 0x7f0c01b8 float:1.8610084E38 double:1.053097616E-314;	 Catch:{ Exception -> 0x0207 }
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Exception -> 0x0207 }
        r3.addMenuItem(r4, r2);	 Catch:{ Exception -> 0x0207 }
        r2 = "actionBarDefault";	 Catch:{ Exception -> 0x0207 }
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);	 Catch:{ Exception -> 0x0207 }
        r3.setToolbarColor(r2);	 Catch:{ Exception -> 0x0207 }
        r2 = 1;	 Catch:{ Exception -> 0x0207 }
        r3.setShowTitle(r2);	 Catch:{ Exception -> 0x0207 }
        r2 = r17.getResources();	 Catch:{ Exception -> 0x0207 }
        r4 = NUM; // 0x7f070001 float:1.794458E38 double:1.0529355035E-314;	 Catch:{ Exception -> 0x0207 }
        r2 = android.graphics.BitmapFactory.decodeResource(r2, r4);	 Catch:{ Exception -> 0x0207 }
        r4 = "ShareFile";	 Catch:{ Exception -> 0x0207 }
        r5 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;	 Catch:{ Exception -> 0x0207 }
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Exception -> 0x0207 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0207 }
        r1 = android.app.PendingIntent.getBroadcast(r5, r11, r1, r11);	 Catch:{ Exception -> 0x0207 }
        r3.setActionButton(r2, r4, r1, r11);	 Catch:{ Exception -> 0x0207 }
        r1 = r3.build();	 Catch:{ Exception -> 0x0207 }
        r1.setUseNewTask();	 Catch:{ Exception -> 0x0207 }
        r1.launchUrl(r7, r8);	 Catch:{ Exception -> 0x0207 }
        return;
    L_0x0207:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x020c:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x023a }
        r2 = "android.intent.action.VIEW";	 Catch:{ Exception -> 0x023a }
        r1.<init>(r2, r8);	 Catch:{ Exception -> 0x023a }
        if (r12 == 0) goto L_0x0227;	 Catch:{ Exception -> 0x023a }
    L_0x0215:
        r2 = new android.content.ComponentName;	 Catch:{ Exception -> 0x023a }
        r3 = r17.getPackageName();	 Catch:{ Exception -> 0x023a }
        r4 = org.telegram.ui.LaunchActivity.class;	 Catch:{ Exception -> 0x023a }
        r4 = r4.getName();	 Catch:{ Exception -> 0x023a }
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x023a }
        r1.setComponent(r2);	 Catch:{ Exception -> 0x023a }
    L_0x0227:
        r2 = "create_new_tab";	 Catch:{ Exception -> 0x023a }
        r3 = 1;	 Catch:{ Exception -> 0x023a }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x023a }
        r2 = "com.android.browser.application_id";	 Catch:{ Exception -> 0x023a }
        r3 = r17.getPackageName();	 Catch:{ Exception -> 0x023a }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x023a }
        r7.startActivity(r1);	 Catch:{ Exception -> 0x023a }
        goto L_0x023f;
    L_0x023a:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x023f:
        return;
    L_0x0240:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    public static boolean isInternalUrl(String str, boolean[] zArr) {
        return isInternalUri(Uri.parse(str), zArr);
    }

    public static boolean isInternalUri(Uri uri, boolean[] zArr) {
        String host = uri.getHost();
        Object toLowerCase = host != null ? host.toLowerCase() : TtmlNode.ANONYMOUS_REGION_ID;
        if ("tg".equals(uri.getScheme())) {
            return true;
        }
        if ("telegram.dog".equals(toLowerCase)) {
            uri = uri.getPath();
            if (uri != null && uri.length() > 1) {
                uri = uri.substring(1).toLowerCase();
                if (!(uri.startsWith("blog") || uri.equals("iv") || uri.startsWith("faq"))) {
                    if (uri.equals("apps") == null) {
                        return true;
                    }
                }
                if (zArr != null) {
                    zArr[0] = true;
                }
                return false;
            }
        } else if ("telegram.me".equals(toLowerCase) || "t.me".equals(toLowerCase) || "telesco.pe".equals(toLowerCase)) {
            uri = uri.getPath();
            if (uri != null && uri.length() > 1) {
                if (uri.substring(1).toLowerCase().equals("iv") == null) {
                    return true;
                }
                if (zArr != null) {
                    zArr[0] = true;
                }
                return false;
            }
        }
        return false;
    }
}

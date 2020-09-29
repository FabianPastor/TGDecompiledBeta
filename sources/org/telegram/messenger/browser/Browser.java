package org.telegram.messenger.browser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    /* access modifiers changed from: private */
    public static CustomTabsClient customTabsClient;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    private static void setCurrentSession(CustomTabsSession customTabsSession2) {
        new WeakReference(customTabsSession2);
    }

    private static CustomTabsSession getSession() {
        CustomTabsClient customTabsClient2 = customTabsClient;
        if (customTabsClient2 == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            CustomTabsSession newSession = customTabsClient2.newSession(new NavigationCallback());
            customTabsSession = newSession;
            setCurrentSession(newSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        WeakReference<Activity> weakReference = currentCustomTabsActivity;
        Activity activity2 = weakReference == null ? null : (Activity) weakReference.get();
        if (!(activity2 == null || activity2 == activity)) {
            unbindCustomTabsService(activity2);
        }
        if (customTabsClient == null) {
            currentCustomTabsActivity = new WeakReference<>(activity);
            try {
                if (TextUtils.isEmpty(customTabsPackageToBind)) {
                    String packageNameToUse = CustomTabsHelper.getPackageNameToUse(activity);
                    customTabsPackageToBind = packageNameToUse;
                    if (packageNameToUse == null) {
                        return;
                    }
                }
                ServiceConnection serviceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                    public void onServiceConnected(CustomTabsClient customTabsClient) {
                        CustomTabsClient unused = Browser.customTabsClient = customTabsClient;
                        if (SharedConfig.customTabs && Browser.customTabsClient != null) {
                            try {
                                Browser.customTabsClient.warmup(0);
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }

                    public void onServiceDisconnected() {
                        CustomTabsClient unused = Browser.customTabsClient = null;
                    }
                });
                customTabsServiceConnection = serviceConnection;
                if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, serviceConnection)) {
                    customTabsServiceConnection = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (customTabsServiceConnection != null) {
            WeakReference<Activity> weakReference = currentCustomTabsActivity;
            if ((weakReference == null ? null : (Activity) weakReference.get()) == activity) {
                currentCustomTabsActivity.clear();
            }
            try {
                activity.unbindService(customTabsServiceConnection);
            } catch (Exception unused) {
            }
            customTabsClient = null;
            customTabsSession = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        public void onNavigationEvent(int i, Bundle bundle) {
        }

        private NavigationCallback() {
        }
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
        if (context != null && str != null) {
            openUrl(context, Uri.parse(str), z);
        }
    }

    public static void openUrl(Context context, Uri uri, boolean z) {
        openUrl(context, uri, z, true);
    }

    public static void openUrl(Context context, String str, boolean z, boolean z2) {
        openUrl(context, Uri.parse(str), z, z2);
    }

    public static boolean isTelegraphUrl(String str, boolean z) {
        if (z) {
            return str.equals("telegra.ph") || str.equals("te.legra.ph") || str.equals("graph.org");
        }
        if (str.contains("telegra.ph") || str.contains("te.legra.ph") || str.contains("graph.org")) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(13:3|(3:5|6|(2:10|11))|12|13|(1:15)(1:16)|17|(3:21|22|23)|(14:34|35|36|(3:40|41|(11:42|43|(3:45|(2:47|98)(1:99)|48)|51|52|(3:54|(4:57|(2:58|(1:101)(2:60|(3:103|62|102)(1:63)))|64|55)|100)(3:65|(4:68|(2:72|105)(1:106)|73|66)|104)|74|(2:76|(3:79|80|77))|81|82|(2:87|88)))|49|51|52|(0)(0)|74|(0)|81|82|87|88)|91|92|(1:94)|95|110) */
    /* JADX WARNING: Can't wrap try/catch for region: R(14:34|35|36|(3:40|41|(11:42|43|(3:45|(2:47|98)(1:99)|48)|51|52|(3:54|(4:57|(2:58|(1:101)(2:60|(3:103|62|102)(1:63)))|64|55)|100)(3:65|(4:68|(2:72|105)(1:106)|73|66)|104)|74|(2:76|(3:79|80|77))|81|82|(2:87|88)))|49|51|52|(0)(0)|74|(0)|81|82|87|88) */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0221, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0222, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0070 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x0100 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:81:0x01ab */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0076 A[Catch:{ Exception -> 0x0221 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x007f A[Catch:{ Exception -> 0x0221 }] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d6 A[Catch:{ Exception -> 0x0100 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x010f A[Catch:{ Exception -> 0x01ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0138 A[Catch:{ Exception -> 0x01ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0177 A[Catch:{ Exception -> 0x01ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x022c A[Catch:{ Exception -> 0x0251 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void openUrl(android.content.Context r16, android.net.Uri r17, boolean r18, boolean r19) {
        /*
            r7 = r16
            r8 = r17
            java.lang.String r9 = "android.intent.action.VIEW"
            if (r7 == 0) goto L_0x0255
            if (r8 != 0) goto L_0x000c
            goto L_0x0255
        L_0x000c:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            r10 = 1
            boolean[] r11 = new boolean[r10]
            r12 = 0
            r11[r12] = r12
            boolean r13 = isInternalUri(r8, r11)
            if (r19 == 0) goto L_0x0070
            java.lang.String r0 = r17.getHost()     // Catch:{ Exception -> 0x0070 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0070 }
            boolean r0 = isTelegraphUrl(r0, r10)     // Catch:{ Exception -> 0x0070 }
            if (r0 != 0) goto L_0x0038
            java.lang.String r0 = r17.toString()     // Catch:{ Exception -> 0x0070 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0070 }
            java.lang.String r1 = "telegram.org/faq"
            boolean r0 = r0.contains(r1)     // Catch:{ Exception -> 0x0070 }
            if (r0 == 0) goto L_0x0070
        L_0x0038:
            org.telegram.ui.ActionBar.AlertDialog[] r0 = new org.telegram.ui.ActionBar.AlertDialog[r10]     // Catch:{ Exception -> 0x0070 }
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog     // Catch:{ Exception -> 0x0070 }
            r2 = 3
            r1.<init>(r7, r2)     // Catch:{ Exception -> 0x0070 }
            r0[r12] = r1     // Catch:{ Exception -> 0x0070 }
            org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview     // Catch:{ Exception -> 0x0070 }
            r14.<init>()     // Catch:{ Exception -> 0x0070 }
            java.lang.String r1 = r17.toString()     // Catch:{ Exception -> 0x0070 }
            r14.message = r1     // Catch:{ Exception -> 0x0070 }
            int r1 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x0070 }
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x0070 }
            org.telegram.messenger.browser.-$$Lambda$Browser$UGNHp8wusapZ_BRGXGaD_Ayz4-I r6 = new org.telegram.messenger.browser.-$$Lambda$Browser$UGNHp8wusapZ_BRGXGaD_Ayz4-I     // Catch:{ Exception -> 0x0070 }
            r1 = r6
            r2 = r0
            r4 = r17
            r5 = r16
            r10 = r6
            r6 = r18
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0070 }
            int r1 = r15.sendRequest(r14, r10)     // Catch:{ Exception -> 0x0070 }
            org.telegram.messenger.browser.-$$Lambda$Browser$tzkgx-C1l2oH_szh01yqocI6uLg r2 = new org.telegram.messenger.browser.-$$Lambda$Browser$tzkgx-C1l2oH_szh01yqocI6uLg     // Catch:{ Exception -> 0x0070 }
            r2.<init>(r0, r1)     // Catch:{ Exception -> 0x0070 }
            r0 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r0)     // Catch:{ Exception -> 0x0070 }
            return
        L_0x0070:
            java.lang.String r0 = r17.getScheme()     // Catch:{ Exception -> 0x0221 }
            if (r0 == 0) goto L_0x007f
            java.lang.String r0 = r17.getScheme()     // Catch:{ Exception -> 0x0221 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0221 }
            goto L_0x0081
        L_0x007f:
            java.lang.String r0 = ""
        L_0x0081:
            r1 = r0
            java.lang.String r0 = "http"
            boolean r0 = r0.equals(r1)     // Catch:{ Exception -> 0x0221 }
            if (r0 != 0) goto L_0x0092
            java.lang.String r0 = "https"
            boolean r0 = r0.equals(r1)     // Catch:{ Exception -> 0x0221 }
            if (r0 == 0) goto L_0x009d
        L_0x0092:
            android.net.Uri r0 = r17.normalizeScheme()     // Catch:{ Exception -> 0x0098 }
            r8 = r0
            goto L_0x009d
        L_0x0098:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0221 }
        L_0x009d:
            if (r18 == 0) goto L_0x0225
            boolean r0 = org.telegram.messenger.SharedConfig.customTabs     // Catch:{ Exception -> 0x0221 }
            if (r0 == 0) goto L_0x0225
            if (r13 != 0) goto L_0x0225
            java.lang.String r0 = "tel"
            boolean r0 = r1.equals(r0)     // Catch:{ Exception -> 0x0221 }
            if (r0 != 0) goto L_0x0225
            r0 = 0
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x00ff }
            java.lang.String r2 = "http://www.google.com"
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x00ff }
            r1.<init>(r9, r2)     // Catch:{ Exception -> 0x00ff }
            android.content.pm.PackageManager r2 = r16.getPackageManager()     // Catch:{ Exception -> 0x00ff }
            java.util.List r1 = r2.queryIntentActivities(r1, r12)     // Catch:{ Exception -> 0x00ff }
            if (r1 == 0) goto L_0x00ff
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x00ff }
            if (r2 != 0) goto L_0x00ff
            int r2 = r1.size()     // Catch:{ Exception -> 0x00ff }
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x00ff }
            r3 = 0
        L_0x00d0:
            int r4 = r1.size()     // Catch:{ Exception -> 0x0100 }
            if (r3 >= r4) goto L_0x0100
            java.lang.Object r4 = r1.get(r3)     // Catch:{ Exception -> 0x0100 }
            android.content.pm.ResolveInfo r4 = (android.content.pm.ResolveInfo) r4     // Catch:{ Exception -> 0x0100 }
            android.content.pm.ActivityInfo r4 = r4.activityInfo     // Catch:{ Exception -> 0x0100 }
            java.lang.String r4 = r4.packageName     // Catch:{ Exception -> 0x0100 }
            r2[r3] = r4     // Catch:{ Exception -> 0x0100 }
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0100 }
            if (r4 == 0) goto L_0x00fc
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0100 }
            r4.<init>()     // Catch:{ Exception -> 0x0100 }
            java.lang.String r5 = "default browser name = "
            r4.append(r5)     // Catch:{ Exception -> 0x0100 }
            r5 = r2[r3]     // Catch:{ Exception -> 0x0100 }
            r4.append(r5)     // Catch:{ Exception -> 0x0100 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0100 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0100 }
        L_0x00fc:
            int r3 = r3 + 1
            goto L_0x00d0
        L_0x00ff:
            r2 = r0
        L_0x0100:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x01ab }
            r1.<init>(r9, r8)     // Catch:{ Exception -> 0x01ab }
            android.content.pm.PackageManager r3 = r16.getPackageManager()     // Catch:{ Exception -> 0x01ab }
            java.util.List r0 = r3.queryIntentActivities(r1, r12)     // Catch:{ Exception -> 0x01ab }
            if (r2 == 0) goto L_0x0138
            r1 = 0
        L_0x0110:
            int r3 = r0.size()     // Catch:{ Exception -> 0x01ab }
            if (r1 >= r3) goto L_0x0173
            r3 = 0
        L_0x0117:
            int r4 = r2.length     // Catch:{ Exception -> 0x01ab }
            if (r3 >= r4) goto L_0x0135
            r4 = r2[r3]     // Catch:{ Exception -> 0x01ab }
            java.lang.Object r5 = r0.get(r1)     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ActivityInfo r5 = r5.activityInfo     // Catch:{ Exception -> 0x01ab }
            java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x01ab }
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x01ab }
            if (r4 == 0) goto L_0x0132
            r0.remove(r1)     // Catch:{ Exception -> 0x01ab }
            int r1 = r1 + -1
            goto L_0x0135
        L_0x0132:
            int r3 = r3 + 1
            goto L_0x0117
        L_0x0135:
            r3 = 1
            int r1 = r1 + r3
            goto L_0x0110
        L_0x0138:
            r1 = 0
        L_0x0139:
            int r2 = r0.size()     // Catch:{ Exception -> 0x01ab }
            if (r1 >= r2) goto L_0x0173
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ResolveInfo r2 = (android.content.pm.ResolveInfo) r2     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ActivityInfo r2 = r2.activityInfo     // Catch:{ Exception -> 0x01ab }
            java.lang.String r2 = r2.packageName     // Catch:{ Exception -> 0x01ab }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = "browser"
            boolean r2 = r2.contains(r3)     // Catch:{ Exception -> 0x01ab }
            if (r2 != 0) goto L_0x016b
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ResolveInfo r2 = (android.content.pm.ResolveInfo) r2     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ActivityInfo r2 = r2.activityInfo     // Catch:{ Exception -> 0x01ab }
            java.lang.String r2 = r2.packageName     // Catch:{ Exception -> 0x01ab }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = "chrome"
            boolean r2 = r2.contains(r3)     // Catch:{ Exception -> 0x01ab }
            if (r2 == 0) goto L_0x0170
        L_0x016b:
            r0.remove(r1)     // Catch:{ Exception -> 0x01ab }
            int r1 = r1 + -1
        L_0x0170:
            r2 = 1
            int r1 = r1 + r2
            goto L_0x0139
        L_0x0173:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01ab }
            if (r1 == 0) goto L_0x01ab
            r1 = 0
        L_0x0178:
            int r2 = r0.size()     // Catch:{ Exception -> 0x01ab }
            if (r1 >= r2) goto L_0x01ab
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ab }
            r2.<init>()     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = "device has "
            r2.append(r3)     // Catch:{ Exception -> 0x01ab }
            java.lang.Object r3 = r0.get(r1)     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ResolveInfo r3 = (android.content.pm.ResolveInfo) r3     // Catch:{ Exception -> 0x01ab }
            android.content.pm.ActivityInfo r3 = r3.activityInfo     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = r3.packageName     // Catch:{ Exception -> 0x01ab }
            r2.append(r3)     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = " to open "
            r2.append(r3)     // Catch:{ Exception -> 0x01ab }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x01ab }
            r2.append(r3)     // Catch:{ Exception -> 0x01ab }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01ab }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x01ab }
            int r1 = r1 + 1
            goto L_0x0178
        L_0x01ab:
            boolean r1 = r11[r12]     // Catch:{ Exception -> 0x0221 }
            if (r1 != 0) goto L_0x01b7
            if (r0 == 0) goto L_0x01b7
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0221 }
            if (r0 == 0) goto L_0x0225
        L_0x01b7:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0221 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0221 }
            java.lang.Class<org.telegram.messenger.ShareBroadcastReceiver> r2 = org.telegram.messenger.ShareBroadcastReceiver.class
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x0221 }
            java.lang.String r1 = "android.intent.action.SEND"
            r0.setAction(r1)     // Catch:{ Exception -> 0x0221 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0221 }
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0221 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0221 }
            java.lang.Class<org.telegram.messenger.CustomTabsCopyReceiver> r4 = org.telegram.messenger.CustomTabsCopyReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0221 }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r12, r2, r3)     // Catch:{ Exception -> 0x0221 }
            org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder r2 = new org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder     // Catch:{ Exception -> 0x0221 }
            org.telegram.messenger.support.customtabs.CustomTabsSession r3 = getSession()     // Catch:{ Exception -> 0x0221 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0221 }
            java.lang.String r3 = "CopyLink"
            r4 = 2131624901(0x7f0e03c5, float:1.8876995E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0221 }
            r2.addMenuItem(r3, r1)     // Catch:{ Exception -> 0x0221 }
            java.lang.String r1 = "actionBarBrowser"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)     // Catch:{ Exception -> 0x0221 }
            r2.setToolbarColor(r1)     // Catch:{ Exception -> 0x0221 }
            r1 = 1
            r2.setShowTitle(r1)     // Catch:{ Exception -> 0x0221 }
            android.content.res.Resources r1 = r16.getResources()     // Catch:{ Exception -> 0x0221 }
            r3 = 2131165243(0x7var_b, float:1.7944698E38)
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeResource(r1, r3)     // Catch:{ Exception -> 0x0221 }
            java.lang.String r3 = "ShareFile"
            r4 = 2131626990(0x7f0e0bee, float:1.8881232E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0221 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0221 }
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r12, r0, r12)     // Catch:{ Exception -> 0x0221 }
            r4 = 1
            r2.setActionButton(r1, r3, r0, r4)     // Catch:{ Exception -> 0x0221 }
            org.telegram.messenger.support.customtabs.CustomTabsIntent r0 = r2.build()     // Catch:{ Exception -> 0x0221 }
            r0.setUseNewTask()     // Catch:{ Exception -> 0x0221 }
            r0.launchUrl(r7, r8)     // Catch:{ Exception -> 0x0221 }
            return
        L_0x0221:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0225:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0251 }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x0251 }
            if (r13 == 0) goto L_0x023e
            android.content.ComponentName r1 = new android.content.ComponentName     // Catch:{ Exception -> 0x0251 }
            java.lang.String r2 = r16.getPackageName()     // Catch:{ Exception -> 0x0251 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r3 = org.telegram.ui.LaunchActivity.class
            java.lang.String r3 = r3.getName()     // Catch:{ Exception -> 0x0251 }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0251 }
            r0.setComponent(r1)     // Catch:{ Exception -> 0x0251 }
        L_0x023e:
            java.lang.String r1 = "create_new_tab"
            r2 = 1
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0251 }
            java.lang.String r1 = "com.android.browser.application_id"
            java.lang.String r2 = r16.getPackageName()     // Catch:{ Exception -> 0x0251 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0251 }
            r7.startActivity(r0)     // Catch:{ Exception -> 0x0251 }
            goto L_0x0255
        L_0x0251:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0255:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$null$0(org.telegram.ui.ActionBar.AlertDialog[] r3, org.telegram.tgnet.TLObject r4, int r5, android.net.Uri r6, android.content.Context r7, boolean r8) {
        /*
            r0 = 0
            r1 = r3[r0]     // Catch:{ all -> 0x0007 }
            r1.dismiss()     // Catch:{ all -> 0x0007 }
            goto L_0x0008
        L_0x0007:
        L_0x0008:
            r1 = 0
            r3[r0] = r1
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1 = 1
            if (r3 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaWebPage) r4
            org.telegram.tgnet.TLRPC$WebPage r3 = r4.webpage
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r2 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$Page r3 = r3.cached_page
            if (r3 == 0) goto L_0x0033
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r5 = org.telegram.messenger.NotificationCenter.openArticle
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r2[r0] = r4
            java.lang.String r4 = r6.toString()
            r2[r1] = r4
            r3.postNotificationName(r5, r2)
            goto L_0x0034
        L_0x0033:
            r1 = 0
        L_0x0034:
            if (r1 != 0) goto L_0x0039
            openUrl((android.content.Context) r7, (android.net.Uri) r6, (boolean) r8, (boolean) r0)
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.lambda$null$0(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, int, android.net.Uri, android.content.Context, boolean):void");
    }

    static /* synthetic */ void lambda$openUrl$3(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            try {
                alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                    public final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.f$0, true);
                    }
                });
                alertDialogArr[0].show();
            } catch (Exception unused) {
            }
        }
    }

    public static boolean isInternalUrl(String str, boolean[] zArr) {
        return isInternalUri(Uri.parse(str), zArr);
    }

    public static boolean isPassportUrl(String str) {
        if (str == null) {
            return false;
        }
        try {
            String lowerCase = str.toLowerCase();
            if (lowerCase.startsWith("tg:passport") || lowerCase.startsWith("tg://passport") || lowerCase.startsWith("tg:secureid")) {
                return true;
            }
            if (!lowerCase.contains("resolve") || !lowerCase.contains("domain=telegrampassport")) {
                return false;
            }
            return true;
        } catch (Throwable unused) {
        }
    }

    public static boolean isInternalUri(Uri uri, boolean[] zArr) {
        String path;
        String host = uri.getHost();
        String lowerCase = host != null ? host.toLowerCase() : "";
        if ("ton".equals(uri.getScheme())) {
            try {
                List<ResolveInfo> queryIntentActivities = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri), 0);
                return queryIntentActivities == null || queryIntentActivities.size() <= 1;
            } catch (Exception unused) {
            }
        } else if ("tg".equals(uri.getScheme())) {
            return true;
        } else {
            if ("telegram.dog".equals(lowerCase)) {
                String path2 = uri.getPath();
                if (path2 != null && path2.length() > 1) {
                    String lowerCase2 = path2.substring(1).toLowerCase();
                    if (!lowerCase2.startsWith("blog") && !lowerCase2.equals("iv") && !lowerCase2.startsWith("faq") && !lowerCase2.equals("apps") && !lowerCase2.startsWith("s/")) {
                        return true;
                    }
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                    return false;
                }
            } else if (("telegram.me".equals(lowerCase) || "t.me".equals(lowerCase)) && (path = uri.getPath()) != null && path.length() > 1) {
                String lowerCase3 = path.substring(1).toLowerCase();
                if (!lowerCase3.equals("iv") && !lowerCase3.startsWith("s/")) {
                    return true;
                }
                if (zArr != null) {
                    zArr[0] = true;
                }
            }
            return false;
        }
    }
}

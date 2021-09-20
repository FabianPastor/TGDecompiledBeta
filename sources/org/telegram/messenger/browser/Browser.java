package org.telegram.messenger.browser;

import android.app.Activity;
import android.content.Context;
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
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
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

    /* JADX WARNING: Can't wrap try/catch for region: R(14:3|(3:5|6|(2:12|13))|14|15|16|(2:18|19)(1:20)|21|(3:25|26|27)|32|(1:34)|35|(9:37|(1:39)|40|41|(2:43|44)|45|(1:47)(1:48)|(1:50)|51)|(14:58|59|60|(3:64|65|(4:66|67|(3:69|(2:71|124)(1:125)|72)(0)|75))|73|75|76|(3:78|(4:81|(2:82|(1:127)(2:84|(3:129|86|128)(1:87)))|88|79)|126)(3:89|(4:92|(2:96|131)(1:132)|97|90)|130)|98|(3:100|(3:103|104|101)|133)|105|107|108|(2:113|114))|(5:117|118|(1:120)|121|137)) */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x030c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x030d, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0085 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:75:0x01e7 */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x025f A[Catch:{ Exception -> 0x0293 }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0317 A[Catch:{ Exception -> 0x033c }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x008d A[SYNTHETIC, Splitter:B:18:0x008d] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0097 A[Catch:{ Exception -> 0x030c }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b9 A[Catch:{ Exception -> 0x030c }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00d1 A[Catch:{ Exception -> 0x030c }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01bd A[Catch:{ Exception -> 0x01e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01f7 A[Catch:{ Exception -> 0x0293 }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0220 A[Catch:{ Exception -> 0x0293 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void openUrl(android.content.Context r16, android.net.Uri r17, boolean r18, boolean r19) {
        /*
            r7 = r16
            r8 = r17
            java.lang.String r9 = "android.intent.action.VIEW"
            if (r7 == 0) goto L_0x0340
            if (r8 != 0) goto L_0x000c
            goto L_0x0340
        L_0x000c:
            int r10 = org.telegram.messenger.UserConfig.selectedAccount
            r11 = 1
            boolean[] r12 = new boolean[r11]
            r13 = 0
            r12[r13] = r13
            boolean r14 = isInternalUri(r8, r12)
            r15 = 3
            if (r19 == 0) goto L_0x0085
            java.lang.String r0 = r17.getHost()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0085 }
            boolean r0 = isTelegraphUrl(r0, r11)     // Catch:{ Exception -> 0x0085 }
            if (r0 != 0) goto L_0x004b
            java.lang.String r0 = r17.toString()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r1 = "telegram.org/faq"
            boolean r0 = r0.contains(r1)     // Catch:{ Exception -> 0x0085 }
            if (r0 != 0) goto L_0x004b
            java.lang.String r0 = r17.toString()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r1 = "telegram.org/privacy"
            boolean r0 = r0.contains(r1)     // Catch:{ Exception -> 0x0085 }
            if (r0 == 0) goto L_0x0085
        L_0x004b:
            org.telegram.ui.ActionBar.AlertDialog[] r0 = new org.telegram.ui.ActionBar.AlertDialog[r11]     // Catch:{ Exception -> 0x0085 }
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog     // Catch:{ Exception -> 0x0085 }
            r1.<init>(r7, r15)     // Catch:{ Exception -> 0x0085 }
            r0[r13] = r1     // Catch:{ Exception -> 0x0085 }
            org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview r6 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview     // Catch:{ Exception -> 0x0085 }
            r6.<init>()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r1 = r17.toString()     // Catch:{ Exception -> 0x0085 }
            r6.message = r1     // Catch:{ Exception -> 0x0085 }
            int r1 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x0085 }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x0085 }
            org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda3 r4 = new org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda3     // Catch:{ Exception -> 0x0085 }
            r1 = r4
            r2 = r0
            r3 = r10
            r11 = r4
            r4 = r17
            r13 = r5
            r5 = r16
            r15 = r6
            r6 = r18
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0085 }
            int r1 = r13.sendRequest(r15, r11)     // Catch:{ Exception -> 0x0085 }
            org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x0085 }
            r2.<init>(r0, r1)     // Catch:{ Exception -> 0x0085 }
            r0 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r0)     // Catch:{ Exception -> 0x0085 }
            return
        L_0x0085:
            java.lang.String r0 = r17.getScheme()     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x0097
            java.lang.String r0 = r17.getScheme()     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x030c }
            r2 = r0
            goto L_0x0098
        L_0x0097:
            r2 = r1
        L_0x0098:
            java.lang.String r0 = "http"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x030c }
            if (r0 != 0) goto L_0x00a8
            java.lang.String r0 = "https"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x030c }
            if (r0 == 0) goto L_0x00b3
        L_0x00a8:
            android.net.Uri r0 = r17.normalizeScheme()     // Catch:{ Exception -> 0x00ae }
            r8 = r0
            goto L_0x00b3
        L_0x00ae:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x030c }
        L_0x00b3:
            java.lang.String r0 = r8.getHost()     // Catch:{ Exception -> 0x030c }
            if (r0 == 0) goto L_0x00c1
            java.lang.String r0 = r8.getHost()     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = r0.toLowerCase()     // Catch:{ Exception -> 0x030c }
        L_0x00c1:
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r10)     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ Exception -> 0x030c }
            java.util.Set<java.lang.String> r0 = r0.autologinDomains     // Catch:{ Exception -> 0x030c }
            boolean r0 = r0.contains(r1)     // Catch:{ Exception -> 0x030c }
            if (r0 == 0) goto L_0x0182
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r0.<init>()     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = "autologin_token="
            r0.append(r1)     // Catch:{ Exception -> 0x030c }
            int r1 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.AccountInstance r1 = org.telegram.messenger.AccountInstance.getInstance(r1)     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = r1.autologinToken     // Catch:{ Exception -> 0x030c }
            java.lang.String r3 = "UTF-8"
            java.lang.String r1 = java.net.URLEncoder.encode(r1, r3)     // Catch:{ Exception -> 0x030c }
            r0.append(r1)     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = r8.toString()     // Catch:{ Exception -> 0x030c }
            java.lang.String r3 = "://"
            int r3 = r1.indexOf(r3)     // Catch:{ Exception -> 0x030c }
            if (r3 < 0) goto L_0x0106
            r4 = 3
            int r3 = r3 + r4
            java.lang.String r1 = r1.substring(r3)     // Catch:{ Exception -> 0x030c }
        L_0x0106:
            java.lang.String r3 = r8.getEncodedFragment()     // Catch:{ Exception -> 0x030c }
            java.lang.String r4 = "#"
            if (r3 != 0) goto L_0x010f
            goto L_0x0127
        L_0x010f:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r5.<init>()     // Catch:{ Exception -> 0x030c }
            r5.append(r4)     // Catch:{ Exception -> 0x030c }
            r5.append(r3)     // Catch:{ Exception -> 0x030c }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x030c }
            int r5 = r1.indexOf(r5)     // Catch:{ Exception -> 0x030c }
            r6 = 0
            java.lang.String r1 = r1.substring(r6, r5)     // Catch:{ Exception -> 0x030c }
        L_0x0127:
            r5 = 63
            int r5 = r1.indexOf(r5)     // Catch:{ Exception -> 0x030c }
            if (r5 < 0) goto L_0x0144
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r5.<init>()     // Catch:{ Exception -> 0x030c }
            r5.append(r1)     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = "&"
            r5.append(r1)     // Catch:{ Exception -> 0x030c }
            r5.append(r0)     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r5.toString()     // Catch:{ Exception -> 0x030c }
            goto L_0x0158
        L_0x0144:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r5.<init>()     // Catch:{ Exception -> 0x030c }
            r5.append(r1)     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = "?"
            r5.append(r1)     // Catch:{ Exception -> 0x030c }
            r5.append(r0)     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r5.toString()     // Catch:{ Exception -> 0x030c }
        L_0x0158:
            if (r3 == 0) goto L_0x016c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r1.<init>()     // Catch:{ Exception -> 0x030c }
            r1.append(r0)     // Catch:{ Exception -> 0x030c }
            r1.append(r4)     // Catch:{ Exception -> 0x030c }
            r1.append(r3)     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x030c }
        L_0x016c:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x030c }
            r1.<init>()     // Catch:{ Exception -> 0x030c }
            java.lang.String r3 = "https://"
            r1.append(r3)     // Catch:{ Exception -> 0x030c }
            r1.append(r0)     // Catch:{ Exception -> 0x030c }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x030c }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x030c }
            r8 = r0
        L_0x0182:
            if (r18 == 0) goto L_0x0310
            boolean r0 = org.telegram.messenger.SharedConfig.customTabs     // Catch:{ Exception -> 0x030c }
            if (r0 == 0) goto L_0x0310
            if (r14 != 0) goto L_0x0310
            java.lang.String r0 = "tel"
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x030c }
            if (r0 != 0) goto L_0x0310
            r0 = 0
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x01e6 }
            java.lang.String r2 = "http://www.google.com"
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x01e6 }
            r1.<init>(r9, r2)     // Catch:{ Exception -> 0x01e6 }
            android.content.pm.PackageManager r2 = r16.getPackageManager()     // Catch:{ Exception -> 0x01e6 }
            r3 = 0
            java.util.List r1 = r2.queryIntentActivities(r1, r3)     // Catch:{ Exception -> 0x01e6 }
            if (r1 == 0) goto L_0x01e6
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x01e6 }
            if (r2 != 0) goto L_0x01e6
            int r2 = r1.size()     // Catch:{ Exception -> 0x01e6 }
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x01e6 }
            r3 = 0
        L_0x01b7:
            int r4 = r1.size()     // Catch:{ Exception -> 0x01e7 }
            if (r3 >= r4) goto L_0x01e7
            java.lang.Object r4 = r1.get(r3)     // Catch:{ Exception -> 0x01e7 }
            android.content.pm.ResolveInfo r4 = (android.content.pm.ResolveInfo) r4     // Catch:{ Exception -> 0x01e7 }
            android.content.pm.ActivityInfo r4 = r4.activityInfo     // Catch:{ Exception -> 0x01e7 }
            java.lang.String r4 = r4.packageName     // Catch:{ Exception -> 0x01e7 }
            r2[r3] = r4     // Catch:{ Exception -> 0x01e7 }
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01e7 }
            if (r4 == 0) goto L_0x01e3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01e7 }
            r4.<init>()     // Catch:{ Exception -> 0x01e7 }
            java.lang.String r5 = "default browser name = "
            r4.append(r5)     // Catch:{ Exception -> 0x01e7 }
            r5 = r2[r3]     // Catch:{ Exception -> 0x01e7 }
            r4.append(r5)     // Catch:{ Exception -> 0x01e7 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01e7 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x01e7 }
        L_0x01e3:
            int r3 = r3 + 1
            goto L_0x01b7
        L_0x01e6:
            r2 = r0
        L_0x01e7:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0293 }
            r1.<init>(r9, r8)     // Catch:{ Exception -> 0x0293 }
            android.content.pm.PackageManager r3 = r16.getPackageManager()     // Catch:{ Exception -> 0x0293 }
            r4 = 0
            java.util.List r0 = r3.queryIntentActivities(r1, r4)     // Catch:{ Exception -> 0x0293 }
            if (r2 == 0) goto L_0x0220
            r1 = 0
        L_0x01f8:
            int r3 = r0.size()     // Catch:{ Exception -> 0x0293 }
            if (r1 >= r3) goto L_0x025b
            r3 = 0
        L_0x01ff:
            int r4 = r2.length     // Catch:{ Exception -> 0x0293 }
            if (r3 >= r4) goto L_0x021d
            r4 = r2[r3]     // Catch:{ Exception -> 0x0293 }
            java.lang.Object r5 = r0.get(r1)     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ActivityInfo r5 = r5.activityInfo     // Catch:{ Exception -> 0x0293 }
            java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x0293 }
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x0293 }
            if (r4 == 0) goto L_0x021a
            r0.remove(r1)     // Catch:{ Exception -> 0x0293 }
            int r1 = r1 + -1
            goto L_0x021d
        L_0x021a:
            int r3 = r3 + 1
            goto L_0x01ff
        L_0x021d:
            r3 = 1
            int r1 = r1 + r3
            goto L_0x01f8
        L_0x0220:
            r1 = 0
        L_0x0221:
            int r2 = r0.size()     // Catch:{ Exception -> 0x0293 }
            if (r1 >= r2) goto L_0x025b
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ResolveInfo r2 = (android.content.pm.ResolveInfo) r2     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ActivityInfo r2 = r2.activityInfo     // Catch:{ Exception -> 0x0293 }
            java.lang.String r2 = r2.packageName     // Catch:{ Exception -> 0x0293 }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = "browser"
            boolean r2 = r2.contains(r3)     // Catch:{ Exception -> 0x0293 }
            if (r2 != 0) goto L_0x0253
            java.lang.Object r2 = r0.get(r1)     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ResolveInfo r2 = (android.content.pm.ResolveInfo) r2     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ActivityInfo r2 = r2.activityInfo     // Catch:{ Exception -> 0x0293 }
            java.lang.String r2 = r2.packageName     // Catch:{ Exception -> 0x0293 }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = "chrome"
            boolean r2 = r2.contains(r3)     // Catch:{ Exception -> 0x0293 }
            if (r2 == 0) goto L_0x0258
        L_0x0253:
            r0.remove(r1)     // Catch:{ Exception -> 0x0293 }
            int r1 = r1 + -1
        L_0x0258:
            r2 = 1
            int r1 = r1 + r2
            goto L_0x0221
        L_0x025b:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0293 }
            if (r1 == 0) goto L_0x0293
            r1 = 0
        L_0x0260:
            int r2 = r0.size()     // Catch:{ Exception -> 0x0293 }
            if (r1 >= r2) goto L_0x0293
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0293 }
            r2.<init>()     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = "device has "
            r2.append(r3)     // Catch:{ Exception -> 0x0293 }
            java.lang.Object r3 = r0.get(r1)     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ResolveInfo r3 = (android.content.pm.ResolveInfo) r3     // Catch:{ Exception -> 0x0293 }
            android.content.pm.ActivityInfo r3 = r3.activityInfo     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = r3.packageName     // Catch:{ Exception -> 0x0293 }
            r2.append(r3)     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = " to open "
            r2.append(r3)     // Catch:{ Exception -> 0x0293 }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x0293 }
            r2.append(r3)     // Catch:{ Exception -> 0x0293 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0293 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0293 }
            int r1 = r1 + 1
            goto L_0x0260
        L_0x0293:
            r1 = 0
            boolean r2 = r12[r1]     // Catch:{ Exception -> 0x030c }
            if (r2 != 0) goto L_0x02a0
            if (r0 == 0) goto L_0x02a0
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x030c }
            if (r0 == 0) goto L_0x0310
        L_0x02a0:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x030c }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x030c }
            java.lang.Class<org.telegram.messenger.ShareBroadcastReceiver> r2 = org.telegram.messenger.ShareBroadcastReceiver.class
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = "android.intent.action.SEND"
            r0.setAction(r1)     // Catch:{ Exception -> 0x030c }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x030c }
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x030c }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x030c }
            java.lang.Class<org.telegram.messenger.CustomTabsCopyReceiver> r4 = org.telegram.messenger.CustomTabsCopyReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x030c }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r4 = 0
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r4, r2, r3)     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder r2 = new org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.support.customtabs.CustomTabsSession r3 = getSession()     // Catch:{ Exception -> 0x030c }
            r2.<init>(r3)     // Catch:{ Exception -> 0x030c }
            java.lang.String r3 = "CopyLink"
            r4 = 2131625055(0x7f0e045f, float:1.8877307E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x030c }
            r2.addMenuItem(r3, r1)     // Catch:{ Exception -> 0x030c }
            java.lang.String r1 = "actionBarBrowser"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)     // Catch:{ Exception -> 0x030c }
            r2.setToolbarColor(r1)     // Catch:{ Exception -> 0x030c }
            r1 = 1
            r2.setShowTitle(r1)     // Catch:{ Exception -> 0x030c }
            android.content.res.Resources r1 = r16.getResources()     // Catch:{ Exception -> 0x030c }
            r3 = 2131165243(0x7var_b, float:1.7944698E38)
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeResource(r1, r3)     // Catch:{ Exception -> 0x030c }
            java.lang.String r3 = "ShareFile"
            r4 = 2131627657(0x7f0e0e89, float:1.8882585E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x030c }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x030c }
            r5 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r5, r0, r5)     // Catch:{ Exception -> 0x030c }
            r4 = 1
            r2.setActionButton(r1, r3, r0, r4)     // Catch:{ Exception -> 0x030c }
            org.telegram.messenger.support.customtabs.CustomTabsIntent r0 = r2.build()     // Catch:{ Exception -> 0x030c }
            r0.setUseNewTask()     // Catch:{ Exception -> 0x030c }
            r0.launchUrl(r7, r8)     // Catch:{ Exception -> 0x030c }
            return
        L_0x030c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0310:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x033c }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x033c }
            if (r14 == 0) goto L_0x0329
            android.content.ComponentName r1 = new android.content.ComponentName     // Catch:{ Exception -> 0x033c }
            java.lang.String r2 = r16.getPackageName()     // Catch:{ Exception -> 0x033c }
            java.lang.Class<org.telegram.ui.LaunchActivity> r3 = org.telegram.ui.LaunchActivity.class
            java.lang.String r3 = r3.getName()     // Catch:{ Exception -> 0x033c }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x033c }
            r0.setComponent(r1)     // Catch:{ Exception -> 0x033c }
        L_0x0329:
            java.lang.String r1 = "create_new_tab"
            r2 = 1
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x033c }
            java.lang.String r1 = "com.android.browser.application_id"
            java.lang.String r2 = r16.getPackageName()     // Catch:{ Exception -> 0x033c }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x033c }
            r7.startActivity(r0)     // Catch:{ Exception -> 0x033c }
            goto L_0x0340
        L_0x033c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0340:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$openUrl$0(org.telegram.ui.ActionBar.AlertDialog[] r3, org.telegram.tgnet.TLObject r4, int r5, android.net.Uri r6, android.content.Context r7, boolean r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.lambda$openUrl$0(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, int, android.net.Uri, android.content.Context, boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$openUrl$3(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            try {
                alertDialogArr[0].setOnCancelListener(new Browser$$ExternalSyntheticLambda0(i));
                alertDialogArr[0].show();
            } catch (Exception unused) {
            }
        }
    }

    public static boolean isInternalUrl(String str, boolean[] zArr) {
        return isInternalUri(Uri.parse(str), false, zArr);
    }

    public static boolean isInternalUrl(String str, boolean z, boolean[] zArr) {
        return isInternalUri(Uri.parse(str), z, zArr);
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
        return isInternalUri(uri, false, zArr);
    }

    public static boolean isInternalUri(Uri uri, boolean z, boolean[] zArr) {
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
                String path = uri.getPath();
                if (path != null && path.length() > 1) {
                    if (z) {
                        return true;
                    }
                    String lowerCase2 = path.substring(1).toLowerCase();
                    if (!lowerCase2.startsWith("blog") && !lowerCase2.equals("iv") && !lowerCase2.startsWith("faq") && !lowerCase2.equals("apps") && !lowerCase2.startsWith("s/")) {
                        return true;
                    }
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                    return false;
                }
            } else if (!"telegram.me".equals(lowerCase) && !"t.me".equals(lowerCase)) {
                return z && (lowerCase.endsWith("telegram.org") || lowerCase.endsWith("telegra.ph") || lowerCase.endsWith("telesco.pe"));
            } else {
                String path2 = uri.getPath();
                if (path2 != null && path2.length() > 1) {
                    if (z) {
                        return true;
                    }
                    String lowerCase3 = path2.substring(1).toLowerCase();
                    if (!lowerCase3.equals("iv") && !lowerCase3.startsWith("s/")) {
                        return true;
                    }
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                }
            }
        }
    }
}

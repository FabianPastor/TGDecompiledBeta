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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    /* access modifiers changed from: private */
    public static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    private static CustomTabsSession getCurrentSession() {
        WeakReference<CustomTabsSession> weakReference = customTabsCurrentSession;
        if (weakReference == null) {
            return null;
        }
        return (CustomTabsSession) weakReference.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference<>(session);
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
        Activity currentActivity = weakReference == null ? null : (Activity) weakReference.get();
        if (!(currentActivity == null || currentActivity == activity)) {
            unbindCustomTabsService(currentActivity);
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
                    public void onServiceConnected(CustomTabsClient client) {
                        CustomTabsClient unused = Browser.customTabsClient = client;
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
            } catch (Exception e) {
            }
            customTabsClient = null;
            customTabsSession = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        private NavigationCallback() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
        }
    }

    public static void openUrl(Context context, String url) {
        if (url != null) {
            openUrl(context, Uri.parse(url), true);
        }
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context != null && url != null) {
            openUrl(context, Uri.parse(url), allowCustom);
        }
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        openUrl(context, uri, allowCustom, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom, boolean tryTelegraph) {
        openUrl(context, Uri.parse(url), allowCustom, tryTelegraph);
    }

    public static boolean isTelegraphUrl(String url, boolean equals) {
        if (equals) {
            if (url.equals("telegra.ph") || url.equals("te.legra.ph") || url.equals("graph.org")) {
                return true;
            }
            return false;
        } else if (url.contains("telegra.ph") || url.contains("te.legra.ph") || url.contains("graph.org")) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:127:0x0335 A[Catch:{ Exception -> 0x035a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void openUrl(android.content.Context r17, android.net.Uri r18, boolean r19, boolean r20) {
        /*
            r7 = r17
            r8 = r18
            java.lang.String r9 = "android.intent.action.VIEW"
            if (r7 == 0) goto L_0x035f
            if (r8 != 0) goto L_0x000c
            goto L_0x035f
        L_0x000c:
            int r10 = org.telegram.messenger.UserConfig.selectedAccount
            r11 = 1
            boolean[] r0 = new boolean[r11]
            r12 = 0
            r0[r12] = r12
            r13 = r0
            boolean r14 = isInternalUri(r8, r13)
            if (r20 == 0) goto L_0x008d
            java.lang.String r0 = r18.getHost()     // Catch:{ Exception -> 0x008c }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x008c }
            boolean r1 = isTelegraphUrl(r0, r11)     // Catch:{ Exception -> 0x008c }
            if (r1 != 0) goto L_0x004d
            java.lang.String r1 = r18.toString()     // Catch:{ Exception -> 0x008c }
            java.lang.String r1 = r1.toLowerCase()     // Catch:{ Exception -> 0x008c }
            java.lang.String r2 = "telegram.org/faq"
            boolean r1 = r1.contains(r2)     // Catch:{ Exception -> 0x008c }
            if (r1 != 0) goto L_0x004d
            java.lang.String r1 = r18.toString()     // Catch:{ Exception -> 0x008c }
            java.lang.String r1 = r1.toLowerCase()     // Catch:{ Exception -> 0x008c }
            java.lang.String r2 = "telegram.org/privacy"
            boolean r1 = r1.contains(r2)     // Catch:{ Exception -> 0x008c }
            if (r1 == 0) goto L_0x004c
            goto L_0x004d
        L_0x004c:
            goto L_0x008d
        L_0x004d:
            org.telegram.ui.ActionBar.AlertDialog[] r1 = new org.telegram.ui.ActionBar.AlertDialog[r11]     // Catch:{ Exception -> 0x008c }
            org.telegram.ui.ActionBar.AlertDialog r2 = new org.telegram.ui.ActionBar.AlertDialog     // Catch:{ Exception -> 0x008c }
            r3 = 3
            r2.<init>(r7, r3)     // Catch:{ Exception -> 0x008c }
            r1[r12] = r2     // Catch:{ Exception -> 0x008c }
            r15 = r1
            r4 = r18
            org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview r1 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview     // Catch:{ Exception -> 0x008c }
            r1.<init>()     // Catch:{ Exception -> 0x008c }
            r6 = r1
            java.lang.String r1 = r18.toString()     // Catch:{ Exception -> 0x008c }
            r6.message = r1     // Catch:{ Exception -> 0x008c }
            int r1 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x008c }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x008c }
            org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda3 r3 = new org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda3     // Catch:{ Exception -> 0x008c }
            r1 = r3
            r2 = r15
            r11 = r3
            r3 = r10
            r12 = r5
            r5 = r17
            r16 = r0
            r0 = r6
            r6 = r19
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x008c }
            int r1 = r12.sendRequest(r0, r11)     // Catch:{ Exception -> 0x008c }
            org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda1     // Catch:{ Exception -> 0x008c }
            r2.<init>(r15, r1)     // Catch:{ Exception -> 0x008c }
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r5)     // Catch:{ Exception -> 0x008c }
            return
        L_0x008c:
            r0 = move-exception
        L_0x008d:
            java.lang.String r0 = r18.getScheme()     // Catch:{ Exception -> 0x032a }
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x009e
            java.lang.String r0 = r18.getScheme()     // Catch:{ Exception -> 0x032a }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x032a }
            goto L_0x009f
        L_0x009e:
            r0 = r1
        L_0x009f:
            r2 = r0
            java.lang.String r0 = "http"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x032a }
            if (r0 != 0) goto L_0x00b0
            java.lang.String r0 = "https"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x032a }
            if (r0 == 0) goto L_0x00bc
        L_0x00b0:
            android.net.Uri r0 = r18.normalizeScheme()     // Catch:{ Exception -> 0x00b6 }
            r8 = r0
            goto L_0x00bc
        L_0x00b6:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x032a }
        L_0x00bc:
            java.lang.String r0 = r8.getHost()     // Catch:{ Exception -> 0x0328 }
            if (r0 == 0) goto L_0x00ca
            java.lang.String r0 = r8.getHost()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r1 = r0.toLowerCase()     // Catch:{ Exception -> 0x0328 }
        L_0x00ca:
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r10)     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ Exception -> 0x0328 }
            java.util.Set<java.lang.String> r0 = r0.autologinDomains     // Catch:{ Exception -> 0x0328 }
            boolean r0 = r0.contains(r1)     // Catch:{ Exception -> 0x0328 }
            if (r0 == 0) goto L_0x0194
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r0.<init>()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r3 = "autologin_token="
            r0.append(r3)     // Catch:{ Exception -> 0x0328 }
            int r3 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.AccountInstance r3 = org.telegram.messenger.AccountInstance.getInstance(r3)     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r3 = r3.autologinToken     // Catch:{ Exception -> 0x0328 }
            java.lang.String r4 = "UTF-8"
            java.lang.String r3 = java.net.URLEncoder.encode(r3, r4)     // Catch:{ Exception -> 0x0328 }
            r0.append(r3)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r4 = "://"
            int r4 = r3.indexOf(r4)     // Catch:{ Exception -> 0x0328 }
            if (r4 < 0) goto L_0x0110
            int r5 = r4 + 3
            java.lang.String r5 = r3.substring(r5)     // Catch:{ Exception -> 0x0328 }
            goto L_0x0111
        L_0x0110:
            r5 = r3
        L_0x0111:
            java.lang.String r6 = r8.getEncodedFragment()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r11 = "#"
            if (r6 != 0) goto L_0x011b
            r12 = r5
            goto L_0x0133
        L_0x011b:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r12.<init>()     // Catch:{ Exception -> 0x0328 }
            r12.append(r11)     // Catch:{ Exception -> 0x0328 }
            r12.append(r6)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0328 }
            int r12 = r5.indexOf(r12)     // Catch:{ Exception -> 0x0328 }
            r15 = 0
            java.lang.String r12 = r5.substring(r15, r12)     // Catch:{ Exception -> 0x0328 }
        L_0x0133:
            r15 = 63
            int r15 = r12.indexOf(r15)     // Catch:{ Exception -> 0x0328 }
            if (r15 < 0) goto L_0x0152
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r15.<init>()     // Catch:{ Exception -> 0x0328 }
            r15.append(r12)     // Catch:{ Exception -> 0x0328 }
            r18 = r1
            java.lang.String r1 = "&"
            r15.append(r1)     // Catch:{ Exception -> 0x0328 }
            r15.append(r0)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r1 = r15.toString()     // Catch:{ Exception -> 0x0328 }
            goto L_0x0168
        L_0x0152:
            r18 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r1.<init>()     // Catch:{ Exception -> 0x0328 }
            r1.append(r12)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r15 = "?"
            r1.append(r15)     // Catch:{ Exception -> 0x0328 }
            r1.append(r0)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0328 }
        L_0x0168:
            if (r6 == 0) goto L_0x017d
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r12.<init>()     // Catch:{ Exception -> 0x0328 }
            r12.append(r1)     // Catch:{ Exception -> 0x0328 }
            r12.append(r11)     // Catch:{ Exception -> 0x0328 }
            r12.append(r6)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r11 = r12.toString()     // Catch:{ Exception -> 0x0328 }
            r1 = r11
        L_0x017d:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0328 }
            r11.<init>()     // Catch:{ Exception -> 0x0328 }
            java.lang.String r12 = "https://"
            r11.append(r12)     // Catch:{ Exception -> 0x0328 }
            r11.append(r1)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0328 }
            android.net.Uri r11 = android.net.Uri.parse(r11)     // Catch:{ Exception -> 0x0328 }
            r8 = r11
            goto L_0x0196
        L_0x0194:
            r18 = r1
        L_0x0196:
            if (r19 == 0) goto L_0x0327
            boolean r0 = org.telegram.messenger.SharedConfig.customTabs     // Catch:{ Exception -> 0x0328 }
            if (r0 == 0) goto L_0x0327
            if (r14 != 0) goto L_0x0327
            java.lang.String r0 = "tel"
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x0328 }
            if (r0 != 0) goto L_0x0327
            r1 = 0
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x01fc }
            java.lang.String r3 = "http://www.google.com"
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x01fc }
            r0.<init>(r9, r3)     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PackageManager r3 = r17.getPackageManager()     // Catch:{ Exception -> 0x01fc }
            r4 = 0
            java.util.List r3 = r3.queryIntentActivities(r0, r4)     // Catch:{ Exception -> 0x01fc }
            if (r3 == 0) goto L_0x01fb
            boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x01fc }
            if (r4 != 0) goto L_0x01fb
            int r4 = r3.size()     // Catch:{ Exception -> 0x01fc }
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ Exception -> 0x01fc }
            r1 = r4
            r4 = 0
        L_0x01cc:
            int r5 = r3.size()     // Catch:{ Exception -> 0x01fc }
            if (r4 >= r5) goto L_0x01fb
            java.lang.Object r5 = r3.get(r4)     // Catch:{ Exception -> 0x01fc }
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5     // Catch:{ Exception -> 0x01fc }
            android.content.pm.ActivityInfo r5 = r5.activityInfo     // Catch:{ Exception -> 0x01fc }
            java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x01fc }
            r1[r4] = r5     // Catch:{ Exception -> 0x01fc }
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01fc }
            if (r5 == 0) goto L_0x01f8
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fc }
            r5.<init>()     // Catch:{ Exception -> 0x01fc }
            java.lang.String r6 = "default browser name = "
            r5.append(r6)     // Catch:{ Exception -> 0x01fc }
            r6 = r1[r4]     // Catch:{ Exception -> 0x01fc }
            r5.append(r6)     // Catch:{ Exception -> 0x01fc }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x01fc }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x01fc }
        L_0x01f8:
            int r4 = r4 + 1
            goto L_0x01cc
        L_0x01fb:
            goto L_0x01fd
        L_0x01fc:
            r0 = move-exception
        L_0x01fd:
            r3 = 0
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x02ad }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x02ad }
            android.content.pm.PackageManager r4 = r17.getPackageManager()     // Catch:{ Exception -> 0x02ad }
            r5 = 0
            java.util.List r4 = r4.queryIntentActivities(r0, r5)     // Catch:{ Exception -> 0x02ad }
            r3 = r4
            if (r1 == 0) goto L_0x0239
            r4 = 0
        L_0x0210:
            int r5 = r3.size()     // Catch:{ Exception -> 0x02ad }
            if (r4 >= r5) goto L_0x0238
            r5 = 0
        L_0x0217:
            int r6 = r1.length     // Catch:{ Exception -> 0x02ad }
            if (r5 >= r6) goto L_0x0235
            r6 = r1[r5]     // Catch:{ Exception -> 0x02ad }
            java.lang.Object r11 = r3.get(r4)     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ResolveInfo r11 = (android.content.pm.ResolveInfo) r11     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ActivityInfo r11 = r11.activityInfo     // Catch:{ Exception -> 0x02ad }
            java.lang.String r11 = r11.packageName     // Catch:{ Exception -> 0x02ad }
            boolean r6 = r6.equals(r11)     // Catch:{ Exception -> 0x02ad }
            if (r6 == 0) goto L_0x0232
            r3.remove(r4)     // Catch:{ Exception -> 0x02ad }
            int r4 = r4 + -1
            goto L_0x0235
        L_0x0232:
            int r5 = r5 + 1
            goto L_0x0217
        L_0x0235:
            r5 = 1
            int r4 = r4 + r5
            goto L_0x0210
        L_0x0238:
            goto L_0x0274
        L_0x0239:
            r4 = 0
        L_0x023a:
            int r5 = r3.size()     // Catch:{ Exception -> 0x02ad }
            if (r4 >= r5) goto L_0x0274
            java.lang.Object r5 = r3.get(r4)     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ActivityInfo r5 = r5.activityInfo     // Catch:{ Exception -> 0x02ad }
            java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x02ad }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = "browser"
            boolean r5 = r5.contains(r6)     // Catch:{ Exception -> 0x02ad }
            if (r5 != 0) goto L_0x026c
            java.lang.Object r5 = r3.get(r4)     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ActivityInfo r5 = r5.activityInfo     // Catch:{ Exception -> 0x02ad }
            java.lang.String r5 = r5.packageName     // Catch:{ Exception -> 0x02ad }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = "chrome"
            boolean r5 = r5.contains(r6)     // Catch:{ Exception -> 0x02ad }
            if (r5 == 0) goto L_0x0271
        L_0x026c:
            r3.remove(r4)     // Catch:{ Exception -> 0x02ad }
            int r4 = r4 + -1
        L_0x0271:
            r5 = 1
            int r4 = r4 + r5
            goto L_0x023a
        L_0x0274:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02ad }
            if (r4 == 0) goto L_0x02ac
            r4 = 0
        L_0x0279:
            int r5 = r3.size()     // Catch:{ Exception -> 0x02ad }
            if (r4 >= r5) goto L_0x02ac
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02ad }
            r5.<init>()     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = "device has "
            r5.append(r6)     // Catch:{ Exception -> 0x02ad }
            java.lang.Object r6 = r3.get(r4)     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ResolveInfo r6 = (android.content.pm.ResolveInfo) r6     // Catch:{ Exception -> 0x02ad }
            android.content.pm.ActivityInfo r6 = r6.activityInfo     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = r6.packageName     // Catch:{ Exception -> 0x02ad }
            r5.append(r6)     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = " to open "
            r5.append(r6)     // Catch:{ Exception -> 0x02ad }
            java.lang.String r6 = r8.toString()     // Catch:{ Exception -> 0x02ad }
            r5.append(r6)     // Catch:{ Exception -> 0x02ad }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x02ad }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x02ad }
            int r4 = r4 + 1
            goto L_0x0279
        L_0x02ac:
            goto L_0x02ae
        L_0x02ad:
            r0 = move-exception
        L_0x02ae:
            r4 = 0
            boolean r0 = r13[r4]     // Catch:{ Exception -> 0x0328 }
            if (r0 != 0) goto L_0x02bb
            if (r3 == 0) goto L_0x02bb
            boolean r0 = r3.isEmpty()     // Catch:{ Exception -> 0x0328 }
            if (r0 == 0) goto L_0x0327
        L_0x02bb:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0328 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0328 }
            java.lang.Class<org.telegram.messenger.ShareBroadcastReceiver> r5 = org.telegram.messenger.ShareBroadcastReceiver.class
            r0.<init>(r4, r5)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r4 = "android.intent.action.SEND"
            r0.setAction(r4)     // Catch:{ Exception -> 0x0328 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0328 }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x0328 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0328 }
            java.lang.Class<org.telegram.messenger.CustomTabsCopyReceiver> r11 = org.telegram.messenger.CustomTabsCopyReceiver.class
            r5.<init>(r6, r11)     // Catch:{ Exception -> 0x0328 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r11, r5, r6)     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder r5 = new org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.support.customtabs.CustomTabsSession r6 = getSession()     // Catch:{ Exception -> 0x0328 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r6 = "CopyLink"
            r11 = 2131625091(0x7f0e0483, float:1.887738E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r11)     // Catch:{ Exception -> 0x0328 }
            r5.addMenuItem(r6, r4)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r6 = "actionBarBrowser"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)     // Catch:{ Exception -> 0x0328 }
            r5.setToolbarColor(r6)     // Catch:{ Exception -> 0x0328 }
            r6 = 1
            r5.setShowTitle(r6)     // Catch:{ Exception -> 0x0328 }
            android.content.res.Resources r6 = r17.getResources()     // Catch:{ Exception -> 0x0328 }
            r11 = 2131165243(0x7var_b, float:1.7944698E38)
            android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeResource(r6, r11)     // Catch:{ Exception -> 0x0328 }
            java.lang.String r11 = "ShareFile"
            r12 = 2131627793(0x7f0e0var_, float:1.888286E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)     // Catch:{ Exception -> 0x0328 }
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0328 }
            r15 = 0
            android.app.PendingIntent r12 = android.app.PendingIntent.getBroadcast(r12, r15, r0, r15)     // Catch:{ Exception -> 0x0328 }
            r15 = 1
            r5.setActionButton(r6, r11, r12, r15)     // Catch:{ Exception -> 0x0328 }
            org.telegram.messenger.support.customtabs.CustomTabsIntent r6 = r5.build()     // Catch:{ Exception -> 0x0328 }
            r6.setUseNewTask()     // Catch:{ Exception -> 0x0328 }
            r6.launchUrl(r7, r8)     // Catch:{ Exception -> 0x0328 }
            return
        L_0x0327:
            goto L_0x032e
        L_0x0328:
            r0 = move-exception
            goto L_0x032b
        L_0x032a:
            r0 = move-exception
        L_0x032b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x032e:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x035a }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x035a }
            if (r14 == 0) goto L_0x0347
            android.content.ComponentName r1 = new android.content.ComponentName     // Catch:{ Exception -> 0x035a }
            java.lang.String r2 = r17.getPackageName()     // Catch:{ Exception -> 0x035a }
            java.lang.Class<org.telegram.ui.LaunchActivity> r3 = org.telegram.ui.LaunchActivity.class
            java.lang.String r3 = r3.getName()     // Catch:{ Exception -> 0x035a }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x035a }
            r0.setComponent(r1)     // Catch:{ Exception -> 0x035a }
        L_0x0347:
            java.lang.String r1 = "create_new_tab"
            r2 = 1
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x035a }
            java.lang.String r1 = "com.android.browser.application_id"
            java.lang.String r2 = r17.getPackageName()     // Catch:{ Exception -> 0x035a }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x035a }
            r7.startActivity(r0)     // Catch:{ Exception -> 0x035a }
            goto L_0x035e
        L_0x035a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x035e:
            return
        L_0x035f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$openUrl$0(AlertDialog[] progressDialog, TLObject response, int currentAccount, Uri finalUri, Context context, boolean allowCustom) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        boolean ok = false;
        if (response instanceof TLRPC.TL_messageMediaWebPage) {
            TLRPC.TL_messageMediaWebPage webPage = (TLRPC.TL_messageMediaWebPage) response;
            if ((webPage.webpage instanceof TLRPC.TL_webPage) && webPage.webpage.cached_page != null) {
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.openArticle, webPage.webpage, finalUri.toString());
                ok = true;
            }
        }
        if (!ok) {
            openUrl(context, finalUri, allowCustom, false);
        }
    }

    static /* synthetic */ void lambda$openUrl$3(AlertDialog[] progressDialog, int reqId) {
        if (progressDialog[0] != null) {
            try {
                progressDialog[0].setOnCancelListener(new Browser$$ExternalSyntheticLambda0(reqId));
                progressDialog[0].show();
            } catch (Exception e) {
            }
        }
    }

    public static boolean isInternalUrl(String url, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), false, forceBrowser);
    }

    public static boolean isInternalUrl(String url, boolean all, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), all, forceBrowser);
    }

    public static boolean isPassportUrl(String url) {
        if (url == null) {
            return false;
        }
        try {
            String url2 = url.toLowerCase();
            if (url2.startsWith("tg:passport") || url2.startsWith("tg://passport") || url2.startsWith("tg:secureid")) {
                return true;
            }
            if (!url2.contains("resolve") || !url2.contains("domain=telegrampassport")) {
                return false;
            }
            return true;
        } catch (Throwable th) {
        }
    }

    public static boolean isInternalUri(Uri uri, boolean[] forceBrowser) {
        return isInternalUri(uri, false, forceBrowser);
    }

    public static boolean isInternalUri(Uri uri, boolean all, boolean[] forceBrowser) {
        String host = uri.getHost();
        String host2 = host != null ? host.toLowerCase() : "";
        if ("ton".equals(uri.getScheme())) {
            try {
                List<ResolveInfo> allActivities = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri), 0);
                return allActivities == null || allActivities.size() <= 1;
            } catch (Exception e) {
            }
        } else if ("tg".equals(uri.getScheme())) {
            return true;
        } else {
            if ("telegram.dog".equals(host2)) {
                String path = uri.getPath();
                if (path != null && path.length() > 1) {
                    if (all) {
                        return true;
                    }
                    String path2 = path.substring(1).toLowerCase();
                    if (!path2.startsWith("blog") && !path2.equals("iv") && !path2.startsWith("faq") && !path2.equals("apps") && !path2.startsWith("s/")) {
                        return true;
                    }
                    if (forceBrowser != null) {
                        forceBrowser[0] = true;
                    }
                    return false;
                }
            } else if (!"telegram.me".equals(host2) && !"t.me".equals(host2)) {
                return all && (host2.endsWith("telegram.org") || host2.endsWith("telegra.ph") || host2.endsWith("telesco.pe"));
            } else {
                String path3 = uri.getPath();
                if (path3 != null && path3.length() > 1) {
                    if (all) {
                        return true;
                    }
                    String path4 = path3.substring(1).toLowerCase();
                    if (!path4.equals("iv") && !path4.startsWith("s/")) {
                        return true;
                    }
                    if (forceBrowser != null) {
                        forceBrowser[0] = true;
                    }
                    return false;
                }
            }
        }
    }
}

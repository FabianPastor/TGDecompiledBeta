package org.telegram.messenger.support.customtabsclient.shared;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.util.Log;
import java.util.List;

public class CustomTabsHelper {
    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
    static final String BETA_PACKAGE = "com.chrome.beta";
    static final String DEV_PACKAGE = "com.chrome.dev";
    private static final String EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";
    static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String TAG = "CustomTabsHelper";
    private static String sPackageNameToUse;

    private CustomTabsHelper() {
    }

    public static void addKeepAliveExtra(Context context, Intent intent) {
        intent.putExtra("android.support.customtabs.extra.KEEP_ALIVE", new Intent().setClassName(context.getPackageName(), KeepAliveService.class.getCanonicalName()));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00be, code lost:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext.getPackageManager();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getPackageNameToUse(android.content.Context r11) {
        /*
            java.lang.String r0 = sPackageNameToUse
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            android.content.pm.PackageManager r0 = r11.getPackageManager()
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "http://www.example.com"
            android.net.Uri r2 = android.net.Uri.parse(r2)
            java.lang.String r3 = "android.intent.action.VIEW"
            r1.<init>(r3, r2)
            r2 = 0
            android.content.pm.ResolveInfo r3 = r0.resolveActivity(r1, r2)
            r4 = 0
            if (r3 == 0) goto L_0x0022
            android.content.pm.ActivityInfo r5 = r3.activityInfo
            java.lang.String r4 = r5.packageName
        L_0x0022:
            java.util.List r5 = r0.queryIntentActivities(r1, r2)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.Iterator r7 = r5.iterator()
        L_0x002f:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x005a
            java.lang.Object r8 = r7.next()
            android.content.pm.ResolveInfo r8 = (android.content.pm.ResolveInfo) r8
            android.content.Intent r9 = new android.content.Intent
            r9.<init>()
            java.lang.String r10 = "android.support.customtabs.action.CustomTabsService"
            r9.setAction(r10)
            android.content.pm.ActivityInfo r10 = r8.activityInfo
            java.lang.String r10 = r10.packageName
            r9.setPackage(r10)
            android.content.pm.ResolveInfo r10 = r0.resolveService(r9, r2)
            if (r10 == 0) goto L_0x0059
            android.content.pm.ActivityInfo r10 = r8.activityInfo
            java.lang.String r10 = r10.packageName
            r6.add(r10)
        L_0x0059:
            goto L_0x002f
        L_0x005a:
            boolean r7 = r6.isEmpty()
            r8 = 1
            java.lang.String r9 = "com.android.chrome"
            if (r7 == 0) goto L_0x0067
            r7 = 0
            sPackageNameToUse = r7
            goto L_0x00b4
        L_0x0067:
            int r7 = r6.size()
            if (r7 != r8) goto L_0x0076
            java.lang.Object r7 = r6.get(r2)
            java.lang.String r7 = (java.lang.String) r7
            sPackageNameToUse = r7
            goto L_0x00b4
        L_0x0076:
            boolean r7 = android.text.TextUtils.isEmpty(r4)
            if (r7 != 0) goto L_0x008b
            boolean r7 = hasSpecializedHandlerIntents(r11, r1)
            if (r7 != 0) goto L_0x008b
            boolean r7 = r6.contains(r4)
            if (r7 == 0) goto L_0x008b
            sPackageNameToUse = r4
            goto L_0x00b4
        L_0x008b:
            boolean r7 = r6.contains(r9)
            if (r7 == 0) goto L_0x0094
            sPackageNameToUse = r9
            goto L_0x00b4
        L_0x0094:
            java.lang.String r7 = "com.chrome.beta"
            boolean r10 = r6.contains(r7)
            if (r10 == 0) goto L_0x009f
            sPackageNameToUse = r7
            goto L_0x00b4
        L_0x009f:
            java.lang.String r7 = "com.chrome.dev"
            boolean r10 = r6.contains(r7)
            if (r10 == 0) goto L_0x00aa
            sPackageNameToUse = r7
            goto L_0x00b4
        L_0x00aa:
            java.lang.String r7 = "com.google.android.apps.chrome"
            boolean r10 = r6.contains(r7)
            if (r10 == 0) goto L_0x00b4
            sPackageNameToUse = r7
        L_0x00b4:
            java.lang.String r7 = "com.sec.android.app.sbrowser"
            java.lang.String r10 = sPackageNameToUse     // Catch:{ all -> 0x00d6 }
            boolean r7 = r7.equalsIgnoreCase(r10)     // Catch:{ all -> 0x00d6 }
            if (r7 == 0) goto L_0x00d5
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00d6 }
            android.content.pm.PackageManager r7 = r7.getPackageManager()     // Catch:{ all -> 0x00d6 }
            r0 = r7
            android.content.pm.ApplicationInfo r2 = r0.getApplicationInfo(r9, r2)     // Catch:{ all -> 0x00d6 }
            if (r2 == 0) goto L_0x00d5
            boolean r7 = r2.enabled     // Catch:{ all -> 0x00d6 }
            if (r7 == 0) goto L_0x00d5
            android.content.pm.PackageInfo r7 = r0.getPackageInfo(r9, r8)     // Catch:{ all -> 0x00d6 }
            sPackageNameToUse = r9     // Catch:{ all -> 0x00d6 }
        L_0x00d5:
            goto L_0x00d7
        L_0x00d6:
            r2 = move-exception
        L_0x00d7:
            java.lang.String r2 = sPackageNameToUse
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper.getPackageNameToUse(android.content.Context):java.lang.String");
    }

    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            List<ResolveInfo> handlers = context.getPackageManager().queryIntentActivities(intent, 64);
            if (handlers != null) {
                if (handlers.size() != 0) {
                    for (ResolveInfo resolveInfo : handlers) {
                        IntentFilter filter = resolveInfo.filter;
                        if (filter != null) {
                            if (filter.countDataAuthorities() == 0) {
                                continue;
                            } else if (filter.countDataPaths() != 0) {
                                if (resolveInfo.activityInfo != null) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            }
            return false;
        } catch (RuntimeException e) {
            Log.e("CustomTabsHelper", "Runtime exception while getting specialized handlers");
        }
    }

    public static String[] getPackages() {
        return new String[]{"", "com.android.chrome", "com.chrome.beta", "com.chrome.dev", "com.google.android.apps.chrome"};
    }
}

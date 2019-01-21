package org.telegram.messenger.support.customtabsclient.shared;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;

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

    public static String getPackageNameToUse(Context context) {
        if (sPackageNameToUse != null) {
            return sPackageNameToUse;
        }
        PackageManager pm = context.getPackageManager();
        Intent activityIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.example.com"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("android.support.customtabs.action.CustomTabsService");
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }
        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            sPackageNameToUse = (String) packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName) && !hasSpecializedHandlerIntents(context, activityIntent) && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains("com.android.chrome")) {
            sPackageNameToUse = "com.android.chrome";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.beta")) {
            sPackageNameToUse = "com.chrome.beta";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.dev")) {
            sPackageNameToUse = "com.chrome.dev";
        } else if (packagesSupportingCustomTabs.contains("com.google.android.apps.chrome")) {
            sPackageNameToUse = "com.google.android.apps.chrome";
        }
        try {
            if ("com.sec.android.app.sbrowser".equalsIgnoreCase(sPackageNameToUse)) {
                pm = ApplicationLoader.applicationContext.getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.android.chrome", 0);
                if (applicationInfo != null && applicationInfo.enabled) {
                    PackageInfo packageInfo = pm.getPackageInfo("com.android.chrome", 1);
                    sPackageNameToUse = "com.android.chrome";
                }
            }
        } catch (Throwable th) {
        }
        return sPackageNameToUse;
    }

    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            List<ResolveInfo> handlers = context.getPackageManager().queryIntentActivities(intent, 64);
            if (handlers == null || handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter != null && filter.countDataAuthorities() != 0 && filter.countDataPaths() != 0 && resolveInfo.activityInfo != null) {
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            Log.e("CustomTabsHelper", "Runtime exception while getting specialized handlers");
            return false;
        }
    }

    public static String[] getPackages() {
        return new String[]{"", "com.android.chrome", "com.chrome.beta", "com.chrome.dev", "com.google.android.apps.chrome"};
    }
}

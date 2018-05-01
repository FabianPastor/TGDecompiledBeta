package org.telegram.messenger.support.customtabsclient.shared;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import java.util.ArrayList;
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
        intent.putExtra(EXTRA_CUSTOM_TABS_KEEP_ALIVE, new Intent().setClassName(context.getPackageName(), KeepAliveService.class.getCanonicalName()));
    }

    public static String getPackageNameToUse(Context context) {
        if (sPackageNameToUse != null) {
            return sPackageNameToUse;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.example.com"));
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
        Object obj = resolveActivity != null ? resolveActivity.activityInfo.packageName : null;
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        List arrayList = new ArrayList();
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            Intent intent2 = new Intent();
            intent2.setAction("android.support.customtabs.action.CustomTabsService");
            intent2.setPackage(resolveInfo.activityInfo.packageName);
            if (packageManager.resolveService(intent2, 0) != null) {
                arrayList.add(resolveInfo.activityInfo.packageName);
            }
        }
        if (arrayList.isEmpty()) {
            sPackageNameToUse = null;
        } else if (arrayList.size() == 1) {
            sPackageNameToUse = (String) arrayList.get(0);
        } else if (!TextUtils.isEmpty(obj) && hasSpecializedHandlerIntents(context, intent) == null && arrayList.contains(obj) != null) {
            sPackageNameToUse = obj;
        } else if (arrayList.contains(STABLE_PACKAGE) != null) {
            sPackageNameToUse = STABLE_PACKAGE;
        } else if (arrayList.contains(BETA_PACKAGE) != null) {
            sPackageNameToUse = BETA_PACKAGE;
        } else if (arrayList.contains(DEV_PACKAGE) != null) {
            sPackageNameToUse = DEV_PACKAGE;
        } else if (arrayList.contains(LOCAL_PACKAGE) != null) {
            sPackageNameToUse = LOCAL_PACKAGE;
        }
        return sPackageNameToUse;
    }

    private static boolean hasSpecializedHandlerIntents(android.content.Context r3, android.content.Intent r4) {
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
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 0;
        r3 = r3.getPackageManager();	 Catch:{ RuntimeException -> 0x003e }
        r1 = 64;	 Catch:{ RuntimeException -> 0x003e }
        r3 = r3.queryIntentActivities(r4, r1);	 Catch:{ RuntimeException -> 0x003e }
        if (r3 == 0) goto L_0x003d;	 Catch:{ RuntimeException -> 0x003e }
    L_0x000d:
        r4 = r3.size();	 Catch:{ RuntimeException -> 0x003e }
        if (r4 != 0) goto L_0x0014;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0013:
        goto L_0x003d;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0014:
        r3 = r3.iterator();	 Catch:{ RuntimeException -> 0x003e }
    L_0x0018:
        r4 = r3.hasNext();	 Catch:{ RuntimeException -> 0x003e }
        if (r4 == 0) goto L_0x0045;	 Catch:{ RuntimeException -> 0x003e }
    L_0x001e:
        r4 = r3.next();	 Catch:{ RuntimeException -> 0x003e }
        r4 = (android.content.pm.ResolveInfo) r4;	 Catch:{ RuntimeException -> 0x003e }
        r1 = r4.filter;	 Catch:{ RuntimeException -> 0x003e }
        if (r1 != 0) goto L_0x0029;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0028:
        goto L_0x0018;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0029:
        r2 = r1.countDataAuthorities();	 Catch:{ RuntimeException -> 0x003e }
        if (r2 == 0) goto L_0x0018;	 Catch:{ RuntimeException -> 0x003e }
    L_0x002f:
        r1 = r1.countDataPaths();	 Catch:{ RuntimeException -> 0x003e }
        if (r1 != 0) goto L_0x0036;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0035:
        goto L_0x0018;	 Catch:{ RuntimeException -> 0x003e }
    L_0x0036:
        r4 = r4.activityInfo;	 Catch:{ RuntimeException -> 0x003e }
        if (r4 != 0) goto L_0x003b;
    L_0x003a:
        goto L_0x0018;
    L_0x003b:
        r3 = 1;
        return r3;
    L_0x003d:
        return r0;
    L_0x003e:
        r3 = "CustomTabsHelper";
        r4 = "Runtime exception while getting specialized handlers";
        android.util.Log.e(r3, r4);
    L_0x0045:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper.hasSpecializedHandlerIntents(android.content.Context, android.content.Intent):boolean");
    }

    public static String[] getPackages() {
        return new String[]{TtmlNode.ANONYMOUS_REGION_ID, STABLE_PACKAGE, BETA_PACKAGE, DEV_PACKAGE, LOCAL_PACKAGE};
    }
}

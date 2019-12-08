package org.telegram.messenger.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    private static class NavigationCallback extends CustomTabsCallback {
        public void onNavigationEvent(int i, Bundle bundle) {
        }

        private NavigationCallback() {
        }

        /* synthetic */ NavigationCallback(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private static CustomTabsSession getCurrentSession() {
        WeakReference weakReference = customTabsCurrentSession;
        return weakReference == null ? null : (CustomTabsSession) weakReference.get();
    }

    private static void setCurrentSession(CustomTabsSession customTabsSession) {
        customTabsCurrentSession = new WeakReference(customTabsSession);
    }

    private static CustomTabsSession getSession() {
        CustomTabsClient customTabsClient = customTabsClient;
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        WeakReference weakReference = currentCustomTabsActivity;
        Activity activity2 = weakReference == null ? null : (Activity) weakReference.get();
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
                customTabsServiceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                    public void onServiceConnected(CustomTabsClient customTabsClient) {
                        Browser.customTabsClient = customTabsClient;
                        if (SharedConfig.customTabs && Browser.customTabsClient != null) {
                            try {
                                Browser.customTabsClient.warmup(0);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }

                    public void onServiceDisconnected() {
                        Browser.customTabsClient = null;
                    }
                });
                if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                    customTabsServiceConnection = null;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (customTabsServiceConnection != null) {
            WeakReference weakReference = currentCustomTabsActivity;
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

    /* JADX WARNING: Removed duplicated region for block: B:65:0x013a A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0111 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0179 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0081 A:{Catch:{ Exception -> 0x0222 }} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0078 A:{Catch:{ Exception -> 0x0222 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d8 A:{Catch:{ Exception -> 0x0102 }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0111 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x013a A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0179 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x022d A:{Catch:{ Exception -> 0x0252 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0072 */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x013a A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0111 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0179 A:{Catch:{ Exception -> 0x01ad }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x0102 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:81:0x01ad */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Can't wrap try/catch for region: R(12:34|(3:35|36|(3:40|41|(11:42|43|(3:45|(2:47|99)(1:100)|48)|51|52|(3:54|(5:57|(3:60|(3:104|62|103)(1:63)|58)|102|64|55)|101)(3:65|(4:68|(2:72|106)(1:107)|73|66)|105)|74|(2:76|(3:79|80|77))|81|82|(2:87|88))))|49|51|52|(0)(0)|74|(0)|81|82|87|88) */
    public static void openUrl(android.content.Context r16, android.net.Uri r17, boolean r18, boolean r19) {
        /*
        r7 = r16;
        r8 = r17;
        r9 = "android.intent.action.VIEW";
        if (r7 == 0) goto L_0x0256;
    L_0x0008:
        if (r8 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0256;
    L_0x000c:
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        r10 = 1;
        r11 = new boolean[r10];
        r12 = 0;
        r11[r12] = r12;
        r13 = isInternalUri(r8, r11);
        if (r19 == 0) goto L_0x0072;
    L_0x001a:
        r0 = r17.getHost();	 Catch:{ Exception -> 0x0072 }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0072 }
        r1 = "telegra.ph";
        r0 = r0.equals(r1);	 Catch:{ Exception -> 0x0072 }
        if (r0 != 0) goto L_0x003a;
    L_0x002a:
        r0 = r17.toString();	 Catch:{ Exception -> 0x0072 }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0072 }
        r1 = "telegram.org/faq";
        r0 = r0.contains(r1);	 Catch:{ Exception -> 0x0072 }
        if (r0 == 0) goto L_0x0072;
    L_0x003a:
        r0 = new org.telegram.ui.ActionBar.AlertDialog[r10];	 Catch:{ Exception -> 0x0072 }
        r1 = new org.telegram.ui.ActionBar.AlertDialog;	 Catch:{ Exception -> 0x0072 }
        r2 = 3;
        r1.<init>(r7, r2);	 Catch:{ Exception -> 0x0072 }
        r0[r12] = r1;	 Catch:{ Exception -> 0x0072 }
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPagePreview;	 Catch:{ Exception -> 0x0072 }
        r14.<init>();	 Catch:{ Exception -> 0x0072 }
        r1 = r17.toString();	 Catch:{ Exception -> 0x0072 }
        r14.message = r1;	 Catch:{ Exception -> 0x0072 }
        r1 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Exception -> 0x0072 }
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x0072 }
        r6 = new org.telegram.messenger.browser.-$$Lambda$Browser$UGNHp8wusapZ_BRGXGaD_Ayz4-I;	 Catch:{ Exception -> 0x0072 }
        r1 = r6;
        r2 = r0;
        r4 = r17;
        r5 = r16;
        r10 = r6;
        r6 = r18;
        r1.<init>(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0072 }
        r1 = r15.sendRequest(r14, r10);	 Catch:{ Exception -> 0x0072 }
        r2 = new org.telegram.messenger.browser.-$$Lambda$Browser$tzkgx-C1l2oH_szh01yqocI6uLg;	 Catch:{ Exception -> 0x0072 }
        r2.<init>(r0, r1);	 Catch:{ Exception -> 0x0072 }
        r0 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r0);	 Catch:{ Exception -> 0x0072 }
        return;
    L_0x0072:
        r0 = r17.getScheme();	 Catch:{ Exception -> 0x0222 }
        if (r0 == 0) goto L_0x0081;
    L_0x0078:
        r0 = r17.getScheme();	 Catch:{ Exception -> 0x0222 }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0222 }
        goto L_0x0083;
    L_0x0081:
        r0 = "";
    L_0x0083:
        r1 = r0;
        r0 = "http";
        r0 = r0.equals(r1);	 Catch:{ Exception -> 0x0222 }
        if (r0 != 0) goto L_0x0094;
    L_0x008c:
        r0 = "https";
        r0 = r0.equals(r1);	 Catch:{ Exception -> 0x0222 }
        if (r0 == 0) goto L_0x009f;
    L_0x0094:
        r0 = r17.normalizeScheme();	 Catch:{ Exception -> 0x009a }
        r8 = r0;
        goto L_0x009f;
    L_0x009a:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x0222 }
    L_0x009f:
        if (r18 == 0) goto L_0x0226;
    L_0x00a1:
        r0 = org.telegram.messenger.SharedConfig.customTabs;	 Catch:{ Exception -> 0x0222 }
        if (r0 == 0) goto L_0x0226;
    L_0x00a5:
        if (r13 != 0) goto L_0x0226;
    L_0x00a7:
        r0 = "tel";
        r0 = r1.equals(r0);	 Catch:{ Exception -> 0x0222 }
        if (r0 != 0) goto L_0x0226;
    L_0x00af:
        r0 = 0;
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0101 }
        r2 = "http://www.google.com";
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0101 }
        r1.<init>(r9, r2);	 Catch:{ Exception -> 0x0101 }
        r2 = r16.getPackageManager();	 Catch:{ Exception -> 0x0101 }
        r1 = r2.queryIntentActivities(r1, r12);	 Catch:{ Exception -> 0x0101 }
        if (r1 == 0) goto L_0x0101;
    L_0x00c5:
        r2 = r1.isEmpty();	 Catch:{ Exception -> 0x0101 }
        if (r2 != 0) goto L_0x0101;
    L_0x00cb:
        r2 = r1.size();	 Catch:{ Exception -> 0x0101 }
        r2 = new java.lang.String[r2];	 Catch:{ Exception -> 0x0101 }
        r3 = 0;
    L_0x00d2:
        r4 = r1.size();	 Catch:{ Exception -> 0x0102 }
        if (r3 >= r4) goto L_0x0102;
    L_0x00d8:
        r4 = r1.get(r3);	 Catch:{ Exception -> 0x0102 }
        r4 = (android.content.pm.ResolveInfo) r4;	 Catch:{ Exception -> 0x0102 }
        r4 = r4.activityInfo;	 Catch:{ Exception -> 0x0102 }
        r4 = r4.packageName;	 Catch:{ Exception -> 0x0102 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x0102 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0102 }
        if (r4 == 0) goto L_0x00fe;
    L_0x00e8:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0102 }
        r4.<init>();	 Catch:{ Exception -> 0x0102 }
        r5 = "default browser name = ";
        r4.append(r5);	 Catch:{ Exception -> 0x0102 }
        r5 = r2[r3];	 Catch:{ Exception -> 0x0102 }
        r4.append(r5);	 Catch:{ Exception -> 0x0102 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0102 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x0102 }
    L_0x00fe:
        r3 = r3 + 1;
        goto L_0x00d2;
    L_0x0101:
        r2 = r0;
    L_0x0102:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x01ad }
        r1.<init>(r9, r8);	 Catch:{ Exception -> 0x01ad }
        r3 = r16.getPackageManager();	 Catch:{ Exception -> 0x01ad }
        r0 = r3.queryIntentActivities(r1, r12);	 Catch:{ Exception -> 0x01ad }
        if (r2 == 0) goto L_0x013a;
    L_0x0111:
        r1 = 0;
    L_0x0112:
        r3 = r0.size();	 Catch:{ Exception -> 0x01ad }
        if (r1 >= r3) goto L_0x0175;
    L_0x0118:
        r3 = 0;
    L_0x0119:
        r4 = r2.length;	 Catch:{ Exception -> 0x01ad }
        if (r3 >= r4) goto L_0x0137;
    L_0x011c:
        r4 = r2[r3];	 Catch:{ Exception -> 0x01ad }
        r5 = r0.get(r1);	 Catch:{ Exception -> 0x01ad }
        r5 = (android.content.pm.ResolveInfo) r5;	 Catch:{ Exception -> 0x01ad }
        r5 = r5.activityInfo;	 Catch:{ Exception -> 0x01ad }
        r5 = r5.packageName;	 Catch:{ Exception -> 0x01ad }
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x01ad }
        if (r4 == 0) goto L_0x0134;
    L_0x012e:
        r0.remove(r1);	 Catch:{ Exception -> 0x01ad }
        r1 = r1 + -1;
        goto L_0x0137;
    L_0x0134:
        r3 = r3 + 1;
        goto L_0x0119;
    L_0x0137:
        r3 = 1;
        r1 = r1 + r3;
        goto L_0x0112;
    L_0x013a:
        r1 = 0;
    L_0x013b:
        r2 = r0.size();	 Catch:{ Exception -> 0x01ad }
        if (r1 >= r2) goto L_0x0175;
    L_0x0141:
        r2 = r0.get(r1);	 Catch:{ Exception -> 0x01ad }
        r2 = (android.content.pm.ResolveInfo) r2;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.activityInfo;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.packageName;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x01ad }
        r3 = "browser";
        r2 = r2.contains(r3);	 Catch:{ Exception -> 0x01ad }
        if (r2 != 0) goto L_0x016d;
    L_0x0157:
        r2 = r0.get(r1);	 Catch:{ Exception -> 0x01ad }
        r2 = (android.content.pm.ResolveInfo) r2;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.activityInfo;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.packageName;	 Catch:{ Exception -> 0x01ad }
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x01ad }
        r3 = "chrome";
        r2 = r2.contains(r3);	 Catch:{ Exception -> 0x01ad }
        if (r2 == 0) goto L_0x0172;
    L_0x016d:
        r0.remove(r1);	 Catch:{ Exception -> 0x01ad }
        r1 = r1 + -1;
    L_0x0172:
        r2 = 1;
        r1 = r1 + r2;
        goto L_0x013b;
    L_0x0175:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x01ad }
        if (r1 == 0) goto L_0x01ad;
    L_0x0179:
        r1 = 0;
    L_0x017a:
        r2 = r0.size();	 Catch:{ Exception -> 0x01ad }
        if (r1 >= r2) goto L_0x01ad;
    L_0x0180:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01ad }
        r2.<init>();	 Catch:{ Exception -> 0x01ad }
        r3 = "device has ";
        r2.append(r3);	 Catch:{ Exception -> 0x01ad }
        r3 = r0.get(r1);	 Catch:{ Exception -> 0x01ad }
        r3 = (android.content.pm.ResolveInfo) r3;	 Catch:{ Exception -> 0x01ad }
        r3 = r3.activityInfo;	 Catch:{ Exception -> 0x01ad }
        r3 = r3.packageName;	 Catch:{ Exception -> 0x01ad }
        r2.append(r3);	 Catch:{ Exception -> 0x01ad }
        r3 = " to open ";
        r2.append(r3);	 Catch:{ Exception -> 0x01ad }
        r3 = r8.toString();	 Catch:{ Exception -> 0x01ad }
        r2.append(r3);	 Catch:{ Exception -> 0x01ad }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01ad }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x01ad }
        r1 = r1 + 1;
        goto L_0x017a;
    L_0x01ad:
        r1 = r11[r12];	 Catch:{ Exception -> 0x0222 }
        if (r1 != 0) goto L_0x01b9;
    L_0x01b1:
        if (r0 == 0) goto L_0x01b9;
    L_0x01b3:
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x0222 }
        if (r0 == 0) goto L_0x0226;
    L_0x01b9:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x0222 }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0222 }
        r2 = org.telegram.messenger.ShareBroadcastReceiver.class;
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x0222 }
        r1 = "android.intent.action.SEND";
        r0.setAction(r1);	 Catch:{ Exception -> 0x0222 }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0222 }
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0222 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0222 }
        r4 = org.telegram.messenger.CustomTabsCopyReceiver.class;
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x0222 }
        r3 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r1 = android.app.PendingIntent.getBroadcast(r1, r12, r2, r3);	 Catch:{ Exception -> 0x0222 }
        r2 = new org.telegram.messenger.support.customtabs.CustomTabsIntent$Builder;	 Catch:{ Exception -> 0x0222 }
        r3 = getSession();	 Catch:{ Exception -> 0x0222 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0222 }
        r3 = "CopyLink";
        r4 = NUM; // 0x7f0e0335 float:1.8876703E38 double:1.0531625623E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0222 }
        r2.addMenuItem(r3, r1);	 Catch:{ Exception -> 0x0222 }
        r1 = "actionBarBrowser";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);	 Catch:{ Exception -> 0x0222 }
        r2.setToolbarColor(r1);	 Catch:{ Exception -> 0x0222 }
        r1 = 1;
        r2.setShowTitle(r1);	 Catch:{ Exception -> 0x0222 }
        r1 = r16.getResources();	 Catch:{ Exception -> 0x0222 }
        r3 = NUM; // 0x7var_b float:1.7944698E38 double:1.052935532E-314;
        r1 = android.graphics.BitmapFactory.decodeResource(r1, r3);	 Catch:{ Exception -> 0x0222 }
        r3 = "ShareFile";
        r4 = NUM; // 0x7f0e0a25 float:1.8880305E38 double:1.0531634397E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0222 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0222 }
        r0 = android.app.PendingIntent.getBroadcast(r4, r12, r0, r12);	 Catch:{ Exception -> 0x0222 }
        r2.setActionButton(r1, r3, r0, r12);	 Catch:{ Exception -> 0x0222 }
        r0 = r2.build();	 Catch:{ Exception -> 0x0222 }
        r0.setUseNewTask();	 Catch:{ Exception -> 0x0222 }
        r0.launchUrl(r7, r8);	 Catch:{ Exception -> 0x0222 }
        return;
    L_0x0222:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0226:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x0252 }
        r0.<init>(r9, r8);	 Catch:{ Exception -> 0x0252 }
        if (r13 == 0) goto L_0x023f;
    L_0x022d:
        r1 = new android.content.ComponentName;	 Catch:{ Exception -> 0x0252 }
        r2 = r16.getPackageName();	 Catch:{ Exception -> 0x0252 }
        r3 = org.telegram.ui.LaunchActivity.class;
        r3 = r3.getName();	 Catch:{ Exception -> 0x0252 }
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0252 }
        r0.setComponent(r1);	 Catch:{ Exception -> 0x0252 }
    L_0x023f:
        r1 = "create_new_tab";
        r2 = 1;
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x0252 }
        r1 = "com.android.browser.application_id";
        r2 = r16.getPackageName();	 Catch:{ Exception -> 0x0252 }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x0252 }
        r7.startActivity(r0);	 Catch:{ Exception -> 0x0252 }
        goto L_0x0256;
    L_0x0252:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0256:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
    static /* synthetic */ void lambda$null$0(org.telegram.ui.ActionBar.AlertDialog[] r3, org.telegram.tgnet.TLObject r4, int r5, android.net.Uri r6, android.content.Context r7, boolean r8) {
        /*
        r0 = 0;
        r1 = r3[r0];	 Catch:{ all -> 0x0007 }
        r1.dismiss();	 Catch:{ all -> 0x0007 }
        goto L_0x0008;
    L_0x0008:
        r1 = 0;
        r3[r0] = r1;
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        r1 = 1;
        if (r3 == 0) goto L_0x0033;
    L_0x0010:
        r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaWebPage) r4;
        r3 = r4.webpage;
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r2 == 0) goto L_0x0033;
    L_0x0018:
        r3 = r3.cached_page;
        if (r3 == 0) goto L_0x0033;
    L_0x001c:
        r3 = org.telegram.messenger.NotificationCenter.getInstance(r5);
        r5 = org.telegram.messenger.NotificationCenter.openArticle;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r4 = r4.webpage;
        r2[r0] = r4;
        r4 = r6.toString();
        r2[r1] = r4;
        r3.postNotificationName(r5, r2);
        goto L_0x0034;
    L_0x0033:
        r1 = 0;
    L_0x0034:
        if (r1 != 0) goto L_0x0039;
    L_0x0036:
        openUrl(r7, r6, r8, r0);
    L_0x0039:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.lambda$null$0(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, int, android.net.Uri, android.content.Context, boolean):void");
    }

    static /* synthetic */ void lambda$openUrl$3(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            try {
                alertDialogArr[0].setOnCancelListener(new -$$Lambda$Browser$gkq8pxwFDBSthMGmf2aFQ3i1KTE(i));
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
            str = str.toLowerCase();
            if (str.startsWith("tg:passport") || str.startsWith("tg://passport") || str.startsWith("tg:secureid") || (str.contains("resolve") && str.contains("domain=telegrampassport"))) {
                return true;
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    public static boolean isInternalUri(Uri uri, boolean[] zArr) {
        String host = uri.getHost();
        Object toLowerCase = host != null ? host.toLowerCase() : "";
        if ("ton".equals(uri.getScheme())) {
            try {
                List queryIntentActivities = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri), 0);
                return queryIntentActivities == null || queryIntentActivities.size() <= 1;
            } catch (Exception unused) {
            }
        } else {
            if ("tg".equals(uri.getScheme())) {
                return true;
            }
            String str = "s/";
            String str2 = "iv";
            String path;
            if ("telegram.dog".equals(toLowerCase)) {
                path = uri.getPath();
                if (path != null && path.length() > 1) {
                    path = path.substring(1).toLowerCase();
                    if (!path.startsWith("blog") && !path.equals(str2) && !path.startsWith("faq") && !path.equals("apps") && !path.startsWith(str)) {
                        return true;
                    }
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                    return false;
                }
            } else if ("telegram.me".equals(toLowerCase) || "t.me".equals(toLowerCase)) {
                path = uri.getPath();
                if (path != null && path.length() > 1) {
                    path = path.substring(1).toLowerCase();
                    if (!path.equals(str2) && !path.startsWith(str)) {
                        return true;
                    }
                    if (zArr != null) {
                        zArr[0] = true;
                    }
                }
            }
            return false;
        }
    }
}

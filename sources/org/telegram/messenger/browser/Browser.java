package org.telegram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CustomTabsCopyReceiver;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.ShareBroadcastReceiver;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsIntent;
import org.telegram.messenger.support.customtabs.CustomTabsIntent.Builder;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    /* renamed from: org.telegram.messenger.browser.Browser$1 */
    static class C18251 implements ServiceConnectionCallback {
        C18251() {
        }

        public void onServiceConnected(CustomTabsClient client) {
            Browser.customTabsClient = client;
            if (SharedConfig.customTabs && Browser.customTabsClient != null) {
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
        private NavigationCallback() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
        }
    }

    private static CustomTabsSession getCurrentSession() {
        return customTabsCurrentSession == null ? null : (CustomTabsSession) customTabsCurrentSession.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference(session);
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
        Activity currentActivity = currentCustomTabsActivity == null ? null : (Activity) currentCustomTabsActivity.get();
        if (!(currentActivity == null || currentActivity == activity)) {
            unbindCustomTabsService(currentActivity);
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
                customTabsServiceConnection = new ServiceConnection(new C18251());
                if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                    customTabsServiceConnection = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (customTabsServiceConnection != null) {
            if ((currentCustomTabsActivity == null ? null : (Activity) currentCustomTabsActivity.get()) == activity) {
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

    public static void openUrl(Context context, String url) {
        if (url != null) {
            openUrl(context, Uri.parse(url), true);
        }
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context != null) {
            if (url != null) {
                openUrl(context, Uri.parse(url), allowCustom);
            }
        }
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        openUrl(context, uri, allowCustom, true);
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom, boolean tryTelegraph) {
        String scheme;
        List<ResolveInfo> allActivities;
        String[] browserPackageNames;
        List<ResolveInfo> list;
        StringBuilder stringBuilder;
        int a;
        StringBuilder stringBuilder2;
        Intent share;
        PendingIntent copy;
        Builder builder;
        CustomTabsIntent intent;
        Context context2 = context;
        Uri uri2 = uri;
        if (context2 != null) {
            if (uri2 != null) {
                int currentAccount = UserConfig.selectedAccount;
                boolean[] forceBrowser = new boolean[]{false};
                boolean internalUri = isInternalUri(uri2, forceBrowser);
                if (tryTelegraph) {
                    int i;
                    try {
                        if (!uri.getHost().toLowerCase().equals("telegra.ph")) {
                            try {
                                if (!uri.toString().toLowerCase().contains("telegram.org/faq")) {
                                    i = currentAccount;
                                }
                            } catch (Exception e) {
                                i = currentAccount;
                                scheme = uri.getScheme() == null ? TtmlNode.ANONYMOUS_REGION_ID : uri.getScheme().toLowerCase();
                                allActivities = null;
                                browserPackageNames = null;
                                try {
                                    list = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com")), 0);
                                    browserPackageNames = new String[list.size()];
                                    for (currentAccount = 0; currentAccount < list.size(); currentAccount++) {
                                        browserPackageNames[currentAccount] = ((ResolveInfo) list.get(currentAccount)).activityInfo.packageName;
                                        if (!BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("default browser name = ");
                                            stringBuilder.append(browserPackageNames[currentAccount]);
                                            FileLog.m0d(stringBuilder.toString());
                                        }
                                    }
                                } catch (Exception e2) {
                                }
                                try {
                                    allActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri2), 0);
                                    if (browserPackageNames == null) {
                                        a = 0;
                                        while (a < allActivities.size()) {
                                            if (!((ResolveInfo) allActivities.get(a)).activityInfo.packageName.toLowerCase().contains("browser")) {
                                            }
                                            allActivities.remove(a);
                                            a--;
                                            a++;
                                        }
                                    } else {
                                        a = 0;
                                        while (a < allActivities.size()) {
                                            for (String equals : browserPackageNames) {
                                                if (!equals.equals(((ResolveInfo) allActivities.get(a)).activityInfo.packageName)) {
                                                    allActivities.remove(a);
                                                    a--;
                                                    break;
                                                }
                                            }
                                            a++;
                                        }
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        for (a = 0; a < allActivities.size(); a++) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("device has ");
                                            stringBuilder2.append(((ResolveInfo) allActivities.get(a)).activityInfo.packageName);
                                            stringBuilder2.append(" to open ");
                                            stringBuilder2.append(uri.toString());
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                    }
                                } catch (Exception e3) {
                                }
                                share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                                share.setAction("android.intent.action.SEND");
                                copy = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, CustomTabsCopyReceiver.class), 134217728);
                                builder = new Builder(getSession());
                                builder.addMenuItem(LocaleController.getString("CopyLink", R.string.CopyLink), copy);
                                builder.setToolbarColor(Theme.getColor(Theme.key_actionBarDefault));
                                builder.setShowTitle(true);
                                builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0), false);
                                intent = builder.build();
                                intent.setUseNewTask();
                                intent.launchUrl(context2, uri2);
                                return;
                            }
                        }
                        AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(context2, 1)};
                        TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
                        req.message = uri.toString();
                        C18262 c18262 = c18262;
                        final AlertDialog[] alertDialogArr = progressDialog;
                        C18262 c182622 = c18262;
                        final int i2 = currentAccount;
                        ConnectionsManager instance = ConnectionsManager.getInstance(UserConfig.selectedAccount);
                        final Uri uri3 = uri2;
                        currentAccount = req;
                        final Context context3 = context2;
                        final AlertDialog[] progressDialog2 = progressDialog;
                        progressDialog = allowCustom;
                        try {
                            c18262 = new RequestDelegate() {
                                public void run(final TLObject response, TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            try {
                                                alertDialogArr[0].dismiss();
                                            } catch (Throwable th) {
                                            }
                                            alertDialogArr[0] = null;
                                            boolean ok = false;
                                            if (response instanceof TL_messageMediaWebPage) {
                                                TL_messageMediaWebPage webPage = response;
                                                if ((webPage.webpage instanceof TL_webPage) && webPage.webpage.cached_page != null) {
                                                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.openArticle, webPage.webpage, uri3.toString());
                                                    ok = true;
                                                }
                                            }
                                            if (!ok) {
                                                Browser.openUrl(context3, uri3, progressDialog, false);
                                            }
                                        }
                                    });
                                }
                            };
                            final int reqId = instance.sendRequest(currentAccount, c182622);
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.browser.Browser$3$1 */
                                class C05201 implements OnClickListener {
                                    C05201() {
                                    }

                                    public void onClick(DialogInterface dialog, int which) {
                                        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(reqId, true);
                                        try {
                                            dialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                }

                                public void run() {
                                    if (progressDialog2[0] != null) {
                                        try {
                                            progressDialog2[0].setMessage(LocaleController.getString("Loading", R.string.Loading));
                                            progressDialog2[0].setCanceledOnTouchOutside(false);
                                            progressDialog2[0].setCancelable(false);
                                            progressDialog2[0].setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C05201());
                                            progressDialog2[0].show();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }, 1000);
                            return;
                        } catch (Exception e4) {
                            if (uri.getScheme() == null) {
                            }
                            allActivities = null;
                            browserPackageNames = null;
                            list = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com")), 0);
                            browserPackageNames = new String[list.size()];
                            for (currentAccount = 0; currentAccount < list.size(); currentAccount++) {
                                browserPackageNames[currentAccount] = ((ResolveInfo) list.get(currentAccount)).activityInfo.packageName;
                                if (!BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("default browser name = ");
                                    stringBuilder.append(browserPackageNames[currentAccount]);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                            }
                            allActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri2), 0);
                            if (browserPackageNames == null) {
                                a = 0;
                                while (a < allActivities.size()) {
                                    while (currentAccount < browserPackageNames.length) {
                                        if (!equals.equals(((ResolveInfo) allActivities.get(a)).activityInfo.packageName)) {
                                            allActivities.remove(a);
                                            a--;
                                            break;
                                        }
                                    }
                                    a++;
                                }
                            } else {
                                a = 0;
                                while (a < allActivities.size()) {
                                    if (((ResolveInfo) allActivities.get(a)).activityInfo.packageName.toLowerCase().contains("browser")) {
                                    }
                                    allActivities.remove(a);
                                    a--;
                                    a++;
                                }
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                for (a = 0; a < allActivities.size(); a++) {
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("device has ");
                                    stringBuilder2.append(((ResolveInfo) allActivities.get(a)).activityInfo.packageName);
                                    stringBuilder2.append(" to open ");
                                    stringBuilder2.append(uri.toString());
                                    FileLog.m0d(stringBuilder2.toString());
                                }
                            }
                            share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                            share.setAction("android.intent.action.SEND");
                            copy = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, CustomTabsCopyReceiver.class), 134217728);
                            builder = new Builder(getSession());
                            builder.addMenuItem(LocaleController.getString("CopyLink", R.string.CopyLink), copy);
                            builder.setToolbarColor(Theme.getColor(Theme.key_actionBarDefault));
                            builder.setShowTitle(true);
                            builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0), false);
                            intent = builder.build();
                            intent.setUseNewTask();
                            intent.launchUrl(context2, uri2);
                            return;
                        }
                    } catch (Exception e5) {
                        i = currentAccount;
                        if (uri.getScheme() == null) {
                        }
                        allActivities = null;
                        browserPackageNames = null;
                        list = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com")), 0);
                        browserPackageNames = new String[list.size()];
                        for (currentAccount = 0; currentAccount < list.size(); currentAccount++) {
                            browserPackageNames[currentAccount] = ((ResolveInfo) list.get(currentAccount)).activityInfo.packageName;
                            if (!BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("default browser name = ");
                                stringBuilder.append(browserPackageNames[currentAccount]);
                                FileLog.m0d(stringBuilder.toString());
                            }
                        }
                        allActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri2), 0);
                        if (browserPackageNames == null) {
                            a = 0;
                            while (a < allActivities.size()) {
                                if (((ResolveInfo) allActivities.get(a)).activityInfo.packageName.toLowerCase().contains("browser")) {
                                }
                                allActivities.remove(a);
                                a--;
                                a++;
                            }
                        } else {
                            a = 0;
                            while (a < allActivities.size()) {
                                while (currentAccount < browserPackageNames.length) {
                                    if (!equals.equals(((ResolveInfo) allActivities.get(a)).activityInfo.packageName)) {
                                        allActivities.remove(a);
                                        a--;
                                        break;
                                    }
                                }
                                a++;
                            }
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            for (a = 0; a < allActivities.size(); a++) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("device has ");
                                stringBuilder2.append(((ResolveInfo) allActivities.get(a)).activityInfo.packageName);
                                stringBuilder2.append(" to open ");
                                stringBuilder2.append(uri.toString());
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                        share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                        share.setAction("android.intent.action.SEND");
                        copy = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, CustomTabsCopyReceiver.class), 134217728);
                        builder = new Builder(getSession());
                        builder.addMenuItem(LocaleController.getString("CopyLink", R.string.CopyLink), copy);
                        builder.setToolbarColor(Theme.getColor(Theme.key_actionBarDefault));
                        builder.setShowTitle(true);
                        builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0), false);
                        intent = builder.build();
                        intent.setUseNewTask();
                        intent.launchUrl(context2, uri2);
                        return;
                    }
                }
                try {
                    if (uri.getScheme() == null) {
                    }
                    if (allowCustom && SharedConfig.customTabs && !internalUri && !scheme.equals("tel")) {
                        allActivities = null;
                        browserPackageNames = null;
                        list = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com")), 0);
                        if (!(list == null || list.isEmpty())) {
                            browserPackageNames = new String[list.size()];
                            for (currentAccount = 0; currentAccount < list.size(); currentAccount++) {
                                browserPackageNames[currentAccount] = ((ResolveInfo) list.get(currentAccount)).activityInfo.packageName;
                                if (!BuildVars.LOGS_ENABLED) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("default browser name = ");
                                    stringBuilder.append(browserPackageNames[currentAccount]);
                                    FileLog.m0d(stringBuilder.toString());
                                }
                            }
                        }
                        allActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri2), 0);
                        if (browserPackageNames == null) {
                            a = 0;
                            while (a < allActivities.size()) {
                                while (currentAccount < browserPackageNames.length) {
                                    if (!equals.equals(((ResolveInfo) allActivities.get(a)).activityInfo.packageName)) {
                                        allActivities.remove(a);
                                        a--;
                                        break;
                                    }
                                }
                                a++;
                            }
                        } else {
                            a = 0;
                            while (a < allActivities.size()) {
                                if (((ResolveInfo) allActivities.get(a)).activityInfo.packageName.toLowerCase().contains("browser") || ((ResolveInfo) allActivities.get(a)).activityInfo.packageName.toLowerCase().contains("chrome")) {
                                    allActivities.remove(a);
                                    a--;
                                }
                                a++;
                            }
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            for (a = 0; a < allActivities.size(); a++) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("device has ");
                                stringBuilder2.append(((ResolveInfo) allActivities.get(a)).activityInfo.packageName);
                                stringBuilder2.append(" to open ");
                                stringBuilder2.append(uri.toString());
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                        if (forceBrowser[0] || allActivities == null || allActivities.isEmpty()) {
                            share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                            share.setAction("android.intent.action.SEND");
                            copy = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, CustomTabsCopyReceiver.class), 134217728);
                            builder = new Builder(getSession());
                            builder.addMenuItem(LocaleController.getString("CopyLink", R.string.CopyLink), copy);
                            builder.setToolbarColor(Theme.getColor(Theme.key_actionBarDefault));
                            builder.setShowTitle(true);
                            builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0), false);
                            intent = builder.build();
                            intent.setUseNewTask();
                            intent.launchUrl(context2, uri2);
                            return;
                        }
                    }
                } catch (Throwable e6) {
                    FileLog.m3e(e6);
                }
                try {
                    Intent intent2 = new Intent("android.intent.action.VIEW", uri2);
                    if (internalUri) {
                        intent2.setComponent(new ComponentName(context.getPackageName(), LaunchActivity.class.getName()));
                    }
                    intent2.putExtra("create_new_tab", true);
                    intent2.putExtra("com.android.browser.application_id", context.getPackageName());
                    context2.startActivity(intent2);
                } catch (Throwable e62) {
                    FileLog.m3e(e62);
                }
            }
        }
    }

    public static boolean isInternalUrl(String url, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), forceBrowser);
    }

    public static boolean isInternalUri(Uri uri, boolean[] forceBrowser) {
        String host = uri.getHost();
        host = host != null ? host.toLowerCase() : TtmlNode.ANONYMOUS_REGION_ID;
        if ("tg".equals(uri.getScheme())) {
            return true;
        }
        String path;
        if ("telegram.dog".equals(host)) {
            path = uri.getPath();
            if (path != null && path.length() > 1) {
                path = path.substring(1).toLowerCase();
                if (!(path.startsWith("blog") || path.equals("iv") || path.startsWith("faq"))) {
                    if (!path.equals("apps")) {
                        return true;
                    }
                }
                if (forceBrowser != null) {
                    forceBrowser[0] = true;
                }
                return false;
            }
        } else if ("telegram.me".equals(host) || "t.me".equals(host) || "telesco.pe".equals(host)) {
            path = uri.getPath();
            if (path != null && path.length() > 1) {
                if (!path.substring(1).toLowerCase().equals("iv")) {
                    return true;
                }
                if (forceBrowser != null) {
                    forceBrowser[0] = true;
                }
                return false;
            }
        }
        return false;
    }
}

package org.telegram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import java.lang.ref.WeakReference;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.ShareBroadcastReceiver;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsIntent.Builder;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

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
        Activity currentActivity = null;
        if (currentCustomTabsActivity != null) {
            currentActivity = (Activity) currentCustomTabsActivity.get();
        }
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
                customTabsServiceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                    public void onServiceConnected(CustomTabsClient client) {
                        Browser.customTabsClient = client;
                        if (MediaController.getInstance().canCustomTabs() && Browser.customTabsClient != null) {
                            try {
                                Browser.customTabsClient.warmup(0);
                            } catch (Throwable e) {
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
            } catch (Throwable e) {
                FileLog.e(e);
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
            } catch (Throwable e) {
                FileLog.e(e);
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
        if (context != null && url != null) {
            openUrl(context, Uri.parse(url), allowCustom);
        }
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        if (context != null && uri != null) {
            boolean[] forceBrowser = new boolean[]{false};
            boolean internalUri = isInternalUri(uri, forceBrowser);
            try {
                String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase() : "";
                if (allowCustom && MediaController.getInstance().canCustomTabs() && !internalUri && !scheme.equals("tel")) {
                    String browserPackageName = null;
                    try {
                        browserPackageName = context.getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://")), 65536).activityInfo.packageName;
                    } catch (Exception e) {
                    }
                    List<ResolveInfo> list = null;
                    try {
                        list = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", uri), 0);
                        int a;
                        if (browserPackageName != null) {
                            a = 0;
                            while (a < list.size()) {
                                if (browserPackageName.equals(((ResolveInfo) list.get(a)).activityInfo.packageName)) {
                                    list.remove(a);
                                    a--;
                                }
                                a++;
                            }
                        } else {
                            a = 0;
                            while (a < list.size()) {
                                if (((ResolveInfo) list.get(a)).activityInfo.packageName.toLowerCase().contains("browser")) {
                                    list.remove(a);
                                    a--;
                                }
                                a++;
                            }
                        }
                    } catch (Exception e2) {
                    }
                    if (forceBrowser[0] || allActivities == null || allActivities.isEmpty()) {
                        Intent intent = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                        intent.setAction("android.intent.action.SEND");
                        Builder builder = new Builder(getSession());
                        builder.setToolbarColor(Theme.getColor(Theme.key_actionBarDefault));
                        builder.setShowTitle(true);
                        builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, intent, 0), false);
                        builder.build().launchUrl(context, uri);
                        return;
                    }
                }
            } catch (Throwable e3) {
                FileLog.e(e3);
            }
            try {
                Intent intent2 = new Intent("android.intent.action.VIEW", uri);
                if (internalUri) {
                    intent2.setComponent(new ComponentName(context.getPackageName(), LaunchActivity.class.getName()));
                }
                intent2.putExtra("com.android.browser.application_id", context.getPackageName());
                context.startActivity(intent2);
            } catch (Throwable e32) {
                FileLog.e(e32);
            }
        }
    }

    public static boolean isInternalUrl(String url, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), forceBrowser);
    }

    public static boolean isInternalUri(Uri uri, boolean[] forceBrowser) {
        String host = uri.getHost();
        host = host != null ? host.toLowerCase() : "";
        if ("tg".equals(uri.getScheme())) {
            return true;
        }
        if ("telegram.me".equals(host) || "t.me".equals(host) || "telegram.dog".equals(host) || "telesco.pe".equals(host)) {
            String path = uri.getPath();
            if (path != null && path.length() > 1) {
                path = path.substring(1).toLowerCase();
                if (path.startsWith("joinchat") || path.startsWith("addstickers") || path.startsWith("msg") || path.startsWith(Event.SHARE) || path.startsWith("confirmphone")) {
                    return true;
                }
                if (path.lastIndexOf(47) <= 0 && !path.equals("iv")) {
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

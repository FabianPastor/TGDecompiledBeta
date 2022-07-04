package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.support.customtabs.ICustomTabsCallback;

public class CustomTabsClient {
    private final ICustomTabsService mService;
    private final ComponentName mServiceComponentName;

    CustomTabsClient(ICustomTabsService service, ComponentName componentName) {
        this.mService = service;
        this.mServiceComponentName = componentName;
    }

    public static boolean bindCustomTabsService(Context context, String packageName, CustomTabsServiceConnection connection) {
        Intent intent = new Intent("android.support.customtabs.action.CustomTabsService");
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        return context.bindService(intent, connection, 33);
    }

    public static String getPackageName(Context context, List<String> packages) {
        return getPackageName(context, packages, false);
    }

    public static String getPackageName(Context context, List<String> packages, boolean ignoreDefault) {
        ResolveInfo defaultViewHandlerInfo;
        PackageManager pm = context.getPackageManager();
        List<String> packageNames = packages == null ? new ArrayList<>() : packages;
        Intent activityIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        if (!ignoreDefault && (defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)) != null) {
            String packageName = defaultViewHandlerInfo.activityInfo.packageName;
            packageNames = new ArrayList<>(packageNames.size() + 1);
            packageNames.add(packageName);
            if (packages != null) {
                packageNames.addAll(packages);
            }
        }
        Intent serviceIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        for (String packageName2 : packageNames) {
            serviceIntent.setPackage(packageName2);
            if (pm.resolveService(serviceIntent, 0) != null) {
                return packageName2;
            }
        }
        return null;
    }

    public static boolean connectAndInitialize(Context context, String packageName) {
        if (packageName == null) {
            return false;
        }
        final Context applicationContext = context.getApplicationContext();
        try {
            return bindCustomTabsService(applicationContext, packageName, new CustomTabsServiceConnection() {
                public final void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                    client.warmup(0);
                    applicationContext.unbindService(this);
                }

                public final void onServiceDisconnected(ComponentName componentName) {
                }
            });
        } catch (SecurityException e) {
            return false;
        }
    }

    public boolean warmup(long flags) {
        try {
            return this.mService.warmup(flags);
        } catch (RemoteException e) {
            return false;
        }
    }

    public CustomTabsSession newSession(final CustomTabsCallback callback) {
        ICustomTabsCallback.Stub wrapper = new ICustomTabsCallback.Stub() {
            private Handler mHandler = new Handler(Looper.getMainLooper());

            public void onNavigationEvent(final int navigationEvent, final Bundle extras) {
                if (callback != null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            callback.onNavigationEvent(navigationEvent, extras);
                        }
                    });
                }
            }

            public void extraCallback(final String callbackName, final Bundle args) throws RemoteException {
                if (callback != null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            callback.extraCallback(callbackName, args);
                        }
                    });
                }
            }

            public void onMessageChannelReady(final Bundle extras) throws RemoteException {
                if (callback != null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            callback.onMessageChannelReady(extras);
                        }
                    });
                }
            }

            public void onPostMessage(final String message, final Bundle extras) throws RemoteException {
                if (callback != null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            callback.onPostMessage(message, extras);
                        }
                    });
                }
            }
        };
        try {
            if (!this.mService.newSession(wrapper)) {
                return null;
            }
            return new CustomTabsSession(this.mService, wrapper, this.mServiceComponentName);
        } catch (RemoteException e) {
            return null;
        }
    }

    public Bundle extraCommand(String commandName, Bundle args) {
        try {
            return this.mService.extraCommand(commandName, args);
        } catch (RemoteException e) {
            return null;
        }
    }
}

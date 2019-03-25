package org.telegram.messenger.support.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;
import java.util.List;

public final class CustomTabsSession {
    private static final String TAG = "CustomTabsSession";
    private final ICustomTabsCallback mCallback;
    private final ComponentName mComponentName;
    private final Object mLock = new Object();
    private final ICustomTabsService mService;

    public static CustomTabsSession createDummySessionForTesting(ComponentName componentName) {
        return new CustomTabsSession(null, new DummyCallback(), componentName);
    }

    CustomTabsSession(ICustomTabsService service, ICustomTabsCallback callback, ComponentName componentName) {
        this.mService = service;
        this.mCallback = callback;
        this.mComponentName = componentName;
    }

    public boolean mayLaunchUrl(Uri url, Bundle extras, List<Bundle> otherLikelyBundles) {
        try {
            return this.mService.mayLaunchUrl(this.mCallback, url, extras, otherLikelyBundles);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setActionButton(Bitmap icon, String description) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("android.support.customtabs.customaction.ICON", icon);
        bundle.putString("android.support.customtabs.customaction.DESCRIPTION", description);
        Bundle metaBundle = new Bundle();
        metaBundle.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle);
        try {
            return this.mService.updateVisuals(this.mCallback, metaBundle);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setSecondaryToolbarViews(RemoteViews remoteViews, int[] clickableIDs, PendingIntent pendingIntent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", remoteViews);
        bundle.putIntArray("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", clickableIDs);
        bundle.putParcelable("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", pendingIntent);
        try {
            return this.mService.updateVisuals(this.mCallback, bundle);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Deprecated
    public boolean setToolbarItem(int id, Bitmap icon, String description) {
        Bundle bundle = new Bundle();
        bundle.putInt("android.support.customtabs.customaction.ID", id);
        bundle.putParcelable("android.support.customtabs.customaction.ICON", icon);
        bundle.putString("android.support.customtabs.customaction.DESCRIPTION", description);
        Bundle metaBundle = new Bundle();
        metaBundle.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle);
        try {
            return this.mService.updateVisuals(this.mCallback, metaBundle);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean requestPostMessageChannel(Uri postMessageOrigin) {
        try {
            return this.mService.requestPostMessageChannel(this.mCallback, postMessageOrigin);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int postMessage(String message, Bundle extras) {
        int postMessage;
        synchronized (this.mLock) {
            try {
                postMessage = this.mService.postMessage(this.mCallback, message, extras);
            } catch (RemoteException e) {
                postMessage = -2;
            }
        }
        return postMessage;
    }

    /* Access modifiers changed, original: 0000 */
    public IBinder getBinder() {
        return this.mCallback.asBinder();
    }

    /* Access modifiers changed, original: 0000 */
    public ComponentName getComponentName() {
        return this.mComponentName;
    }
}

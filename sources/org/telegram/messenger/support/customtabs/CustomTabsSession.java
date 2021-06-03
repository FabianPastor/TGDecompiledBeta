package org.telegram.messenger.support.customtabs;

import android.content.ComponentName;
import android.os.IBinder;

public final class CustomTabsSession {
    private final ICustomTabsCallback mCallback;
    private final ComponentName mComponentName;
    private final Object mLock = new Object();
    private final ICustomTabsService mService;

    CustomTabsSession(ICustomTabsService iCustomTabsService, ICustomTabsCallback iCustomTabsCallback, ComponentName componentName) {
        this.mService = iCustomTabsService;
        this.mCallback = iCustomTabsCallback;
        this.mComponentName = componentName;
    }

    /* access modifiers changed from: package-private */
    public IBinder getBinder() {
        return this.mCallback.asBinder();
    }

    /* access modifiers changed from: package-private */
    public ComponentName getComponentName() {
        return this.mComponentName;
    }
}

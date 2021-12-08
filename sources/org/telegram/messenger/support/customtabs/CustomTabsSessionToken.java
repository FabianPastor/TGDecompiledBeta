package org.telegram.messenger.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.core.app.BundleCompat;
import org.telegram.messenger.support.customtabs.ICustomTabsCallback;

public class CustomTabsSessionToken {
    private static final String TAG = "CustomTabsSessionToken";
    private final CustomTabsCallback mCallback = new CustomTabsCallback() {
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(navigationEvent, extras);
            } catch (RemoteException e) {
                Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
            }
        }

        public void extraCallback(String callbackName, Bundle args) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.extraCallback(callbackName, args);
            } catch (RemoteException e) {
                Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
            }
        }

        public void onMessageChannelReady(Bundle extras) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.onMessageChannelReady(extras);
            } catch (RemoteException e) {
                Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
            }
        }

        public void onPostMessage(String message, Bundle extras) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.onPostMessage(message, extras);
            } catch (RemoteException e) {
                Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
            }
        }
    };
    /* access modifiers changed from: private */
    public final ICustomTabsCallback mCallbackBinder;

    static class DummyCallback extends ICustomTabsCallback.Stub {
        DummyCallback() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
        }

        public void extraCallback(String callbackName, Bundle args) {
        }

        public void onMessageChannelReady(Bundle extras) {
        }

        public void onPostMessage(String message, Bundle extras) {
        }

        public IBinder asBinder() {
            return this;
        }
    }

    public static CustomTabsSessionToken getSessionTokenFromIntent(Intent intent) {
        IBinder binder = BundleCompat.getBinder(intent.getExtras(), "android.support.customtabs.extra.SESSION");
        if (binder == null) {
            return null;
        }
        return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(binder));
    }

    public static CustomTabsSessionToken createDummySessionTokenForTesting() {
        return new CustomTabsSessionToken(new DummyCallback());
    }

    CustomTabsSessionToken(ICustomTabsCallback callbackBinder) {
        this.mCallbackBinder = callbackBinder;
    }

    /* access modifiers changed from: package-private */
    public IBinder getCallbackBinder() {
        return this.mCallbackBinder.asBinder();
    }

    public int hashCode() {
        return getCallbackBinder().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof CustomTabsSessionToken)) {
            return false;
        }
        return ((CustomTabsSessionToken) o).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
    }

    public CustomTabsCallback getCallback() {
        return this.mCallback;
    }

    public boolean isAssociatedWith(CustomTabsSession session) {
        return session.getBinder().equals(this.mCallbackBinder);
    }
}

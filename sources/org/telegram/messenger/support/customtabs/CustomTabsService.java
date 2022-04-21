package org.telegram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.collection.ArrayMap;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.telegram.messenger.support.customtabs.ICustomTabsService;

public abstract class CustomTabsService extends Service {
    public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
    public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
    public static final int RESULT_FAILURE_DISALLOWED = -1;
    public static final int RESULT_FAILURE_MESSAGING_ERROR = -3;
    public static final int RESULT_FAILURE_REMOTE_ERROR = -2;
    public static final int RESULT_SUCCESS = 0;
    private ICustomTabsService.Stub mBinder = new ICustomTabsService.Stub() {
        public boolean warmup(long flags) {
            return CustomTabsService.this.warmup(flags);
        }

        public boolean newSession(ICustomTabsCallback callback) {
            final CustomTabsSessionToken sessionToken = new CustomTabsSessionToken(callback);
            try {
                IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
                    public void binderDied() {
                        CustomTabsService.this.cleanUpSession(sessionToken);
                    }
                };
                synchronized (CustomTabsService.this.mDeathRecipientMap) {
                    callback.asBinder().linkToDeath(deathRecipient, 0);
                    CustomTabsService.this.mDeathRecipientMap.put(callback.asBinder(), deathRecipient);
                }
                return CustomTabsService.this.newSession(sessionToken);
            } catch (RemoteException e) {
                return false;
            }
        }

        public boolean mayLaunchUrl(ICustomTabsCallback callback, Uri url, Bundle extras, List<Bundle> otherLikelyBundles) {
            return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(callback), url, extras, otherLikelyBundles);
        }

        public Bundle extraCommand(String commandName, Bundle args) {
            return CustomTabsService.this.extraCommand(commandName, args);
        }

        public boolean updateVisuals(ICustomTabsCallback callback, Bundle bundle) {
            return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(callback), bundle);
        }

        public boolean requestPostMessageChannel(ICustomTabsCallback callback, Uri postMessageOrigin) {
            return CustomTabsService.this.requestPostMessageChannel(new CustomTabsSessionToken(callback), postMessageOrigin);
        }

        public int postMessage(ICustomTabsCallback callback, String message, Bundle extras) {
            return CustomTabsService.this.postMessage(new CustomTabsSessionToken(callback), message, extras);
        }
    };
    /* access modifiers changed from: private */
    public final Map<IBinder, IBinder.DeathRecipient> mDeathRecipientMap = new ArrayMap();

    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

    /* access modifiers changed from: protected */
    public abstract Bundle extraCommand(String str, Bundle bundle);

    /* access modifiers changed from: protected */
    public abstract boolean mayLaunchUrl(CustomTabsSessionToken customTabsSessionToken, Uri uri, Bundle bundle, List<Bundle> list);

    /* access modifiers changed from: protected */
    public abstract boolean newSession(CustomTabsSessionToken customTabsSessionToken);

    /* access modifiers changed from: protected */
    public abstract int postMessage(CustomTabsSessionToken customTabsSessionToken, String str, Bundle bundle);

    /* access modifiers changed from: protected */
    public abstract boolean requestPostMessageChannel(CustomTabsSessionToken customTabsSessionToken, Uri uri);

    /* access modifiers changed from: protected */
    public abstract boolean updateVisuals(CustomTabsSessionToken customTabsSessionToken, Bundle bundle);

    /* access modifiers changed from: protected */
    public abstract boolean warmup(long j);

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    /* access modifiers changed from: protected */
    public boolean cleanUpSession(CustomTabsSessionToken sessionToken) {
        try {
            synchronized (this.mDeathRecipientMap) {
                IBinder binder = sessionToken.getCallbackBinder();
                binder.unlinkToDeath(this.mDeathRecipientMap.get(binder), 0);
                this.mDeathRecipientMap.remove(binder);
            }
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

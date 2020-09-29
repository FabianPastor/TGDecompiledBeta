package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import org.telegram.messenger.NotificationCenter;

public final class ContactsLoadingObserver {
    private final Callback callback;
    private final ContactsController contactsController;
    private final int currentAccount;
    private final Handler handler;
    private final NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() {
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.contactsDidLoad) {
                boolean unused = ContactsLoadingObserver.this.onContactsLoadingStateUpdated(i2, false);
            }
        }
    };
    private final Runnable releaseRunnable;
    private boolean released;

    public interface Callback {
        void onResult(boolean z);
    }

    public static void observe(Callback callback2, long j) {
        new ContactsLoadingObserver(callback2).start(j);
    }

    private ContactsLoadingObserver(Callback callback2) {
        this.callback = callback2;
        this.currentAccount = UserConfig.selectedAccount;
        this.releaseRunnable = new Runnable() {
            public final void run() {
                ContactsLoadingObserver.this.lambda$new$0$ContactsLoadingObserver();
            }
        };
        this.contactsController = ContactsController.getInstance(this.currentAccount);
        this.notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        this.handler = new Handler(Looper.myLooper());
    }

    public /* synthetic */ void lambda$new$0$ContactsLoadingObserver() {
        onContactsLoadingStateUpdated(this.currentAccount, true);
    }

    public void start(long j) {
        if (!onContactsLoadingStateUpdated(this.currentAccount, false)) {
            this.notificationCenter.addObserver(this.observer, NotificationCenter.contactsDidLoad);
            this.handler.postDelayed(this.releaseRunnable, j);
        }
    }

    public void release() {
        if (!this.released) {
            NotificationCenter notificationCenter2 = this.notificationCenter;
            if (notificationCenter2 != null) {
                notificationCenter2.removeObserver(this.observer, NotificationCenter.contactsDidLoad);
            }
            Handler handler2 = this.handler;
            if (handler2 != null) {
                handler2.removeCallbacks(this.releaseRunnable);
            }
            this.released = true;
        }
    }

    /* access modifiers changed from: private */
    public boolean onContactsLoadingStateUpdated(int i, boolean z) {
        if (this.released) {
            return false;
        }
        boolean z2 = this.contactsController.contactsLoaded;
        if (!z2 && !z) {
            return false;
        }
        release();
        this.callback.onResult(z2);
        return true;
    }
}

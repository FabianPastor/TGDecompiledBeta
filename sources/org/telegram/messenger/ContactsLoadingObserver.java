package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes.dex */
public final class ContactsLoadingObserver {
    private final Callback callback;
    private final ContactsController contactsController;
    private final int currentAccount;
    private final Handler handler;
    private final NotificationCenter notificationCenter;
    private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.messenger.ContactsLoadingObserver$$ExternalSyntheticLambda1
        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public final void didReceivedNotification(int i, int i2, Object[] objArr) {
            ContactsLoadingObserver.this.lambda$new$0(i, i2, objArr);
        }
    };
    private final Runnable releaseRunnable;
    private boolean released;

    /* loaded from: classes.dex */
    public interface Callback {
        void onResult(boolean z);
    }

    public static void observe(Callback callback, long j) {
        new ContactsLoadingObserver(callback).start(j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
            onContactsLoadingStateUpdated(i2, false);
        }
    }

    private ContactsLoadingObserver(Callback callback) {
        this.callback = callback;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.releaseRunnable = new Runnable() { // from class: org.telegram.messenger.ContactsLoadingObserver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ContactsLoadingObserver.this.lambda$new$1();
            }
        };
        this.contactsController = ContactsController.getInstance(i);
        this.notificationCenter = NotificationCenter.getInstance(i);
        this.handler = new Handler(Looper.myLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
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
            NotificationCenter notificationCenter = this.notificationCenter;
            if (notificationCenter != null) {
                notificationCenter.removeObserver(this.observer, NotificationCenter.contactsDidLoad);
            }
            Handler handler = this.handler;
            if (handler != null) {
                handler.removeCallbacks(this.releaseRunnable);
            }
            this.released = true;
        }
    }

    private boolean onContactsLoadingStateUpdated(int i, boolean z) {
        if (!this.released) {
            boolean z2 = this.contactsController.contactsLoaded;
            if (!z2 && !z) {
                return false;
            }
            release();
            this.callback.onResult(z2);
            return true;
        }
        return false;
    }
}

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
    private final NotificationCenter.NotificationCenterDelegate observer = new ContactsLoadingObserver$$ExternalSyntheticLambda1(this);
    private final Runnable releaseRunnable;
    private boolean released;

    public interface Callback {
        void onResult(boolean z);
    }

    public static void observe(Callback callback2, long expirationTime) {
        new ContactsLoadingObserver(callback2).start(expirationTime);
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-ContactsLoadingObserver  reason: not valid java name */
    public /* synthetic */ void m534lambda$new$0$orgtelegrammessengerContactsLoadingObserver(int id, int account, Object[] args) {
        if (id == NotificationCenter.contactsDidLoad) {
            onContactsLoadingStateUpdated(account, false);
        }
    }

    private ContactsLoadingObserver(Callback callback2) {
        this.callback = callback2;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.releaseRunnable = new ContactsLoadingObserver$$ExternalSyntheticLambda0(this);
        this.contactsController = ContactsController.getInstance(i);
        this.notificationCenter = NotificationCenter.getInstance(i);
        this.handler = new Handler(Looper.myLooper());
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-ContactsLoadingObserver  reason: not valid java name */
    public /* synthetic */ void m535lambda$new$1$orgtelegrammessengerContactsLoadingObserver() {
        onContactsLoadingStateUpdated(this.currentAccount, true);
    }

    public void start(long expirationTime) {
        if (!onContactsLoadingStateUpdated(this.currentAccount, false)) {
            this.notificationCenter.addObserver(this.observer, NotificationCenter.contactsDidLoad);
            this.handler.postDelayed(this.releaseRunnable, expirationTime);
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

    private boolean onContactsLoadingStateUpdated(int account, boolean force) {
        if (this.released) {
            return false;
        }
        boolean contactsLoaded = this.contactsController.contactsLoaded;
        if (!contactsLoaded && !force) {
            return false;
        }
        release();
        this.callback.onResult(contactsLoaded);
        return true;
    }
}

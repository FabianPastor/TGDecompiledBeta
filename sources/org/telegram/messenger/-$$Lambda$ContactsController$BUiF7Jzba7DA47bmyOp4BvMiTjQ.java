package org.telegram.messenger;

import android.util.SparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$BUiF7Jzba7DA47bmyOp4BvMiTjQ implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ SparseArray f$1;

    public /* synthetic */ -$$Lambda$ContactsController$BUiF7Jzba7DA47bmyOp4BvMiTjQ(ContactsController contactsController, SparseArray sparseArray) {
        this.f$0 = contactsController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$migratePhoneBookToV7$11$ContactsController(this.f$1);
    }
}

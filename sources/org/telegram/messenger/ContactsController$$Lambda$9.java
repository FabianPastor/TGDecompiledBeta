package org.telegram.messenger;

import android.util.SparseArray;

final /* synthetic */ class ContactsController$$Lambda$9 implements Runnable {
    private final ContactsController arg$1;
    private final SparseArray arg$2;

    ContactsController$$Lambda$9(ContactsController contactsController, SparseArray sparseArray) {
        this.arg$1 = contactsController;
        this.arg$2 = sparseArray;
    }

    public void run() {
        this.arg$1.lambda$migratePhoneBookToV7$11$ContactsController(this.arg$2);
    }
}

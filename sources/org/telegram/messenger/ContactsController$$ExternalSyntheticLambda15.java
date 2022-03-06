package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ SparseArray f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda15(ContactsController contactsController, SparseArray sparseArray) {
        this.f$0 = contactsController;
        this.f$1 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$migratePhoneBookToV7$12(this.f$1);
    }
}

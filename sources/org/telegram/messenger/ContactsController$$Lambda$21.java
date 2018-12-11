package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class ContactsController$$Lambda$21 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;

    ContactsController$$Lambda$21(ContactsController contactsController, ArrayList arrayList, ArrayList arrayList2) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$applyContactsUpdates$46$ContactsController(this.arg$2, this.arg$3);
    }
}

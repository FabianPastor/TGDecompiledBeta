package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class ContactsController$$Lambda$18 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;

    ContactsController$$Lambda$18(ContactsController contactsController, ArrayList arrayList) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$performWriteContactsToPhoneBook$43$ContactsController(this.arg$2);
    }
}

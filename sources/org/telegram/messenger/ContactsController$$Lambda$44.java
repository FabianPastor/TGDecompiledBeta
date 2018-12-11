package org.telegram.messenger;

import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$44 implements Runnable {
    private final ContactsController arg$1;
    private final HashMap arg$2;
    private final HashMap arg$3;

    ContactsController$$Lambda$44(ContactsController contactsController, HashMap hashMap, HashMap hashMap2) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = hashMap2;
    }

    public void run() {
        this.arg$1.lambda$null$32$ContactsController(this.arg$2, this.arg$3);
    }
}

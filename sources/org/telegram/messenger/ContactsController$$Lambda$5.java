package org.telegram.messenger;

import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$5 implements Runnable {
    private final ContactsController arg$1;
    private final HashMap arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final boolean arg$5;

    ContactsController$$Lambda$5(ContactsController contactsController, HashMap hashMap, boolean z, boolean z2, boolean z3) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = z3;
    }

    public void run() {
        this.arg$1.lambda$syncPhoneBookByAlert$6$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

package org.telegram.messenger;

import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$10 implements Runnable {
    private final ContactsController arg$1;
    private final HashMap arg$2;
    private final boolean arg$3;
    private final boolean arg$4;
    private final boolean arg$5;
    private final boolean arg$6;
    private final boolean arg$7;
    private final boolean arg$8;

    ContactsController$$Lambda$10(ContactsController contactsController, HashMap hashMap, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = z;
        this.arg$4 = z2;
        this.arg$5 = z3;
        this.arg$6 = z4;
        this.arg$7 = z5;
        this.arg$8 = z6;
    }

    public void run() {
        this.arg$1.lambda$performSyncPhoneBook$24$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}

package org.telegram.messenger;

import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$47 implements Runnable {
    private final ContactsController arg$1;
    private final int arg$2;
    private final HashMap arg$3;
    private final boolean arg$4;
    private final boolean arg$5;

    ContactsController$$Lambda$47(ContactsController contactsController, int i, HashMap hashMap, boolean z, boolean z2) {
        this.arg$1 = contactsController;
        this.arg$2 = i;
        this.arg$3 = hashMap;
        this.arg$4 = z;
        this.arg$5 = z2;
    }

    public void run() {
        this.arg$1.lambda$null$13$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$37 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final HashMap arg$3;

    ContactsController$$Lambda$37(ContactsController contactsController, ArrayList arrayList, HashMap hashMap) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = hashMap;
    }

    public void run() {
        this.arg$1.lambda$null$38$ContactsController(this.arg$2, this.arg$3);
    }
}

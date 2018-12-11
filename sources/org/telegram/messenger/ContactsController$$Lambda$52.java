package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$52 implements Runnable {
    private final ContactsController arg$1;
    private final HashMap arg$2;
    private final ArrayList arg$3;
    private final HashMap arg$4;

    ContactsController$$Lambda$52(ContactsController contactsController, HashMap hashMap, ArrayList arrayList, HashMap hashMap2) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = arrayList;
        this.arg$4 = hashMap2;
    }

    public void run() {
        this.arg$1.lambda$null$22$ContactsController(this.arg$2, this.arg$3, this.arg$4);
    }
}

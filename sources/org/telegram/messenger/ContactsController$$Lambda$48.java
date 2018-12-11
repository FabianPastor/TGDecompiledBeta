package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$48 implements Runnable {
    private final ContactsController arg$1;
    private final HashMap arg$2;
    private final HashMap arg$3;
    private final boolean arg$4;
    private final HashMap arg$5;
    private final ArrayList arg$6;
    private final HashMap arg$7;

    ContactsController$$Lambda$48(ContactsController contactsController, HashMap hashMap, HashMap hashMap2, boolean z, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4) {
        this.arg$1 = contactsController;
        this.arg$2 = hashMap;
        this.arg$3 = hashMap2;
        this.arg$4 = z;
        this.arg$5 = hashMap3;
        this.arg$6 = arrayList;
        this.arg$7 = hashMap4;
    }

    public void run() {
        this.arg$1.lambda$null$15$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

final /* synthetic */ class ContactsController$$Lambda$42 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final ConcurrentHashMap arg$3;
    private final HashMap arg$4;
    private final HashMap arg$5;
    private final ArrayList arg$6;
    private final ArrayList arg$7;
    private final int arg$8;
    private final boolean arg$9;

    ContactsController$$Lambda$42(ContactsController contactsController, ArrayList arrayList, ConcurrentHashMap concurrentHashMap, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2, ArrayList arrayList3, int i, boolean z) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = concurrentHashMap;
        this.arg$4 = hashMap;
        this.arg$5 = hashMap2;
        this.arg$6 = arrayList2;
        this.arg$7 = arrayList3;
        this.arg$8 = i;
        this.arg$9 = z;
    }

    public void run() {
        this.arg$1.lambda$null$31$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}

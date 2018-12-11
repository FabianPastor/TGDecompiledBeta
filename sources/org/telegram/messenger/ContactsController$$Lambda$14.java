package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class ContactsController$$Lambda$14 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final HashMap arg$3;
    private final HashMap arg$4;
    private final ArrayList arg$5;

    ContactsController$$Lambda$14(ContactsController contactsController, ArrayList arrayList, HashMap hashMap, HashMap hashMap2, ArrayList arrayList2) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = hashMap;
        this.arg$4 = hashMap2;
        this.arg$5 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$mergePhonebookAndTelegramContacts$39$ContactsController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class ContactsController$$Lambda$13 implements Runnable {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final ArrayList arg$4;

    ContactsController$$Lambda$13(ContactsController contactsController, ArrayList arrayList, int i, ArrayList arrayList2) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$processLoadedContacts$35$ContactsController(this.arg$2, this.arg$3, this.arg$4);
    }
}

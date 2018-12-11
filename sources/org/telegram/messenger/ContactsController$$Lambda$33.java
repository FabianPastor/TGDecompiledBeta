package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ContactsController$$Lambda$33 implements Runnable {
    private final ContactsController arg$1;
    private final User arg$2;

    ContactsController$$Lambda$33(ContactsController contactsController, User user) {
        this.arg$1 = contactsController;
        this.arg$2 = user;
    }

    public void run() {
        this.arg$1.lambda$null$48$ContactsController(this.arg$2);
    }
}

package org.telegram.messenger;

final /* synthetic */ class ContactsController$$Lambda$58 implements Runnable {
    private final ContactsController arg$1;
    private final Runnable arg$2;

    ContactsController$$Lambda$58(ContactsController contactsController, Runnable runnable) {
        this.arg$1 = contactsController;
        this.arg$2 = runnable;
    }

    public void run() {
        this.arg$1.lambda$null$7$ContactsController(this.arg$2);
    }
}

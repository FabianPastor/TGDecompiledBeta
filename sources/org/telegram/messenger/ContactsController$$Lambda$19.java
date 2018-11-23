package org.telegram.messenger;

final /* synthetic */ class ContactsController$$Lambda$19 implements Runnable {
    private final ContactsController arg$1;
    private final Integer arg$2;

    ContactsController$$Lambda$19(ContactsController contactsController, Integer num) {
        this.arg$1 = contactsController;
        this.arg$2 = num;
    }

    public void run() {
        this.arg$1.lambda$applyContactsUpdates$44$ContactsController(this.arg$2);
    }
}

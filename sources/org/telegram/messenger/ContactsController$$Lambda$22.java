package org.telegram.messenger;

final /* synthetic */ class ContactsController$$Lambda$22 implements Runnable {
    private final String arg$1;

    ContactsController$$Lambda$22(String str) {
        this.arg$1 = str;
    }

    public void run() {
        ContactsController.lambda$markAsContacted$47$ContactsController(this.arg$1);
    }
}

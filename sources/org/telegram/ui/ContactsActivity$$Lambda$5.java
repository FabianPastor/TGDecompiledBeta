package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class ContactsActivity$$Lambda$5 implements IntCallback {
    private final ContactsActivity arg$1;

    ContactsActivity$$Lambda$5(ContactsActivity contactsActivity) {
        this.arg$1 = contactsActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$askForPermissons$6$ContactsActivity(i);
    }
}

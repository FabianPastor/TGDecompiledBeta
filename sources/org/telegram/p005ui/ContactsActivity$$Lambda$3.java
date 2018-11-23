package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

/* renamed from: org.telegram.ui.ContactsActivity$$Lambda$3 */
final /* synthetic */ class ContactsActivity$$Lambda$3 implements IntCallback {
    private final ContactsActivity arg$1;

    ContactsActivity$$Lambda$3(ContactsActivity contactsActivity) {
        this.arg$1 = contactsActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$askForPermissons$4$ContactsActivity(i);
    }
}

package org.telegram.p005ui;

import org.telegram.messenger.MessagesStorage.IntCallback;

/* renamed from: org.telegram.ui.ContactsActivity$$Lambda$2 */
final /* synthetic */ class ContactsActivity$$Lambda$2 implements IntCallback {
    private final ContactsActivity arg$1;

    ContactsActivity$$Lambda$2(ContactsActivity contactsActivity) {
        this.arg$1 = contactsActivity;
    }

    public void run(int i) {
        this.arg$1.lambda$onResume$3$ContactsActivity(i);
    }
}

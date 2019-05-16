package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$3Lppzk_aX70-Q6C3adV0V2TBG9k implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ TL_contacts_importedContacts f$1;

    public /* synthetic */ -$$Lambda$ContactsController$3Lppzk_aX70-Q6C3adV0V2TBG9k(ContactsController contactsController, TL_contacts_importedContacts tL_contacts_importedContacts) {
        this.f$0 = contactsController;
        this.f$1 = tL_contacts_importedContacts;
    }

    public final void run() {
        this.f$0.lambda$null$49$ContactsController(this.f$1);
    }
}

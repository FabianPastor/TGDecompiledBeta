package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.ContactsController;
/* loaded from: classes.dex */
public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda49 implements Comparator {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda49 INSTANCE = new ContactsController$$ExternalSyntheticLambda49();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda49() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$updateUnregisteredContacts$42;
        lambda$updateUnregisteredContacts$42 = ContactsController.lambda$updateUnregisteredContacts$42((ContactsController.Contact) obj, (ContactsController.Contact) obj2);
        return lambda$updateUnregisteredContacts$42;
    }
}

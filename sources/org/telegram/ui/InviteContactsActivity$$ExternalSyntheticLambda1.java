package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.ContactsController;

public final /* synthetic */ class InviteContactsActivity$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ InviteContactsActivity$$ExternalSyntheticLambda1 INSTANCE = new InviteContactsActivity$$ExternalSyntheticLambda1();

    private /* synthetic */ InviteContactsActivity$$ExternalSyntheticLambda1() {
    }

    public final int compare(Object obj, Object obj2) {
        return InviteContactsActivity.lambda$fetchContacts$2((ContactsController.Contact) obj, (ContactsController.Contact) obj2);
    }
}

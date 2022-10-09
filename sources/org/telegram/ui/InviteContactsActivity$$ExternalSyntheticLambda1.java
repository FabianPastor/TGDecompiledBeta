package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.ContactsController;
/* loaded from: classes3.dex */
public final /* synthetic */ class InviteContactsActivity$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ InviteContactsActivity$$ExternalSyntheticLambda1 INSTANCE = new InviteContactsActivity$$ExternalSyntheticLambda1();

    private /* synthetic */ InviteContactsActivity$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$fetchContacts$2;
        lambda$fetchContacts$2 = InviteContactsActivity.lambda$fetchContacts$2((ContactsController.Contact) obj, (ContactsController.Contact) obj2);
        return lambda$fetchContacts$2;
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_contact;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda51 implements Comparator {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda51 INSTANCE = new ContactsController$$ExternalSyntheticLambda51();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda51() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$26((TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
    }
}

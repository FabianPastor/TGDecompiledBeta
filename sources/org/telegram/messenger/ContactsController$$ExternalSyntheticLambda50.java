package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_contact;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda50 implements Comparator {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda50 INSTANCE = new ContactsController$$ExternalSyntheticLambda50();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda50() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$25((TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
    }
}

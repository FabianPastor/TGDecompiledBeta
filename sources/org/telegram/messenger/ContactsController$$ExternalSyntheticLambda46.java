package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda46 implements Comparator {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda46 INSTANCE = new ContactsController$$ExternalSyntheticLambda46();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda46() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$25((TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda39 implements Comparator {
    public final /* synthetic */ ContactsController f$0;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda39(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.m17x277ef5f((TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
    }
}

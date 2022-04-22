package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_contact;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda44 implements Comparator {
    public final /* synthetic */ ContactsController f$0;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda44(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$buildContactsSectionsArrays$43((TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
    }
}

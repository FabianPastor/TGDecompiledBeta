package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_contact;
/* loaded from: classes.dex */
public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda50 implements Comparator {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda50 INSTANCE = new ContactsController$$ExternalSyntheticLambda50();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda50() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getContactsHash$25;
        lambda$getContactsHash$25 = ContactsController.lambda$getContactsHash$25((TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
        return lambda$getContactsHash$25;
    }
}

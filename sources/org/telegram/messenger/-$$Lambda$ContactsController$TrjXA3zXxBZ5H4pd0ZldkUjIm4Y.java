package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_contact;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$TrjXA3zXxBZ5H4pd0ZldkUjIm4Y implements Comparator {
    public static final /* synthetic */ -$$Lambda$ContactsController$TrjXA3zXxBZ5H4pd0ZldkUjIm4Y INSTANCE = new -$$Lambda$ContactsController$TrjXA3zXxBZ5H4pd0ZldkUjIm4Y();

    private /* synthetic */ -$$Lambda$ContactsController$TrjXA3zXxBZ5H4pd0ZldkUjIm4Y() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$25((TL_contact) obj, (TL_contact) obj2);
    }
}

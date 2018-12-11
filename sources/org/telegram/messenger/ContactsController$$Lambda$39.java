package org.telegram.messenger;

import android.util.SparseArray;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class ContactsController$$Lambda$39 implements Comparator {
    private final SparseArray arg$1;

    ContactsController$$Lambda$39(SparseArray sparseArray) {
        this.arg$1 = sparseArray;
    }

    public int compare(Object obj, Object obj2) {
        return UserObject.getFirstName((User) this.arg$1.get(((TL_contact) obj).user_id)).compareTo(UserObject.getFirstName((User) this.arg$1.get(((TL_contact) obj2).user_id)));
    }
}

package org.telegram.messenger;

import android.util.SparseArray;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$hjm8gMZUsX8pFMsciXhWnMP28rs implements Comparator {
    private final /* synthetic */ SparseArray f$0;

    public /* synthetic */ -$$Lambda$ContactsController$hjm8gMZUsX8pFMsciXhWnMP28rs(SparseArray sparseArray) {
        this.f$0 = sparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return UserObject.getFirstName((User) this.f$0.get(((TL_contact) obj).user_id)).compareTo(UserObject.getFirstName((User) this.f$0.get(((TL_contact) obj2).user_id)));
    }
}

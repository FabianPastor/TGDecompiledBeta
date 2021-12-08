package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda43 implements Comparator {
    public final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda43(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return UserObject.getFirstName((TLRPC$User) this.f$0.get(((TLRPC$TL_contact) obj).user_id)).compareTo(UserObject.getFirstName((TLRPC$User) this.f$0.get(((TLRPC$TL_contact) obj2).user_id)));
    }
}

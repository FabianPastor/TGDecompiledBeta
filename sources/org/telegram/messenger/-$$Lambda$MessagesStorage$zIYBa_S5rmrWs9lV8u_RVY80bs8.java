package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$zIYBa_S5rmrWs9lV8u_RVY80bs8 implements Comparator {
    private final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$zIYBa_S5rmrWs9lV8u_RVY80bs8(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$49(this.f$0, (Long) obj, (Long) obj2);
    }
}

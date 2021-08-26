package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda181 implements Comparator {
    public final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda181(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$checkLoadedRemoteFilters$34(this.f$0, (Long) obj, (Long) obj2);
    }
}

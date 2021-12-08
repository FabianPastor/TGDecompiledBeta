package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda95 implements Comparator {
    public final /* synthetic */ LongSparseIntArray f$0;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda95(LongSparseIntArray longSparseIntArray) {
        this.f$0 = longSparseIntArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$checkLoadedRemoteFilters$39(this.f$0, (Long) obj, (Long) obj2);
    }
}

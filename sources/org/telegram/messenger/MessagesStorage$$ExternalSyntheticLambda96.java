package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda96 implements Comparator {
    public final /* synthetic */ LongSparseIntArray f$0;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda96(LongSparseIntArray longSparseIntArray) {
        this.f$0 = longSparseIntArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$resetDialogs$64(this.f$0, (Long) obj, (Long) obj2);
    }
}

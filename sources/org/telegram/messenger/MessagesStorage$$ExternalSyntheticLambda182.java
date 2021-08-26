package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda182 implements Comparator {
    public final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda182(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$resetDialogs$59(this.f$0, (Long) obj, (Long) obj2);
    }
}

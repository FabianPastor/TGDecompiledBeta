package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

final /* synthetic */ class MessagesStorage$$Lambda$119 implements Comparator {
    private final LongSparseArray arg$1;

    MessagesStorage$$Lambda$119(LongSparseArray longSparseArray) {
        this.arg$1 = longSparseArray;
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$43$MessagesStorage(this.arg$1, (Long) obj, (Long) obj2);
    }
}

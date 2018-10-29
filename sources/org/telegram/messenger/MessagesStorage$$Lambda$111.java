package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

final /* synthetic */ class MessagesStorage$$Lambda$111 implements Comparator {
    private final LongSparseArray arg$1;

    MessagesStorage$$Lambda$111(LongSparseArray longSparseArray) {
        this.arg$1 = longSparseArray;
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$41$MessagesStorage(this.arg$1, (Long) obj, (Long) obj2);
    }
}

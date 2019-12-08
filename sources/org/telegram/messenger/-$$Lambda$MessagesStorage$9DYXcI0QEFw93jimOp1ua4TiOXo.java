package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$9DYXcI0QEFw93jimOp1ua4TiOXo implements Comparator {
    private final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$9DYXcI0QEFw93jimOp1ua4TiOXo(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$48(this.f$0, (Long) obj, (Long) obj2);
    }
}

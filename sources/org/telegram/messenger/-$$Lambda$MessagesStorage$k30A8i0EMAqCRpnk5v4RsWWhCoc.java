package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$k30A8i0EMAqCRpnk5v4RsWWhCoc implements Comparator {
    private final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$k30A8i0EMAqCRpnk5v4RsWWhCoc(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$51(this.f$0, (Long) obj, (Long) obj2);
    }
}

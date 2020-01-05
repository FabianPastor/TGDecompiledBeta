package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$E1-iJ1217-dt315WyL33fblo70c implements Comparator {
    private final /* synthetic */ LongSparseArray f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$E1-iJ1217-dt315WyL33fblo70c(LongSparseArray longSparseArray) {
        this.f$0 = longSparseArray;
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$45(this.f$0, (Long) obj, (Long) obj2);
    }
}

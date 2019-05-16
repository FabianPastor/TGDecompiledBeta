package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$WzRDIT94H3f0FgoSArkqNqup9dc implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ -$$Lambda$DataQuery$WzRDIT94H3f0FgoSArkqNqup9dc(DataQuery dataQuery, ArrayList arrayList, long j, LongSparseArray longSparseArray) {
        this.f$0 = dataQuery;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$91$DataQuery(this.f$1, this.f$2, this.f$3);
    }
}

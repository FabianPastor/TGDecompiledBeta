package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$9HunpxjIAo_6OSplvvM0lciXK4o implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$9HunpxjIAo_6OSplvvM0lciXK4o(MediaDataController mediaDataController, ArrayList arrayList, long j, LongSparseArray longSparseArray) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$93$MediaDataController(this.f$1, this.f$2, this.f$3);
    }
}

package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda69(MediaDataController mediaDataController, ArrayList arrayList, long j, LongSparseArray longSparseArray, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = longSparseArray;
        this.f$4 = runnable;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$145(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda70 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda70(MediaDataController mediaDataController, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, long j, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = longSparseArray;
        this.f$3 = arrayList2;
        this.f$4 = j;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$processLoadedFeaturedStickers$35(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

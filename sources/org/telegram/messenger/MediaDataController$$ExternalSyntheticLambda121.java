package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda121 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ boolean f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda121(MediaDataController mediaDataController, boolean z, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, long j, int i, boolean z2) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = arrayList2;
        this.f$5 = j;
        this.f$6 = i;
        this.f$7 = z2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedFeaturedStickers$45(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}

package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda63 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda63(MediaDataController mediaDataController, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = longSparseArray;
        this.f$3 = arrayList2;
        this.f$4 = i;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedFeaturedStickers$26(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

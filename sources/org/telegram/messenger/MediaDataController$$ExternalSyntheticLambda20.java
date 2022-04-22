package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ LongSparseArray f$7;
    public final /* synthetic */ HashMap f$8;
    public final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda20(MediaDataController mediaDataController, int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, long j, int i2, LongSparseArray longSparseArray2, HashMap hashMap2, LongSparseArray longSparseArray3) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = longSparseArray;
        this.f$3 = hashMap;
        this.f$4 = arrayList;
        this.f$5 = j;
        this.f$6 = i2;
        this.f$7 = longSparseArray2;
        this.f$8 = hashMap2;
        this.f$9 = longSparseArray3;
    }

    public final void run() {
        this.f$0.lambda$processLoadedStickers$65(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}

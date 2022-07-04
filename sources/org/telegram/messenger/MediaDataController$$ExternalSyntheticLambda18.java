package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda18(MediaDataController mediaDataController, boolean z, ArrayList arrayList, LongSparseArray longSparseArray) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.m2103x51190305(this.f$1, this.f$2, this.f$3);
    }
}

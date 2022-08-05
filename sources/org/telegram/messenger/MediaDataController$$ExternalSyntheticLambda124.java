package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda124 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean[] f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int[] f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda124(MediaDataController mediaDataController, boolean[] zArr, ArrayList arrayList, int i, int[] iArr) {
        this.f$0 = mediaDataController;
        this.f$1 = zArr;
        this.f$2 = arrayList;
        this.f$3 = i;
        this.f$4 = iArr;
    }

    public final void run() {
        this.f$0.lambda$removeMultipleStickerSets$89(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

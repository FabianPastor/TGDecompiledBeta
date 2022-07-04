package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda114 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda114(MediaDataController mediaDataController, boolean z, ArrayList arrayList, int i, long j, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = i;
        this.f$4 = j;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedStickers$75(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

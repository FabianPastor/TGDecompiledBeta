package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda69(MediaDataController mediaDataController, ArrayList arrayList, long j, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$processLoadedFeaturedStickers$44(this.f$1, this.f$2, this.f$3);
    }
}

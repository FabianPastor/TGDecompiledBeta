package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda77(MediaDataController mediaDataController, ArrayList arrayList, ArrayList arrayList2, int i, long j, boolean z, boolean z2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = i;
        this.f$4 = j;
        this.f$5 = z;
        this.f$6 = z2;
    }

    public final void run() {
        this.f$0.lambda$putFeaturedStickersToCache$50(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

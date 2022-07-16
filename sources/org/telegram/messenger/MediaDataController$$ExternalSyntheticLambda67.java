package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda67 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda67(MediaDataController mediaDataController, ArrayList arrayList, int i, int i2, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = j;
    }

    public final void run() {
        this.f$0.lambda$putStickersToCache$74(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

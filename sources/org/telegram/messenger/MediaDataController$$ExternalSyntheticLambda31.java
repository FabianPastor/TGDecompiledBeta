package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda31(MediaDataController mediaDataController, long j, int i, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$loadPinnedMessages$106(this.f$1, this.f$2, this.f$3);
    }
}

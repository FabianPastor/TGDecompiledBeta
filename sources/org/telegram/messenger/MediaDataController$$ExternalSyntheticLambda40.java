package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda40(MediaDataController mediaDataController, long j, long j2, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$loadPinnedMessages$135(this.f$1, this.f$2, this.f$3);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda66 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda66(MediaDataController mediaDataController, ArrayList arrayList, boolean z, long j, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$putMediaDatabase$83(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

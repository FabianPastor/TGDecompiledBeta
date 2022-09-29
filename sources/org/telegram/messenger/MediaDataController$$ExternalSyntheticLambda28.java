package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda28(MediaDataController mediaDataController, int i, ArrayList arrayList, boolean z, long j, int i2, int i3) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = z;
        this.f$4 = j;
        this.f$5 = i2;
        this.f$6 = i3;
    }

    public final void run() {
        this.f$0.lambda$putMediaDatabase$118(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$uVH2dRIV8o8juRnyOtyvE6ykpsk implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$uVH2dRIV8o8juRnyOtyvE6ykpsk(MediaDataController mediaDataController, boolean z, int i, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processLoadedRecentDocuments$16$MediaDataController(this.f$1, this.f$2, this.f$3);
    }
}

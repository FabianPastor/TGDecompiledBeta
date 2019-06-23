package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ZnV_qmtLmwC1gIohVVsBEFY05mw implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$MediaDataController$ZnV_qmtLmwC1gIohVVsBEFY05mw(MediaDataController mediaDataController, ArrayList arrayList, int i, int i2, int i3) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
    }

    public final void run() {
        this.f$0.lambda$putStickersToCache$36$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

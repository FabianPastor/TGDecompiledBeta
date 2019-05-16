package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$7M05Rnf_3mDk_ioxbIIY86tv1DE implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$DataQuery$7M05Rnf_3mDk_ioxbIIY86tv1DE(DataQuery dataQuery, boolean z, int i, ArrayList arrayList) {
        this.f$0 = dataQuery;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processLoadedRecentDocuments$15$DataQuery(this.f$1, this.f$2, this.f$3);
    }
}

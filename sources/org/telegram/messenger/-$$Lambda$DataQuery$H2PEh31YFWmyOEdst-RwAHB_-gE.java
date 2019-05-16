package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$H2PEh31YFWmyOEdst-RwAHB_-gE implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$DataQuery$H2PEh31YFWmyOEdst-RwAHB_-gE(DataQuery dataQuery, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = dataQuery;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$saveReplyMessages$95$DataQuery(this.f$1, this.f$2);
    }
}

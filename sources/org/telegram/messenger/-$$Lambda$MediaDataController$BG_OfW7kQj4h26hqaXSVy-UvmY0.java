package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$BG_OfW7kQj4h26hqaXSVy-UvmY0 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$BG_OfW7kQj4h26hqaXSVy-UvmY0(MediaDataController mediaDataController, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$saveReplyMessages$97$MediaDataController(this.f$1, this.f$2);
    }
}

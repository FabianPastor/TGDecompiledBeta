package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$rRteuseUCLPEGifXqEl5lbllTCw implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$rRteuseUCLPEGifXqEl5lbllTCw(MediaDataController mediaDataController, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$saveReplyMessages$96$MediaDataController(this.f$1, this.f$2);
    }
}

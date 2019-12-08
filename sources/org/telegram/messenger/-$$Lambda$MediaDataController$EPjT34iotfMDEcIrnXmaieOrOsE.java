package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$EPjT34iotfMDEcIrnXmaieOrOsE implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ SparseArray f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$EPjT34iotfMDEcIrnXmaieOrOsE(MediaDataController mediaDataController, boolean z, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$saveReplyMessages$98$MediaDataController(this.f$1, this.f$2, this.f$3);
    }
}

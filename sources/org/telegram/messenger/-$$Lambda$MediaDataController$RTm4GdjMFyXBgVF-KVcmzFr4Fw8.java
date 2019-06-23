package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$RTm4GdjMFyXBgVF-KVcmzFr4Fw8 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ ArrayList f$4;
    private final /* synthetic */ SparseArray f$5;
    private final /* synthetic */ SparseArray f$6;
    private final /* synthetic */ SparseArray f$7;
    private final /* synthetic */ long f$8;

    public /* synthetic */ -$$Lambda$MediaDataController$RTm4GdjMFyXBgVF-KVcmzFr4Fw8(MediaDataController mediaDataController, ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = sparseArray;
        this.f$6 = sparseArray2;
        this.f$7 = sparseArray3;
        this.f$8 = j;
    }

    public final void run() {
        this.f$0.lambda$broadcastReplyMessages$96$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}

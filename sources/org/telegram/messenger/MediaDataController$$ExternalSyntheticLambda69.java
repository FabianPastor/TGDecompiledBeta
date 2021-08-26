package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ SparseArray f$6;
    public final /* synthetic */ SparseArray f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda69(MediaDataController mediaDataController, ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, SparseArray sparseArray, SparseArray sparseArray2) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = arrayList4;
        this.f$6 = sparseArray;
        this.f$7 = sparseArray2;
    }

    public final void run() {
        this.f$0.lambda$broadcastPinnedMessage$112(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}

package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$kKgvar_CGjTqorWBEogGTKvY68Q implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ StringBuilder f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ SparseArray f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MediaDataController$kKgvar_CGjTqorWBEogGTKvY68Q(MediaDataController mediaDataController, StringBuilder stringBuilder, long j, ArrayList arrayList, SparseArray sparseArray, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = stringBuilder;
        this.f$2 = j;
        this.f$3 = arrayList;
        this.f$4 = sparseArray;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$95$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
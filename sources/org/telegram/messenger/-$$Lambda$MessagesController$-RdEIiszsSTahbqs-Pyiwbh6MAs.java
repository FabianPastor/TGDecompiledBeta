package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$-RdEIiszsSTahbqs-Pyiwbh6MAs implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SparseArray f$10;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ LongSparseArray f$3;
    private final /* synthetic */ LongSparseArray f$4;
    private final /* synthetic */ LongSparseArray f$5;
    private final /* synthetic */ LongSparseArray f$6;
    private final /* synthetic */ boolean f$7;
    private final /* synthetic */ ArrayList f$8;
    private final /* synthetic */ ArrayList f$9;

    public /* synthetic */ -$$Lambda$MessagesController$-RdEIiszsSTahbqs-Pyiwbh6MAs(MessagesController messagesController, int i, ArrayList arrayList, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, LongSparseArray longSparseArray4, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = longSparseArray2;
        this.f$5 = longSparseArray3;
        this.f$6 = longSparseArray4;
        this.f$7 = z;
        this.f$8 = arrayList2;
        this.f$9 = arrayList3;
        this.f$10 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$268$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}

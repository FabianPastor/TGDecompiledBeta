package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ LongSparseArray f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ ArrayList f$12;
    public final /* synthetic */ ArrayList f$13;
    public final /* synthetic */ SparseArray f$14;
    public final /* synthetic */ SparseArray f$15;
    public final /* synthetic */ SparseArray f$16;
    public final /* synthetic */ ArrayList f$17;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ SparseArray f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ LongSparseArray f$6;
    public final /* synthetic */ LongSparseArray f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda49(MessagesController messagesController, int i, ArrayList arrayList, SparseArray sparseArray, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, ArrayList arrayList2, LongSparseArray longSparseArray4, LongSparseArray longSparseArray5, boolean z, ArrayList arrayList3, ArrayList arrayList4, SparseArray sparseArray2, SparseArray sparseArray3, SparseArray sparseArray4, ArrayList arrayList5) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = sparseArray;
        this.f$4 = i2;
        this.f$5 = longSparseArray;
        this.f$6 = longSparseArray2;
        this.f$7 = longSparseArray3;
        this.f$8 = arrayList2;
        this.f$9 = longSparseArray4;
        this.f$10 = longSparseArray5;
        this.f$11 = z;
        this.f$12 = arrayList3;
        this.f$13 = arrayList4;
        this.f$14 = sparseArray2;
        this.f$15 = sparseArray3;
        this.f$16 = sparseArray4;
        this.f$17 = arrayList5;
    }

    public final void run() {
        MessagesController messagesController = this.f$0;
        MessagesController messagesController2 = messagesController;
        messagesController2.lambda$processUpdateArray$304(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17);
    }
}

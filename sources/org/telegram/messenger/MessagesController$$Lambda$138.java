package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$138 implements Runnable {
    private final MessagesController arg$1;
    private final SparseArray arg$10;
    private final int arg$2;
    private final ArrayList arg$3;
    private final LongSparseArray arg$4;
    private final LongSparseArray arg$5;
    private final LongSparseArray arg$6;
    private final boolean arg$7;
    private final ArrayList arg$8;
    private final ArrayList arg$9;

    MessagesController$$Lambda$138(MessagesController messagesController, int i, ArrayList arrayList, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = arrayList;
        this.arg$4 = longSparseArray;
        this.arg$5 = longSparseArray2;
        this.arg$6 = longSparseArray3;
        this.arg$7 = z;
        this.arg$8 = arrayList2;
        this.arg$9 = arrayList3;
        this.arg$10 = sparseArray;
    }

    public void run() {
        this.arg$1.lambda$processUpdateArray$229$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10);
    }
}

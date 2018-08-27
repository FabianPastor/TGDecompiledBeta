package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

final /* synthetic */ class MessagesController$$Lambda$139 implements Runnable {
    private final MessagesController arg$1;
    private final SparseLongArray arg$2;
    private final SparseLongArray arg$3;
    private final SparseIntArray arg$4;
    private final ArrayList arg$5;
    private final SparseArray arg$6;
    private final SparseIntArray arg$7;

    MessagesController$$Lambda$139(MessagesController messagesController, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, ArrayList arrayList, SparseArray sparseArray, SparseIntArray sparseIntArray2) {
        this.arg$1 = messagesController;
        this.arg$2 = sparseLongArray;
        this.arg$3 = sparseLongArray2;
        this.arg$4 = sparseIntArray;
        this.arg$5 = arrayList;
        this.arg$6 = sparseArray;
        this.arg$7 = sparseIntArray2;
    }

    public void run() {
        this.arg$1.lambda$processUpdateArray$231$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}

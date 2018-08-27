package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class MessagesStorage$$Lambda$118 implements Runnable {
    private final MessagesStorage arg$1;
    private final LongSparseArray arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;
    private final ArrayList arg$5;
    private final ArrayList arg$6;

    MessagesStorage$$Lambda$118(MessagesStorage messagesStorage, LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        this.arg$1 = messagesStorage;
        this.arg$2 = longSparseArray;
        this.arg$3 = arrayList;
        this.arg$4 = arrayList2;
        this.arg$5 = arrayList3;
        this.arg$6 = arrayList4;
    }

    public void run() {
        this.arg$1.lambda$null$19$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

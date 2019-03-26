package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_Difference;

final /* synthetic */ class MessagesController$$Lambda$175 implements Runnable {
    private final MessagesController arg$1;
    private final updates_Difference arg$2;
    private final ArrayList arg$3;
    private final SparseArray arg$4;
    private final SparseArray arg$5;

    MessagesController$$Lambda$175(MessagesController messagesController, updates_Difference updates_difference, ArrayList arrayList, SparseArray sparseArray, SparseArray sparseArray2) {
        this.arg$1 = messagesController;
        this.arg$2 = updates_difference;
        this.arg$3 = arrayList;
        this.arg$4 = sparseArray;
        this.arg$5 = sparseArray2;
    }

    public void run() {
        this.arg$1.lambda$null$211$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

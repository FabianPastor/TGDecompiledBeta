package org.telegram.messenger;

import android.util.SparseIntArray;
import java.util.ArrayList;

final /* synthetic */ class MessagesController$$Lambda$36 implements Runnable {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final SparseIntArray arg$4;

    MessagesController$$Lambda$36(MessagesController messagesController, ArrayList arrayList, boolean z, SparseIntArray sparseIntArray) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = sparseIntArray;
    }

    public void run() {
        this.arg$1.lambda$processLoadedBlockedUsers$53$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}

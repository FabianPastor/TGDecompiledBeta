package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.support.SparseLongArray;

final /* synthetic */ class MessagesStorage$$Lambda$35 implements Runnable {
    private final MessagesStorage arg$1;
    private final SparseLongArray arg$2;
    private final SparseLongArray arg$3;
    private final ArrayList arg$4;

    MessagesStorage$$Lambda$35(MessagesStorage messagesStorage, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        this.arg$1 = messagesStorage;
        this.arg$2 = sparseLongArray;
        this.arg$3 = sparseLongArray2;
        this.arg$4 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateDialogsWithReadMessages$54$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}

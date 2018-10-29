package org.telegram.messenger;

import android.util.SparseArray;

final /* synthetic */ class MessagesController$$Lambda$224 implements Runnable {
    private final MessagesController arg$1;
    private final SparseArray arg$2;

    MessagesController$$Lambda$224(MessagesController messagesController, SparseArray sparseArray) {
        this.arg$1 = messagesController;
        this.arg$2 = sparseArray;
    }

    public void run() {
        this.arg$1.lambda$null$67$MessagesController(this.arg$2);
    }
}

package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class MessagesController$$Lambda$201 implements Runnable {
    private final IntCallback arg$1;
    private final Updates arg$2;

    MessagesController$$Lambda$201(IntCallback intCallback, Updates updates) {
        this.arg$1 = intCallback;
        this.arg$2 = updates;
    }

    public void run() {
        MessagesController.lambda$null$158$MessagesController(this.arg$1, this.arg$2);
    }
}

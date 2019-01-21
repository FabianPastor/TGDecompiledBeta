package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class MessagesStorage$$Lambda$112 implements Runnable {
    private final IntCallback arg$1;
    private final int arg$2;

    MessagesStorage$$Lambda$112(IntCallback intCallback, int i) {
        this.arg$1 = intCallback;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$null$87$MessagesStorage(this.arg$2);
    }
}

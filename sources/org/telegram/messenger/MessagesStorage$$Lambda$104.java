package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class MessagesStorage$$Lambda$104 implements Runnable {
    private final IntCallback arg$1;
    private final int arg$2;

    MessagesStorage$$Lambda$104(IntCallback intCallback, int i) {
        this.arg$1 = intCallback;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.run(this.arg$2);
    }
}

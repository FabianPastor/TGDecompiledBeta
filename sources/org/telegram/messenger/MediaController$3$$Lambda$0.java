package org.telegram.messenger;

import org.telegram.messenger.MediaController.C02503;

final /* synthetic */ class MediaController$3$$Lambda$0 implements Runnable {
    private final C02503 arg$1;
    private final MessageObject arg$2;

    MediaController$3$$Lambda$0(C02503 c02503, MessageObject messageObject) {
        this.arg$1 = c02503;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$run$0$MediaController$3(this.arg$2);
    }
}

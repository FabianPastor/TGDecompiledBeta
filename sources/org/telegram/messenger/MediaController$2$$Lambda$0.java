package org.telegram.messenger;

import org.telegram.messenger.MediaController.C02492;

final /* synthetic */ class MediaController$2$$Lambda$0 implements Runnable {
    private final C02492 arg$1;
    private final int arg$2;

    MediaController$2$$Lambda$0(C02492 c02492, int i) {
        this.arg$1 = c02492;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$onCallStateChanged$0$MediaController$2(this.arg$2);
    }
}

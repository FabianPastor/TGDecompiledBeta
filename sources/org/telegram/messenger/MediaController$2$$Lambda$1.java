package org.telegram.messenger;

import org.telegram.messenger.MediaController.AnonymousClass2;

final /* synthetic */ class MediaController$2$$Lambda$1 implements Runnable {
    private final AnonymousClass2 arg$1;
    private final double arg$2;

    MediaController$2$$Lambda$1(AnonymousClass2 anonymousClass2, double d) {
        this.arg$1 = anonymousClass2;
        this.arg$2 = d;
    }

    public void run() {
        this.arg$1.lambda$run$2$MediaController$2(this.arg$2);
    }
}

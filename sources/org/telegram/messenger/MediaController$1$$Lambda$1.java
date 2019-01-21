package org.telegram.messenger;

import org.telegram.messenger.MediaController.AnonymousClass1;

final /* synthetic */ class MediaController$1$$Lambda$1 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final double arg$2;

    MediaController$1$$Lambda$1(AnonymousClass1 anonymousClass1, double d) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = d;
    }

    public void run() {
        this.arg$1.lambda$run$2$MediaController$1(this.arg$2);
    }
}

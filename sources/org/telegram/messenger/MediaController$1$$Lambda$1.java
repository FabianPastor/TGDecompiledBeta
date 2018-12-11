package org.telegram.messenger;

import org.telegram.messenger.MediaController.CLASSNAME;

final /* synthetic */ class MediaController$1$$Lambda$1 implements Runnable {
    private final CLASSNAME arg$1;
    private final double arg$2;

    MediaController$1$$Lambda$1(CLASSNAME CLASSNAME, double d) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = d;
    }

    public void run() {
        this.arg$1.lambda$run$2$MediaController$1(this.arg$2);
    }
}
